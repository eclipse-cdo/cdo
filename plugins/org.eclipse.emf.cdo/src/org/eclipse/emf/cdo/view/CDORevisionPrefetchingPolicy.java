/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.session.CDORevisionManager;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Collection;
import java.util.Collections;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDORevisionPrefetchingPolicy
{
  public static final CDORevisionPrefetchingPolicy NO_PREFETCHING = new CDORevisionPrefetchingPolicy()
  {
    public Collection<CDOID> loadAhead(CDORevisionManager revisionManager, EObject targetObject,
        EStructuralFeature feature, CDOList list, int accessIndex, CDOID accessID)
    {
      return Collections.emptyList();
    }
  };

  /**
   * @param revisionManager
   *          Lookup availability of objects in the cache with {@link CDORevisionManager#containsRevision(CDOID)}.
   * @param targetObject
   *          Container of the list
   * @param feature
   * @param list
   * @param accessIndex
   * @param accessID
   * @return Should return a list of id's to be fetch.
   */
  public Collection<CDOID> loadAhead(CDORevisionManager revisionManager, EObject targetObject,
      EStructuralFeature feature, CDOList list, int accessIndex, CDOID accessID);
}
