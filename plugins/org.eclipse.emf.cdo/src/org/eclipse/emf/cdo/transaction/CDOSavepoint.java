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
  public boolean isDirty();

  /**
   * Return the list of new objects from this point.
   * 
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getAllDirtyObjects();

  /**
   * Return the list of new objects from this point without objects that are removed.
   * 
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getAllNewObjects();

  /**
   * Return the list of new resources from this point without objects that are removed.
   * 
   * @since 3.0
   */
  public Map<CDOID, CDOResource> getAllNewResources();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDOObject> getAllDetachedObjects();

  /**
   * @since 3.0
   */
  public Map<CDOID, CDORevision> getAllBaseNewObjects();

  /**
   * Return the list of all deltas without objects that are removed.
   * 
   * @since 3.0
   */
  public Map<CDOID, CDORevisionDelta> getAllRevisionDeltas();
}
