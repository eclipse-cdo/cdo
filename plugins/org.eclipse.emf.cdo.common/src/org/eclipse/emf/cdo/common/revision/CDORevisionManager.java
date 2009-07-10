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
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;

import org.eclipse.emf.ecore.EClass;

import java.util.Collection;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface CDORevisionManager
{
  /**
   * @return The type of an object if a revision for that object is in the revision cache, <code>null</code> otherwise.
   */
  public EClass getObjectType(CDOID id);

  public boolean containsRevision(CDOID id);

  public boolean containsRevisionByTime(CDOID id, long timeStamp);

  public boolean containsRevisionByVersion(CDOID id, int version);

  public CDORevision getRevision(CDOID id, int referenceChunk);

  public CDORevision getRevision(CDOID id, int referenceChunk, boolean loadOnDemand);

  public CDORevision getRevisionByTime(CDOID id, int referenceChunk, long timeStamp);

  public CDORevision getRevisionByTime(CDOID id, int referenceChunk, long timeStamp, boolean loadOnDemand);

  public CDORevision getRevisionByVersion(CDOID id, int referenceChunk, int version);

  public CDORevision getRevisionByVersion(CDOID id, int referenceChunk, int version, boolean loadOnDemand);

  public List<CDORevision> getRevisions(Collection<CDOID> ids, int referenceChunk);

  public List<CDORevision> getRevisionsByTime(Collection<CDOID> ids, int referenceChunk, long timeStamp,
      boolean loadMissingRevisions);

  public CDOID getResourceID(CDOID folderID, String name, long timeStamp);

  public CDORevisionCache getCache();
}
