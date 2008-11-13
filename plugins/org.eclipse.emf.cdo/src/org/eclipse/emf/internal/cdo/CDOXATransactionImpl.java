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

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.om.monitor.EclipseMonitor;
import org.eclipse.net4j.util.om.monitor.SynchonizedSubProgressMonitor;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;

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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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
  private List<InternalCDOTransaction> transactions = new ArrayList<InternalCDOTransaction>();

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

  public synchronized void add(CDOViewSet viewSet)
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

  public synchronized void remove(CDOViewSet viewSet)
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

  private void send(Collection<CDOXATransactionCommitContext> xaContexts, final IProgressMonitor progressMonitor)
      throws InterruptedException, ExecutionException
  {
    progressMonitor.beginTask("", xaContexts.size());

    try
    {
      List<Future<Object>> futures = new ArrayList<Future<Object>>();
      for (CDOXATransactionCommitContext xaContext : xaContexts)
      {
        xaContext.setProgressMonitor(new SynchonizedSubProgressMonitor(progressMonitor, 1));
        futures.add(executorService.submit(xaContext));
      }

      int nbProcessDone;
      do
      {
        nbProcessDone = 0;
        for (Future<Object> future : futures)
        {
          try
          {
            future.get(1, TimeUnit.MILLISECONDS);
            nbProcessDone++;
          }
          catch (TimeoutException ex)
          {
          }
        }
      } while (xaContexts.size() != nbProcessDone);
    }
    finally
    {
      progressMonitor.done();
      for (CDOXATransactionCommitContext xaContext : xaContexts)
      {
        xaContext.setProgressMonitor(null);
      }
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
    commit(new NullProgressMonitor());
  }

  public void commit(IProgressMonitor progressMonitor) throws TransactionException
  {
    CheckUtil.checkArg(progressMonitor, "progressMonitor");
    progressMonitor.beginTask("Committing XA transaction", 3);
    int phase = 0;

    for (InternalCDOTransaction transaction : transactions)
    {
      CDOCommitContext context = transaction.createCommitContext();
      CDOXATransactionCommitContext xaContext = new CDOXATransactionCommitContext(this, context);
      xaContext.setState(CDOXAPhase1State.INSTANCE);
      activeContext.put(transaction, xaContext);
    }

    try
    {
      // We need to complete 3 phases
      while (phase < 3)
      {
        send(activeContext.values(), new SubProgressMonitor(progressMonitor, 1));
        ++phase;
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
          send(activeContext.values(), new SubProgressMonitor(progressMonitor, 2 - phase));
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
      progressMonitor.done();
    }
  }

  public CDOXASavepoint getLastSavepoint()
  {
    return lastSavepoint;
  }

  public void rollback()
  {
    rollback(firstSavepoint);
  }

  public void rollback(CDOSavepoint savepoint)
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
      CDOSingleTransactionStrategy.INSTANCE.rollback(transaction, indexSavePoint);
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
    synchronized (transactions)
    {
      List<CDOSavepoint> savepoints = new ArrayList<CDOSavepoint>();
      for (InternalCDOTransaction transaction : transactions)
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
      synchronized (transactions)
      {
        transactions.add(transaction);
      }
    }

    public void unsetTarget(InternalCDOTransaction transaction)
    {
      synchronized (transactions)
      {
        transactions.remove(transaction);
      }
    }

    private void checkAccess()
    {
      if (!allRequestEnabled)
      {
        throw new IllegalStateException("Commit from CDOTransaction is not allowed.");
      }
    }

    public void commit(InternalCDOTransaction transactionCommit, IProgressMonitor progressMonitor) throws Exception
    {
      checkAccess();
      CDOXATransactionImpl.this.commit(progressMonitor);
    }

    public void rollback(InternalCDOTransaction transaction, CDOSavepoint savepoint)
    {
      checkAccess();
      CDOXATransactionImpl.this.rollback(savepoint);
    }

    public CDOSavepoint setSavepoint(InternalCDOTransaction transaction)
    {
      checkAccess();
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
      protected void handle(CDOXATransactionCommitContext xaContext, IProgressMonitor progressMonitor) throws Exception
      {
        progressMonitor.done();
      }
    };

    protected void check_result(CommitTransactionResult result)
    {
      if (result != null && result.getRollbackMessage() != null)
      {
        throw new TransactionException(result.getRollbackMessage());
      }
    }

    protected abstract void handle(CDOXATransactionCommitContext xaContext, IProgressMonitor progressMonitor)
        throws Exception;
  };

  /**
   * @author Simon McDuff
   */
  public static class CDOXAPhase1State extends CDOXAState
  {
    public static final CDOXAPhase1State INSTANCE = new CDOXAPhase1State();

    @Override
    protected void handle(CDOXATransactionCommitContext xaContext, IProgressMonitor progressMonitor) throws Exception
    {
      xaContext.preCommit();
      CDOSessionImpl session = (CDOSessionImpl)xaContext.getTransaction().getSession();

      // Phase 1
      {
        CommitTransactionResult result = new CommitTransactionPhase1Request(session.getProtocol(), xaContext)
            .send(new EclipseMonitor(progressMonitor));
        check_result(result);

        xaContext.setResult(result);
        xaContext.setState(CDOXAPhase2State.INSTANCE);
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
    protected void handle(CDOXATransactionCommitContext xaContext, IProgressMonitor progressMonitor) throws Exception
    {
      CDOSessionImpl session = (CDOSessionImpl)xaContext.getTransaction().getSession();

      // Phase 2
      {
        CommitTransactionResult result = new CommitTransactionPhase2Request(session.getProtocol(), xaContext)
            .send(new EclipseMonitor(progressMonitor));
        check_result(result);
        xaContext.setState(CDOXAPhase3State.INSTANCE);
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
    protected void handle(CDOXATransactionCommitContext xaContext, IProgressMonitor progressMonitor) throws Exception
    {
      CDOSessionImpl session = (CDOSessionImpl)xaContext.getTransaction().getSession();

      // Phase 2
      {
        CommitTransactionResult result = new CommitTransactionPhase3Request(session.getProtocol(), xaContext)
            .send(new EclipseMonitor(progressMonitor));
        check_result(result);
        xaContext.postCommit(xaContext.getResult());
        xaContext.setState(null);
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
    protected void handle(CDOXATransactionCommitContext xaContext, IProgressMonitor progressMonitor) throws Exception
    {
      CDOSessionImpl session = (CDOSessionImpl)xaContext.getTransaction().getSession();

      // Phase 2
      {
        CommitTransactionResult result = new CommitTransactionCancelRequest(session.getProtocol(), xaContext)
            .send(new EclipseMonitor(progressMonitor));
        check_result(result);
      }
    }
  };

  /**
   * @author Simon McDuff
   */
  public class CDOXAInternalAdapter implements Adapter
  {
    public CDOXAInternalAdapter()
    {
    }

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
