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
package org.eclipse.emf.cdo.internal.server.embedded;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;

import org.eclipse.net4j.util.lifecycle.Lifecycle;

import org.eclipse.emf.ecore.EClass;

import java.util.Collection;
import java.util.List;

/**
 * @author Eike Stepper
 */
public abstract class DelegatingCDORevisionManager extends Lifecycle implements InternalCDORevisionManager
{
  public DelegatingCDORevisionManager()
  {
  }

  public CDORevisionCache getCache()
  {
    return getDelegate().getCache();
  }

  public void setCache(CDORevisionCache cache)
  {
    getDelegate().setCache(cache);
  }

  public void setFactory(CDORevisionFactory factory)
  {
    getDelegate().setFactory(factory);
  }

  public CDORevisionFactory getFactory()
  {
    return getDelegate().getFactory();
  }

  public RevisionLoader getRevisionLoader()
  {
    return getDelegate().getRevisionLoader();
  }

  public void setRevisionLoader(RevisionLoader revisionLoader)
  {
    getDelegate().setRevisionLoader(revisionLoader);
  }

  public RevisionLocker getRevisionLocker()
  {
    return getDelegate().getRevisionLocker();
  }

  public void setRevisionLocker(RevisionLocker revisionLocker)
  {
    getDelegate().setRevisionLocker(revisionLocker);
  }

  public boolean containsRevision(CDOID id)
  {
    return getDelegate().containsRevision(id);
  }

  public boolean containsRevisionByTime(CDOID id, long timeStamp)
  {
    return getDelegate().containsRevisionByTime(id, timeStamp);
  }

  public boolean containsRevisionByVersion(CDOID id, int version)
  {
    return getDelegate().containsRevisionByVersion(id, version);
  }

  public EClass getObjectType(CDOID id)
  {
    return getDelegate().getObjectType(id);
  }

  public CDOID getResourceID(CDOID folderID, String name, long timeStamp)
  {
    return getDelegate().getResourceID(folderID, name, timeStamp);
  }

  public CDORevision getRevision(CDOID id, int referenceChunk, boolean loadOnDemand)
  {
    return getDelegate().getRevision(id, referenceChunk, loadOnDemand);
  }

  public CDORevision getRevision(CDOID id, int referenceChunk)
  {
    return getDelegate().getRevision(id, referenceChunk);
  }

  public CDORevision getRevisionByTime(CDOID id, int referenceChunk, long timeStamp, boolean loadOnDemand)
  {
    return getDelegate().getRevisionByTime(id, referenceChunk, timeStamp, loadOnDemand);
  }

  public CDORevision getRevisionByTime(CDOID id, int referenceChunk, long timeStamp)
  {
    return getDelegate().getRevisionByTime(id, referenceChunk, timeStamp);
  }

  public CDORevision getRevisionByVersion(CDOID id, int referenceChunk, int version, boolean loadOnDemand)
  {
    return getDelegate().getRevisionByVersion(id, referenceChunk, version, loadOnDemand);
  }

  public CDORevision getRevisionByVersion(CDOID id, int referenceChunk, int version)
  {
    return getDelegate().getRevisionByVersion(id, referenceChunk, version);
  }

  public List<CDORevision> getRevisions(Collection<CDOID> ids, int referenceChunk)
  {
    return getDelegate().getRevisions(ids, referenceChunk);
  }

  public List<CDORevision> getRevisionsByTime(Collection<CDOID> ids, int referenceChunk, long timeStamp,
      boolean loadMissingRevisions)
  {
    return getDelegate().getRevisionsByTime(ids, referenceChunk, timeStamp, loadMissingRevisions);
  }

  public void revisedRevision(CDOID id, long timeStamp)
  {
    getDelegate().revisedRevision(id, timeStamp);
  }

  public void revisedRevisionByVersion(CDOID id, int version, long timeStamp)
  {
    getDelegate().revisedRevisionByVersion(id, version, timeStamp);
  }

  protected abstract InternalCDORevisionManager getDelegate();
}
