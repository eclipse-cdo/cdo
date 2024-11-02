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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Comment</b></em>'.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Comment#getContainer <em>Container</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Comment#getReview <em>Review</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Comment#getReplyTo <em>Reply To</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getComment()
 * @model
 * @generated
 */
public interface Comment extends Authorable
{
  /**
   * Returns the value of the '<em><b>Container</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.reviews.TopicContainer#getComments <em>Comments</em>}'.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @return the value of the '<em>Container</em>' container reference.
   * @see #setContainer(TopicContainer)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getComment_Container()
   * @see org.eclipse.emf.cdo.lm.reviews.TopicContainer#getComments
   * @model opposite="comments" resolveProxies="false" required="true" transient="false"
   * @generated
   */
  TopicContainer getContainer();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.Comment#getContainer <em>Container</em>}' container reference.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Container</em>' container reference.
   * @see #getContainer()
   * @generated
   */
  void setContainer(TopicContainer value);

  /**
   * Returns the value of the '<em><b>Reply To</b></em>' reference.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @return the value of the '<em>Reply To</em>' reference.
   * @see #setReplyTo(Comment)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getComment_ReplyTo()
   * @model
   * @generated
   */
  Comment getReplyTo();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.Comment#getReplyTo <em>Reply To</em>}' reference.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Reply To</em>' reference.
   * @see #getReplyTo()
   * @generated
   */
  void setReplyTo(Comment value);

  /**
   * Returns the value of the '<em><b>Review</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Review</em>' reference.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getComment_Review()
   * @model resolveProxies="false" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Review getReview();

} // Comment
