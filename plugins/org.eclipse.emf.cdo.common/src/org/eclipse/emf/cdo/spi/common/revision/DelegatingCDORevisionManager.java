/*
 * Copyright (c) 2010-2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.revision;

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

import org.eclipse.net4j.util.lifecycle.Lifecycle;

import org.eclipse.emf.ecore.EClass;

import java.util.List;
import java.util.function.Consumer;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 3.0
 */
public abstract class DelegatingCDORevisionManager extends Lifecycle implements InternalCDORevisionManager
{
  public DelegatingCDORevisionManager()
  {
  }

  @Override
  public InternalCDORevisionCache getCache()
  {
    return getDelegate().getCache();
  }

  /**
   * @since 4.0
   */
  @Override
  public void setCache(CDORevisionCache cache)
  {
    getDelegate().setCache(cache);
  }

  @Override
  public void setFactory(CDORevisionFactory factory)
  {
    getDelegate().setFactory(factory);
  }

  @Override
  public CDORevisionFactory getFactory()
  {
    return getDelegate().getFactory();
  }

  @Override
  public RevisionLoader getRevisionLoader()
  {
    return getDelegate().getRevisionLoader();
  }

  @Override
  public void setRevisionLoader(RevisionLoader revisionLoader)
  {
    getDelegate().setRevisionLoader(revisionLoader);
  }

  @Override
  public RevisionLocker getRevisionLocker()
  {
    return getDelegate().getRevisionLocker();
  }

  @Override
  public void setRevisionLocker(RevisionLocker revisionLocker)
  {
    getDelegate().setRevisionLocker(revisionLocker);
  }

  /**
   * @since 4.0
   */
  @Override
  public boolean isSupportingAudits()
  {
    return getDelegate().isSupportingAudits();
  }

  /**
   * @since 4.0
   */
  @Override
  public void setSupportingAudits(boolean on)
  {
    getDelegate().setSupportingAudits(on);
  }

  @Override
  public boolean isSupportingBranches()
  {
    return getDelegate().isSupportingBranches();
  }

  @Override
  public void setSupportingBranches(boolean on)
  {
    getDelegate().setSupportingBranches(on);
  }

  /**
   * @since 4.0
   */
  @Deprecated
  @Override
  public void addRevision(CDORevision revision)
  {
    getDelegate().addRevision(revision);
  }

  @Override
  public CDORevision internRevision(CDORevision revision)
  {
    return getDelegate().internRevision(revision);
  }

  @Override
  public boolean containsRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    return getDelegate().containsRevision(id, branchPoint);
  }

  @Override
  public boolean containsRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    return getDelegate().containsRevisionByVersion(id, branchVersion);
  }

  @Override
  public EClass getObjectType(CDOID id)
  {
    return getDelegate().getObjectType(id);
  }

  @Override
  public EClass getObjectType(CDOID id, CDOBranchManager branchManagerForLoadOnDemand)
  {
    return getDelegate().getObjectType(id, branchManagerForLoadOnDemand);
  }

  @Override
  public CDOBranchPointRange getObjectLifetime(CDOID id, CDOBranchPoint branchPoint)
  {
    return getDelegate().getObjectLifetime(id, branchPoint);
  }

  @Override
  public InternalCDORevision getBaseRevision(CDORevision revision, int referenceChunk, boolean loadOnDemand)
  {
    return getDelegate().getBaseRevision(revision, referenceChunk, loadOnDemand);
  }

  @Override
  public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk, boolean loadOnDemand)
  {
    return getDelegate().getRevisionByVersion(id, branchVersion, referenceChunk, loadOnDemand);
  }

  @Override
  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand)
  {
    return getDelegate().getRevision(id, branchPoint, referenceChunk, prefetchDepth, loadOnDemand);
  }

  @Override
  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand,
      SyntheticCDORevision[] synthetics)
  {
    return getDelegate().getRevision(id, branchPoint, referenceChunk, prefetchDepth, loadOnDemand, synthetics);
  }

  @Override
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand)
  {
    return getDelegate().getRevisions(ids, branchPoint, referenceChunk, prefetchDepth, loadOnDemand);
  }

  @Override
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand,
      SyntheticCDORevision[] synthetics)
  {
    return getDelegate().getRevisions(ids, branchPoint, referenceChunk, prefetchDepth, loadOnDemand, synthetics);
  }

  /**
   * @since 4.15
   */
  @Override
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth, boolean loadOnDemand,
      List<CDORevision> additionalRevisions)
  {
    return getDelegate().getRevisions(ids, branchPoint, referenceChunk, prefetchDepth, loadOnDemand, additionalRevisions);
  }

  @Override
  public void prefetchRevisions(CDOID id, CDOBranchPoint branchPoint, int prefetchDepth, Consumer<CDORevision> consumer)
  {
    getDelegate().prefetchRevisions(id, branchPoint, prefetchDepth, consumer);
  }

  @Override
  public void handleRevisions(EClass eClass, CDOBranch branch, boolean exactBranch, long timeStamp, boolean exactTime, CDORevisionHandler handler)
  {
    getDelegate().handleRevisions(eClass, branch, exactBranch, timeStamp, exactTime, handler);
  }

  @Override
  public void reviseLatest(CDOID id, CDOBranch branch)
  {
    getDelegate().reviseLatest(id, branch);
  }

  @Override
  public void reviseVersion(CDOID id, CDOBranchVersion branchVersion, long timeStamp)
  {
    getDelegate().reviseVersion(id, branchVersion, timeStamp);
  }

  @Override
  protected void doActivate() throws Exception
  {
    if (isDelegatingLifecycle())
    {
      getDelegate().activate();
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    if (isDelegatingLifecycle())
    {
      getDelegate().deactivate();
    }
  }

  protected boolean isDelegatingLifecycle()
  {
    return true;
  }

  protected abstract InternalCDORevisionManager getDelegate();

  /**
   * @author Eike Stepper
   */
  @SuppressWarnings("unused")
  private static final class InternalCompletenessChecker extends DelegatingCDORevisionManager
  {
    @Override
    protected InternalCDORevisionManager getDelegate()
    {
      return null;
    }
  }
}
