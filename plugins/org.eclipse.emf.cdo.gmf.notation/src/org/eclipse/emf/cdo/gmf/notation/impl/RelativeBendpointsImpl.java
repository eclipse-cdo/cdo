/******************************************************************************
 * Copyright (c) 2018-2020 IBM Corporation and others.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 ****************************************************************************/

package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.RelativeBendpoints;
import org.eclipse.gmf.runtime.notation.datatype.RelativeBendpoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Relative Bendpoints</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.RelativeBendpointsImpl#getPoints <em>Points</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy %partners
 */
public class RelativeBendpointsImpl extends CDOObjectImpl implements RelativeBendpoints
{
  /**
   * The default value of the '{@link #getPoints() <em>Points</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @see #getPoints()
   * @generated NOT
   * @ordered
   */
  protected static final List POINTS_EDEFAULT = Collections.EMPTY_LIST;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected RelativeBendpointsImpl()
  {
    super();
    setPointsGen(POINTS_EDEFAULT);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return NotationPackage.Literals.RELATIVE_BENDPOINTS;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public List getPoints()
  {
    return Collections.unmodifiableList(getPointsGen());
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public List getPointsGen()
  {
    return (List)eDynamicGet(NotationPackage.RELATIVE_BENDPOINTS__POINTS, NotationPackage.Literals.RELATIVE_BENDPOINTS__POINTS, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public void setPoints(List newPoints)
  {
    if (newPoints == null)
    {
      throw new NullPointerException("the 'newPoints' parameter is null"); //$NON-NLS-1$
    }

    if (newPoints.isEmpty())
    {
      setPointsGen(POINTS_EDEFAULT);
    }
    else
    {
      List tempList = new ArrayList(newPoints.size());
      for (Iterator i = newPoints.iterator(); i.hasNext();)
      {
        Object point = i.next();
        if (!(point instanceof RelativeBendpoint))
        {
          throw new IllegalArgumentException("One or more objects in the list is not of type org.eclipse.gmf.runtime.notation.datatype.RelativeBendpoint"); //$NON-NLS-1$
        }
        tempList.add(point);
      }
      setPointsGen(tempList);
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setPointsGen(List newPoints)
  {
    eDynamicSet(NotationPackage.RELATIVE_BENDPOINTS__POINTS, NotationPackage.Literals.RELATIVE_BENDPOINTS__POINTS, newPoints);
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
    case NotationPackage.RELATIVE_BENDPOINTS__POINTS:
      return getPoints();
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case NotationPackage.RELATIVE_BENDPOINTS__POINTS:
      setPoints((List)newValue);
      return;
    }
    eDynamicSet(featureID, newValue);
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
    case NotationPackage.RELATIVE_BENDPOINTS__POINTS:
      setPoints(POINTS_EDEFAULT);
      return;
    }
    eDynamicUnset(featureID);
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
    case NotationPackage.RELATIVE_BENDPOINTS__POINTS:
      return POINTS_EDEFAULT == null ? getPointsGen() != null : !POINTS_EDEFAULT.equals(getPointsGen());
    }
    return eDynamicIsSet(featureID);
  }

} // RelativeBendpointsImpl
