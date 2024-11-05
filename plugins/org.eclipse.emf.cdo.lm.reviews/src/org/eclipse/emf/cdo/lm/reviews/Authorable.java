/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Authorable</b></em>'.
 * @since 1.2
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Authorable#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Authorable#getText <em>Text</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Authorable#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Authorable#getCreationTime <em>Creation Time</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Authorable#getEditTime <em>Edit Time</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getAuthorable()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface Authorable extends SystemElement
{
  /**
   * Returns the value of the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Id</em>' attribute.
   * @see #setId(int)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getAuthorable_Id()
   * @model required="true"
   * @generated
   */
  int getId();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.Authorable#getId <em>Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Id</em>' attribute.
   * @see #getId()
   * @generated
   */
  void setId(int value);

  /**
   * Returns the value of the '<em><b>Text</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Text</em>' attribute.
   * @see #setText(String)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getAuthorable_Text()
   * @model required="true"
   * @generated
   */
  String getText();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.Authorable#getText <em>Text</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Text</em>' attribute.
   * @see #getText()
   * @generated
   */
  void setText(String value);

  /**
   * Returns the value of the '<em><b>Author</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Author</em>' attribute.
   * @see #setAuthor(String)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getAuthorable_Author()
   * @model required="true"
   * @generated
   */
  String getAuthor();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.Authorable#getAuthor <em>Author</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Author</em>' attribute.
   * @see #getAuthor()
   * @generated
   */
  void setAuthor(String value);

  /**
   * Returns the value of the '<em><b>Creation Time</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Creation Time</em>' attribute.
   * @see #setCreationTime(long)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getAuthorable_CreationTime()
   * @model
   * @generated
   */
  long getCreationTime();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.Authorable#getCreationTime <em>Creation Time</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Creation Time</em>' attribute.
   * @see #getCreationTime()
   * @generated
   */
  void setCreationTime(long value);

  /**
   * Returns the value of the '<em><b>Edit Time</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Edit Time</em>' attribute.
   * @see #setEditTime(long)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getAuthorable_EditTime()
   * @model
   * @generated
   */
  long getEditTime();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.Authorable#getEditTime <em>Edit Time</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Edit Time</em>' attribute.
   * @see #getEditTime()
   * @generated
   */
  void setEditTime(long value);

} // Authorable
