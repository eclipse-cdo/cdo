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

import org.eclipse.gmf.runtime.notation.LineStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Line
 * Style</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.LineStyleImpl#getLineColor <em>Line Color</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.LineStyleImpl#getLineWidth <em>Line Width</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy %partners
 */
public class LineStyleImpl extends CDOObjectImpl implements LineStyle
{
  /**
   * The default value of the '{@link #getLineColor() <em>Line Color</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getLineColor()
   * @generated
   * @ordered
   */
  protected static final int LINE_COLOR_EDEFAULT = 11579568;

  /**
   * The default value of the '{@link #getLineWidth() <em>Line Width</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getLineWidth()
   * @generated
   * @ordered
   */
  protected static final int LINE_WIDTH_EDEFAULT = -1;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected LineStyleImpl()
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
    return NotationPackage.Literals.LINE_STYLE;
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
   * @generated
   */
  @Override
  public int getLineColor()
  {
    return ((Integer)eDynamicGet(NotationPackage.LINE_STYLE__LINE_COLOR, NotationPackage.Literals.LINE_STYLE__LINE_COLOR, true, true)).intValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setLineColor(int newLineColor)
  {
    eDynamicSet(NotationPackage.LINE_STYLE__LINE_COLOR, NotationPackage.Literals.LINE_STYLE__LINE_COLOR, new Integer(newLineColor));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getLineWidth()
  {
    return ((Integer)eDynamicGet(NotationPackage.LINE_STYLE__LINE_WIDTH, NotationPackage.Literals.LINE_STYLE__LINE_WIDTH, true, true)).intValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setLineWidth(int newLineWidth)
  {
    eDynamicSet(NotationPackage.LINE_STYLE__LINE_WIDTH, NotationPackage.Literals.LINE_STYLE__LINE_WIDTH, new Integer(newLineWidth));
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
    case NotationPackage.LINE_STYLE__LINE_COLOR:
      return new Integer(getLineColor());
    case NotationPackage.LINE_STYLE__LINE_WIDTH:
      return new Integer(getLineWidth());
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
    case NotationPackage.LINE_STYLE__LINE_COLOR:
      setLineColor(((Integer)newValue).intValue());
      return;
    case NotationPackage.LINE_STYLE__LINE_WIDTH:
      setLineWidth(((Integer)newValue).intValue());
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
    case NotationPackage.LINE_STYLE__LINE_COLOR:
      setLineColor(LINE_COLOR_EDEFAULT);
      return;
    case NotationPackage.LINE_STYLE__LINE_WIDTH:
      setLineWidth(LINE_WIDTH_EDEFAULT);
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
    case NotationPackage.LINE_STYLE__LINE_COLOR:
      return getLineColor() != LINE_COLOR_EDEFAULT;
    case NotationPackage.LINE_STYLE__LINE_WIDTH:
      return getLineWidth() != LINE_WIDTH_EDEFAULT;
    }
    return eDynamicIsSet(featureID);
  }

} // LineStyleImpl
