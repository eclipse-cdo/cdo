/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model5.impl;

import org.eclipse.emf.cdo.tests.model5.Doctor;
import org.eclipse.emf.cdo.tests.model5.Manager;
import org.eclipse.emf.cdo.tests.model5.Model5Package;
import org.eclipse.emf.cdo.tests.model5.TestFeatureMap;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Test Feature Map</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model5.impl.TestFeatureMapImpl#getManagers <em>Managers</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model5.impl.TestFeatureMapImpl#getDoctors <em>Doctors</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model5.impl.TestFeatureMapImpl#getPeople <em>People</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TestFeatureMapImpl extends CDOObjectImpl implements TestFeatureMap
{
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
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Manager> getManagers()
  {
    return (EList<Manager>)eGet(Model5Package.eINSTANCE.getTestFeatureMap_Managers(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Doctor> getDoctors()
  {
    return (EList<Doctor>)eGet(Model5Package.eINSTANCE.getTestFeatureMap_Doctors(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated NOT
   */
  @Override
  public FeatureMap getPeople()
  {
    return (FeatureMap)eGet(Model5Package.eINSTANCE.getTestFeatureMap_People(), true);
  }

} // TestFeatureMapImpl
