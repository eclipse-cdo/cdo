/*
 * Copyright (c) 2009, 2011, 2012, 2014, 2019 Eike Stepper (Loehne, Germany) and others.
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
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.internal.cdo.analyzer.NOOPFeatureAnalyzer;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * A call-back interface that is called by a {@link CDOView view} on each model read access.
 *
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOFeatureAnalyzer
{
  public static final CDOFeatureAnalyzer NOOP = new NOOPFeatureAnalyzer();

  public void preTraverseFeature(CDOObject revision, EStructuralFeature feature, int index);

  public void postTraverseFeature(CDOObject revision, EStructuralFeature feature, int index, Object value);
}
