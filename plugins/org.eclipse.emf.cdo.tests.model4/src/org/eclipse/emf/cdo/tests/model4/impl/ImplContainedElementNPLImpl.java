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

import org.eclipse.emf.cdo.tests.model4.ImplContainedElementNPL;
import org.eclipse.emf.cdo.tests.model4.model4Package;
import org.eclipse.emf.cdo.tests.model4interfaces.INamedElement;
import org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Impl Contained Element NPL</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.impl.ImplContainedElementNPLImpl#getName <em>Name</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ImplContainedElementNPLImpl extends CDOObjectImpl implements ImplContainedElementNPL
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected ImplContainedElementNPLImpl()
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
    return model4Package.eINSTANCE.getImplContainedElementNPL();
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
  public String getName()
  {
    return (String)eGet(model4interfacesPackage.eINSTANCE.getINamedElement_Name(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    eSet(model4interfacesPackage.eINSTANCE.getINamedElement_Name(), newName);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
  {
    if (baseClass == INamedElement.class)
    {
      switch (derivedFeatureID)
      {
      case model4Package.IMPL_CONTAINED_ELEMENT_NPL__NAME:
        return model4interfacesPackage.INAMED_ELEMENT__NAME;
      default:
        return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
  {
    if (baseClass == INamedElement.class)
    {
      switch (baseFeatureID)
      {
      case model4interfacesPackage.INAMED_ELEMENT__NAME:
        return model4Package.IMPL_CONTAINED_ELEMENT_NPL__NAME;
      default:
        return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

} // ImplContainedElementNPLImpl
