/*
 * Copyright (c) 2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.legacy.impl;

import org.eclipse.emf.cdo.tests.model3.Class1;
import org.eclipse.emf.cdo.tests.model3.legacy.Model3Package;
import org.eclipse.emf.cdo.tests.model3.subpackage.Class2;
import org.eclipse.emf.cdo.tests.model3.subpackage.legacy.SubpackagePackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Class1</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.Class1Impl#getClass2 <em>Class2</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.legacy.impl.Class1Impl#getAdditionalValue <em>Additional Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class Class1Impl extends EObjectImpl implements Class1
{
  /**
   * The cached value of the '{@link #getClass2() <em>Class2</em>}' reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getClass2()
   * @generated
   * @ordered
   */
  protected EList<Class2> class2;

  /**
   * The default value of the '{@link #getAdditionalValue() <em>Additional Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAdditionalValue()
   * @generated
   * @ordered
   */
  protected static final String ADDITIONAL_VALUE_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getAdditionalValue() <em>Additional Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAdditionalValue()
   * @generated
   * @ordered
   */
  protected String additionalValue = ADDITIONAL_VALUE_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected Class1Impl()
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
    return Model3Package.eINSTANCE.getClass1();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<Class2> getClass2()
  {
    if (class2 == null)
    {
      class2 = new EObjectWithInverseResolvingEList.Unsettable.ManyInverse<Class2>(Class2.class, this, Model3Package.CLASS1__CLASS2,
          SubpackagePackage.CLASS2__CLASS1);
    }
    return class2;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetClass2()
  {
    if (class2 != null)
    {
      ((InternalEList.Unsettable<?>)class2).unset();
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetClass2()
  {
    return class2 != null && ((InternalEList.Unsettable<?>)class2).isSet();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getAdditionalValue()
  {
    return additionalValue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAdditionalValue(String newAdditionalValue)
  {
    String oldAdditionalValue = additionalValue;
    additionalValue = newAdditionalValue;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Model3Package.CLASS1__ADDITIONAL_VALUE, oldAdditionalValue, additionalValue));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case Model3Package.CLASS1__CLASS2:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getClass2()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case Model3Package.CLASS1__CLASS2:
      return ((InternalEList<?>)getClass2()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
    case Model3Package.CLASS1__CLASS2:
      return getClass2();
    case Model3Package.CLASS1__ADDITIONAL_VALUE:
      return getAdditionalValue();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Model3Package.CLASS1__CLASS2:
      getClass2().clear();
      getClass2().addAll((Collection<? extends Class2>)newValue);
      return;
    case Model3Package.CLASS1__ADDITIONAL_VALUE:
      setAdditionalValue((String)newValue);
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
    case Model3Package.CLASS1__CLASS2:
      unsetClass2();
      return;
    case Model3Package.CLASS1__ADDITIONAL_VALUE:
      setAdditionalValue(ADDITIONAL_VALUE_EDEFAULT);
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
    case Model3Package.CLASS1__CLASS2:
      return isSetClass2();
    case Model3Package.CLASS1__ADDITIONAL_VALUE:
      return ADDITIONAL_VALUE_EDEFAULT == null ? additionalValue != null : !ADDITIONAL_VALUE_EDEFAULT.equals(additionalValue);
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

    StringBuilder result = new StringBuilder(super.toString());
    result.append(" (additionalValue: ");
    result.append(additionalValue);
    result.append(')');
    return result.toString();
  }

} // Class1Impl
