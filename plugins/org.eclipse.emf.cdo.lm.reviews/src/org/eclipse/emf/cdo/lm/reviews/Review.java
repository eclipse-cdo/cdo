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

import org.eclipse.emf.cdo.lm.Baseline;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Review</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Review#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Review#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Review#getReviewers <em>Reviewers</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getReview()
 * @model abstract="true"
 * @generated
 */
public interface Review extends Baseline, Commentable
{
  /**
   * Returns the value of the '<em><b>Type</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.lm.reviews.ReviewType}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Type</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewType
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getReview_Type()
   * @model required="true" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  ReviewType getType();

  /**
   * Returns the value of the '<em><b>Author</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Author</em>' attribute.
   * @see #setAuthor(String)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getReview_Author()
   * @model required="true"
   * @generated
   */
  String getAuthor();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.Review#getAuthor <em>Author</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Author</em>' attribute.
   * @see #getAuthor()
   * @generated
   */
  void setAuthor(String value);

  /**
   * Returns the value of the '<em><b>Reviewers</b></em>' attribute list.
   * The list contents are of type {@link java.lang.String}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Reviewers</em>' attribute list.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getReview_Reviewers()
   * @model required="true"
   * @generated
   */
  EList<String> getReviewers();

} // Review
