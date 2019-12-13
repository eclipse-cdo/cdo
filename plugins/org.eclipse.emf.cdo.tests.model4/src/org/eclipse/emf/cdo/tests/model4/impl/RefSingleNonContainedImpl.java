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

import org.eclipse.emf.cdo.tests.model4.RefSingleNonContained;
import org.eclipse.emf.cdo.tests.model4.SingleNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.model4Package;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Ref Single Non Contained</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.impl.RefSingleNonContainedImpl#getElement <em>Element</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RefSingleNonContainedImpl extends CDOObjectImpl implements RefSingleNonContained
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected RefSingleNonContainedImpl()
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
    return model4Package.eINSTANCE.getRefSingleNonContained();
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
  public SingleNonContainedElement getElement()
  {
    return (SingleNonContainedElement)eGet(model4Package.eINSTANCE.getRefSingleNonContained_Element(), true);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setElement(SingleNonContainedElement newElement)
  {
    eSet(model4Package.eINSTANCE.getRefSingleNonContained_Element(), newElement);
  }

} // RefSingleNonContainedImpl
