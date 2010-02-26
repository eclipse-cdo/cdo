/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.offline;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

import org.eclipse.net4j.util.collection.IndexedList;
import org.eclipse.net4j.util.om.monitor.Monitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;

import java.util.Arrays;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CloneRepository extends Repository.Default
{
  private CloneSynchronizer synchronizer;

  private InternalSession replicatorSession;

  private int lastTransactionID;

  public CloneRepository()
  {
  }

  public CloneSynchronizer getSynchronizer()
  {
    return synchronizer;
  }

  public void setSynchronizer(CloneSynchronizer synchronizer)
  {
    checkInactive();
    this.synchronizer = synchronizer;
  }

  @Override
  public Object[] getElements()
  {
    List<Object> list = Arrays.asList(super.getElements());
    list.add(synchronizer);
    return list.toArray();
  }

  public void replicate(CDOCommitInfo commitInfo)
  {
    CDOBranchPoint head = commitInfo.getBranch().getHead();
    InternalTransaction transaction = replicatorSession.openTransaction(++lastTransactionID, head);
    ReplicatorCommitContext commitContext = new ReplicatorCommitContext(transaction, commitInfo);
    commitContext.preWrite();

    try
    {
      commitContext.write(new Monitor());
      commitContext.commit(new Monitor());
      setLastCommitTimeStamp(commitInfo.getTimeStamp());
    }
    finally
    {
      commitContext.postCommit(true);
      transaction.close();
    }
  }

  @Override
  public InternalCommitContext createCommitContext(InternalTransaction transaction)
  {
    return new ClientCommitContext(transaction);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(synchronizer, "synchronizer"); //$NON-NLS-1$
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    replicatorSession = getSessionManager().openSession(null);
    replicatorSession.options().setPassiveUpdateEnabled(false);

    synchronizer.setClone(this);
    synchronizer.activate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    synchronizer.deactivate();
    super.doDeactivate();
  }

  /**
   * TODO Optimize createCommitInfo()
   * 
   * @author Eike Stepper
   */
  private static final class ReplicatorCommitContext extends TransactionCommitContext
  {
    private final CDOCommitInfo commitInfo;

    public ReplicatorCommitContext(InternalTransaction transaction, CDOCommitInfo commitInfo)
    {
      super(transaction);
      this.commitInfo = commitInfo;
      setCommitComment(commitInfo.getComment());

      InternalCDOPackageUnit[] newPackageUnits = getNewPackageUnits(commitInfo);
      setNewPackageUnits(newPackageUnits);

      InternalCDORevision[] newObjects = getNewObjects(commitInfo);
      setNewObjects(newObjects);

      InternalCDORevisionDelta[] dirtyObjectDeltas = getDirtyObjectDeltas(commitInfo);
      setDirtyObjectDeltas(dirtyObjectDeltas);

      CDOID[] detachedObjects = getDetachedObjects(commitInfo);
      setDetachedObjects(detachedObjects);
    }

    @Override
    public String getUserID()
    {
      return commitInfo.getUserID();
    }

    @Override
    protected long createTimeStamp()
    {
      return commitInfo.getTimeStamp();
    }

    @Override
    protected void adjustMetaRanges()
    {
      // Do nothing
    }

    @Override
    protected void adjustForCommit()
    {
      // Do nothing
    }

    @Override
    public void applyIDMappings(OMMonitor monitor)
    {
      monitor.done();
    }

    @Override
    protected void lockObjects() throws InterruptedException
    {
      // Do nothing
    }

    private static InternalCDOPackageUnit[] getNewPackageUnits(CDOCommitInfo commitInfo)
    {
      List<CDOPackageUnit> list = commitInfo.getNewPackageUnits();
      InternalCDOPackageUnit[] result = new InternalCDOPackageUnit[list.size()];

      int i = 0;
      for (CDOPackageUnit packageUnit : list)
      {
        result[i++] = (InternalCDOPackageUnit)packageUnit;
      }

      return result;
    }

    private static InternalCDORevision[] getNewObjects(CDOCommitInfo commitInfo)
    {
      List<CDOIDAndVersion> list = commitInfo.getNewObjects();
      InternalCDORevision[] result = new InternalCDORevision[list.size()];

      int i = 0;
      for (CDOIDAndVersion revision : list)
      {
        result[i++] = (InternalCDORevision)revision;
      }

      return result;
    }

    private static InternalCDORevisionDelta[] getDirtyObjectDeltas(CDOCommitInfo commitInfo)
    {
      List<CDORevisionKey> list = commitInfo.getChangedObjects();
      InternalCDORevisionDelta[] result = new InternalCDORevisionDelta[list.size()];

      int i = 0;
      for (CDORevisionKey delta : list)
      {
        result[i++] = (InternalCDORevisionDelta)delta;
      }

      return result;
    }

    private static CDOID[] getDetachedObjects(CDOCommitInfo commitInfo)
    {
      List<CDOIDAndVersion> list = commitInfo.getDetachedObjects();
      CDOID[] result = new CDOID[list.size()];

      int i = 0;
      for (CDOIDAndVersion key : list)
      {
        result[i++] = key.getID();
      }

      return result;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ClientCommitContext extends TransactionCommitContext
  {
    private InternalCDOSession master = (InternalCDOSession)synchronizer.getMaster();

    public ClientCommitContext(InternalTransaction transaction)
    {
      super(transaction);
    }

    @Override
    public void write(OMMonitor monitor)
    {
      // InternalCDOPackageRegistry masterPackageRegistry = master.getPackageRegistry();
      // for (InternalCDOPackageUnit packageUnit : getNewPackageUnits())
      // {
      // // packageUnit.setState(CDOPackageUnit.State.NEW);
      // // getPackageRegistry().putPackageUnit(packageUnit);
      // masterPackageRegistry.putPackageUnit(packageUnit);
      // }
    }

    @Override
    public void commit(OMMonitor monitor)
    {
      CDOBranch branch = getTransaction().getBranch();
      String userID = getUserID();
      String comment = getCommitComment();
      CDOCommitData commitData = new CommitData();

      CommitTransactionResult result = master.getSessionProtocol().commitDelegation(branch, userID, comment,
          commitData, monitor);

      String rollbackMessage = result.getRollbackMessage();
      if (rollbackMessage != null)
      {
        throw new TransactionException(rollbackMessage);
      }

      long timeStamp = result.getTimeStamp();
      setTimeStamp(timeStamp);

      master.waitForUpdate(timeStamp);
      throw new RuntimeException();

      // ConcurrencyUtil.sleep(100000000);

      // Map<CDOID, CDOID> idMappings = result.getIDMappings();
      // for (Entry<CDOID, CDOID> idMapping : idMappings.entrySet())
      // {
      // CDOID oldID = idMapping.getKey();
      // CDOID newID = idMapping.getValue();
      // addIDMapping(oldID, newID);
      // }
    }

    /**
     * @author Eike Stepper
     */
    private final class CommitData implements CDOCommitData
    {
      public List<CDOPackageUnit> getNewPackageUnits()
      {
        final InternalCDOPackageUnit[] newPackageUnits = ClientCommitContext.this.getNewPackageUnits();
        return new IndexedList<CDOPackageUnit>()
        {
          @Override
          public CDOPackageUnit get(int index)
          {
            InternalCDOPackageUnit packageUnit = newPackageUnits[index];
            // packageUnit.setState(CDOPackageUnit.State.NEW);
            return packageUnit;
          }

          @Override
          public int size()
          {
            return newPackageUnits.length;
          }
        };
      }

      public List<CDOIDAndVersion> getNewObjects()
      {
        final InternalCDORevision[] newObjects = ClientCommitContext.this.getNewObjects();
        return new IndexedList<CDOIDAndVersion>()
        {
          @Override
          public CDOIDAndVersion get(int index)
          {
            return newObjects[index];
          }

          @Override
          public int size()
          {
            return newObjects.length;
          }
        };
      }

      public List<CDORevisionKey> getChangedObjects()
      {
        final InternalCDORevisionDelta[] changedObjects = getDirtyObjectDeltas();
        return new IndexedList<CDORevisionKey>()
        {
          @Override
          public CDORevisionKey get(int index)
          {
            return changedObjects[index];
          }

          @Override
          public int size()
          {
            return changedObjects.length;
          }
        };
      }

      public List<CDOIDAndVersion> getDetachedObjects()
      {
        final CDOID[] detachedObjects = ClientCommitContext.this.getDetachedObjects();
        return new IndexedList<CDOIDAndVersion>()
        {
          @Override
          public CDOIDAndVersion get(int index)
          {
            return CDOIDUtil.createIDAndVersion(detachedObjects[index], CDOBranchVersion.UNSPECIFIED_VERSION);
          }

          @Override
          public int size()
          {
            return detachedObjects.length;
          }
        };
      }
    }
  }
}
