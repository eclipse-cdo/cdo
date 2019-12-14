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
import org.eclipse.gmf.runtime.notation.PageStyle;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Page Style</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.PageStyleImpl#getPageX <em>Page X</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.PageStyleImpl#getPageY <em>Page Y</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.PageStyleImpl#getPageWidth <em>Page Width</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.PageStyleImpl#getPageHeight <em>Page Height</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy org.eclipse.gmf.runtime.notation.*
 */
public class PageStyleImpl extends CDOObjectImpl implements PageStyle
{
  /**
  * The default value of the '{@link #getPageX() <em>Page X</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getPageX()
  * @generated
  * @ordered
  */
  protected static final int PAGE_X_EDEFAULT = 0;

  /**
  * The default value of the '{@link #getPageY() <em>Page Y</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getPageY()
  * @generated
  * @ordered
  */
  protected static final int PAGE_Y_EDEFAULT = 0;

  /**
  * The default value of the '{@link #getPageWidth() <em>Page Width</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getPageWidth()
  * @generated
  * @ordered
  */
  protected static final int PAGE_WIDTH_EDEFAULT = 100;

  /**
  * The default value of the '{@link #getPageHeight() <em>Page Height</em>}' attribute.
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @see #getPageHeight()
  * @generated
  * @ordered
  */
  protected static final int PAGE_HEIGHT_EDEFAULT = 100;

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  protected PageStyleImpl()
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
    return NotationPackage.Literals.PAGE_STYLE;
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
  public int getPageX()
  {
    return ((Integer)eDynamicGet(NotationPackage.PAGE_STYLE__PAGE_X, NotationPackage.Literals.PAGE_STYLE__PAGE_X, true, true)).intValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setPageX(int newPageX)
  {
    eDynamicSet(NotationPackage.PAGE_STYLE__PAGE_X, NotationPackage.Literals.PAGE_STYLE__PAGE_X, new Integer(newPageX));
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public int getPageY()
  {
    return ((Integer)eDynamicGet(NotationPackage.PAGE_STYLE__PAGE_Y, NotationPackage.Literals.PAGE_STYLE__PAGE_Y, true, true)).intValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setPageY(int newPageY)
  {
    eDynamicSet(NotationPackage.PAGE_STYLE__PAGE_Y, NotationPackage.Literals.PAGE_STYLE__PAGE_Y, new Integer(newPageY));
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public int getPageWidth()
  {
    return ((Integer)eDynamicGet(NotationPackage.PAGE_STYLE__PAGE_WIDTH, NotationPackage.Literals.PAGE_STYLE__PAGE_WIDTH, true, true)).intValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setPageWidth(int newPageWidth)
  {
    eDynamicSet(NotationPackage.PAGE_STYLE__PAGE_WIDTH, NotationPackage.Literals.PAGE_STYLE__PAGE_WIDTH, new Integer(newPageWidth));
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public int getPageHeight()
  {
    return ((Integer)eDynamicGet(NotationPackage.PAGE_STYLE__PAGE_HEIGHT, NotationPackage.Literals.PAGE_STYLE__PAGE_HEIGHT, true, true)).intValue();
  }

  /**
  * <!-- begin-user-doc -->
  * <!-- end-user-doc -->
  * @generated
  */
  @Override
  public void setPageHeight(int newPageHeight)
  {
    eDynamicSet(NotationPackage.PAGE_STYLE__PAGE_HEIGHT, NotationPackage.Literals.PAGE_STYLE__PAGE_HEIGHT, new Integer(newPageHeight));
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
    case NotationPackage.PAGE_STYLE__PAGE_X:
      return new Integer(getPageX());
    case NotationPackage.PAGE_STYLE__PAGE_Y:
      return new Integer(getPageY());
    case NotationPackage.PAGE_STYLE__PAGE_WIDTH:
      return new Integer(getPageWidth());
    case NotationPackage.PAGE_STYLE__PAGE_HEIGHT:
      return new Integer(getPageHeight());
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
    case NotationPackage.PAGE_STYLE__PAGE_X:
      setPageX(((Integer)newValue).intValue());
      return;
    case NotationPackage.PAGE_STYLE__PAGE_Y:
      setPageY(((Integer)newValue).intValue());
      return;
    case NotationPackage.PAGE_STYLE__PAGE_WIDTH:
      setPageWidth(((Integer)newValue).intValue());
      return;
    case NotationPackage.PAGE_STYLE__PAGE_HEIGHT:
      setPageHeight(((Integer)newValue).intValue());
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
    case NotationPackage.PAGE_STYLE__PAGE_X:
      setPageX(PAGE_X_EDEFAULT);
      return;
    case NotationPackage.PAGE_STYLE__PAGE_Y:
      setPageY(PAGE_Y_EDEFAULT);
      return;
    case NotationPackage.PAGE_STYLE__PAGE_WIDTH:
      setPageWidth(PAGE_WIDTH_EDEFAULT);
      return;
    case NotationPackage.PAGE_STYLE__PAGE_HEIGHT:
      setPageHeight(PAGE_HEIGHT_EDEFAULT);
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
    case NotationPackage.PAGE_STYLE__PAGE_X:
      return getPageX() != PAGE_X_EDEFAULT;
    case NotationPackage.PAGE_STYLE__PAGE_Y:
      return getPageY() != PAGE_Y_EDEFAULT;
    case NotationPackage.PAGE_STYLE__PAGE_WIDTH:
      return getPageWidth() != PAGE_WIDTH_EDEFAULT;
    case NotationPackage.PAGE_STYLE__PAGE_HEIGHT:
      return getPageHeight() != PAGE_HEIGHT_EDEFAULT;
    }
    return eDynamicIsSet(featureID);
  }

} // PageStyleImpl
