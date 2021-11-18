/*
 * Copyright (c) 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.embedded;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRange;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager.RevisionLoader3;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.emf.ecore.EClass;

import java.util.List;

/**
 * @author Eike Stepper
 */
public final class ServerRevisionLoader implements RevisionLoader3
{
  private final RevisionLoader3 delegate;

  public ServerRevisionLoader(RevisionLoader3 delegate)
  {
    this.delegate = delegate;
  }

  public RevisionLoader3 getDelegate()
  {
    return delegate;
  }

  @Override
  @Deprecated
  public List<RevisionInfo> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<RevisionInfo> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth,
      boolean prefetchLockStates)
  {
    InternalSession serverSession = ServerSession.get();
    if (serverSession != null)
    {
      try
      {
        StoreThreadLocal.setSession(serverSession);
        return delegate.loadRevisions(infos, branchPoint, referenceChunk, prefetchDepth, prefetchLockStates);
      }
      finally
      {
        StoreThreadLocal.release();
      }
    }

    return delegate.loadRevisions(infos, branchPoint, referenceChunk, prefetchDepth, prefetchLockStates);
  }

  @Override
  public InternalCDORevision loadRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk)
  {
    InternalSession serverSession = ServerSession.get();
    if (serverSession != null)
    {
      try
      {
        StoreThreadLocal.setSession(serverSession);
        return delegate.loadRevisionByVersion(id, branchVersion, referenceChunk);
      }
      finally
      {
        StoreThreadLocal.release();
      }
    }

    return delegate.loadRevisionByVersion(id, branchVersion, referenceChunk);
  }

  @Override
  public void handleRevisions(EClass eClass, CDOBranch branch, boolean exactBranch, long timeStamp, boolean exactTime, CDORevisionHandler handler)
  {
    InternalSession serverSession = ServerSession.get();
    if (serverSession != null)
    {
      try
      {
        StoreThreadLocal.setSession(serverSession);
        delegate.handleRevisions(eClass, branch, exactBranch, timeStamp, exactTime, handler);
      }
      finally
      {
        StoreThreadLocal.release();
      }
    }
    else
    {
      delegate.handleRevisions(eClass, branch, exactBranch, timeStamp, exactTime, handler);
    }
  }

  @Override
  public CDOBranchPointRange loadObjectLifetime(CDOID id, CDOBranchPoint branchPoint)
  {
    InternalSession serverSession = ServerSession.get();
    if (serverSession != null)
    {
      try
      {
        StoreThreadLocal.setSession(serverSession);
        return delegate.loadObjectLifetime(id, branchPoint);
      }
      finally
      {
        StoreThreadLocal.release();
      }
    }

    return delegate.loadObjectLifetime(id, branchPoint);
  }
}
