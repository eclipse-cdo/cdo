/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Adapts a released legacy policy to the internal legacy sizing strategy.
 *
 * @author Eike Stepper
 * @since 4.30
 */
@SuppressWarnings("deprecation")
final class CDOLegacyCollectionLoadingPolicyAdapter implements LegacyCollectionLoadingStrategy
{
  private final CDOCollectionLoadingPolicy delegate;

  CDOLegacyCollectionLoadingPolicyAdapter(CDOCollectionLoadingPolicy delegate)
  {
    this.delegate = delegate;
  }

  @Override
  public int getInitialChunkSize(CDORevision revision, EStructuralFeature feature)
  {
    return delegate.getInitialChunkSize(revision, feature);
  }

  @Override
  public int getResolveChunkSize(CDORevision revision, EStructuralFeature feature)
  {
    return delegate.getResolveChunkSize(revision, feature);
  }
}
