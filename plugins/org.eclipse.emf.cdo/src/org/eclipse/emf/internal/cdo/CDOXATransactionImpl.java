/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOSavepoint;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.CDOViewSet;
import org.eclipse.emf.cdo.CDOXATransaction;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.protocol.CommitTransactionCancelRequest;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionPhase1Request;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionPhase2Request;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionPhase3Request;
import org.eclipse.emf.internal.cdo.protocol.CommitTransactionResult;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.signal.failover.IFailOverStrategy;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Three-phase commit.
 * <p>
 * Phase 1 does the following for each CDOTransaction:<br>
 * - preCommit <br>
 * - Accumulate external temporary ID.<br>
 * - request the commit to the server.<br>
 * - The server registers the commit context and returns the final ID for each temporary ID.
 * <p>
 * Phase 2 does the following for each CDOTransaction:<br>
 * - Transfer to the server a list of mapping of temporary externalID and final external ID that we accumulate
 * previously<br>
 * - Returns to the client only when commit process is ready to flush to disk (commit). <br>
 * <p>
 * Phase 3 does the following for each CDOTransaction:<br>
 * - Make modifications permanent.<br>
 * - PostCommit.
 * <p>
 * If an exception occurred during phase 1 or phase 2, the commit will be cancelled for all {@link CDOTransaction}
 * include in the XA transaction. If an exception occurred during phase 3, the commit will be cancelled only for the
 * {@link CDOTransaction} where the error happened.
 * <p>
 * All {@link CDOTransaction} includes in the commit process need to have finish their phase before moving to the next
 * phase. For one phase, every {@link CDOTransaction} could have their own thread. It depends of the ExecutorService.
 * <p>
 * 
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOXATransactionImpl implements CDOXATransaction
{
  private List<InternalCDOTransaction> views = new ArrayList<InternalCDOTransaction>();

  private boolean allRequestEnabled = true;

  private ExecutorService executorService = Executors.newFixedThreadPool(10);

  private Map<InternalCDOTransaction, CDOXATransactionCommitContext> activeContext = new HashMap<InternalCDOTransaction, CDOXATransactionCommitContext>();

  private Map<InternalCDOTransaction, Set<CDOID>> requestedCDOID = new HashMap<InternalCDOTransaction, Set<CDOID>>();

  private CDOXASavepoint lastSavepoint = new CDOXASavepoint(this, null);

  private CDOXASavepoint firstSavepoint = lastSavepoint;

  private CDOTransactionStrategy transactionStrategy = new CDOXATransactionStrategyImpl();

  private CDOXAInternalAdapter internalAdapter = new CDOXAInternalAdapter();

  public CDOXATransactionImpl()
  {
  }

  public boolean isAllowRequestFromTransactionEnabled()
  {
    return allRequestEnabled;
  }

  public void setAllowRequestFromTransactionEnabled(boolean allRequest)
  {
    allRequestEnabled = allRequest;
  }

  public void add(InternalCDOTransaction transaction)
  {
    transaction.setTransactionStrategy(transactionStrategy);
  }

  public void remove(InternalCDOTransaction transaction)
  {
    transaction.setTransactionStrategy(null);
  }

  synchronized public void add(CDOViewSet viewSet)
  {
    CDOXATransaction transSet = CDOUtil.getXATransaction(viewSet);
    if (transSet != null)
    {
      throw new IllegalArgumentException("XATransaction is already attached to this viewSet");
    }

    viewSet.eAdapters().add(internalAdapter);

    for (InternalCDOTransaction transaction : getTransactions(viewSet))
    {
      add(transaction);
    }
  }

  synchronized public void remove(CDOViewSet viewSet)
  {
    CDOXATransaction transSet = CDOUtil.getXATransaction(viewSet);
    if (transSet != this)
    {
      throw new IllegalArgumentException("ViewSet isn't attached to this XATransaction");
    }

    for (InternalCDOTransaction transaction : getTransactions(viewSet))
    {
      remove(transaction);
    }

    viewSet.eAdapters().remove(internalAdapter);
  };

  public void add(InternalCDOTransaction view, CDOID object)
  {
    synchronized (requestedCDOID)
    {
      Set<CDOID> ids = requestedCDOID.get(view);
      if (ids == null)
      {
        ids = new HashSet<CDOID>();
        requestedCDOID.put(view, ids);
      }

      ids.add(object);
    }
  }

  public CDOID[] get(InternalCDOTransaction transaction)
  {
    Set<CDOID> ids = requestedCDOID.get(transaction);
    return ids.toArray(new CDOID[ids.size()]);
  }

  public CDOXATransactionCommitContext getCommitContext(CDOTransaction transaction)
  {
    return activeContext.get(transaction);
  }

  private void send(Collection<CDOXATransactionCommitContext> xaTransactions) throws InterruptedException,
      ExecutionException
  {
    List<Future<Object>> futures = new ArrayList<Future<Object>>();
    for (CDOXATransactionCommitContext xaTransaction : xaTransactions)
    {
      futures.add(executorService.submit(xaTransaction));
    }

    for (Future<Object> future : futures)
    {
      future.get();
    }
  }

  private void cleanUp()
  {
    activeContext.clear();
    requestedCDOID.clear();
  }

  private List<InternalCDOTransaction> getTransactions(CDOViewSet viewSet)
  {
    List<InternalCDOTransaction> transactions = new ArrayList<InternalCDOTransaction>();
    for (CDOView view : viewSet.getViews())
    {
      if (view instanceof InternalCDOTransaction)
      {
        transactions.add((InternalCDOTransaction)view);
      }
    }

    return transactions;
  }

  public void commit() throws TransactionException
  {
    int phase = 0;
    try
    {
      CDOXAState defaultState = CDOXAPhase1State.INSTANCE;

      /*
       * if (transactions.size() == 1) { defaultState = CDOXAALLPhaseState.INSTANCE; }
       */
      for (InternalCDOTransaction transaction : views)
      {
        CDOXATransactionCommitContext xaTransaction = new CDOXATransactionCommitContext(this, transaction
            .createCommitContext());
        xaTransaction.setState(defaultState);
        activeContext.put(transaction, xaTransaction);
      }

      // We need to complete 3 phases
      for (phase = 0; phase < 3; phase++)
      {
        send(activeContext.values());
      }
    }
    catch (Exception ex)
    {
      if (phase < 2)
      {
        // Phase 0 and 1 are the only two phases we can cancel.
        for (CDOXATransactionCommitContext transaction : activeContext.values())
        {
          transaction.setState(CDOXACancel.INSTANCE);
        }

        try
        {
          send(activeContext.values());
        }
        catch (InterruptedException ignore)
        {
        }
        catch (ExecutionException ignore)
        {
        }
      }

      throw new TransactionException(ex);
    }
    finally
    {
      cleanUp();
    }
  }

  public CDOXASavepoint getLastSavepoint()
  {
    return lastSavepoint;
  }

  public void rollback(boolean remote)
  {
    rollback(firstSavepoint, true);
  }

  public void rollback()
  {
    rollback(true);
  }

  public void rollback(CDOSavepoint savepoint)
  {
    rollback(savepoint, true);
  }

  public void rollback(CDOSavepoint savepoint, boolean remote)
  {
    if (savepoint == null)
    {
      throw new IllegalArgumentException("Save point is null");
    }

    if (savepoint.getUserTransaction() != this)
    {
      throw new IllegalArgumentException("Save point to rollback doesn't belong to this transaction: " + savepoint);
    }

    if (!savepoint.isValid())
    {
      throw new IllegalArgumentException("Savepoint isn't valid : " + savepoint);
    }

    CDOXASavepoint savepointSet = (CDOXASavepoint)savepoint;
    List<CDOSavepoint> savepoints = savepointSet.getSavepoints();
    if (savepoints == null)
    {
      savepoints = getListSavepoints();
    }

    for (CDOSavepoint indexSavePoint : savepoints)
    {
      InternalCDOTransaction transaction = (InternalCDOTransaction)indexSavePoint.getUserTransaction();
      CDOSingleTransactionStrategy.INSTANCE.rollback(transaction, indexSavePoint, remote);
    }

    lastSavepoint = savepointSet;
    lastSavepoint.setNextSavepoint(null);
    lastSavepoint.setSavepoints(null);
  }

  public CDOSavepoint setSavepoint()
  {
    List<CDOSavepoint> savepoints = getListSavepoints();
    for (CDOSavepoint savepoint : savepoints)
    {
      InternalCDOTransaction transaction = (InternalCDOTransaction)savepoint.getUserTransaction();
      CDOSingleTransactionStrategy.INSTANCE.setSavepoint(transaction);
    }

    getLastSavepoint().setSavepoints(savepoints);
    lastSavepoint = new CDOXASavepoint(this, getLastSavepoint());
    return lastSavepoint;
  }

  private List<CDOSavepoint> getListSavepoints()
  {
    synchronized (views)
    {
      List<CDOSavepoint> savepoints = new ArrayList<CDOSavepoint>();
      for (InternalCDOTransaction transaction : views)
      {
        savepoints.add(transaction.getLastSavepoint());
      }

      return savepoints;
    }
  }

  /**
   * @author Simon McDuff
   */
  private final class CDOXATransactionStrategyImpl implements CDOTransactionStrategy
  {
    public CDOXATransactionStrategyImpl()
    {
    }

    public void setTarget(InternalCDOTransaction transaction)
    {
      synchronized (views)
      {
        views.add(transaction);
      }
    }

    public void unsetTarget(InternalCDOTransaction transaction)
    {
      synchronized (views)
      {
        views.remove(transaction);
      }
    }

    private void check_access()
    {
      if (!allRequestEnabled)
      {
        throw new IllegalStateException("Commit from CDOTransaction is not allowed.");
      }
    }

    public void commit(InternalCDOTransaction transactionCommit) throws Exception
    {
      check_access();
      CDOXATransactionImpl.this.commit();
    }

    public void rollback(InternalCDOTransaction transaction, CDOSavepoint savepoint, boolean remote)
    {
      check_access();
      CDOXATransactionImpl.this.rollback(savepoint, remote);
    }

    public CDOSavepoint setSavepoint(InternalCDOTransaction transaction)
    {
      check_access();
      return CDOXATransactionImpl.this.setSavepoint();
    }
  }

  /**
   * @author Simon McDuff
   */
  public static abstract class CDOXAState
  {
    public static final CDOXAState DONE = new CDOXAState()
    {
      @Override
      protected void handle(CDOXATransactionCommitContext xaTransaction) throws Exception
      {
        // Do nothing
      }
    };

    protected void check_result(CommitTransactionResult result)
    {
      if (result != null && result.getRollbackMessage() != null)
      {
        throw new TransactionException(result.getRollbackMessage());
      }
    }

    protected abstract void handle(CDOXATransactionCommitContext xaTransaction) throws Exception;
  };

  /**
   * @author Simon McDuff
   */
  public static class CDOXAPhase1State extends CDOXAState
  {
    public static final CDOXAPhase1State INSTANCE = new CDOXAPhase1State();

    @Override
    protected void handle(CDOXATransactionCommitContext xaTransaction) throws Exception
    {
      xaTransaction.preCommit();

      InternalCDOTransaction transaction = xaTransaction.getTransaction();
      CDOSession session = xaTransaction.getTransaction().getSession();
      IChannel channel = session.getChannel();
      IFailOverStrategy failOverStrategy = session.getFailOverStrategy();

      // Phase 1
      {
        CommitTransactionPhase1Request requestPhase1 = new CommitTransactionPhase1Request(channel, xaTransaction);
        CommitTransactionResult result = failOverStrategy.send(requestPhase1, transaction.getCommitTimeout());
        check_result(result);
        xaTransaction.setResult(result);
        xaTransaction.setState(CDOXAPhase2State.INSTANCE);
      }
    }
  };

  /**
   * @author Simon McDuff
   */
  public static class CDOXAPhase2State extends CDOXAState
  {
    public static final CDOXAPhase2State INSTANCE = new CDOXAPhase2State();

    public CDOXAPhase2State()
    {
    }

    @Override
    protected void handle(CDOXATransactionCommitContext xaTransaction) throws Exception
    {
      InternalCDOTransaction transaction = xaTransaction.getTransaction();
      CDOSession session = xaTransaction.getTransaction().getSession();
      IChannel channel = session.getChannel();
      IFailOverStrategy failOverStrategy = session.getFailOverStrategy();

      // Phase 2
      {
        CommitTransactionPhase2Request requestPhase2 = new CommitTransactionPhase2Request(channel, xaTransaction);
        CommitTransactionResult result = failOverStrategy.send(requestPhase2, transaction.getCommitTimeout());
        check_result(result);
        xaTransaction.setState(CDOXAPhase3State.INSTANCE);
      }
    }
  };

  /**
   * @author Simon McDuff
   */
  public static class CDOXAPhase3State extends CDOXAState
  {
    public static final CDOXAPhase3State INSTANCE = new CDOXAPhase3State();

    public CDOXAPhase3State()
    {
    }

    @Override
    protected void handle(CDOXATransactionCommitContext xaTransaction) throws Exception
    {
      InternalCDOTransaction transaction = xaTransaction.getTransaction();
      CDOSession session = xaTransaction.getTransaction().getSession();
      IChannel channel = session.getChannel();
      IFailOverStrategy failOverStrategy = session.getFailOverStrategy();

      // Phase 2
      {
        CommitTransactionPhase3Request requestPhase3 = new CommitTransactionPhase3Request(channel, xaTransaction);
        CommitTransactionResult result = failOverStrategy.send(requestPhase3, transaction.getCommitTimeout());
        check_result(result);
        xaTransaction.postCommit(xaTransaction.getResult());
        xaTransaction.setState(null);
      }
    }
  };

  /**
   * @author Simon McDuff
   */
  public static class CDOXACancel extends CDOXAState
  {
    public static final CDOXACancel INSTANCE = new CDOXACancel();

    public CDOXACancel()
    {
    }

    @Override
    protected void handle(CDOXATransactionCommitContext xaTransaction) throws Exception
    {
      InternalCDOTransaction transaction = xaTransaction.getTransaction();
      CDOSession session = xaTransaction.getTransaction().getSession();
      IChannel channel = session.getChannel();
      IFailOverStrategy failOverStrategy = session.getFailOverStrategy();

      // Phase 2
      {
        CommitTransactionCancelRequest requestCancel = new CommitTransactionCancelRequest(channel, xaTransaction);
        CommitTransactionResult result = failOverStrategy.send(requestCancel, transaction.getCommitTimeout());
        check_result(result);
      }
    }
  };

  /**
   * @author Simon McDuff
   */
  public static class CDOXAALLPhaseState extends CDOXAState
  {
    public static final CDOXAALLPhaseState INSTANCE = new CDOXAALLPhaseState();

    public CDOXAALLPhaseState()
    {
    }

    @Override
    protected void handle(CDOXATransactionCommitContext xaTransaction) throws Exception
    {
      CDOTransactionStrategy.DEFAULT.commit(xaTransaction.getTransaction());
      xaTransaction.setState(null);
    }
  }

  /**
   * @author Simon McDuff
   */
  public class CDOXAInternalAdapter implements Adapter
  {
    public CDOXATransactionImpl getCDOXA()
    {
      return CDOXATransactionImpl.this;
    }

    public Notifier getTarget()
    {
      return null;
    }

    public boolean isAdapterForType(Object type)
    {
      return false;
    }

    public void notifyChanged(Notification notification)
    {
      switch (notification.getEventType())
      {
      case Notification.ADD:
        if (notification.getNewValue() instanceof InternalCDOTransaction)
        {
          CDOXATransactionImpl.this.add((InternalCDOTransaction)notification.getNewValue());
        }
        break;

      case Notification.REMOVE:
        if (notification.getOldValue() instanceof InternalCDOTransaction)
        {
          CDOXATransactionImpl.this.remove((InternalCDOTransaction)notification.getNewValue());
        }
        break;
      }
    }

    public void setTarget(Notifier newTarget)
    {
    }
  }
}
