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
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.transaction.CDOSavepoint;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface InternalCDOSavepoint extends CDOSavepoint, InternalCDOUserSavepoint
{
  public InternalCDOTransaction getTransaction();

  public InternalCDOSavepoint getFirstSavePoint();

  public InternalCDOSavepoint getPreviousSavepoint();

  public InternalCDOSavepoint getNextSavepoint();

  public void clear();

  public Map<CDOID, CDOResource> getNewResources();

  public Map<CDOID, CDOObject> getNewObjects();

  public Map<CDOID, CDOObject> getDetachedObjects();

  /**
   * Bug 283985 (Re-attachment)
   */
  public Map<CDOID, CDOObject> getReattachedObjects();

  public Map<CDOID, CDOObject> getDirtyObjects();

  public Map<CDOID, CDORevision> getBaseNewObjects();

  public Set<CDOID> getSharedDetachedObjects();

  public ConcurrentMap<CDOID, CDORevisionDelta> getRevisionDeltas();

  public void recalculateSharedDetachedObjects();
}
