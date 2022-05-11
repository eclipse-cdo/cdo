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

import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;

/**
 * <!-- begin-user-doc --> A representation of the model object
 * '<em><b>Delivery</b></em>'.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.Delivery#getChange <em>Change</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Delivery#getMergeSource <em>Merge Source</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.Delivery#getMergeTarget <em>Merge Target</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.LMPackage#getDelivery()
 * @model
 * @generated
 */
public interface Delivery extends FixedBaseline
{
  /**
   * Returns the value of the '<em><b>Change</b></em>' reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.Change#getDeliveries <em>Deliveries</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>Change</em>' reference.
   * @see #setChange(Change)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getDelivery_Change()
   * @see org.eclipse.emf.cdo.lm.Change#getDeliveries
   * @model opposite="deliveries" required="true"
   * @generated
   */
  Change getChange();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Delivery#getChange <em>Change</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Change</em>' reference.
   * @see #getChange()
   * @generated
   */
  void setChange(Change value);

  /**
   * Returns the value of the '<em><b>Merge Source</b></em>' attribute. The
   * default value is <code>""</code>. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return the value of the '<em>Merge Source</em>' attribute.
   * @see #setMergeSource(CDOBranchPointRef)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getDelivery_MergeSource()
   * @model default="" dataType="org.eclipse.emf.cdo.etypes.BranchPointRef"
   *        required="true"
   * @generated
   */
  CDOBranchPointRef getMergeSource();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Delivery#getMergeSource <em>Merge Source</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Merge Source</em>' attribute.
   * @see #getMergeSource()
   * @generated
   */
  void setMergeSource(CDOBranchPointRef value);

  /**
   * Returns the value of the '<em><b>Merge Target</b></em>' attribute. The
   * default value is <code>""</code>. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @return the value of the '<em>Merge Target</em>' attribute.
   * @see #setMergeTarget(CDOBranchPointRef)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getDelivery_MergeTarget()
   * @model default="" dataType="org.eclipse.emf.cdo.etypes.BranchPointRef"
   *        required="true"
   * @generated
   */
  CDOBranchPointRef getMergeTarget();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.Delivery#getMergeTarget <em>Merge Target</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Merge Target</em>' attribute.
   * @see #getMergeTarget()
   * @generated
   */
  void setMergeTarget(CDOBranchPointRef value);

} // Delivery
