/*
 * Copyright (c) 2013, 2015, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.legacy.impl;

import org.eclipse.emf.cdo.tests.model3.Point;
import org.eclipse.emf.cdo.tests.model3.PolygonWithDuplicates;
import org.eclipse.emf.cdo.tests.model3.legacy.Model3Package;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EDataTypeEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Polygon With Duplicates</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.PolygonWithDuplicatesImpl#getPoints <em>Points</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PolygonWithDuplicatesImpl extends EObjectImpl implements PolygonWithDuplicates
{
  /**
   * The cached value of the '{@link #getPoints() <em>Points</em>}' attribute list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getPoints()
   * @generated
   * @ordered
   */
  protected EList<Point> points;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected PolygonWithDuplicatesImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Model3Package.eINSTANCE.getPolygonWithDuplicates();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<Point> getPoints()
  {
    if (points == null)
    {
      points = new EDataTypeEList<Point>(Point.class, this, Model3Package.POLYGON_WITH_DUPLICATES__POINTS);
    }
    return points;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case Model3Package.POLYGON_WITH_DUPLICATES__POINTS:
      return getPoints();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Model3Package.POLYGON_WITH_DUPLICATES__POINTS:
      getPoints().clear();
      getPoints().addAll((Collection<? extends Point>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case Model3Package.POLYGON_WITH_DUPLICATES__POINTS:
      getPoints().clear();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case Model3Package.POLYGON_WITH_DUPLICATES__POINTS:
      return points != null && !points.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (points: ");
    result.append(points);
    result.append(')');
    return result.toString();
  }

} // PolygonWithDuplicatesImpl
