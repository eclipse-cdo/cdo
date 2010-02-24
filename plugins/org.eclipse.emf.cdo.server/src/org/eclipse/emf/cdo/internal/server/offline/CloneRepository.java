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
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;

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
    InternalTransaction transaction = openReplicatorTransaction(commitInfo);
    InternalCommitContext commitContext = createReplicatorCommitContext(transaction, commitInfo);

    System.out.println(commitContext);
  }

  private InternalTransaction openReplicatorTransaction(CDOCommitInfo commitInfo)
  {
    CDOBranchPoint head = commitInfo.getBranch().getHead();
    InternalTransaction transaction = replicatorSession.openTransaction(1, head);
    return transaction;
  }

  private static InternalCommitContext createReplicatorCommitContext(InternalTransaction transaction,
      CDOCommitInfo commitInfo)
  {
    InternalCommitContext commitContext = transaction.createCommitContext();
    commitContext.setCommitComment(commitInfo.getComment());

    InternalCDOPackageUnit[] newPackageUnits = getNewPackageUnits(commitInfo);
    commitContext.setNewPackageUnits(newPackageUnits);

    InternalCDORevision[] newObjects = getNewObjects(commitInfo);
    commitContext.setNewObjects(newObjects);

    InternalCDORevisionDelta[] dirtyObjectDeltas = getDirtyObjectDeltas(commitInfo);
    commitContext.setDirtyObjectDeltas(dirtyObjectDeltas);

    CDOID[] detachedObjects = getDetachedObjects(commitInfo);
    commitContext.setDetachedObjects(detachedObjects);

    return commitContext;
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

  /**
   * @author Eike Stepper
   */
  private final class ReplicatorTransaction implements InternalTransaction
  {
    private CDOBranch branch;

    public void unsubscribe(CDOID id)
    {
      throw new UnsupportedOperationException();
    }

    public void subscribe(CDOID id)
    {
      throw new UnsupportedOperationException();
    }

    public boolean hasSubscription(CDOID id)
    {
      return false;
    }

    public void doClose()
    {
      // Do nothing
    }

    public void clearChangeSubscription()
    {
    }

    public boolean[] changeTarget(CDOBranchPoint branchPoint, List<CDOID> invalidObjects)
    {
      throw new UnsupportedOperationException();
    }

    public boolean isClosed()
    {
      return false;
    }

    public void close()
    {
    }

    public int compareTo(CDOBranchPoint o)
    {
      return 0;
    }

    public long getTimeStamp()
    {
      return CDOBranchPoint.UNSPECIFIED_DATE;
    }

    public CDOBranch getBranch()
    {
      return branch;
    }

    public boolean isReadOnly()
    {
      return false;
    }

    public int getViewID()
    {
      return 0;
    }

    public InternalSession getSession()
    {
      return null;
    }

    public InternalRepository getRepository()
    {
      return CloneRepository.this;
    }

    public InternalCommitContext createCommitContext()
    {
      return null;
    }
  }
}
