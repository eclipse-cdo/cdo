/*
 * Copyright (c) 2008-2012, 2015, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.impl;

import org.eclipse.emf.cdo.tests.model3.Class1;
import org.eclipse.emf.cdo.tests.model3.Model3Package;
import org.eclipse.emf.cdo.tests.model3.subpackage.Class2;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Class1</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.impl.Class1Impl#getClass2 <em>Class2</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.impl.Class1Impl#getAdditionalValue <em>Additional Value</em>}</li>
 * </ul>
 *
 * @generated
 */
public class Class1Impl extends CDOObjectImpl implements Class1
{
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
  public EList<Class2> getClass2()
  {
    return (EList<Class2>)eGet(Model3Package.eINSTANCE.getClass1_Class2(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void unsetClass2()
  {
    eUnset(Model3Package.eINSTANCE.getClass1_Class2());
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isSetClass2()
  {
    return eIsSet(Model3Package.eINSTANCE.getClass1_Class2());
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getAdditionalValue()
  {
    return (String)eGet(Model3Package.eINSTANCE.getClass1_AdditionalValue(), true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAdditionalValue(String newAdditionalValue)
  {
    eSet(Model3Package.eINSTANCE.getClass1_AdditionalValue(), newAdditionalValue);
  }

} // Class1Impl
