/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.revision.cache.branch;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCacheFactory;
import org.eclipse.emf.cdo.common.revision.cache.InternalCDORevisionCache;

import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.EClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class BranchDispatcher extends Lifecycle implements InternalCDORevisionCache
{
  private Map<CDOBranch, InternalCDORevisionCache> caches = new HashMap<CDOBranch, InternalCDORevisionCache>();

  private CDORevisionCacheFactory factory;

  public BranchDispatcher()
  {
  }

  public InternalCDORevisionCache instantiate(CDORevision revision)
  {
    BranchDispatcher cache = new BranchDispatcher();
    cache.setFactory(factory);
    return cache;
  }

  public boolean isSupportingBranches()
  {
    return true;
  }

  public CDORevisionCacheFactory getFactory()
  {
    return factory;
  }

  public void setFactory(CDORevisionCacheFactory factory)
  {
    checkInactive();
    this.factory = factory;
  }

  public EClass getObjectType(CDOID id)
  {
    for (CDORevisionCache cache : getCaches())
    {
      EClass type = cache.getObjectType(id);
      if (type != null)
      {
        return type;
      }
    }

    return null;
  }

  public CDORevision getRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    CDORevisionCache cache = getCache(branchPoint.getBranch());
    if (cache == null)
    {
      return null;
    }

    return cache.getRevision(id, branchPoint);
  }

  public CDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    CDORevisionCache cache = getCache(branchVersion.getBranch());
    if (cache == null)
    {
      return null;
    }

    return cache.getRevisionByVersion(id, branchVersion);
  }

  public CDORevision removeRevision(CDOID id, CDOBranchVersion branchVersion)
  {
    InternalCDORevisionCache cache = getCache(branchVersion.getBranch());
    if (cache == null)
    {
      return null;
    }

    return cache.removeRevision(id, branchVersion);
  }

  public boolean addRevision(CDORevision revision)
  {
    InternalCDORevisionCache cache;
    CDOBranch branch = revision.getBranch();
    synchronized (caches)
    {
      cache = caches.get(branch);
      if (cache == null)
      {
        cache = (InternalCDORevisionCache)factory.createRevisionCache(revision);
        LifecycleUtil.activate(cache);
        caches.put(branch, cache);
      }
    }

    return cache.addRevision(revision);
  }

  public List<CDORevision> getCurrentRevisions()
  {
    List<CDORevision> result = new ArrayList<CDORevision>();
    for (CDORevisionCache cache : getCaches())
    {
      result.addAll(cache.getCurrentRevisions());
    }

    return result;
  }

  public void clear()
  {
    for (InternalCDORevisionCache cache : getCaches())
    {
      cache.clear();
    }
  }

  public Map<CDOBranch, List<CDORevision>> getAllRevisions()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(factory, "factory");
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (CDORevisionCache cache : getCaches())
    {
      LifecycleUtil.deactivate(cache);
    }

    super.doDeactivate();
  }

  private InternalCDORevisionCache getCache(CDOBranch branch)
  {
    synchronized (caches)
    {
      return caches.get(branch);
    }
  }

  private InternalCDORevisionCache[] getCaches()
  {
    synchronized (caches)
    {
      return caches.values().toArray(new InternalCDORevisionCache[caches.size()]);
    }
  }
}
