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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Heading</b></em>'.
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Heading#getPreviousHeading <em>Previous Heading</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Heading#getNextHeading <em>Next Heading</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Heading#getParentIndex <em>Parent Index</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Heading#getOutlineNumber <em>Outline Number</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getHeading()
 * @model
 * @generated
 */
public interface Heading extends Comment
{

  /**
   * Returns the value of the '<em><b>Previous Heading</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Previous Heading</em>' reference.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getHeading_PreviousHeading()
   * @model resolveProxies="false" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Heading getPreviousHeading();

  /**
   * Returns the value of the '<em><b>Next Heading</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Next Heading</em>' reference.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getHeading_NextHeading()
   * @model resolveProxies="false" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Heading getNextHeading();

  /**
   * Returns the value of the '<em><b>Parent Index</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Parent Index</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getHeading_ParentIndex()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  int getParentIndex();

  /**
   * Returns the value of the '<em><b>Outline Number</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Outline Number</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getHeading_OutlineNumber()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  String getOutlineNumber();
} // Heading
