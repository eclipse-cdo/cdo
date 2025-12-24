/*
 * Copyright (c) 2007-2009, 2011, 2012, 2014, 2019 Eike Stepper (Loehne, Germany) and others.
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
package org.eclipse.emf.internal.cdo.analyzer;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Simon McDuff
 */
public class NOOPFeatureAnalyzer implements CDOFeatureAnalyzer
{
  public NOOPFeatureAnalyzer()
  {
  }

  @Override
  public void preTraverseFeature(CDOObject eClass, EStructuralFeature feature, int index)
  {
  }

  @Override
  public void postTraverseFeature(CDOObject eClass, EStructuralFeature feature, int index, Object value)
  {
  }
}
