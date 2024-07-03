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

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage
 * @generated
 */
public interface ReviewsFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  ReviewsFactory eINSTANCE = org.eclipse.emf.cdo.lm.reviews.impl.ReviewsFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Comment</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Comment</em>'.
   * @generated
   */
  Comment createComment();

  /**
   * Returns a new object of class '<em>Heading</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Heading</em>'.
   * @generated
   */
  Heading createHeading();

  /**
   * Returns a new object of class '<em>Review Template</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Review Template</em>'.
   * @generated
   */
  ReviewTemplate createReviewTemplate();

  /**
   * Returns a new object of class '<em>Delivery Review</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Delivery Review</em>'.
   * @generated
   */
  DeliveryReview createDeliveryReview();

  /**
   * Returns a new object of class '<em>Drop Review</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Drop Review</em>'.
   * @generated
   */
  DropReview createDropReview();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  ReviewsPackage getReviewsPackage();

} // ReviewsFactory
