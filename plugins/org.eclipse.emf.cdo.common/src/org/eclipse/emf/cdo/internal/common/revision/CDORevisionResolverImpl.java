/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/201266
 *    Simon McDuff - http://bugs.eclipse.org/230832
 */
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionResolver;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCacheUtil;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionResolver;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.concurrent.IRWLockManager;
import org.eclipse.net4j.util.concurrent.RWLockManager;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class CDORevisionResolverImpl extends Lifecycle implements InternalCDORevisionResolver
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, CDORevisionResolverImpl.class);

  private CDORevisionCache cache;

  private IRWLockManager<CDORevisionResolver, Object> lockmanager;

  @ExcludeFromDump
  private Set<CDORevisionResolverImpl> singletonCollection = Collections.singleton(this);

  @ExcludeFromDump
  private Object loadAndAddLock = new Object();

  @ExcludeFromDump
  private Object revisedLock = new Object();

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

  public IRWLockManager<CDORevisionResolver, Object> getLockmanager()
  {
    return lockmanager;
  }

  public void setLockmanager(IRWLockManager<CDORevisionResolver, Object> lockmanager)
  {
    this.lockmanager = lockmanager;
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

  public EClass getObjectType(CDOID id)
  {
    return cache.getObjectType(id);
  }

  public void revisedRevision(CDOID id, long timeStamp)
  {
    acquireAtomicRequestLock(revisedLock);

    try
    {
      InternalCDORevision revision = cache.getRevision(id);
      if (revision != null)
      {
        if (timeStamp == CDORevision.UNSPECIFIED_DATE)
        {
          removeCachedRevision(revision.getID(), revision.getVersion());
        }
        else
        {
          revision.setRevised(timeStamp - 1);
        }
      }
    }
    finally
    {
      releaseAtomicRequestLock(revisedLock);
    }
  }

  public void revisedRevisionByVersion(CDOID id, int version, long timeStamp)
  {
    acquireAtomicRequestLock(revisedLock);

    try
    {
      InternalCDORevision revision = cache.getRevisionByVersion(id, version);
      if (revision != null)
      {
        if (timeStamp == CDORevision.UNSPECIFIED_DATE)
        {
          removeCachedRevision(revision.getID(), revision.getVersion());
        }
        else
        {
          revision.setRevised(timeStamp - 1);
        }
      }
    }
    finally
    {
      releaseAtomicRequestLock(revisedLock);
    }
  }

  public InternalCDORevision getRevision(CDOID id, int referenceChunk)
  {
    return getRevision(id, referenceChunk, true);
  }

  public InternalCDORevision getRevision(CDOID id, int referenceChunk, boolean loadOnDemand)
  {
    acquireAtomicRequestLock(loadAndAddLock);

    try
    {
      InternalCDORevision revision = cache.getRevision(id);
      if (revision == null)
      {
        if (loadOnDemand)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Loading revision {0}", id); //$NON-NLS-1$
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
    finally
    {
      releaseAtomicRequestLock(loadAndAddLock);
    }
  }

  public InternalCDORevision getRevisionByTime(CDOID id, int referenceChunk, long timeStamp)
  {
    return getRevisionByTime(id, referenceChunk, timeStamp, true);
  }

  public InternalCDORevision getRevisionByTime(CDOID id, int referenceChunk, long timeStamp, boolean loadOnDemand)
  {
    acquireAtomicRequestLock(loadAndAddLock);

    try
    {
      InternalCDORevision revision = cache.getRevisionByTime(id, timeStamp);
      if (revision == null)
      {
        if (loadOnDemand)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Loading revision {0} by time {1,date} {1,time}", id, timeStamp); //$NON-NLS-1$
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
    finally
    {
      releaseAtomicRequestLock(loadAndAddLock);
    }
  }

  public synchronized InternalCDORevision getRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    return getRevisionByVersion(id, referenceChunk, version, true);
  }

  public InternalCDORevision getRevisionByVersion(CDOID id, int referenceChunk, int version, boolean loadOnDemand)
  {
    acquireAtomicRequestLock(loadAndAddLock);

    try
    {
      InternalCDORevision revision = cache.getRevisionByVersion(id, version);
      if (revision == null)
      {
        if (loadOnDemand)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Loading revision {0} by version {1}", id, version); //$NON-NLS-1$
          }

          revision = loadRevisionByVersion(id, referenceChunk, version);
          addCachedRevisionIfNotNull(revision);
        }
      }

      return revision;
    }
    finally
    {
      releaseAtomicRequestLock(loadAndAddLock);
    }
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
      acquireAtomicRequestLock(loadAndAddLock);

      try
      {
        List<InternalCDORevision> missingRevisions = loadRevisions(missingIDs, referenceChunk);
        handleMissingRevisions(revisions, missingRevisions);
      }
      finally
      {
        releaseAtomicRequestLock(loadAndAddLock);
      }
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
      acquireAtomicRequestLock(loadAndAddLock);

      try
      {
        List<InternalCDORevision> missingRevisions = loadRevisionsByTime(missingIDs, referenceChunk, timeStamp);
        handleMissingRevisions(revisions, missingRevisions);
      }
      finally
      {
        releaseAtomicRequestLock(loadAndAddLock);
      }
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

    throw new IllegalArgumentException("revision == null"); //$NON-NLS-1$
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
    LifecycleUtil.activate(cache);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(cache);
    super.doDeactivate();
  }

  protected void acquireAtomicRequestLock(Object key)
  {
    if (lockmanager != null)
    {
      try
      {
        lockmanager.lock(LockType.WRITE, key, this, RWLockManager.WAIT);
      }
      catch (InterruptedException ex)
      {
        throw WrappedException.wrap(ex);
      }
    }
  }

  protected void releaseAtomicRequestLock(Object key)
  {
    if (lockmanager != null)
    {
      lockmanager.unlock(LockType.WRITE, key, singletonCollection);
    }
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
