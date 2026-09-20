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

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 */
final class LegacyCollectionLoadingStrategies
{
  static final LegacyCollectionLoadingStrategy FULLY_UNCHUNKED = new LegacyCollectionLoadingStrategy()
  {
    @Override
    public int getInitialChunkSize(CDORevision revision, EStructuralFeature feature)
    {
      return CDORevision.UNCHUNKED;
    }

    @Override
    public int getResolveChunkSize(CDORevision revision, EStructuralFeature feature)
    {
      return CDORevision.UNCHUNKED;
    }
  };

  private LegacyCollectionLoadingStrategies()
  {
  }
}
