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
import org.eclipse.emf.cdo.tests.model6.ContainmentObject;
import org.eclipse.emf.cdo.tests.model6.Model6Package;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Containment Object</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model6.impl.ContainmentObjectImpl#getContainmentOptional <em>Containment
 * Optional</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model6.impl.ContainmentObjectImpl#getContainmentList <em>Containment List</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ContainmentObjectImpl extends BaseObjectImpl implements ContainmentObject
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ContainmentObjectImpl()
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
    return Model6Package.Literals.CONTAINMENT_OBJECT;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public BaseObject getContainmentOptional()
  {
    return (BaseObject)eGet(Model6Package.Literals.CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setContainmentOptional(BaseObject newContainmentOptional)
  {
    eSet(Model6Package.Literals.CONTAINMENT_OBJECT__CONTAINMENT_OPTIONAL, newContainmentOptional);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<BaseObject> getContainmentList()
  {
    return (EList<BaseObject>)eGet(Model6Package.Literals.CONTAINMENT_OBJECT__CONTAINMENT_LIST, true);
  }

} // ContainmentObjectImpl
