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
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRange;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.SyntheticCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.emf.ecore.EClass;

import java.util.List;

/**
 * @author Eike Stepper
 */
public final class ClientRevisionManager extends AbstractClientManager<InternalCDORevisionManager> implements InternalCDORevisionManager
{
  public ClientRevisionManager(InternalCDORevisionManager delegate)
  {
    super(delegate);
  }

  public RevisionLocker getRevisionLocker()
  {
    return delegate.getRevisionLocker();
  }

  public void setRevisionLocker(RevisionLocker revisionLocker)
  {
    if (revisionLocker instanceof CDONet4jSession)
    {
      initServerSession((CDONet4jSession)revisionLocker);
    }
  }

  public RevisionLoader getRevisionLoader()
  {
    return delegate.getRevisionLoader();
  }

  public void setRevisionLoader(RevisionLoader revisionLoader)
  {
    // Do nothing.
  }

  public CDORevisionFactory getFactory()
  {
    return delegate.getFactory();
  }

  public void setFactory(CDORevisionFactory factory)
  {
    // Do nothing.
  }

  public InternalCDORevisionCache getCache()
  {
    return delegate.getCache();
  }

  public void setCache(CDORevisionCache cache)
  {
    // Do nothing.
  }

  public boolean isSupportingAudits()
  {
    return delegate.isSupportingAudits();
  }

  public void setSupportingAudits(boolean on)
  {
    // Do nothing.
  }

  public boolean isSupportingBranches()
  {
    return delegate.isSupportingBranches();
  }

  public void setSupportingBranches(boolean on)
  {
    // Do nothing.
  }

  public EClass getObjectType(CDOID id)
  {
    try
    {
      ServerSession.set(serverSession);
      return delegate.getObjectType(id);
    }
    finally
    {
      ServerSession.unset();
    }
  }

  public EClass getObjectType(CDOID id, CDOBranchManager branchManagerForLoadOnDemand)
  {
    try
    {
      ServerSession.set(serverSession);
      return delegate.getObjectType(id, branchManagerForLoadOnDemand);
    }
    finally
    {
      ServerSession.unset();
    }
  }

  public CDOBranchPointRange getObjectLifetime(CDOID id, CDOBranchPoint branchPoint)
  {
    try
    {
      ServerSession.set(serverSession);
      return delegate.getObjectLifetime(id, branchPoint);
    }
    finally
    {
      ServerSession.unset();
    }
  }

  public boolean containsRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    try
    {
      ServerSession.set(serverSession);
      return delegate.containsRevision(id, branchPoint);
    }
    finally
    {
      ServerSession.unset();
    }
  }

  public boolean containsRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    try
    {
      ServerSession.set(serverSession);
      return delegate.containsRevisionByVersion(id, branchVersion);
    }
    finally
    {
      ServerSession.unset();
    }
  }

  public InternalCDORevision getBaseRevision(CDORevision revision, int referenceChunk, boolean loadOnDemand)
  {
    try
    {
      ServerSession.set(serverSession);
      return delegate.getBaseRevision(revision, referenceChunk, loadOnDemand);
    }
    finally
    {
      ServerSession.unset();
    }
  }

  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand,
      SyntheticCDORevision[] synthetics)
  {
    try
    {
      ServerSession.set(serverSession);
      return delegate.getRevision(id, branchPoint, referenceChunk, prefetchDepth, loadOnDemand, synthetics);
    }
    finally
    {
      ServerSession.unset();
    }
  }

  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand,
      SyntheticCDORevision[] synthetics)
  {
    try
    {
      ServerSession.set(serverSession);
      return delegate.getRevisions(ids, branchPoint, referenceChunk, prefetchDepth, loadOnDemand, synthetics);
    }
    finally
    {
      ServerSession.unset();
    }
  }

  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand)
  {
    try
    {
      ServerSession.set(serverSession);
      return delegate.getRevision(id, branchPoint, referenceChunk, prefetchDepth, loadOnDemand);
    }
    finally
    {
      ServerSession.unset();
    }
  }

  public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk, boolean loadOnDemand)
  {
    try
    {
      ServerSession.set(serverSession);
      return delegate.getRevisionByVersion(id, branchVersion, referenceChunk, loadOnDemand);
    }
    finally
    {
      ServerSession.unset();
    }
  }

  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand)
  {
    try
    {
      ServerSession.set(serverSession);
      return delegate.getRevisions(ids, branchPoint, referenceChunk, prefetchDepth, loadOnDemand);
    }
    finally
    {
      ServerSession.unset();
    }
  }

  public void handleRevisions(EClass eClass, CDOBranch branch, boolean exactBranch, long timeStamp, boolean exactTime, CDORevisionHandler handler)
  {
    try
    {
      ServerSession.set(serverSession);
      delegate.handleRevisions(eClass, branch, exactBranch, timeStamp, exactTime, handler);
    }
    finally
    {
      ServerSession.unset();
    }
  }

  public void addRevision(CDORevision revision)
  {
    try
    {
      ServerSession.set(serverSession);
      delegate.addRevision(revision);
    }
    finally
    {
      ServerSession.unset();
    }
  }

  public void reviseLatest(CDOID id, CDOBranch branch)
  {
    try
    {
      ServerSession.set(serverSession);
      delegate.reviseLatest(id, branch);
    }
    finally
    {
      ServerSession.unset();
    }
  }

  public void reviseVersion(CDOID id, CDOBranchVersion branchVersion, long timeStamp)
  {
    try
    {
      ServerSession.set(serverSession);
      delegate.reviseVersion(id, branchVersion, timeStamp);
    }
    finally
    {
      ServerSession.unset();
    }
  }

  @Override
  protected InternalRepository getRepository(InternalCDORevisionManager delegate)
  {
    ServerRevisionLoader revisionLoader = (ServerRevisionLoader)delegate.getRevisionLoader();
    return (InternalRepository)revisionLoader.getDelegate();
  }
}
