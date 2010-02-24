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

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
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

import org.eclipse.net4j.util.om.monitor.Monitor;

import java.util.Arrays;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CloneRepository extends Repository.Default
{
  // private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REPOSITORY, ClonedRepository.class);

  private CloneSynchronizer synchronizer;

  private InternalSession replicatorSession;

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
    InternalTransaction transaction = replicatorSession.openTransaction(1, head);
    InternalCommitContext commitContext = new ReplicatorCommitContext(transaction, commitInfo);

    commitContext.write(new Monitor());
    commitContext.commit(new Monitor());
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

    synchronizer.setClone(this);
    synchronizer.activate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    synchronizer.deactivate();
    super.doDeactivate();
  }

  // private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REPOSITORY, ClonedRepository.class);

  /**
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
}
