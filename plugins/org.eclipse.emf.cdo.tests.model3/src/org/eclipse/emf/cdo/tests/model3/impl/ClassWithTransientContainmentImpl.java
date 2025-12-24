/*
 * Copyright (c) 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.impl;

import org.eclipse.emf.cdo.tests.model3.ClassWithTransientContainment;
import org.eclipse.emf.cdo.tests.model3.Model3Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Class With Transient Containment</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.impl.ClassWithTransientContainmentImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.impl.ClassWithTransientContainmentImpl#getTransientChild <em>Transient Child</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.impl.ClassWithTransientContainmentImpl#getTransientChildren <em>Transient Children</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.impl.ClassWithTransientContainmentImpl#getPersistentChild <em>Persistent Child</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.impl.ClassWithTransientContainmentImpl#getPersistentChildren <em>Persistent Children</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ClassWithTransientContainmentImpl extends CDOObjectImpl implements ClassWithTransientContainment
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ClassWithTransientContainmentImpl()
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
    return Model3Package.eINSTANCE.getClassWithTransientContainment();
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
  @Override
  public String getName()
  {
    return (String)eGet(Model3Package.eINSTANCE.getClassWithTransientContainment_Name(), true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    eSet(Model3Package.eINSTANCE.getClassWithTransientContainment_Name(), newName);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ClassWithTransientContainment getTransientChild()
  {
    return (ClassWithTransientContainment)eGet(Model3Package.eINSTANCE.getClassWithTransientContainment_TransientChild(), true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setTransientChild(ClassWithTransientContainment newTransientChild)
  {
    eSet(Model3Package.eINSTANCE.getClassWithTransientContainment_TransientChild(), newTransientChild);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<ClassWithTransientContainment> getTransientChildren()
  {
    return (EList<ClassWithTransientContainment>)eGet(Model3Package.eINSTANCE.getClassWithTransientContainment_TransientChildren(), true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ClassWithTransientContainment getPersistentChild()
  {
    return (ClassWithTransientContainment)eGet(Model3Package.eINSTANCE.getClassWithTransientContainment_PersistentChild(), true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setPersistentChild(ClassWithTransientContainment newPersistentChild)
  {
    eSet(Model3Package.eINSTANCE.getClassWithTransientContainment_PersistentChild(), newPersistentChild);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<ClassWithTransientContainment> getPersistentChildren()
  {
    return (EList<ClassWithTransientContainment>)eGet(Model3Package.eINSTANCE.getClassWithTransientContainment_PersistentChildren(), true);
  }

} // ClassWithTransientContainmentImpl
