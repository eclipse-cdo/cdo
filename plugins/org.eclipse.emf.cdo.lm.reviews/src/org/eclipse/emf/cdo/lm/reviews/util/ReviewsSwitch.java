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
package org.eclipse.emf.cdo.lm.reviews.util;

import org.eclipse.emf.cdo.etypes.ModelElement;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.FixedBaseline;
import org.eclipse.emf.cdo.lm.FloatingBaseline;
import org.eclipse.emf.cdo.lm.ModuleElement;
import org.eclipse.emf.cdo.lm.StreamElement;
import org.eclipse.emf.cdo.lm.SystemElement;
import org.eclipse.emf.cdo.lm.reviews.Authorable;
import org.eclipse.emf.cdo.lm.reviews.Comment;
import org.eclipse.emf.cdo.lm.reviews.DeliveryReview;
import org.eclipse.emf.cdo.lm.reviews.DropReview;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewTemplate;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;
import org.eclipse.emf.cdo.lm.reviews.Topic;
import org.eclipse.emf.cdo.lm.reviews.TopicContainer;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage
 * @generated
 */
public class ReviewsSwitch<T> extends Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static ReviewsPackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ReviewsSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = ReviewsPackage.eINSTANCE;
    }
  }

  /**
   * Checks whether this is a switch for the given package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param ePackage the package in question.
   * @return whether this is a switch for the given package.
   * @generated
   */
  @Override
  protected boolean isSwitchFor(EPackage ePackage)
  {
    return ePackage == modelPackage;
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  @Override
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
    case ReviewsPackage.AUTHORABLE:
    {
      Authorable authorable = (Authorable)theEObject;
      T result = caseAuthorable(authorable);
      if (result == null)
      {
        result = caseSystemElement(authorable);
      }
      if (result == null)
      {
        result = caseModelElement(authorable);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ReviewsPackage.TOPIC_CONTAINER:
    {
      TopicContainer topicContainer = (TopicContainer)theEObject;
      T result = caseTopicContainer(topicContainer);
      if (result == null)
      {
        result = caseSystemElement(topicContainer);
      }
      if (result == null)
      {
        result = caseModelElement(topicContainer);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ReviewsPackage.TOPIC:
    {
      Topic topic = (Topic)theEObject;
      T result = caseTopic(topic);
      if (result == null)
      {
        result = caseTopicContainer(topic);
      }
      if (result == null)
      {
        result = caseAuthorable(topic);
      }
      if (result == null)
      {
        result = caseSystemElement(topic);
      }
      if (result == null)
      {
        result = caseModelElement(topic);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ReviewsPackage.COMMENT:
    {
      Comment comment = (Comment)theEObject;
      T result = caseComment(comment);
      if (result == null)
      {
        result = caseAuthorable(comment);
      }
      if (result == null)
      {
        result = caseSystemElement(comment);
      }
      if (result == null)
      {
        result = caseModelElement(comment);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ReviewsPackage.REVIEW_TEMPLATE:
    {
      ReviewTemplate reviewTemplate = (ReviewTemplate)theEObject;
      T result = caseReviewTemplate(reviewTemplate);
      if (result == null)
      {
        result = caseTopicContainer(reviewTemplate);
      }
      if (result == null)
      {
        result = caseSystemElement(reviewTemplate);
      }
      if (result == null)
      {
        result = caseModelElement(reviewTemplate);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ReviewsPackage.REVIEW:
    {
      Review review = (Review)theEObject;
      T result = caseReview(review);
      if (result == null)
      {
        result = caseBaseline(review);
      }
      if (result == null)
      {
        result = caseTopicContainer(review);
      }
      if (result == null)
      {
        result = caseStreamElement(review);
      }
      if (result == null)
      {
        result = caseModuleElement(review);
      }
      if (result == null)
      {
        result = caseSystemElement(review);
      }
      if (result == null)
      {
        result = caseModelElement(review);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ReviewsPackage.DELIVERY_REVIEW:
    {
      DeliveryReview deliveryReview = (DeliveryReview)theEObject;
      T result = caseDeliveryReview(deliveryReview);
      if (result == null)
      {
        result = caseReview(deliveryReview);
      }
      if (result == null)
      {
        result = caseFloatingBaseline(deliveryReview);
      }
      if (result == null)
      {
        result = caseBaseline(deliveryReview);
      }
      if (result == null)
      {
        result = caseTopicContainer(deliveryReview);
      }
      if (result == null)
      {
        result = caseStreamElement(deliveryReview);
      }
      if (result == null)
      {
        result = caseModuleElement(deliveryReview);
      }
      if (result == null)
      {
        result = caseSystemElement(deliveryReview);
      }
      if (result == null)
      {
        result = caseModelElement(deliveryReview);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case ReviewsPackage.DROP_REVIEW:
    {
      DropReview dropReview = (DropReview)theEObject;
      T result = caseDropReview(dropReview);
      if (result == null)
      {
        result = caseReview(dropReview);
      }
      if (result == null)
      {
        result = caseFixedBaseline(dropReview);
      }
      if (result == null)
      {
        result = caseBaseline(dropReview);
      }
      if (result == null)
      {
        result = caseTopicContainer(dropReview);
      }
      if (result == null)
      {
        result = caseStreamElement(dropReview);
      }
      if (result == null)
      {
        result = caseModuleElement(dropReview);
      }
      if (result == null)
      {
        result = caseSystemElement(dropReview);
      }
      if (result == null)
      {
        result = caseModelElement(dropReview);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    default:
      return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Authorable</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * @since 1.2
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Authorable</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAuthorable(Authorable object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Topic Container</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Topic Container</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTopicContainer(TopicContainer object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Comment</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Comment</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseComment(Comment object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Topic</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Topic</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTopic(Topic object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Review Template</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Review Template</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseReviewTemplate(ReviewTemplate object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Review</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Review</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseReview(Review object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Delivery Review</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Delivery Review</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDeliveryReview(DeliveryReview object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Drop Review</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Drop Review</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDropReview(DropReview object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Model Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Model Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseModelElement(ModelElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>System Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>System Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSystemElement(SystemElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Module Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Module Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseModuleElement(ModuleElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Stream Element</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Stream Element</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStreamElement(StreamElement object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Baseline</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Baseline</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseBaseline(Baseline object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Floating Baseline</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Floating Baseline</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFloatingBaseline(FloatingBaseline object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Fixed Baseline</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Fixed Baseline</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFixedBaseline(FixedBaseline object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last case anyway.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  @Override
  public T defaultCase(EObject object)
  {
    return null;
  }

} // ReviewsSwitch
