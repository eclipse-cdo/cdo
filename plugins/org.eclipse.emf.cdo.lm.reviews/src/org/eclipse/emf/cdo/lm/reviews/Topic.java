/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Topic</b></em>'.
 * @since 1.2
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Topic#isHeading <em>Heading</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Topic#getModelReference <em>Model Reference</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Topic#getStatus <em>Status</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Topic#getContainer <em>Container</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Topic#getParentHeading <em>Parent Heading</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Topic#getPreviousHeading <em>Previous Heading</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Topic#getNextHeading <em>Next Heading</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Topic#getOutlineNumber <em>Outline Number</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Topic#getParentIndex <em>Parent Index</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getTopic()
 * @model
 * @generated
 */
public interface Topic extends TopicContainer, Authorable
{
  /**
   * Returns the value of the '<em><b>Heading</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Heading</em>' attribute.
   * @see #setHeading(boolean)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getTopic_Heading()
   * @model
   * @generated
   */
  boolean isHeading();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.Topic#isHeading <em>Heading</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Heading</em>' attribute.
   * @see #isHeading()
   * @generated
   */
  void setHeading(boolean value);

  /**
   * Returns the value of the '<em><b>Model Reference</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Model Reference</em>' attribute.
   * @see #setModelReference(ModelReference)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getTopic_ModelReference()
   * @model dataType="org.eclipse.emf.cdo.lm.reviews.ModelReference"
   * @generated
   */
  ModelReference getModelReference();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.Topic#getModelReference <em>Model Reference</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Model Reference</em>' attribute.
   * @see #getModelReference()
   * @generated
   */
  void setModelReference(ModelReference value);

  /**
   * Returns the value of the '<em><b>Status</b></em>' attribute.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.lm.reviews.TopicStatus}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Status</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.reviews.TopicStatus
   * @see #setStatus(TopicStatus)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getTopic_Status()
   * @model
   * @generated
   */
  TopicStatus getStatus();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.Topic#getStatus <em>Status</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Status</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.reviews.TopicStatus
   * @see #getStatus()
   * @generated
   */
  void setStatus(TopicStatus value);

  /**
   * Returns the value of the '<em><b>Container</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.reviews.TopicContainer#getTopics <em>Topics</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Container</em>' container reference.
   * @see #setContainer(TopicContainer)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getTopic_Container()
   * @see org.eclipse.emf.cdo.lm.reviews.TopicContainer#getTopics
   * @model opposite="topics" required="true" transient="false"
   * @generated
   */
  TopicContainer getContainer();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.Topic#getContainer <em>Container</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Container</em>' container reference.
   * @see #getContainer()
   * @generated
   */
  void setContainer(TopicContainer value);

  /**
   * Returns the value of the '<em><b>Parent Heading</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Parent Heading</em>' reference.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getTopic_ParentHeading()
   * @model resolveProxies="false" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Topic getParentHeading();

  /**
   * Returns the value of the '<em><b>Previous Heading</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Previous Heading</em>' reference.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getTopic_PreviousHeading()
   * @model resolveProxies="false" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Topic getPreviousHeading();

  /**
   * Returns the value of the '<em><b>Next Heading</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Next Heading</em>' reference.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getTopic_NextHeading()
   * @model resolveProxies="false" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Topic getNextHeading();

  /**
   * Returns the value of the '<em><b>Outline Number</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Outline Number</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getTopic_OutlineNumber()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  String getOutlineNumber();

  /**
   * Returns the value of the '<em><b>Parent Index</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Parent Index</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getTopic_ParentIndex()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  int getParentIndex();

} // Topic
