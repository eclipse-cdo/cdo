/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews;

import org.eclipse.emf.cdo.lm.Change;
import org.eclipse.emf.cdo.lm.FloatingBaseline;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Delivery Review</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getSourceChange <em>Source Change</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getSourceCommit <em>Source Commit</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.DeliveryReview#getTargetCommit <em>Target Commit</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getDeliveryReview()
 * @model
 * @generated
 */
public interface DeliveryReview extends Review, FloatingBaseline
{
  /**
   * Returns the value of the '<em><b>Source Change</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Source Change</em>' reference.
   * @see #setSourceChange(Change)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getDeliveryReview_SourceChange()
   * @model required="true"
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

} // DeliveryReview
