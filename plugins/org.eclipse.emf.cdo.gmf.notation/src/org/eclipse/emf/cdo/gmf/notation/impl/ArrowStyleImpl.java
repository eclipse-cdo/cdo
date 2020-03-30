/******************************************************************************
 * Copyright (c) 2008, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.emf.cdo.gmf.notation.impl;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.gmf.runtime.notation.ArrowStyle;
import org.eclipse.gmf.runtime.notation.ArrowType;
import org.eclipse.gmf.runtime.notation.NotationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Arrow Style</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ArrowStyleImpl#getArrowSource <em>Arrow Source</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.ArrowStyleImpl#getArrowTarget <em>Arrow Target</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ArrowStyleImpl extends CDOObjectImpl implements ArrowStyle
{
  /**
   * The default value of the '{@link #getArrowSource() <em>Arrow Source</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getArrowSource()
   * @generated
   * @ordered
   */
  protected static final ArrowType ARROW_SOURCE_EDEFAULT = ArrowType.NONE_LITERAL;

  /**
   * The default value of the '{@link #getArrowTarget() <em>Arrow Target</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getArrowTarget()
   * @generated
   * @ordered
   */
  protected static final ArrowType ARROW_TARGET_EDEFAULT = ArrowType.NONE_LITERAL;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ArrowStyleImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return NotationPackage.Literals.ARROW_STYLE;
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
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ArrowType getArrowSource()
  {
    return (ArrowType)eDynamicGet(NotationPackage.ARROW_STYLE__ARROW_SOURCE, NotationPackage.Literals.ARROW_STYLE__ARROW_SOURCE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setArrowSource(ArrowType newArrowSource)
  {
    eDynamicSet(NotationPackage.ARROW_STYLE__ARROW_SOURCE, NotationPackage.Literals.ARROW_STYLE__ARROW_SOURCE, newArrowSource);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ArrowType getArrowTarget()
  {
    return (ArrowType)eDynamicGet(NotationPackage.ARROW_STYLE__ARROW_TARGET, NotationPackage.Literals.ARROW_STYLE__ARROW_TARGET, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setArrowTarget(ArrowType newArrowTarget)
  {
    eDynamicSet(NotationPackage.ARROW_STYLE__ARROW_TARGET, NotationPackage.Literals.ARROW_STYLE__ARROW_TARGET, newArrowTarget);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case NotationPackage.ARROW_STYLE__ARROW_SOURCE:
      return getArrowSource();
    case NotationPackage.ARROW_STYLE__ARROW_TARGET:
      return getArrowTarget();
    }
    return eDynamicGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case NotationPackage.ARROW_STYLE__ARROW_SOURCE:
      setArrowSource((ArrowType)newValue);
      return;
    case NotationPackage.ARROW_STYLE__ARROW_TARGET:
      setArrowTarget((ArrowType)newValue);
      return;
    }
    eDynamicSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.ARROW_STYLE__ARROW_SOURCE:
      setArrowSource(ARROW_SOURCE_EDEFAULT);
      return;
    case NotationPackage.ARROW_STYLE__ARROW_TARGET:
      setArrowTarget(ARROW_TARGET_EDEFAULT);
      return;
    }
    eDynamicUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case NotationPackage.ARROW_STYLE__ARROW_SOURCE:
      return getArrowSource() != ARROW_SOURCE_EDEFAULT;
    case NotationPackage.ARROW_STYLE__ARROW_TARGET:
      return getArrowTarget() != ARROW_TARGET_EDEFAULT;
    }
    return eDynamicIsSet(featureID);
  }

} // ArrowStyleImpl
