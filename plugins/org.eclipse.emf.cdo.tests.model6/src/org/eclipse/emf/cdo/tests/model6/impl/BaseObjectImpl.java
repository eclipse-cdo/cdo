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

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Base Object</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.BaseObjectImpl#getAttributeOptional <em>Attribute Optional</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.BaseObjectImpl#getAttributeRequired <em>Attribute Required</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.BaseObjectImpl#getAttributeList <em>Attribute List</em>}</li>
 * </ul>
 *
 * @generated
 */
public class BaseObjectImpl extends CDOObjectImpl implements BaseObject
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected BaseObjectImpl()
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
    return Model6Package.Literals.BASE_OBJECT;
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
  public String getAttributeOptional()
  {
    return (String)eGet(Model6Package.Literals.BASE_OBJECT__ATTRIBUTE_OPTIONAL, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttributeOptional(String newAttributeOptional)
  {
    eSet(Model6Package.Literals.BASE_OBJECT__ATTRIBUTE_OPTIONAL, newAttributeOptional);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getAttributeRequired()
  {
    return (String)eGet(Model6Package.Literals.BASE_OBJECT__ATTRIBUTE_REQUIRED, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttributeRequired(String newAttributeRequired)
  {
    eSet(Model6Package.Literals.BASE_OBJECT__ATTRIBUTE_REQUIRED, newAttributeRequired);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<String> getAttributeList()
  {
    return (EList<String>)eGet(Model6Package.Literals.BASE_OBJECT__ATTRIBUTE_LIST, true);
  }

} // BaseObjectImpl
