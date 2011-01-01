/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.impl.TransientContainerImpl#getAttrBefore <em>Attr Before</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.impl.TransientContainerImpl#getParent <em>Parent</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.impl.TransientContainerImpl#getAttrAfter <em>Attr After</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class TransientContainerImpl extends CDOObjectImpl implements TransientContainer
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected TransientContainerImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Model2Package.Literals.TRANSIENT_CONTAINER;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getAttrBefore()
  {
    return (String)eGet(Model2Package.Literals.TRANSIENT_CONTAINER__ATTR_BEFORE, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setAttrBefore(String newAttrBefore)
  {
    eSet(Model2Package.Literals.TRANSIENT_CONTAINER__ATTR_BEFORE, newAttrBefore);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public PersistentContainment getParent()
  {
    return (PersistentContainment)eGet(Model2Package.Literals.TRANSIENT_CONTAINER__PARENT, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setParent(PersistentContainment newParent)
  {
    eSet(Model2Package.Literals.TRANSIENT_CONTAINER__PARENT, newParent);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getAttrAfter()
  {
    return (String)eGet(Model2Package.Literals.TRANSIENT_CONTAINER__ATTR_AFTER, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setAttrAfter(String newAttrAfter)
  {
    eSet(Model2Package.Literals.TRANSIENT_CONTAINER__ATTR_AFTER, newAttrAfter);
  }

} // TransientContainerImpl
