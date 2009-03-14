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
package org.eclipse.emf.cdo.session;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * A strategy that specifies which list elememts must be present (loaded) in a {@link CDOID} list of a
 * {@link CDORevision revision} when a certain list index is accessed. Implementations of this interface can control the
 * exact characteristics of a certain <em>partial collection loading</em> strategy.
 * 
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOCollectionLoadingPolicy
{
  /**
   * A default collection loading strategy that leads to complete loading of {@link CDOID} lists <b>before</b> any of
   * their elements is accessed.
   */
  public static final CDOCollectionLoadingPolicy DEFAULT = new CDOCollectionLoadingPolicy()
  {
    /**
     * Returns {@link CDORevision#UNCHUNKED}.
     */
    public int getInitialChunkSize()
    {
      return CDORevision.UNCHUNKED;
    }

    public Object resolveProxy(CDORevisionManager revisionManager, CDORevision revision, EStructuralFeature feature,
        int accessIndex, int serverIndex)
    {
      return revisionManager.loadChunkByRange(revision, feature, accessIndex, serverIndex, accessIndex, accessIndex);
    }
  };

  /**
   * Returns the maximum number of CDOIDs to be loaded for collections when an object is loaded, i.e. <b>before</b> any
   * of their elements is accessed.
   */
  public int getInitialChunkSize();

  /**
   * Defines a strategy to be used when the collection needs to resolve one element.
   * {@link CDORevisionManager#loadChunkByRange(CDORevision, EStructuralFeature, int, int, int, int)} should be used to
   * resolve them.
   */
  public Object resolveProxy(CDORevisionManager revisionManager, CDORevision revision, EStructuralFeature feature,
      int accessIndex, int serverIndex);
}
