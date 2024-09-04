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

import org.eclipse.emf.cdo.lm.SystemElement;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Commentable</b></em>'.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Commentable#getReview <em>Review</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Commentable#getComments <em>Comments</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Commentable#getCommentCount <em>Comment Count</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Commentable#getUnresolvedCount <em>Unresolved Count</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Commentable#getResolvedCount <em>Resolved Count</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getCommentable()
 * @model abstract="true"
 * @generated
 */
public interface Commentable extends SystemElement
{
  /**
   * Returns the value of the '<em><b>Review</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Review</em>' reference.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getCommentable_Review()
   * @model resolveProxies="false" required="true" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Review getReview();

  /**
   * Returns the value of the '<em><b>Comments</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.lm.reviews.Comment}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.reviews.Comment#getCommentable <em>Commentable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Comments</em>' containment reference list.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getCommentable_Comments()
   * @see org.eclipse.emf.cdo.lm.reviews.Comment#getCommentable
   * @model opposite="commentable" containment="true"
   * @generated
   */
  EList<Comment> getComments();

  /**
   * Returns the value of the '<em><b>Comment Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Comment Count</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getCommentable_CommentCount()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  int getCommentCount();

  /**
   * Returns the value of the '<em><b>Unresolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Unresolved Count</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getCommentable_UnresolvedCount()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  int getUnresolvedCount();

  /**
   * Returns the value of the '<em><b>Resolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Resolved Count</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getCommentable_ResolvedCount()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  int getResolvedCount();

} // Commentable
