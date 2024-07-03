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

import org.eclipse.emf.cdo.lm.Delivery;
import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.FixedBaseline;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Drop Review</b></em>'.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.DropReview#getDelivery <em>Delivery</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.DropReview#getTargetTimeStamp <em>Target Time Stamp</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.DropReview#getDropType <em>Drop Type</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.DropReview#getDropLabel <em>Drop Label</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getDropReview()
 * @model
 * @generated
 */
public interface DropReview extends Review, FixedBaseline
{
  /**
   * Returns the value of the '<em><b>Delivery</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Delivery</em>' reference.
   * @see #setDelivery(Delivery)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getDropReview_Delivery()
   * @model resolveProxies="false" required="true"
   * @generated
   */
  Delivery getDelivery();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.DropReview#getDelivery <em>Delivery</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Delivery</em>' reference.
   * @see #getDelivery()
   * @generated
   */
  void setDelivery(Delivery value);

  /**
   * Returns the value of the '<em><b>Target Time Stamp</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Target Time Stamp</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getDropReview_TargetTimeStamp()
   * @model required="true" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  long getTargetTimeStamp();

  /**
   * Returns the value of the '<em><b>Drop Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Drop Type</em>' reference.
   * @see #setDropType(DropType)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getDropReview_DropType()
   * @model resolveProxies="false" required="true"
   * @generated
   */
  DropType getDropType();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.DropReview#getDropType <em>Drop Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Drop Type</em>' reference.
   * @see #getDropType()
   * @generated
   */
  void setDropType(DropType value);

  /**
   * Returns the value of the '<em><b>Drop Label</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Drop Label</em>' attribute.
   * @see #setDropLabel(String)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getDropReview_DropLabel()
   * @model
   * @generated
   */
  String getDropLabel();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.DropReview#getDropLabel <em>Drop Label</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Drop Label</em>' attribute.
   * @see #getDropLabel()
   * @generated
   */
  void setDropLabel(String value);

} // DropReview
