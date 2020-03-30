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

import org.eclipse.gmf.runtime.notation.FillStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.datatype.GradientData;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Fill
 * Style</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.FillStyleImpl#getFillColor <em>Fill Color</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.FillStyleImpl#getTransparency <em>Transparency</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.FillStyleImpl#getGradient <em>Gradient</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy %partners
 */
public class FillStyleImpl extends CDOObjectImpl implements FillStyle
{
  /**
   * The default value of the '{@link #getFillColor() <em>Fill Color</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getFillColor()
   * @generated
   * @ordered
   */
  protected static final int FILL_COLOR_EDEFAULT = 16777215;

  /**
   * The default value of the '{@link #getTransparency() <em>Transparency</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getTransparency()
   * @generated
   * @ordered
   */
  protected static final int TRANSPARENCY_EDEFAULT = -1;

  /**
   * The default value of the '{@link #getGradient() <em>Gradient</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getGradient()
   * @generated
   * @ordered
   */
  protected static final GradientData GRADIENT_EDEFAULT = null;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected FillStyleImpl()
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
    return NotationPackage.Literals.FILL_STYLE;
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
  public int getFillColor()
  {
    return ((Integer)eDynamicGet(NotationPackage.FILL_STYLE__FILL_COLOR, NotationPackage.Literals.FILL_STYLE__FILL_COLOR, true, true)).intValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setFillColor(int newFillColor)
  {
    eDynamicSet(NotationPackage.FILL_STYLE__FILL_COLOR, NotationPackage.Literals.FILL_STYLE__FILL_COLOR, new Integer(newFillColor));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getTransparency()
  {
    return ((Integer)eDynamicGet(NotationPackage.FILL_STYLE__TRANSPARENCY, NotationPackage.Literals.FILL_STYLE__TRANSPARENCY, true, true)).intValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setTransparency(int newTransparency)
  {
    eDynamicSet(NotationPackage.FILL_STYLE__TRANSPARENCY, NotationPackage.Literals.FILL_STYLE__TRANSPARENCY, new Integer(newTransparency));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public GradientData getGradient()
  {
    return (GradientData)eDynamicGet(NotationPackage.FILL_STYLE__GRADIENT, NotationPackage.Literals.FILL_STYLE__GRADIENT, true, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setGradient(GradientData newGradient)
  {
    eDynamicSet(NotationPackage.FILL_STYLE__GRADIENT, NotationPackage.Literals.FILL_STYLE__GRADIENT, newGradient);
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
    case NotationPackage.FILL_STYLE__FILL_COLOR:
      return new Integer(getFillColor());
    case NotationPackage.FILL_STYLE__TRANSPARENCY:
      return new Integer(getTransparency());
    case NotationPackage.FILL_STYLE__GRADIENT:
      return getGradient();
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
    case NotationPackage.FILL_STYLE__FILL_COLOR:
      setFillColor(((Integer)newValue).intValue());
      return;
    case NotationPackage.FILL_STYLE__TRANSPARENCY:
      setTransparency(((Integer)newValue).intValue());
      return;
    case NotationPackage.FILL_STYLE__GRADIENT:
      setGradient((GradientData)newValue);
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
    case NotationPackage.FILL_STYLE__FILL_COLOR:
      setFillColor(FILL_COLOR_EDEFAULT);
      return;
    case NotationPackage.FILL_STYLE__TRANSPARENCY:
      setTransparency(TRANSPARENCY_EDEFAULT);
      return;
    case NotationPackage.FILL_STYLE__GRADIENT:
      setGradient(GRADIENT_EDEFAULT);
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
    case NotationPackage.FILL_STYLE__FILL_COLOR:
      return getFillColor() != FILL_COLOR_EDEFAULT;
    case NotationPackage.FILL_STYLE__TRANSPARENCY:
      return getTransparency() != TRANSPARENCY_EDEFAULT;
    case NotationPackage.FILL_STYLE__GRADIENT:
      return GRADIENT_EDEFAULT == null ? getGradient() != null : !GRADIENT_EDEFAULT.equals(getGradient());
    }
    return eDynamicIsSet(featureID);
  }

} // FillStyleImpl
