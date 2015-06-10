/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore.impl;

import org.eclipse.emf.cdo.dawn.examples.acore.AParameter;
import org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>AParameter</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.AParameterImpl#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.AParameterImpl#getType <em>Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class AParameterImpl extends CDOObjectImpl implements AParameter
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  protected AParameterImpl()
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
    return AcorePackage.Literals.APARAMETER;
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
  public String getName()
  {
    return (String)eGet(AcorePackage.Literals.APARAMETER__NAME, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public void setName(String newName)
  {
    eSet(AcorePackage.Literals.APARAMETER__NAME, newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public String getType()
  {
    return (String)eGet(AcorePackage.Literals.APARAMETER__TYPE, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public void setType(String newType)
  {
    eSet(AcorePackage.Literals.APARAMETER__TYPE, newType);
  }

} // AParameterImpl
