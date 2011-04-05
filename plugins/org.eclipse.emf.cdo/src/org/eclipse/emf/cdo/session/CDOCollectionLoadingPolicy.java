/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.revision.CDOElementProxy;
import org.eclipse.emf.cdo.common.revision.CDORevision;

import org.eclipse.emf.internal.cdo.session.CDOCollectionLoadingPolicyImpl;

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
  public static final CDOCollectionLoadingPolicy DEFAULT = new CDOCollectionLoadingPolicyImpl(CDORevision.UNCHUNKED,
      CDORevision.UNCHUNKED);

  /**
   * Returns the maximum number of CDOIDs to be loaded for collections when the owning object is loaded initially, i.e.
   * <b>before</b> any of the collection elements is actually accessed. The remaining elements will be initialized as
   * {@link CDOElementProxy proxys}.
   */
  public int getInitialChunkSize();

  /**
   * Returns the maximum number of CDOIDs to be loaded for collections when the owning object is already loaded but the
   * actually accessed element is still a {@link CDOElementProxy proxy}.
   * 
   * @since 4.0
   */
  public int getResolveChunkSize();

  /**
   * Defines a strategy to be used when the collection needs to resolve one element.
   * 
   * @since 3.0
   */
  public Object resolveProxy(CDOSession session, CDORevision revision, EStructuralFeature feature, int accessIndex,
      int serverIndex);

  /**
   * @since 4.0
   */
  public void resolveAllProxies(CDOSession session, CDORevision revision, EStructuralFeature feature);
}
