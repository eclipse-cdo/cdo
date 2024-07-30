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
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Review#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Review#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Review#getReviewers <em>Reviewers</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Review#getStatus <em>Status</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getReview()
 * @model abstract="true"
 * @generated
 */
public interface Review extends Baseline, Commentable
{
  /**
   * Returns the value of the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Id</em>' attribute.
   * @see #setId(int)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getReview_Id()
   * @model required="true"
   * @generated
   */
  int getId();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.Review#getId <em>Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Id</em>' attribute.
   * @see #getId()
   * @generated
   */
  void setId(int value);

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

  /**
   * Returns the value of the '<em><b>Status</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.lm.reviews.ReviewStatus}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Status</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewStatus
   * @see #setStatus(ReviewStatus)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getReview_Status()
   * @model required="true"
   * @generated
   */
  ReviewStatus getStatus();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.Review#getStatus <em>Status</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Status</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewStatus
   * @see #getStatus()
   * @generated
   */
  void setStatus(ReviewStatus value);

} // Review
