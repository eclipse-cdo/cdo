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
package org.eclipse.emf.cdo.lm.reviews.impl;

import org.eclipse.emf.cdo.etypes.impl.ModelElementImpl;
import org.eclipse.emf.cdo.lm.reviews.Comment;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;
import org.eclipse.emf.cdo.lm.reviews.Topic;
import org.eclipse.emf.cdo.lm.reviews.TopicContainer;
import org.eclipse.emf.cdo.lm.reviews.TopicStatus;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Commentable</b></em>'.
 * @since 1.2
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicContainerImpl#getReview <em>Review</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicContainerImpl#getTopics <em>Topics</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicContainerImpl#getComments <em>Comments</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicContainerImpl#getTopicCount <em>Topic Count</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicContainerImpl#getUnresolvedCount <em>Unresolved Count</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicContainerImpl#getResolvedCount <em>Resolved Count</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class TopicContainerImpl extends ModelElementImpl implements TopicContainer
{
  /**
   * The default value of the '{@link #getTopicCount() <em>Topic Count</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getTopicCount()
   * @generated
   * @ordered
   */
  protected static final int TOPIC_COUNT_EDEFAULT = 0;

  /**
   * The default value of the '{@link #getUnresolvedCount() <em>Unresolved Count</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUnresolvedCount()
   * @generated
   * @ordered
   */
  protected static final int UNRESOLVED_COUNT_EDEFAULT = 0;

  /**
   * The default value of the '{@link #getResolvedCount() <em>Resolved Count</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getResolvedCount()
   * @generated
   * @ordered
   */
  protected static final int RESOLVED_COUNT_EDEFAULT = 0;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected TopicContainerImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return ReviewsPackage.Literals.TOPIC_CONTAINER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public abstract Review getReview();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Comment> getComments()
  {
    return (EList<Comment>)eDynamicGet(ReviewsPackage.TOPIC_CONTAINER__COMMENTS, ReviewsPackage.Literals.TOPIC_CONTAINER__COMMENTS, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public int getTopicCount()
  {
    return getTopicCount(this);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public int getUnresolvedCount()
  {
    return getUnresolvedCount(this);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public int getResolvedCount()
  {
    return getResolvedCount(this);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public EList<Topic> getTopics()
  {
    return (EList<Topic>)eDynamicGet(ReviewsPackage.TOPIC_CONTAINER__TOPICS, ReviewsPackage.Literals.TOPIC_CONTAINER__TOPICS, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public abstract org.eclipse.emf.cdo.lm.System getSystem();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case ReviewsPackage.TOPIC_CONTAINER__TOPICS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getTopics()).basicAdd(otherEnd, msgs);
    case ReviewsPackage.TOPIC_CONTAINER__COMMENTS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getComments()).basicAdd(otherEnd, msgs);
    }
    return super.eInverseAdd(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case ReviewsPackage.TOPIC_CONTAINER__TOPICS:
      return ((InternalEList<?>)getTopics()).basicRemove(otherEnd, msgs);
    case ReviewsPackage.TOPIC_CONTAINER__COMMENTS:
      return ((InternalEList<?>)getComments()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case ReviewsPackage.TOPIC_CONTAINER__REVIEW:
      return getReview();
    case ReviewsPackage.TOPIC_CONTAINER__TOPICS:
      return getTopics();
    case ReviewsPackage.TOPIC_CONTAINER__COMMENTS:
      return getComments();
    case ReviewsPackage.TOPIC_CONTAINER__TOPIC_COUNT:
      return getTopicCount();
    case ReviewsPackage.TOPIC_CONTAINER__UNRESOLVED_COUNT:
      return getUnresolvedCount();
    case ReviewsPackage.TOPIC_CONTAINER__RESOLVED_COUNT:
      return getResolvedCount();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case ReviewsPackage.TOPIC_CONTAINER__TOPICS:
      getTopics().clear();
      getTopics().addAll((Collection<? extends Topic>)newValue);
      return;
    case ReviewsPackage.TOPIC_CONTAINER__COMMENTS:
      getComments().clear();
      getComments().addAll((Collection<? extends Comment>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case ReviewsPackage.TOPIC_CONTAINER__TOPICS:
      getTopics().clear();
      return;
    case ReviewsPackage.TOPIC_CONTAINER__COMMENTS:
      getComments().clear();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case ReviewsPackage.TOPIC_CONTAINER__REVIEW:
      return getReview() != null;
    case ReviewsPackage.TOPIC_CONTAINER__TOPICS:
      return !getTopics().isEmpty();
    case ReviewsPackage.TOPIC_CONTAINER__COMMENTS:
      return !getComments().isEmpty();
    case ReviewsPackage.TOPIC_CONTAINER__TOPIC_COUNT:
      return getTopicCount() != TOPIC_COUNT_EDEFAULT;
    case ReviewsPackage.TOPIC_CONTAINER__UNRESOLVED_COUNT:
      return getUnresolvedCount() != UNRESOLVED_COUNT_EDEFAULT;
    case ReviewsPackage.TOPIC_CONTAINER__RESOLVED_COUNT:
      return getResolvedCount() != RESOLVED_COUNT_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
    case ReviewsPackage.TOPIC_CONTAINER___GET_SYSTEM:
      return getSystem();
    }
    return super.eInvoke(operationID, arguments);
  }

  public static TopicStatistics getTopicStatistics(TopicContainer container)
  {
    TopicStatistics statistics = new TopicStatistics();
    updateCommentStatistics(statistics, container);
    return statistics;
  }

  static void updateCommentStatistics(TopicStatistics statistics, TopicContainer container)
  {
    if (container instanceof Topic)
    {
      Topic topic = (Topic)container;

      TopicStatus status = topic.getStatus();
      if (status == TopicStatus.UNRESOLVED)
      {
        statistics.incUnresolvedCount();
      }
      else if (status == TopicStatus.RESOLVED)
      {
        statistics.incResolvedCount();
      }
      else
      {
        statistics.incNoneCount();
      }
    }

    for (Topic topic : container.getTopics())
    {
      updateCommentStatistics(statistics, topic);
    }
  }

  static int getTopicCount(TopicContainer container)
  {
    TopicStatistics statistics = getTopicStatistics(container);
    return statistics.getTotalCount();
  }

  static int getUnresolvedCount(TopicContainer container)
  {
    TopicStatistics statistics = getTopicStatistics(container);
    return statistics.getUnresolvedCount();
  }

  static int getResolvedCount(TopicContainer container)
  {
    TopicStatistics statistics = getTopicStatistics(container);
    return statistics.getResolvedCount();
  }

  /**
   * @author Eike Stepper
   */
  public static final class TopicStatistics
  {
    private int noneCount;

    private int unresolvedCount;

    private int resolvedCount;

    private TopicStatistics()
    {
    }

    private void incNoneCount()
    {
      ++noneCount;
    }

    private void incUnresolvedCount()
    {
      ++unresolvedCount;
    }

    private void incResolvedCount()
    {
      ++resolvedCount;
    }

    public int getTotalCount()
    {
      return noneCount + unresolvedCount + resolvedCount;
    }

    public int getNoneCount()
    {
      return noneCount;
    }

    public int getUnresolvedCount()
    {
      return unresolvedCount;
    }

    public int getResolvedCount()
    {
      return resolvedCount;
    }
  }

} // CommentableImpl
