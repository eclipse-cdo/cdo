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

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Commentable</b></em>'.
 * @since 1.2
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.TopicContainer#getReview <em>Review</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.TopicContainer#getTopics <em>Topics</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.TopicContainer#getComments <em>Comments</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.TopicContainer#getTopicCount <em>Topic Count</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.TopicContainer#getUnresolvedCount <em>Unresolved Count</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.TopicContainer#getResolvedCount <em>Resolved Count</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getTopicContainer()
 * @model abstract="true"
 * @generated
 */
public interface TopicContainer extends SystemElement
{
  /**
   * Returns the value of the '<em><b>Review</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Review</em>' reference.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getTopicContainer_Review()
   * @model resolveProxies="false" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Review getReview();

  /**
   * Returns the value of the '<em><b>Comments</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.lm.reviews.Comment}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.reviews.Comment#getContainer <em>Container</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Comments</em>' containment reference list.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getTopicContainer_Comments()
   * @see org.eclipse.emf.cdo.lm.reviews.Comment#getContainer
   * @model opposite="container" containment="true"
   * @generated
   */
  EList<Comment> getComments();

  /**
   * Returns the value of the '<em><b>Topic Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Topic Count</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getTopicContainer_TopicCount()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  int getTopicCount();

  /**
   * Returns the value of the '<em><b>Unresolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Unresolved Count</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getTopicContainer_UnresolvedCount()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  int getUnresolvedCount();

  /**
   * Returns the value of the '<em><b>Resolved Count</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Resolved Count</em>' attribute.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getTopicContainer_ResolvedCount()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  int getResolvedCount();

  /**
   * Returns the value of the '<em><b>Topics</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.lm.reviews.Topic}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.reviews.Topic#getContainer <em>Container</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Topics</em>' containment reference list.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getTopicContainer_Topics()
   * @see org.eclipse.emf.cdo.lm.reviews.Topic#getContainer
   * @model opposite="container" containment="true"
   * @generated
   */
  EList<Topic> getTopics();

  public default EList<Topic> getTopics(ModelReference modelReference)
  {
    EList<Topic> result = new BasicEList<>();
    forEachTopic(topic -> {
      if (Objects.equals(topic.getModelReference(), modelReference))
      {
        result.add(topic);
      }

      return true;
    });

    return result;
  }

  public default Topic getTopic(ModelReference modelReference)
  {
    Topic[] result = { null };
    forEachTopic(topic -> {
      if (Objects.equals(topic.getModelReference(), modelReference))
      {
        result[0] = topic;
        return false;
      }

      return true;
    });

    return result[0];
  }

  public default boolean forEachTopic(Predicate<Topic> consumer)
  {
    for (Topic topic : getTopics())
    {
      if (!topic.forEachTopic(consumer))
      {
        return false;
      }
    }

    return true;
  }

  public default boolean forEachComment(Predicate<Comment> consumer)
  {
    for (Comment comment : getComments())
    {
      if (!consumer.test(comment))
      {
        return false;
      }
    }

    return forEachTopic(topic -> topic.forEachComment(consumer));
  }

} // Commentable
