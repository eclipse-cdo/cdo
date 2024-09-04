/*
 * Copyright (c) 2023, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews;

import org.eclipse.emf.cdo.common.branch.CDOBranchRef;
import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.FloatingBaseline;
import org.eclipse.emf.cdo.lm.Impact;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Delivery Review</b></em>'.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getBase <em>Base</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getImpact <em>Impact</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getBranch <em>Branch</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getDeliveries <em>Deliveries</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getSourceChange <em>Source Change</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getSourceCommit <em>Source Commit</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getTargetCommit <em>Target Commit</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getRebaseCount <em>Rebase Count</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getDeliveryReview()
 * @model
 * @generated
 */
public interface DeliveryReview extends Review, FloatingBaseline
{
  /**
   * Returns the value of the '<em><b>Base</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Base</em>' reference.
   * @see #setBase(FixedBaseline)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getDeliveryReview_Base()
   * @model resolveProxies="false" required="true"
   * @generated
   */
  @Override
  FixedBaseline getBase();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getBase <em>Base</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Base</em>' reference.
   * @see #getBase()
   * @generated
   */
  void setBase(FixedBaseline value);

  /**
   * Returns the value of the '<em><b>Impact</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.lm.Impact}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Impact</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.Impact
   * @see #setImpact(Impact)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getDeliveryReview_Impact()
   * @model required="true"
   * @generated
   */
  Impact getImpact();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getImpact <em>Impact</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Impact</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.Impact
   * @see #getImpact()
   * @generated
   */
  void setImpact(Impact value);

  /**
   * Returns the value of the '<em><b>Branch</b></em>' attribute.
   * The default value is <code>""</code>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Branch</em>' attribute.
   * @see #setBranch(CDOBranchRef)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getDeliveryReview_Branch()
   * @model default="" dataType="org.eclipse.emf.cdo.etypes.BranchRef" required="true"
   * @generated
   */
  @Override
  CDOBranchRef getBranch();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getBranch <em>Branch</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Branch</em>' attribute.
   * @see #getBranch()
   * @generated
   */
  void setBranch(CDOBranchRef value);

  /**
   * Returns the value of the '<em><b>Deliveries</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.lm.Delivery}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Deliveries</em>' reference list.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getDeliveryReview_Deliveries()
   * @model
   * @generated
   */
  @Override
  EList<Delivery> getDeliveries();

  /**
   * Returns the value of the '<em><b>Source Change</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source Change</em>' reference.
   * @see #setSourceChange(Change)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getDeliveryReview_SourceChange()
   * @model resolveProxies="false" required="true"
   * @generated
   */
  Change getSourceChange();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getSourceChange <em>Source Change</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Source Change</em>' reference.
   * @see #getSourceChange()
   * @generated
   */
  void setSourceChange(Change value);

  /**
   * Returns the value of the '<em><b>Source Commit</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source Commit</em>' attribute.
   * @see #setSourceCommit(long)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getDeliveryReview_SourceCommit()
   * @model required="true"
   * @generated
   */
  long getSourceCommit();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getSourceCommit <em>Source Commit</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Source Commit</em>' attribute.
   * @see #getSourceCommit()
   * @generated
   */
  void setSourceCommit(long value);

  /**
   * Returns the value of the '<em><b>Target Commit</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Target Commit</em>' attribute.
   * @see #setTargetCommit(long)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getDeliveryReview_TargetCommit()
   * @model required="true"
   * @generated
   */
  long getTargetCommit();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getTargetCommit <em>Target Commit</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Target Commit</em>' attribute.
   * @see #getTargetCommit()
   * @generated
   */
  void setTargetCommit(long value);

  /**
   * Returns the value of the '<em><b>Rebase Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Rebase Count</em>' attribute.
   * @see #setRebaseCount(int)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getDeliveryReview_RebaseCount()
   * @model
   * @generated
   */
  int getRebaseCount();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getRebaseCount <em>Rebase Count</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Rebase Count</em>' attribute.
   * @see #getRebaseCount()
   * @generated
   */
  void setRebaseCount(int value);

} // DeliveryReview
