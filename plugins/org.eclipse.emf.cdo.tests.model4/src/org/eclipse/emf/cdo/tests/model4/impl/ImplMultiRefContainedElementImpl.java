/*
 * Copyright (c) 2008, 2009, 2011-2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model4.impl;

import org.eclipse.emf.cdo.tests.model4.ImplMultiRefContainedElement;
import org.eclipse.emf.cdo.tests.model4.model4Package;
import org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainer;
import org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Impl Multi Ref Contained Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.impl.ImplMultiRefContainedElementImpl#getParent <em>Parent</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.impl.ImplMultiRefContainedElementImpl#getName <em>Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ImplMultiRefContainedElementImpl extends CDOObjectImpl implements ImplMultiRefContainedElement
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ImplMultiRefContainedElementImpl()
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
    return model4Package.eINSTANCE.getImplMultiRefContainedElement();
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
  public IMultiRefContainer getParent()
  {
    return (IMultiRefContainer)eGet(model4interfacesPackage.eINSTANCE.getIMultiRefContainedElement_Parent(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setParent(IMultiRefContainer newParent)
  {
    eSet(model4interfacesPackage.eINSTANCE.getIMultiRefContainedElement_Parent(), newParent);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return (String)eGet(model4Package.eINSTANCE.getImplMultiRefContainedElement_Name(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    eSet(model4Package.eINSTANCE.getImplMultiRefContainedElement_Name(), newName);
  }

} // ImplMultiRefContainedElementImpl
