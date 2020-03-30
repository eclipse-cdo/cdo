/******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
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
import org.eclipse.gmf.runtime.notation.TitleStyle;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Title Style</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.TitleStyleImpl#isShowTitle <em>Show Title</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy %partners
 */
public class TitleStyleImpl extends CDOObjectImpl implements TitleStyle
{
  /**
  * The default value of the '{@link #isShowTitle() <em>Show Title</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #isShowTitle()
  * @generated
  * @ordered
  */
  protected static final boolean SHOW_TITLE_EDEFAULT = false;

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  protected TitleStyleImpl()
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
    return NotationPackage.Literals.TITLE_STYLE;
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
  public boolean isShowTitle()
  {
    return ((Boolean)eDynamicGet(NotationPackage.TITLE_STYLE__SHOW_TITLE, NotationPackage.Literals.TITLE_STYLE__SHOW_TITLE, true, true)).booleanValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setShowTitle(boolean newShowTitle)
  {
    eDynamicSet(NotationPackage.TITLE_STYLE__SHOW_TITLE, NotationPackage.Literals.TITLE_STYLE__SHOW_TITLE, new Boolean(newShowTitle));
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
    case NotationPackage.TITLE_STYLE__SHOW_TITLE:
      return isShowTitle() ? Boolean.TRUE : Boolean.FALSE;
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
    case NotationPackage.TITLE_STYLE__SHOW_TITLE:
      setShowTitle(((Boolean)newValue).booleanValue());
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
    case NotationPackage.TITLE_STYLE__SHOW_TITLE:
      setShowTitle(SHOW_TITLE_EDEFAULT);
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
    case NotationPackage.TITLE_STYLE__SHOW_TITLE:
      return isShowTitle() != SHOW_TITLE_EDEFAULT;
    }
    return eDynamicIsSet(featureID);
  }

} // TitleStyleImpl
