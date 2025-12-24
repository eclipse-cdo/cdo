/*
 * Copyright (c) 2023, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.reviews.impl;

import org.eclipse.emf.cdo.lm.reviews.Comment;
import org.eclipse.emf.cdo.lm.reviews.DeliveryReview;
import org.eclipse.emf.cdo.lm.reviews.DropReview;
import org.eclipse.emf.cdo.lm.reviews.ModelReference;
import org.eclipse.emf.cdo.lm.reviews.ReviewStatus;
import org.eclipse.emf.cdo.lm.reviews.ReviewTemplate;
import org.eclipse.emf.cdo.lm.reviews.ReviewsFactory;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;
import org.eclipse.emf.cdo.lm.reviews.Topic;
import org.eclipse.emf.cdo.lm.reviews.TopicStatus;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * @generated
 */
public class ReviewsFactoryImpl extends EFactoryImpl implements ReviewsFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static ReviewsFactory init()
  {
    try
    {
      ReviewsFactory theReviewsFactory = (ReviewsFactory)EPackage.Registry.INSTANCE.getEFactory(ReviewsPackage.eNS_URI);
      if (theReviewsFactory != null)
      {
        return theReviewsFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new ReviewsFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ReviewsFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case ReviewsPackage.TOPIC:
      return createTopic();
    case ReviewsPackage.COMMENT:
      return createComment();
    case ReviewsPackage.REVIEW_TEMPLATE:
      return createReviewTemplate();
    case ReviewsPackage.DELIVERY_REVIEW:
      return createDeliveryReview();
    case ReviewsPackage.DROP_REVIEW:
      return createDropReview();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
    case ReviewsPackage.REVIEW_STATUS:
      return createReviewStatusFromString(eDataType, initialValue);
    case ReviewsPackage.TOPIC_STATUS:
      return createTopicStatusFromString(eDataType, initialValue);
    case ReviewsPackage.MODEL_REFERENCE:
      return createModelReferenceFromString(eDataType, initialValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
    case ReviewsPackage.REVIEW_STATUS:
      return convertReviewStatusToString(eDataType, instanceValue);
    case ReviewsPackage.TOPIC_STATUS:
      return convertTopicStatusToString(eDataType, instanceValue);
    case ReviewsPackage.MODEL_REFERENCE:
      return convertModelReferenceToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Comment createComment()
  {
    CommentImpl comment = new CommentImpl();
    return comment;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Topic createTopic()
  {
    TopicImpl topic = new TopicImpl();
    return topic;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ReviewTemplate createReviewTemplate()
  {
    ReviewTemplateImpl reviewTemplate = new ReviewTemplateImpl();
    return reviewTemplate;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public DeliveryReview createDeliveryReview()
  {
    DeliveryReviewImpl deliveryReview = new DeliveryReviewImpl();
    return deliveryReview;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public DropReview createDropReview()
  {
    DropReviewImpl dropReview = new DropReviewImpl();
    return dropReview;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @generated
   */
  public TopicStatus createTopicStatusFromString(EDataType eDataType, String initialValue)
  {
    TopicStatus result = TopicStatus.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertTopicStatusToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @generated
   */
  public ModelReference createModelReferenceFromString(EDataType eDataType, String initialValue)
  {
    return (ModelReference)super.createFromString(eDataType, initialValue);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertModelReferenceToString(EDataType eDataType, Object instanceValue)
  {
    return super.convertToString(eDataType, instanceValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ReviewStatus createReviewStatusFromString(EDataType eDataType, String initialValue)
  {
    ReviewStatus result = ReviewStatus.get(initialValue);
    if (result == null)
    {
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    }
    return result;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String convertReviewStatusToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ReviewsPackage getReviewsPackage()
  {
    return (ReviewsPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static ReviewsPackage getPackage()
  {
    return ReviewsPackage.eINSTANCE;
  }

} // ReviewsFactoryImpl
