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
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;

import java.util.Collection;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface InternalCDORevisionManager extends CDORevisionManager
{
  public RevisionLoader getRevisionLoader();

  public void setRevisionLoader(RevisionLoader revisionLoader);

  public RevisionLocker getRevisionLocker();

  public void setRevisionLocker(RevisionLocker revisionLocker);

  public void setCache(CDORevisionCache cache);

  public List<CDORevision> getCachedRevisions();

  public boolean addCachedRevision(CDORevision revision);

  public void removeCachedRevision(CDORevision revision);

  public void removeCachedRevision(CDOID id, int version);

  public void clearCache();

  public void revisedRevision(CDOID id, long timeStamp);

  public void revisedRevisionByVersion(CDOID id, int version, long timeStamp);

  /**
   * @author Eike Stepper
   */
  public interface RevisionLoader
  {
    public InternalCDORevision verifyRevision(InternalCDORevision revision, int referenceChunk);

    public InternalCDORevision loadRevision(CDOID id, int referenceChunk);

    public InternalCDORevision loadRevisionByTime(CDOID id, int referenceChunk, long timeStamp);

    public InternalCDORevision loadRevisionByVersion(CDOID id, int referenceChunk, int version);

    public List<InternalCDORevision> loadRevisions(Collection<CDOID> ids, int referenceChunk);

    public List<InternalCDORevision> loadRevisionsByTime(Collection<CDOID> ids, int referenceChunk, long timeStamp);

    // public List<InternalCDORevision> verifyRevisions(List<InternalCDORevision> revisions);
  }

  /**
   * @author Eike Stepper
   */
  public interface RevisionLocker
  {
    public void acquireAtomicRequestLock(Object key);

    public void releaseAtomicRequestLock(Object key);
  }
}
