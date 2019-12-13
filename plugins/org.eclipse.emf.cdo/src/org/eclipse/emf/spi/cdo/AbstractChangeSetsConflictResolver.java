/*
 * Copyright (c) 2010-2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.util.CDOTimeProvider;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDOConflictResolver.NonConflictAware;
import org.eclipse.emf.cdo.transaction.CDODefaultTransactionHandler;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class AbstractChangeSetsConflictResolver extends AbstractConflictResolver implements NonConflictAware
{
  private CDOTransactionHandler handler = new CDODefaultTransactionHandler()
  {
    @Override
    public void attachingObject(CDOTransaction transaction, CDOObject object)
    {
      if (getTransaction() == transaction)
      {
        transactionAttachingObject(object);
      }
    }

    @Override
    public void detachingObject(CDOTransaction transaction, CDOObject object)
    {
      if (getTransaction() == transaction)
      {
        transactionDetachingObject(object);
      }
    }

    @Override
    public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta)
    {
      if (getTransaction() == transaction)
      {
        transactionModifyingObject(object, featureDelta);
      }
    }

    @Override
    public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
      if (getTransaction() == transaction)
      {
        transactionCommitting(commitContext);
      }
    }

    @Override
    public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
      if (getTransaction() == transaction)
      {
        transactionCommitted(commitContext);
      }
    }

    @Override
    public void rolledBackTransaction(CDOTransaction transaction)
    {
      // Reset the accumulation only if it rolled back the transaction completely
      if (getTransaction() == transaction && transaction.getLastSavepoint().getPreviousSavepoint() == null)
      {
        transactionRolledBack();
      }
    }
  };

  private CDOChangeSubscriptionAdapter adapter;

  private RemoteInvalidationEventQueue remoteInvalidationEvents;

  private boolean ensureRemoteNotifications = true;

  private long remoteTimeStamp;

  public AbstractChangeSetsConflictResolver()
  {
  }

  /**
   * @param ensureRemoteNotifications boolean to disable the use of {@link CDOAdapterPolicy} to ensure remote changes reception for conflict resolution, true by default. Can be disabled to limit network traffic when {@link PassiveUpdateMode} is enabled and in {@link PassiveUpdateMode#CHANGES} or {@link PassiveUpdateMode#ADDITIONS}
   * @since 4.4
   */
  public AbstractChangeSetsConflictResolver(boolean ensureRemoteNotifications)
  {
    this.ensureRemoteNotifications = ensureRemoteNotifications;
  }

  public CDOChangeSetData getLocalChangeSetData()
  {
    return getTransaction().getChangeSetData();
  }

  public CDOChangeSet getLocalChangeSet()
  {
    CDOChangeSetData changeSetData = getLocalChangeSetData();
    return createChangeSet(changeSetData);
  }

  public CDOChangeSetData getRemoteChangeSetData()
  {
    return remoteInvalidationEvents.poll();
  }

  public CDOChangeSet getRemoteChangeSet()
  {
    remoteTimeStamp = CDOBranchPoint.UNSPECIFIED_DATE;

    CDOChangeSetData changeSetData = getRemoteChangeSetData();
    if (changeSetData == null)
    {
      return null;
    }

    if (changeSetData instanceof CDOTimeProvider)
    {
      remoteTimeStamp = ((CDOTimeProvider)changeSetData).getTimeStamp();
    }

    return createChangeSet(changeSetData);
  }

  /**
   * @since 4.4
   */
  public final long getRemoteTimeStamp()
  {
    return remoteTimeStamp;
  }

  /**
   * @since 4.3
   */
  @Override
  public void handleNonConflict(long updateTime)
  {
    remoteInvalidationEvents.remove(updateTime);
  }

  @Override
  protected void hookTransaction(CDOTransaction transaction)
  {
    if (ensureRemoteNotifications)
    {
      adapter = new CDOChangeSubscriptionAdapter(getTransaction());
      transaction.addTransactionHandler(handler);
    }

    remoteInvalidationEvents = new RemoteInvalidationEventQueue();
  }

  @Override
  protected void unhookTransaction(CDOTransaction transaction)
  {
    if (ensureRemoteNotifications)
    {
      transaction.removeTransactionHandler(handler);
      if (!transaction.isClosed())
      {
        adapter.dispose();
      }

      adapter = null;
    }

    remoteInvalidationEvents.dispose();
    remoteInvalidationEvents = null;
  }

  /**
   * @since 4.4
   */
  protected void transactionAttachingObject(CDOObject object)
  {
    // Do nothing.
  }

  /**
   * @since 4.4
   */
  protected void transactionDetachingObject(CDOObject object)
  {
    // Do nothing.
  }

  /**
   * @since 4.4
   */
  protected void transactionModifyingObject(CDOObject object, CDOFeatureDelta featureDelta)
  {
    adapter.attach(object);
  }

  /**
   * @since 4.4
   */
  protected void transactionCommitting(CDOCommitContext commitContext)
  {
    // Do nothing.
  }

  /**
   * @since 4.4
   */
  protected void transactionCommitted(CDOCommitContext commitContext)
  {
    adapter.reset();
    remoteInvalidationEvents.reset();
  }

  /**
   * @since 4.4
   */
  protected void transactionRolledBack()
  {
    adapter.reset();
    remoteInvalidationEvents.reset();
  }

  private CDOChangeSet createChangeSet(CDOChangeSetData changeSetData)
  {
    CDOTransaction transaction = getTransaction();
    return CDORevisionUtil.createChangeSet(transaction, transaction, changeSetData);
  }

  /**
   * @author Eike Stepper
   */
  private final class RemoteInvalidationEventQueue extends CDOSessionInvalidationEventQueue
  {
    public RemoteInvalidationEventQueue()
    {
      super(getTransaction().getSession());
    }

    @Override
    protected void handleEvent(CDOSessionInvalidationEvent event) throws Exception
    {
      CDOTransaction transaction = getTransaction();
      if (event.getLocalTransaction() == transaction)
      {
        // Don't handle own changes
        return;
      }

      if (event.getBranch() != transaction.getBranch())
      {
        // Don't handle changes in other branches
        return;
      }

      super.handleEvent(event);
    }
  }
}
