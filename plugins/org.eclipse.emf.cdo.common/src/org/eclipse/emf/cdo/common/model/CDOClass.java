/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.common.model;

/**
 * @author Eike Stepper
 */
public interface CDOClass extends CDOModelElement, Comparable<CDOClass>
{
  public int getClassifierID();

  public boolean isAbstract();

  public boolean isResource();

  public boolean isRoot();

  public int getSuperTypeCount();

  public CDOClass getSuperType(int index);

  public CDOClass[] getSuperTypes();

  /**
   * @return An array of all super types. The array is not a copy and must not be modified!
   */
  public CDOClass[] getAllSuperTypes();

  public int getFeatureCount();

  public CDOFeature lookupFeature(int featureID);

  public CDOFeature lookupFeature(String name);

  public CDOFeature[] getFeatures();

  public int getFeatureID(CDOFeature feature);

  /**
   * @return An array of all features. The array is not a copy and must not be modified!
   */
  public CDOFeature[] getAllFeatures();

  public CDOClassRef createClassRef();

  public CDOPackage getContainingPackage();
}
