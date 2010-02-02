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
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public interface CDOSavepoint extends CDOUserSavepoint
{
  /**
   * @since 3.0
   */
  public CDOTransaction getTransaction();

  public CDOSavepoint getNextSavepoint();

  public CDOSavepoint getPreviousSavepoint();

  /**
   * @since 3.0
   */
  public boolean wasDirty();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDORevision> getBaseNewObjects();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDOResource> getNewResources();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getNewObjects();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getDetachedObjects();

  /**
   * Bug 283985 (Re-attachment)
   * 
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getReattachedObjects();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getDirtyObjects();

  /**
   * @since 3.0
   */
  public ConcurrentMap<CDOID, CDORevisionDelta> getRevisionDeltas();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDORevision> getAllBaseNewObjects();

  /**
   * Return the list of new resources from this point without objects that are removed.
   * 
   * @since 3.0
   */
  public Map<CDOID, CDOResource> getAllNewResources();

  /**
   * Return the list of new objects from this point without objects that are removed.
   * 
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getAllNewObjects();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getAllDetachedObjects();

  /**
   * Return the list of new objects from this point.
   * 
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getAllDirtyObjects();

  /**
   * Return the list of all deltas without objects that are removed.
   * 
   * @since 3.0
   */
  public Map<CDOID, CDORevisionDelta> getAllRevisionDeltas();
}
