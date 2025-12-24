/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.mango.legacy.impl;

import org.eclipse.emf.cdo.tests.mango.MangoParameter;
import org.eclipse.emf.cdo.tests.mango.ParameterPassing;
import org.eclipse.emf.cdo.tests.mango.legacy.MangoPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Parameter</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.mango.legacy.impl.MangoParameterImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.mango.legacy.impl.MangoParameterImpl#getPassing <em>Passing</em>}</li>
 * </ul>
 *
 * @generated
 */
public class MangoParameterImpl extends EObjectImpl implements MangoParameter
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The default value of the '{@link #getPassing() <em>Passing</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getPassing()
   * @generated
   * @ordered
   */
  protected static final ParameterPassing PASSING_EDEFAULT = ParameterPassing.BY_VALUE;

  /**
   * The cached value of the '{@link #getPassing() <em>Passing</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getPassing()
   * @generated
   * @ordered
   */
  protected ParameterPassing passing = PASSING_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected MangoParameterImpl()
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
    return MangoPackage.eINSTANCE.getMangoParameter();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, MangoPackage.MANGO_PARAMETER__NAME, oldName, name));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ParameterPassing getPassing()
  {
    return passing;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setPassing(ParameterPassing newPassing)
  {
    ParameterPassing oldPassing = passing;
    passing = newPassing == null ? PASSING_EDEFAULT : newPassing;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, MangoPackage.MANGO_PARAMETER__PASSING, oldPassing, passing));
    }
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
    case MangoPackage.MANGO_PARAMETER__NAME:
      return getName();
    case MangoPackage.MANGO_PARAMETER__PASSING:
      return getPassing();
    }
    return super.eGet(featureID, resolve, coreType);
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
    case MangoPackage.MANGO_PARAMETER__NAME:
      setName((String)newValue);
      return;
    case MangoPackage.MANGO_PARAMETER__PASSING:
      setPassing((ParameterPassing)newValue);
      return;
    }
    super.eSet(featureID, newValue);
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
    case MangoPackage.MANGO_PARAMETER__NAME:
      setName(NAME_EDEFAULT);
      return;
    case MangoPackage.MANGO_PARAMETER__PASSING:
      setPassing(PASSING_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
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
    case MangoPackage.MANGO_PARAMETER__NAME:
      return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
    case MangoPackage.MANGO_PARAMETER__PASSING:
      return passing != PASSING_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(", passing: ");
    result.append(passing);
    result.append(')');
    return result.toString();
  }

} // ParameterImpl
