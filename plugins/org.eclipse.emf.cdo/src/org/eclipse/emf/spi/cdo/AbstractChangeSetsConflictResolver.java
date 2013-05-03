/*
 * Copyright (c) 2010-2013 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDODefaultTransactionHandler;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class AbstractChangeSetsConflictResolver extends AbstractConflictResolver
{
  private CDOTransactionHandler handler = new CDODefaultTransactionHandler()
  {
    @Override
    public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta ignored)
    {
      if (getTransaction() == transaction)
      {
        adapter.attach(object);
      }
    }

    @Override
    public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
    {
      if (getTransaction() == transaction)
      {
        adapter.reset();
        remoteInvalidationEvents.reset();
      }
    }

    @Override
    public void rolledBackTransaction(CDOTransaction transaction)
    {
      // Reset the accumulation only if it rolled back the transaction completely
      if (getTransaction() == transaction && transaction.getLastSavepoint().getPreviousSavepoint() == null)
      {
        adapter.reset();
        remoteInvalidationEvents.reset();
      }
    }
  };

  private CDOChangeSubscriptionAdapter adapter;

  private RemoteInvalidationEventQueue remoteInvalidationEvents;

  public AbstractChangeSetsConflictResolver()
  {
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
    return remoteInvalidationEvents.getChangeSetData();
  }

  public CDOChangeSet getRemoteChangeSet()
  {
    CDOChangeSetData changeSetData = remoteInvalidationEvents.getChangeSetData();
    return createChangeSet(changeSetData);
  }

  @Override
  protected void hookTransaction(CDOTransaction transaction)
  {
    transaction.addTransactionHandler(handler);
    adapter = new CDOChangeSubscriptionAdapter(getTransaction());
    remoteInvalidationEvents = new RemoteInvalidationEventQueue();
  }

  @Override
  protected void unhookTransaction(CDOTransaction transaction)
  {
    remoteInvalidationEvents.dispose();
    remoteInvalidationEvents = null;

    adapter.dispose();
    adapter = null;

    transaction.removeTransactionHandler(handler);
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
