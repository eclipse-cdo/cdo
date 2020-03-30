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
import org.eclipse.gmf.runtime.notation.Size;

/**
 * <!-- begin-user-doc --> An implementation of the model object
 * '<em><b>Size</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.SizeImpl#getWidth <em>Width</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.gmf.notation.impl.SizeImpl#getHeight <em>Height</em>}</li>
 * </ul>
 *
 * @generated
 */
/*
 * @canBeSeenBy %partners
 */
public class SizeImpl extends CDOObjectImpl implements Size
{
  /**
   * The default value of the '{@link #getWidth() <em>Width</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @see #getWidth()
   * @generated
   * @ordered
   */
  protected static final int WIDTH_EDEFAULT = -1;

  /**
   * The default value of the '{@link #getHeight() <em>Height</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getHeight()
   * @generated
   * @ordered
   */
  protected static final int HEIGHT_EDEFAULT = -1;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected SizeImpl()
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
    return NotationPackage.Literals.SIZE;
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
  public int getWidth()
  {
    return ((Integer)eDynamicGet(NotationPackage.SIZE__WIDTH, NotationPackage.Literals.SIZE__WIDTH, true, true)).intValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setWidth(int newWidth)
  {
    eDynamicSet(NotationPackage.SIZE__WIDTH, NotationPackage.Literals.SIZE__WIDTH, new Integer(newWidth));
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getHeight()
  {
    return ((Integer)eDynamicGet(NotationPackage.SIZE__HEIGHT, NotationPackage.Literals.SIZE__HEIGHT, true, true)).intValue();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setHeight(int newHeight)
  {
    eDynamicSet(NotationPackage.SIZE__HEIGHT, NotationPackage.Literals.SIZE__HEIGHT, new Integer(newHeight));
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
    case NotationPackage.SIZE__WIDTH:
      return new Integer(getWidth());
    case NotationPackage.SIZE__HEIGHT:
      return new Integer(getHeight());
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
    case NotationPackage.SIZE__WIDTH:
      setWidth(((Integer)newValue).intValue());
      return;
    case NotationPackage.SIZE__HEIGHT:
      setHeight(((Integer)newValue).intValue());
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
    case NotationPackage.SIZE__WIDTH:
      setWidth(WIDTH_EDEFAULT);
      return;
    case NotationPackage.SIZE__HEIGHT:
      setHeight(HEIGHT_EDEFAULT);
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
    case NotationPackage.SIZE__WIDTH:
      return getWidth() != WIDTH_EDEFAULT;
    case NotationPackage.SIZE__HEIGHT:
      return getHeight() != HEIGHT_EDEFAULT;
    }
    return eDynamicIsSet(featureID);
  }

} // SizeImpl
