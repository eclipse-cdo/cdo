/*
 * Copyright (c) 2013, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl;

import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.Bz398057B1;
import org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.HibernateTestPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Bz398057 B1</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.hibernate.model.HibernateTest.impl.Bz398057B1Impl#getValueStr <em>Value Str</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class Bz398057B1Impl extends Bz398057BImpl implements Bz398057B1
{
  /**
   * The default value of the '{@link #getValueStr() <em>Value Str</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getValueStr()
   * @generated
   * @ordered
   */
  protected static final String VALUE_STR_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getValueStr() <em>Value Str</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getValueStr()
   * @generated
   * @ordered
   */
  protected String valueStr = VALUE_STR_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected Bz398057B1Impl()
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
    return HibernateTestPackage.Literals.BZ398057_B1;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getValueStr()
  {
    return valueStr;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setValueStr(String newValueStr)
  {
    String oldValueStr = valueStr;
    valueStr = newValueStr;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, HibernateTestPackage.BZ398057_B1__VALUE_STR, oldValueStr,
          valueStr));
    }
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
    case HibernateTestPackage.BZ398057_B1__VALUE_STR:
      return getValueStr();
    }
    return super.eGet(featureID, resolve, coreType);
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
    case HibernateTestPackage.BZ398057_B1__VALUE_STR:
      setValueStr((String)newValue);
      return;
    }
    super.eSet(featureID, newValue);
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
    case HibernateTestPackage.BZ398057_B1__VALUE_STR:
      setValueStr(VALUE_STR_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
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
    case HibernateTestPackage.BZ398057_B1__VALUE_STR:
      return VALUE_STR_EDEFAULT == null ? valueStr != null : !VALUE_STR_EDEFAULT.equals(valueStr);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (valueStr: ");
    result.append(valueStr);
    result.append(')');
    return result.toString();
  }

} // Bz398057B1Impl
