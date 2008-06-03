/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo.analyzer;

import org.eclipse.emf.cdo.analyzer.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.common.model.CDOFeature;

import org.eclipse.emf.internal.cdo.InternalCDOObject;

/**
 * @author Simon McDuff
 */
public class NOOPFeatureAnalyzer implements CDOFeatureAnalyzer
{
  public NOOPFeatureAnalyzer()
  {
  }

  public void preTraverseFeature(InternalCDOObject cdoClass, CDOFeature feature, int index)
  {
  }

  public void postTraverseFeature(InternalCDOObject cdoClass, CDOFeature feature, int index, Object value)
  {
  }
}
