/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.impl;

import org.eclipse.emf.cdo.tests.model6.Model6Package;
import org.eclipse.emf.cdo.tests.model6.MyEnum;
import org.eclipse.emf.cdo.tests.model6.MyEnumListUnsettable;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>My Enum List Unsettable</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.MyEnumListUnsettableImpl#getMyEnum <em>My Enum</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class MyEnumListUnsettableImpl extends CDOObjectImpl implements MyEnumListUnsettable
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected MyEnumListUnsettableImpl()
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
    return Model6Package.Literals.MY_ENUM_LIST_UNSETTABLE;
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
  @SuppressWarnings("unchecked")
  public EList<MyEnum> getMyEnum()
  {
    return (EList<MyEnum>)eGet(Model6Package.Literals.MY_ENUM_LIST_UNSETTABLE__MY_ENUM, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void unsetMyEnum()
  {
    eUnset(Model6Package.Literals.MY_ENUM_LIST_UNSETTABLE__MY_ENUM);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isSetMyEnum()
  {
    return eIsSet(Model6Package.Literals.MY_ENUM_LIST_UNSETTABLE__MY_ENUM);
  }

} // MyEnumListUnsettableImpl
