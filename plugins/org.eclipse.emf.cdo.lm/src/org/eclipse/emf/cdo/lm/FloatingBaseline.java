/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm;

import org.eclipse.emf.cdo.common.branch.CDOBranchRef;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Floating
 * Baseline</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.FloatingBaseline#isClosed <em>Closed</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.LMPackage#getFloatingBaseline()
 * @model abstract="true"
 * @generated
 */
public interface FloatingBaseline extends Baseline
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @model kind="operation" dataType="org.eclipse.emf.cdo.etypes.BranchRef" required="true"
   * @generated
   */
  CDOBranchRef getBranch();

  /**
   * Returns the value of the '<em><b>Closed</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Closed</em>' attribute.
   * @see #setClosed(boolean)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getFloatingBaseline_Closed()
   * @model
   * @generated
   */
  boolean isClosed();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.FloatingBaseline#isClosed <em>Closed</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Closed</em>' attribute.
   * @see #isClosed()
   * @generated
   */
  void setClosed(boolean value);

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  FixedBaseline getBase();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @model kind="operation"
   * @generated
   */
  EList<Delivery> getDeliveries();

} // FloatingBaseline
