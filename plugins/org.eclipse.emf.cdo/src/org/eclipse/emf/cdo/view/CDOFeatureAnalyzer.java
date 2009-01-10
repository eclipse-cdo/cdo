/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.model.CDOFeature;

import org.eclipse.emf.internal.cdo.analyzer.NOOPFeatureAnalyzer;

/**
 * TODO Simon: JavaDoc
 * 
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOFeatureAnalyzer
{
  /**
   * TODO Simon: JavaDoc
   */
  public static final CDOFeatureAnalyzer NOOP = new NOOPFeatureAnalyzer();

  /**
   * TODO Simon: JavaDoc
   * 
   * @since 2.0
   */
  public void preTraverseFeature(CDOObject revision, CDOFeature feature, int index);

  /**
   * TODO Simon: JavaDoc
   * 
   * @since 2.0
   */
  public void postTraverseFeature(CDOObject revision, CDOFeature feature, int index, Object value);
}
