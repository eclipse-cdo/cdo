/*
 * Copyright (c) 2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.lissome;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.internal.lissome.optimizer.CommitTransactionTask;
import org.eclipse.emf.cdo.server.internal.lissome.optimizer.Optimizer;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.util.Collection;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class LissomeStoreWriter extends LissomeStoreReader
{
  public LissomeStoreWriter(LissomeStore store, ITransaction transaction)
  {
    super(store, transaction);
  }

  @Deprecated
  @Override
  public LockArea createLockArea(String durableLockingID, String userID, CDOBranchPoint branchPoint, boolean readOnly, Map<CDOID, LockGrade> locks)
  {
    // TODO: implement LissomeStoreWriter.createLockArea(durableLockingID, userID, branchPoint, readOnly, locks)
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public void updateLockArea(LockArea lockArea)
  {
    // TODO: implement LissomeStoreWriter.updateLockArea(lockArea)
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public void lock(String durableLockingID, LockType type, Collection<? extends Object> objectsToLock)
  {
    // TODO: implement LissomeStoreWriter.lock(durableLockingID, type, objectsToLock)
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public void unlock(String durableLockingID, LockType type, Collection<? extends Object> objectsToUnlock)
  {
    // TODO: implement LissomeStoreWriter.unlock(durableLockingID, type, objectsToUnlock)
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public void unlock(String durableLockingID)
  {
    // TODO: implement LissomeStoreWriter.unlock(durableLockingID)
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public LockArea createLockArea(String userID, CDOBranchPoint branchPoint, boolean readOnly, Map<CDOID, LockGrade> locks) throws LockAreaAlreadyExistsException
  {
    // TODO: implement LissomeStoreWriter.createLockArea(userID, branchPoint, readOnly, locks)
    throw new UnsupportedOperationException();
  }

  @Deprecated
  @Override
  public void deleteLockArea(String durableLockingID)
  {
    // TODO: implement LissomeStoreWriter.deleteLockArea(durableLockingID)
    throw new UnsupportedOperationException();
  }

  @Override
  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor)
  {
    journal.writePackageUnits(packageUnits, monitor);
  }

  @Override
  protected void doWrite(InternalCommitContext context, OMMonitor monitor)
  {
    monitor.begin(100002);

    try
    {
      IDGenerationLocation idGenerationLocation = getStore().getRepository().getIDGenerationLocation();
      if (idGenerationLocation == IDGenerationLocation.STORE)
      {
        addIDMappings(context, monitor.fork());
      }
      else
      {
        monitor.worked();
      }

      context.applyIDMappings(monitor.fork());
      journal.write(context, monitor.fork(monitor.getTotalWork() - 2));
    }
    finally
    {
      monitor.done();
    }
  }

  @Override
  protected void doCommit(OMMonitor monitor)
  {
    CommitTransactionTask task = journal.commit(monitor);
    if (task != null)
    {
      Optimizer optimizer = getStore().getOptimizer();
      optimizer.addTask(task);
    }
  }

  @Override
  protected void doRollback(CommitContext commitContext)
  {
    journal.rollback(commitContext);
  }

  @Override
  protected CDOID getNextCDOID(CDORevision revision)
  {
    return getStore().getNextCDOID();
  }
}
