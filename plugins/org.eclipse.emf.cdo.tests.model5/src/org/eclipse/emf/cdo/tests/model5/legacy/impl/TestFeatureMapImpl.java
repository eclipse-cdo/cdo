/*
 * Copyright (c) 2013, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model5.legacy.impl;

import org.eclipse.emf.cdo.tests.model5.Doctor;
import org.eclipse.emf.cdo.tests.model5.Manager;
import org.eclipse.emf.cdo.tests.model5.TestFeatureMap;
import org.eclipse.emf.cdo.tests.model5.legacy.Model5Package;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Test Feature Map</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.legacy.model5.impl.TestFeatureMapImpl#getManagers <em>Managers</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.legacy.model5.impl.TestFeatureMapImpl#getDoctors <em>Doctors</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.legacy.model5.impl.TestFeatureMapImpl#getPeople <em>People</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TestFeatureMapImpl extends EObjectImpl implements TestFeatureMap
{
  /**
   * The cached value of the '{@link #getManagers() <em>Managers</em>}' reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getManagers()
   * @generated
   * @ordered
   */
  protected EList<Manager> managers;

  /**
   * The cached value of the '{@link #getDoctors() <em>Doctors</em>}' reference list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getDoctors()
   * @generated
   * @ordered
   */
  protected EList<Doctor> doctors;

  /**
   * The cached value of the '{@link #getPeople() <em>People</em>}' attribute list.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getPeople()
   * @generated
   * @ordered
   */
  protected FeatureMap people;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected TestFeatureMapImpl()
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
    return Model5Package.eINSTANCE.getTestFeatureMap();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<Manager> getManagers()
  {
    if (managers == null)
    {
      managers = new EObjectResolvingEList<Manager>(Manager.class, this, Model5Package.TEST_FEATURE_MAP__MANAGERS);
    }
    return managers;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EList<Doctor> getDoctors()
  {
    if (doctors == null)
    {
      doctors = new EObjectResolvingEList<Doctor>(Doctor.class, this, Model5Package.TEST_FEATURE_MAP__DOCTORS);
    }
    return doctors;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public FeatureMap getPeople()
  {
    if (people == null)
    {
      people = new BasicFeatureMap(this, Model5Package.TEST_FEATURE_MAP__PEOPLE);
    }
    return people;
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
    case Model5Package.TEST_FEATURE_MAP__PEOPLE:
      return ((InternalEList<?>)getPeople()).basicRemove(otherEnd, msgs);
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
    case Model5Package.TEST_FEATURE_MAP__MANAGERS:
      return getManagers();
    case Model5Package.TEST_FEATURE_MAP__DOCTORS:
      return getDoctors();
    case Model5Package.TEST_FEATURE_MAP__PEOPLE:
      if (coreType)
      {
        return getPeople();
      }
      return ((FeatureMap.Internal)getPeople()).getWrapper();
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
    case Model5Package.TEST_FEATURE_MAP__MANAGERS:
      getManagers().clear();
      getManagers().addAll((Collection<? extends Manager>)newValue);
      return;
    case Model5Package.TEST_FEATURE_MAP__DOCTORS:
      getDoctors().clear();
      getDoctors().addAll((Collection<? extends Doctor>)newValue);
      return;
    case Model5Package.TEST_FEATURE_MAP__PEOPLE:
      ((FeatureMap.Internal)getPeople()).set(newValue);
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
    case Model5Package.TEST_FEATURE_MAP__MANAGERS:
      getManagers().clear();
      return;
    case Model5Package.TEST_FEATURE_MAP__DOCTORS:
      getDoctors().clear();
      return;
    case Model5Package.TEST_FEATURE_MAP__PEOPLE:
      getPeople().clear();
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
    case Model5Package.TEST_FEATURE_MAP__MANAGERS:
      return managers != null && !managers.isEmpty();
    case Model5Package.TEST_FEATURE_MAP__DOCTORS:
      return doctors != null && !doctors.isEmpty();
    case Model5Package.TEST_FEATURE_MAP__PEOPLE:
      return people != null && !people.isEmpty();
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
    result.append(" (people: ");
    result.append(people);
    result.append(')');
    return result.toString();
  }

} // TestFeatureMapImpl
