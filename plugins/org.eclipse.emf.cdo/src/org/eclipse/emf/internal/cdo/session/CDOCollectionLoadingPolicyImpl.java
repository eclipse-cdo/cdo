/*
 * Copyright (c) 2009, 2011, 2012, 2015, 2016, 2018, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Simon McDuff
 * @since 2.0
 * @deprecated As of 4.31 use the modern collection loading configuration.
 */
@Deprecated
public class CDOCollectionLoadingPolicyImpl implements CDOCollectionLoadingPolicy
{
  private CDOSession session;

  private int initialChunkSize;

  private int resolveChunkSize;

  @Deprecated
  public CDOCollectionLoadingPolicyImpl(int initialChunkSize, int resolveChunkSize)
  {
    this.resolveChunkSize = resolveChunkSize <= 0 ? CDORevision.UNCHUNKED : resolveChunkSize;
    this.initialChunkSize = initialChunkSize < 0 ? this.resolveChunkSize : initialChunkSize;
  }

  @Deprecated
  @Override
  public CDOSession getSession()
  {
    return session;
  }

  @Deprecated
  @Override
  public void setSession(CDOSession session)
  {
    this.session = session;
  }

  @Deprecated
  @Override
  public int getInitialChunkSize()
  {
    return initialChunkSize;
  }

  @Deprecated
  @Override
  public int getInitialChunkSize(CDORevision revision, EStructuralFeature feature)
  {
    return getInitialChunkSize();
  }

  @Deprecated
  @Override
  public int getResolveChunkSize()
  {
    return resolveChunkSize;
  }

  @Deprecated
  @Override
  public int getResolveChunkSize(CDORevision revision, EStructuralFeature feature)
  {
    return getResolveChunkSize();
  }
}
