/*
 * Copyright (c) 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.impl;

import org.eclipse.emf.cdo.tests.model6.BaseObject;
import org.eclipse.emf.cdo.tests.model6.Model6Package;
import org.eclipse.emf.cdo.tests.model6.Root;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Root</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.RootImpl#getListA <em>List A</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.RootImpl#getListB <em>List B</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.RootImpl#getListC <em>List C</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.RootImpl#getListD <em>List D</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RootImpl extends CDOObjectImpl implements Root
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected RootImpl()
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
    return Model6Package.Literals.ROOT;
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
  public EList<BaseObject> getListA()
  {
    return (EList<BaseObject>)eGet(Model6Package.Literals.ROOT__LIST_A, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<BaseObject> getListB()
  {
    return (EList<BaseObject>)eGet(Model6Package.Literals.ROOT__LIST_B, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<BaseObject> getListC()
  {
    return (EList<BaseObject>)eGet(Model6Package.Literals.ROOT__LIST_C, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<BaseObject> getListD()
  {
    return (EList<BaseObject>)eGet(Model6Package.Literals.ROOT__LIST_D, true);
  }

} // RootImpl
