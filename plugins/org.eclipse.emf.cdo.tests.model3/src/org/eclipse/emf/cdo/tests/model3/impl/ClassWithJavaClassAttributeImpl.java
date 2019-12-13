/*
 * Copyright (c) 2012, 2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.impl;

import org.eclipse.emf.cdo.tests.model3.ClassWithJavaClassAttribute;
import org.eclipse.emf.cdo.tests.model3.Model3Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Class With Java Class Attribute</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.impl.ClassWithJavaClassAttributeImpl#getJavaClass <em>Java Class</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ClassWithJavaClassAttributeImpl extends CDOObjectImpl implements ClassWithJavaClassAttribute
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ClassWithJavaClassAttributeImpl()
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
    return Model3Package.eINSTANCE.getClassWithJavaClassAttribute();
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
  public Class<?> getJavaClass()
  {
    return (Class<?>)eGet(Model3Package.eINSTANCE.getClassWithJavaClassAttribute_JavaClass(), true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setJavaClass(Class<?> newJavaClass)
  {
    eSet(Model3Package.eINSTANCE.getClassWithJavaClassAttribute_JavaClass(), newJavaClass);
  }

} // ClassWithJavaClassAttributeImpl
