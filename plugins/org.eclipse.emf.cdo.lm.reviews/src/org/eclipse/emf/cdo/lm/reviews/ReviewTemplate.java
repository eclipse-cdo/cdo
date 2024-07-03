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

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Review Template</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.ReviewTemplate#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.ReviewTemplate#getReviewers <em>Reviewers</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getReviewTemplate()
 * @model
 * @generated
 */
public interface ReviewTemplate extends Commentable
{
  /**
   * Returns the value of the '<em><b>Type</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.lm.reviews.ReviewType}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Type</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewType
   * @see #setType(ReviewType)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getReviewTemplate_Type()
   * @model
   * @generated
   */
  ReviewType getType();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.ReviewTemplate#getType <em>Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Type</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewType
   * @see #getType()
   * @generated
   */
  void setType(ReviewType value);

  /**
   * Returns the value of the '<em><b>Reviewers</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Reviewers</em>' attribute list.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getReviewTemplate_Reviewers()
   * @model
   * @generated
   */
  EList<String> getReviewers();

} // ReviewTemplate
