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
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Comment#getCommentable <em>Commentable</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Comment#getParentHeading <em>Parent Heading</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Comment#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Comment#getText <em>Text</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Comment#getStatus <em>Status</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getComment()
 * @model
 * @generated
 */
public interface Comment extends Commentable
{
  /**
   * Returns the value of the '<em><b>Commentable</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.reviews.Commentable#getComments <em>Comments</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Commentable</em>' container reference.
   * @see #setCommentable(Commentable)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getComment_Commentable()
   * @see org.eclipse.emf.cdo.lm.reviews.Commentable#getComments
   * @model opposite="comments" resolveProxies="false" required="true" transient="false"
   * @generated
   */
  Commentable getCommentable();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.Comment#getCommentable <em>Commentable</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Commentable</em>' container reference.
   * @see #getCommentable()
   * @generated
   */
  void setCommentable(Commentable value);

  /**
   * Returns the value of the '<em><b>Parent Heading</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Parent Heading</em>' reference.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getComment_ParentHeading()
   * @model resolveProxies="false" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Heading getParentHeading();

  /**
   * Returns the value of the '<em><b>Author</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Author</em>' attribute.
   * @see #setAuthor(String)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getComment_Author()
   * @model
   * @generated
   */
  String getAuthor();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.Comment#getAuthor <em>Author</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Author</em>' attribute.
   * @see #getAuthor()
   * @generated
   */
  void setAuthor(String value);

  /**
   * Returns the value of the '<em><b>Text</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Text</em>' attribute.
   * @see #setText(String)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getComment_Text()
   * @model
   * @generated
   */
  String getText();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.Comment#getText <em>Text</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Text</em>' attribute.
   * @see #getText()
   * @generated
   */
  void setText(String value);

  /**
   * Returns the value of the '<em><b>Status</b></em>' attribute.
   * The default value is <code>"None"</code>.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.lm.reviews.CommentStatus}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Status</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.reviews.CommentStatus
   * @see #setStatus(CommentStatus)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getComment_Status()
   * @model default="None"
   * @generated
   */
  CommentStatus getStatus();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.Comment#getStatus <em>Status</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Status</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.reviews.CommentStatus
   * @see #getStatus()
   * @generated
   */
  void setStatus(CommentStatus value);

} // Comment
