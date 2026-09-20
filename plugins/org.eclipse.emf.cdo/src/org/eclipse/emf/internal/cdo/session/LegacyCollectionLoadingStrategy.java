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
 * Internal client-side sizing decisions for legacy collection loading.
 *
 * @author Eike Stepper
 */
interface LegacyCollectionLoadingStrategy
{
  public int getInitialChunkSize(CDORevision revision, EStructuralFeature feature);

  public int getResolveChunkSize(CDORevision revision, EStructuralFeature feature);
}
