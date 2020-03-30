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

import org.eclipse.gmf.runtime.notation.LineType;
import org.eclipse.gmf.runtime.notation.LineTypeStyle;
import org.eclipse.gmf.runtime.notation.NotationPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Line Type Style</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.LineTypeStyleImpl#getLineType <em>Line Type</em>}</li>
 * </ul>
 *
 * @generated
 */
public class LineTypeStyleImpl extends CDOObjectImpl implements LineTypeStyle
{
  /**
   * The default value of the '{@link #getLineType() <em>Line Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLineType()
   * @generated
   * @ordered
   */
  protected static final LineType LINE_TYPE_EDEFAULT = LineType.SOLID_LITERAL;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected LineTypeStyleImpl()
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
    return NotationPackage.Literals.LINE_TYPE_STYLE;
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
  public LineType getLineType()
  {
    return (LineType)eDynamicGet(NotationPackage.LINE_TYPE_STYLE__LINE_TYPE, NotationPackage.Literals.LINE_TYPE_STYLE__LINE_TYPE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setLineType(LineType newLineType)
  {
    eDynamicSet(NotationPackage.LINE_TYPE_STYLE__LINE_TYPE, NotationPackage.Literals.LINE_TYPE_STYLE__LINE_TYPE, newLineType);
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
    case NotationPackage.LINE_TYPE_STYLE__LINE_TYPE:
      return getLineType();
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
    case NotationPackage.LINE_TYPE_STYLE__LINE_TYPE:
      setLineType((LineType)newValue);
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
    case NotationPackage.LINE_TYPE_STYLE__LINE_TYPE:
      setLineType(LINE_TYPE_EDEFAULT);
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
    case NotationPackage.LINE_TYPE_STYLE__LINE_TYPE:
      return getLineType() != LINE_TYPE_EDEFAULT;
    }
    return eDynamicIsSet(featureID);
  }

} // LineTypeStyleImpl
