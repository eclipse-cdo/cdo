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
package org.eclipse.emf.cdo;

import org.eclipse.emf.cdo.common.model.CDOFeature;

import org.eclipse.emf.internal.cdo.analyzer.NOOPFeatureAnalyzer;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOFeatureAnalyzer
{
  public static final CDOFeatureAnalyzer NOOP = new NOOPFeatureAnalyzer();

  /**
   * @since 2.0
   */
  public void preTraverseFeature(CDOObject revision, CDOFeature feature, int index);

  /**
   * @since 2.0
   */
  public void postTraverseFeature(CDOObject revision, CDOFeature feature, int index, Object value);
}
