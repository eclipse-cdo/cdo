/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.dawn.examples.acore.AAttribute;
import org.eclipse.emf.cdo.dawn.examples.acore.ABasicClass;
import org.eclipse.emf.cdo.dawn.examples.acore.AOperation;
import org.eclipse.emf.cdo.dawn.examples.acore.AcorePackage;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>ABasic Class</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.ABasicClassImpl#getOperations <em>Operations</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.ABasicClassImpl#getAttributes <em>Attributes</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.dawn.examples.acore.impl.ABasicClassImpl#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class ABasicClassImpl extends CDOObjectImpl implements ABasicClass
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected ABasicClassImpl()
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
    return AcorePackage.Literals.ABASIC_CLASS;
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
  @SuppressWarnings("unchecked")
  public EList<AOperation> getOperations()
  {
    return (EList<AOperation>)eGet(AcorePackage.Literals.ABASIC_CLASS__OPERATIONS, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<AAttribute> getAttributes()
  {
    return (EList<AAttribute>)eGet(AcorePackage.Literals.ABASIC_CLASS__ATTRIBUTES, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public String getName()
  {
    return (String)eGet(AcorePackage.Literals.ABASIC_CLASS__NAME, true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void setName(String newName)
  {
    eSet(AcorePackage.Literals.ABASIC_CLASS__NAME, newName);
  }

} // ABasicClassImpl
