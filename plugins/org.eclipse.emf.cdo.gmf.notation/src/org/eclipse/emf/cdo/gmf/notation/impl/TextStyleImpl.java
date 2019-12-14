/******************************************************************************
 * Copyright (c) 2018, 2019 IBM Corporation and others.
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

import org.eclipse.gmf.runtime.notation.NotationPackage;
import org.eclipse.gmf.runtime.notation.TextAlignment;
import org.eclipse.gmf.runtime.notation.TextStyle;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Text Style</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.TextStyleImpl#getTextAlignment <em>Text Alignment</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TextStyleImpl extends CDOObjectImpl implements TextStyle
{
  /**
   * The default value of the '{@link #getTextAlignment() <em>Text Alignment</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTextAlignment()
   * @generated
   * @ordered
   */
  protected static final TextAlignment TEXT_ALIGNMENT_EDEFAULT = TextAlignment.LEFT_LITERAL;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected TextStyleImpl()
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
    return NotationPackage.Literals.TEXT_STYLE;
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
  public TextAlignment getTextAlignment()
  {
    return (TextAlignment)eDynamicGet(NotationPackage.TEXT_STYLE__TEXT_ALIGNMENT, NotationPackage.Literals.TEXT_STYLE__TEXT_ALIGNMENT, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setTextAlignment(TextAlignment newTextAlignment)
  {
    eDynamicSet(NotationPackage.TEXT_STYLE__TEXT_ALIGNMENT, NotationPackage.Literals.TEXT_STYLE__TEXT_ALIGNMENT, newTextAlignment);
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
    case NotationPackage.TEXT_STYLE__TEXT_ALIGNMENT:
      return getTextAlignment();
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
    case NotationPackage.TEXT_STYLE__TEXT_ALIGNMENT:
      setTextAlignment((TextAlignment)newValue);
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
    case NotationPackage.TEXT_STYLE__TEXT_ALIGNMENT:
      setTextAlignment(TEXT_ALIGNMENT_EDEFAULT);
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
    case NotationPackage.TEXT_STYLE__TEXT_ALIGNMENT:
      return getTextAlignment() != TEXT_ALIGNMENT_EDEFAULT;
    }
    return eDynamicIsSet(featureID);
  }

} // TextStyleImpl
