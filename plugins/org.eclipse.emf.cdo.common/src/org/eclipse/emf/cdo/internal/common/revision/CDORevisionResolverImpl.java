/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/230832
 **************************************************************************/
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOPackageManager;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCacheUtil;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.InternalCDORevision;

import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class CDORevisionResolverImpl extends Lifecycle implements CDORevisionResolver
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, CDORevisionResolverImpl.class);

  private CDORevisionCache cache;

  public CDORevisionResolverImpl()
  {
  }

  public CDORevisionCache getCache()
  {
    return cache;
  }

  public void setCache(CDORevisionCache cache)
  {
    this.cache = cache;
  }

  public boolean containsRevision(CDOID id)
  {
    return cache.getRevision(id) != null;
  }

  public boolean containsRevisionByTime(CDOID id, long timeStamp)
  {
    return cache.getRevisionByTime(id, timeStamp) != null;
  }

  public boolean containsRevisionByVersion(CDOID id, int version)
  {
    return cache.getRevisionByVersion(id, version) != null;
  }

  public CDOClass getObjectType(CDOID id)
  {
    return cache.getObjectType(id);
  }

  public InternalCDORevision getRevision(CDOID id, int referenceChunk)
  {
    return getRevision(id, referenceChunk, true);
  }

  public InternalCDORevision getRevision(CDOID id, int referenceChunk, boolean loadOnDemand)
  {
    InternalCDORevision revision = cache.getRevision(id);
    if (revision == null)
    {
      if (loadOnDemand)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Loading revision {0}", id);
        }

        revision = loadRevision(id, referenceChunk);
        addCachedRevisionIfNotNull(revision);
      }
    }
    else
    {
      InternalCDORevision oldRevision = revision;
      revision = verifyRevision(oldRevision, referenceChunk);
      if (revision != oldRevision)
      {
        addCachedRevisionIfNotNull(revision);
      }
    }

    return revision;
  }

  public InternalCDORevision getRevisionByTime(CDOID id, int referenceChunk, long timeStamp)
  {
    return getRevisionByTime(id, referenceChunk, timeStamp, true);
  }

  public InternalCDORevision getRevisionByTime(CDOID id, int referenceChunk, long timeStamp, boolean loadOnDemand)
  {
    InternalCDORevision revision = cache.getRevisionByTime(id, timeStamp);
    if (revision == null)
    {
      if (loadOnDemand)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Loading revision {0} by time {1,date} {1,time}", id, timeStamp);
        }

        revision = loadRevisionByTime(id, referenceChunk, timeStamp);
        addCachedRevisionIfNotNull(revision);
      }
    }
    else
    {
      InternalCDORevision verified = verifyRevision(revision, referenceChunk);
      if (revision != verified)
      {
        addCachedRevisionIfNotNull(verified);
        revision = verified;
      }
    }

    return revision;
  }

  public synchronized InternalCDORevision getRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    return getRevisionByVersion(id, referenceChunk, version, true);
  }

  public InternalCDORevision getRevisionByVersion(CDOID id, int referenceChunk, int version, boolean loadOnDemand)
  {
    InternalCDORevision revision = cache.getRevisionByVersion(id, version);
    if (revision == null)
    {
      if (loadOnDemand)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Loading revision {0} by version {1}", id, version);
        }

        revision = loadRevisionByVersion(id, referenceChunk, version);
        addCachedRevisionIfNotNull(revision);
      }
    }

    return revision;
  }

  public List<CDORevision> getRevisions(Collection<CDOID> ids, int referenceChunk)
  {
    List<CDOID> missingIDs = new ArrayList<CDOID>(0);
    List<CDORevision> revisions = new ArrayList<CDORevision>(ids.size());
    for (CDOID id : ids)
    {
      InternalCDORevision revision = getRevision(id, referenceChunk, false);
      revisions.add(revision);
      if (revision == null)
      {
        missingIDs.add(id);
      }
    }

    if (!missingIDs.isEmpty())
    {
      List<InternalCDORevision> missingRevisions = loadRevisions(missingIDs, referenceChunk);
      handleMissingRevisions(revisions, missingRevisions);
    }

    return revisions;
  }

  public List<CDORevision> getRevisionsByTime(Collection<CDOID> ids, int referenceChunk, long timeStamp,
      boolean loadMissingRevisions)
  {
    List<CDOID> missingIDs = loadMissingRevisions ? new ArrayList<CDOID>(0) : null;
    List<CDORevision> revisions = new ArrayList<CDORevision>(ids.size());
    for (CDOID id : ids)
    {
      InternalCDORevision revision = getRevisionByTime(id, referenceChunk, timeStamp, false);
      revisions.add(revision);
      if (revision == null && missingIDs != null)
      {
        missingIDs.add(id);
      }
    }

    if (missingIDs != null && !missingIDs.isEmpty())
    {
      List<InternalCDORevision> missingRevisions = loadRevisionsByTime(missingIDs, referenceChunk, timeStamp);
      handleMissingRevisions(revisions, missingRevisions);
    }

    return revisions;
  }

  public CDOID getResourceID(CDOID folderID, String name, long timeStamp)
  {
    return cache.getResourceID(folderID, name, timeStamp);
  }

  public List<CDORevision> getCachedRevisions()
  {
    return cache.getRevisions();
  }

  private void addCachedRevisionIfNotNull(InternalCDORevision revision)
  {
    if (revision != null)
    {
      cache.addRevision(revision);
    }
  }

  public boolean addCachedRevision(InternalCDORevision revision)
  {
    if (revision != null)
    {
      return cache.addRevision(revision);
    }

    throw new IllegalArgumentException("revision == null");
  }

  public void removeCachedRevision(CDOID id, int version)
  {
    cache.removeRevision(id, version);
  }

  public void clearCache()
  {
    cache.clear();
  }

  protected InternalCDORevision verifyRevision(InternalCDORevision revision, int referenceChunk)
  {
    return revision;
  }

  protected abstract InternalCDORevision loadRevision(CDOID id, int referenceChunk);

  protected abstract InternalCDORevision loadRevisionByTime(CDOID id, int referenceChunk, long timeStamp);

  protected abstract InternalCDORevision loadRevisionByVersion(CDOID id, int referenceChunk, int version);

  protected abstract List<InternalCDORevision> loadRevisions(Collection<CDOID> ids, int referenceChunk);

  protected abstract List<InternalCDORevision> loadRevisionsByTime(Collection<CDOID> ids, int referenceChunk,
      long timeStamp);

  protected abstract CDOPackageManager getPackageManager();

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (cache == null)
    {
      cache = CDORevisionCacheUtil.createDefaultCache();
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    cache.setPackageManager(getPackageManager());
    LifecycleUtil.activate(cache);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(cache);
    super.doDeactivate();
  }

  private void handleMissingRevisions(List<CDORevision> revisions, List<InternalCDORevision> missingRevisions)
  {
    Iterator<InternalCDORevision> it = missingRevisions.iterator();
    for (int i = 0; i < revisions.size(); i++)
    {
      CDORevision revision = revisions.get(i);
      if (revision == null)
      {
        InternalCDORevision missingRevision = it.next();
        revisions.set(i, missingRevision);
        addCachedRevisionIfNotNull(missingRevision);
      }
    }
  }
}
