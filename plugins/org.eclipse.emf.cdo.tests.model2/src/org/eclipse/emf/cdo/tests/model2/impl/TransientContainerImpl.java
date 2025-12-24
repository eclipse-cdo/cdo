/*
 * Copyright (c) 2009, 2011-2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model2.impl;

import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.tests.model2.PersistentContainment;
import org.eclipse.emf.cdo.tests.model2.TransientContainer;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Transient Container</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.impl.TransientContainerImpl#getAttrBefore <em>Attr Before</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.impl.TransientContainerImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.impl.TransientContainerImpl#getAttrAfter <em>Attr After</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TransientContainerImpl extends CDOObjectImpl implements TransientContainer
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected TransientContainerImpl()
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
    return Model2Package.eINSTANCE.getTransientContainer();
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
  public String getAttrBefore()
  {
    return (String)eGet(Model2Package.eINSTANCE.getTransientContainer_AttrBefore(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrBefore(String newAttrBefore)
  {
    eSet(Model2Package.eINSTANCE.getTransientContainer_AttrBefore(), newAttrBefore);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public PersistentContainment getParent()
  {
    return (PersistentContainment)eGet(Model2Package.eINSTANCE.getTransientContainer_Parent(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setParent(PersistentContainment newParent)
  {
    eSet(Model2Package.eINSTANCE.getTransientContainer_Parent(), newParent);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getAttrAfter()
  {
    return (String)eGet(Model2Package.eINSTANCE.getTransientContainer_AttrAfter(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAttrAfter(String newAttrAfter)
  {
    eSet(Model2Package.eINSTANCE.getTransientContainer_AttrAfter(), newAttrAfter);
  }

} // TransientContainerImpl
