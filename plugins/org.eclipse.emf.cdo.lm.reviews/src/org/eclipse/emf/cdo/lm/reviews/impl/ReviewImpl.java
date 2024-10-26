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
package org.eclipse.emf.cdo.lm.reviews.impl;

import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.impl.BaselineImpl;
import org.eclipse.emf.cdo.lm.impl.ExtendedBaseline;
import org.eclipse.emf.cdo.lm.reviews.Comment;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewStatus;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;
import org.eclipse.emf.cdo.lm.reviews.Topic;
import org.eclipse.emf.cdo.lm.reviews.TopicContainer;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Review</b></em>'.
 * @extends ExtendedBaseline
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewImpl#getReview <em>Review</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewImpl#getTopics <em>Topics</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewImpl#getComments <em>Comments</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewImpl#getTopicCount <em>Topic Count</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewImpl#getUnresolvedCount <em>Unresolved Count</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewImpl#getResolvedCount <em>Resolved Count</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewImpl#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewImpl#getReviewers <em>Reviewers</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewImpl#getStatus <em>Status</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class ReviewImpl extends BaselineImpl implements Review, ExtendedBaseline
{
  /**
   * The default value of the '{@link #getTopicCount() <em>Topic Count</em>}' attribute.
   * <!-- begin-user-doc -->
   * @since 1.2
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
   * The default value of the '{@link #getId() <em>Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getId()
   * @generated
   * @ordered
   */
  protected static final int ID_EDEFAULT = 0;

  /**
   * The default value of the '{@link #getAuthor() <em>Author</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAuthor()
   * @generated
   * @ordered
   */
  protected static final String AUTHOR_EDEFAULT = null;

  /**
   * The default value of the '{@link #getStatus() <em>Status</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStatus()
   * @generated
   * @ordered
   */
  protected static final ReviewStatus STATUS_EDEFAULT = ReviewStatus.NEW;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ReviewImpl()
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
    return ReviewsPackage.Literals.REVIEW;
  }

  @Override
  public Stream getStream()
  {
    EObject container = eContainer();

    while (container != null)
    {
      if (container instanceof Stream)
      {
        return (Stream)container;
      }

      container = container.eContainer();
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Review getReview()
  {
    return this;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public EList<Comment> getComments()
  {
    return (EList<Comment>)eDynamicGet(ReviewsPackage.REVIEW__COMMENTS, ReviewsPackage.Literals.TOPIC_CONTAINER__COMMENTS, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public int getTopicCount()
  {
    return TopicContainerImpl.getTopicCount(this);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public int getUnresolvedCount()
  {
    return TopicContainerImpl.getUnresolvedCount(this);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public int getResolvedCount()
  {
    return TopicContainerImpl.getResolvedCount(this);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getId()
  {
    return (Integer)eDynamicGet(ReviewsPackage.REVIEW__ID, ReviewsPackage.Literals.REVIEW__ID, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setId(int newId)
  {
    eDynamicSet(ReviewsPackage.REVIEW__ID, ReviewsPackage.Literals.REVIEW__ID, newId);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getAuthor()
  {
    return (String)eDynamicGet(ReviewsPackage.REVIEW__AUTHOR, ReviewsPackage.Literals.REVIEW__AUTHOR, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAuthor(String newAuthor)
  {
    eDynamicSet(ReviewsPackage.REVIEW__AUTHOR, ReviewsPackage.Literals.REVIEW__AUTHOR, newAuthor);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public EList<String> getReviewers()
  {
    return (EList<String>)eDynamicGet(ReviewsPackage.REVIEW__REVIEWERS, ReviewsPackage.Literals.REVIEW__REVIEWERS, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ReviewStatus getStatus()
  {
    return (ReviewStatus)eDynamicGet(ReviewsPackage.REVIEW__STATUS, ReviewsPackage.Literals.REVIEW__STATUS, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setStatus(ReviewStatus newStatus)
  {
    eDynamicSet(ReviewsPackage.REVIEW__STATUS, ReviewsPackage.Literals.REVIEW__STATUS, newStatus);
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
    return (EList<Topic>)eDynamicGet(ReviewsPackage.REVIEW__TOPICS, ReviewsPackage.Literals.TOPIC_CONTAINER__TOPICS, true, true);
  }

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
    case ReviewsPackage.REVIEW__TOPICS:
      return ((InternalEList<InternalEObject>)(InternalEList<?>)getTopics()).basicAdd(otherEnd, msgs);
    case ReviewsPackage.REVIEW__COMMENTS:
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
    case ReviewsPackage.REVIEW__TOPICS:
      return ((InternalEList<?>)getTopics()).basicRemove(otherEnd, msgs);
    case ReviewsPackage.REVIEW__COMMENTS:
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
    case ReviewsPackage.REVIEW__REVIEW:
      return getReview();
    case ReviewsPackage.REVIEW__TOPICS:
      return getTopics();
    case ReviewsPackage.REVIEW__COMMENTS:
      return getComments();
    case ReviewsPackage.REVIEW__TOPIC_COUNT:
      return getTopicCount();
    case ReviewsPackage.REVIEW__UNRESOLVED_COUNT:
      return getUnresolvedCount();
    case ReviewsPackage.REVIEW__RESOLVED_COUNT:
      return getResolvedCount();
    case ReviewsPackage.REVIEW__ID:
      return getId();
    case ReviewsPackage.REVIEW__AUTHOR:
      return getAuthor();
    case ReviewsPackage.REVIEW__REVIEWERS:
      return getReviewers();
    case ReviewsPackage.REVIEW__STATUS:
      return getStatus();
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
    case ReviewsPackage.REVIEW__TOPICS:
      getTopics().clear();
      getTopics().addAll((Collection<? extends Topic>)newValue);
      return;
    case ReviewsPackage.REVIEW__COMMENTS:
      getComments().clear();
      getComments().addAll((Collection<? extends Comment>)newValue);
      return;
    case ReviewsPackage.REVIEW__ID:
      setId((Integer)newValue);
      return;
    case ReviewsPackage.REVIEW__AUTHOR:
      setAuthor((String)newValue);
      return;
    case ReviewsPackage.REVIEW__REVIEWERS:
      getReviewers().clear();
      getReviewers().addAll((Collection<? extends String>)newValue);
      return;
    case ReviewsPackage.REVIEW__STATUS:
      setStatus((ReviewStatus)newValue);
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
    case ReviewsPackage.REVIEW__TOPICS:
      getTopics().clear();
      return;
    case ReviewsPackage.REVIEW__COMMENTS:
      getComments().clear();
      return;
    case ReviewsPackage.REVIEW__ID:
      setId(ID_EDEFAULT);
      return;
    case ReviewsPackage.REVIEW__AUTHOR:
      setAuthor(AUTHOR_EDEFAULT);
      return;
    case ReviewsPackage.REVIEW__REVIEWERS:
      getReviewers().clear();
      return;
    case ReviewsPackage.REVIEW__STATUS:
      setStatus(STATUS_EDEFAULT);
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
    case ReviewsPackage.REVIEW__REVIEW:
      return getReview() != null;
    case ReviewsPackage.REVIEW__TOPICS:
      return !getTopics().isEmpty();
    case ReviewsPackage.REVIEW__COMMENTS:
      return !getComments().isEmpty();
    case ReviewsPackage.REVIEW__TOPIC_COUNT:
      return getTopicCount() != TOPIC_COUNT_EDEFAULT;
    case ReviewsPackage.REVIEW__UNRESOLVED_COUNT:
      return getUnresolvedCount() != UNRESOLVED_COUNT_EDEFAULT;
    case ReviewsPackage.REVIEW__RESOLVED_COUNT:
      return getResolvedCount() != RESOLVED_COUNT_EDEFAULT;
    case ReviewsPackage.REVIEW__ID:
      return getId() != ID_EDEFAULT;
    case ReviewsPackage.REVIEW__AUTHOR:
      return AUTHOR_EDEFAULT == null ? getAuthor() != null : !AUTHOR_EDEFAULT.equals(getAuthor());
    case ReviewsPackage.REVIEW__REVIEWERS:
      return !getReviewers().isEmpty();
    case ReviewsPackage.REVIEW__STATUS:
      return getStatus() != STATUS_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass)
  {
    if (baseClass == TopicContainer.class)
    {
      switch (derivedFeatureID)
      {
      case ReviewsPackage.REVIEW__REVIEW:
        return ReviewsPackage.TOPIC_CONTAINER__REVIEW;
      case ReviewsPackage.REVIEW__TOPICS:
        return ReviewsPackage.TOPIC_CONTAINER__TOPICS;
      case ReviewsPackage.REVIEW__COMMENTS:
        return ReviewsPackage.TOPIC_CONTAINER__COMMENTS;
      case ReviewsPackage.REVIEW__TOPIC_COUNT:
        return ReviewsPackage.TOPIC_CONTAINER__TOPIC_COUNT;
      case ReviewsPackage.REVIEW__UNRESOLVED_COUNT:
        return ReviewsPackage.TOPIC_CONTAINER__UNRESOLVED_COUNT;
      case ReviewsPackage.REVIEW__RESOLVED_COUNT:
        return ReviewsPackage.TOPIC_CONTAINER__RESOLVED_COUNT;
      default:
        return -1;
      }
    }
    return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass)
  {
    if (baseClass == TopicContainer.class)
    {
      switch (baseFeatureID)
      {
      case ReviewsPackage.TOPIC_CONTAINER__REVIEW:
        return ReviewsPackage.REVIEW__REVIEW;
      case ReviewsPackage.TOPIC_CONTAINER__TOPICS:
        return ReviewsPackage.REVIEW__TOPICS;
      case ReviewsPackage.TOPIC_CONTAINER__COMMENTS:
        return ReviewsPackage.REVIEW__COMMENTS;
      case ReviewsPackage.TOPIC_CONTAINER__TOPIC_COUNT:
        return ReviewsPackage.REVIEW__TOPIC_COUNT;
      case ReviewsPackage.TOPIC_CONTAINER__UNRESOLVED_COUNT:
        return ReviewsPackage.REVIEW__UNRESOLVED_COUNT;
      case ReviewsPackage.TOPIC_CONTAINER__RESOLVED_COUNT:
        return ReviewsPackage.REVIEW__RESOLVED_COUNT;
      default:
        return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

} // ReviewImpl
