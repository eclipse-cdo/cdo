/*
 * Copyright (c) 2013-2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6.impl;

import org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy;
import org.eclipse.emf.cdo.tests.model6.Model6Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Can Reference Legacy</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.CanReferenceLegacyImpl#getSingleContainment <em>Single Containment</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.CanReferenceLegacyImpl#getMultipleContainment <em>Multiple Containment</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.CanReferenceLegacyImpl#getSingleReference <em>Single Reference</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.impl.CanReferenceLegacyImpl#getMultipleReference <em>Multiple Reference</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CanReferenceLegacyImpl extends CDOObjectImpl implements CanReferenceLegacy
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected CanReferenceLegacyImpl()
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
    return Model6Package.Literals.CAN_REFERENCE_LEGACY;
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
  public EObject getSingleContainment()
  {
    return (EObject)eGet(Model6Package.Literals.CAN_REFERENCE_LEGACY__SINGLE_CONTAINMENT, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSingleContainment(EObject newSingleContainment)
  {
    eSet(Model6Package.Literals.CAN_REFERENCE_LEGACY__SINGLE_CONTAINMENT, newSingleContainment);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<EObject> getMultipleContainment()
  {
    return (EList<EObject>)eGet(Model6Package.Literals.CAN_REFERENCE_LEGACY__MULTIPLE_CONTAINMENT, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject getSingleReference()
  {
    return (EObject)eGet(Model6Package.Literals.CAN_REFERENCE_LEGACY__SINGLE_REFERENCE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSingleReference(EObject newSingleReference)
  {
    eSet(Model6Package.Literals.CAN_REFERENCE_LEGACY__SINGLE_REFERENCE, newSingleReference);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<EObject> getMultipleReference()
  {
    return (EList<EObject>)eGet(Model6Package.Literals.CAN_REFERENCE_LEGACY__MULTIPLE_REFERENCE, true);
  }

} // CanReferenceLegacyImpl
