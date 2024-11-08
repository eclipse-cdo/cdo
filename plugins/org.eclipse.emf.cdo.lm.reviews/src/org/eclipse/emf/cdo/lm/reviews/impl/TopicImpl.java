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

import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.reviews.Authorable;
import org.eclipse.emf.cdo.lm.reviews.ModelReference;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;
import org.eclipse.emf.cdo.lm.reviews.Topic;
import org.eclipse.emf.cdo.lm.reviews.TopicContainer;
import org.eclipse.emf.cdo.lm.reviews.TopicStatus;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import java.util.function.Predicate;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Topic</b></em>'.
 * @since 1.2
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicImpl#getText <em>Text</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicImpl#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicImpl#getCreationTime <em>Creation Time</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicImpl#getEditTime <em>Edit Time</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicImpl#isHeading <em>Heading</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicImpl#getModelReference <em>Model Reference</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicImpl#getStatus <em>Status</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicImpl#getContainer <em>Container</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicImpl#getParentHeading <em>Parent Heading</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicImpl#getPreviousHeading <em>Previous Heading</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicImpl#getNextHeading <em>Next Heading</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicImpl#getOutlineNumber <em>Outline Number</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.TopicImpl#getParentIndex <em>Parent Index</em>}</li>
 * </ul>
 *
 * @generated
 */
public class TopicImpl extends TopicContainerImpl implements Topic
{
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
   * The default value of the '{@link #getText() <em>Text</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getText()
   * @generated
   * @ordered
   */
  protected static final String TEXT_EDEFAULT = null;

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
   * The default value of the '{@link #getCreationTime() <em>Creation Time</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCreationTime()
   * @generated
   * @ordered
   */
  protected static final long CREATION_TIME_EDEFAULT = 0L;

  /**
   * The default value of the '{@link #getEditTime() <em>Edit Time</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getEditTime()
   * @generated
   * @ordered
   */
  protected static final long EDIT_TIME_EDEFAULT = 0L;

  /**
   * The default value of the '{@link #isHeading() <em>Heading</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isHeading()
   * @generated
   * @ordered
   */
  protected static final boolean HEADING_EDEFAULT = false;

  /**
   * The default value of the '{@link #getModelReference() <em>Model Reference</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getModelReference()
   * @generated
   * @ordered
   */
  protected static final ModelReference MODEL_REFERENCE_EDEFAULT = null;

  /**
   * The default value of the '{@link #getStatus() <em>Status</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStatus()
   * @generated
   * @ordered
   */
  protected static final TopicStatus STATUS_EDEFAULT = TopicStatus.NONE;

  /**
   * The default value of the '{@link #getOutlineNumber() <em>Outline Number</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOutlineNumber()
   * @generated
   * @ordered
   */
  protected static final String OUTLINE_NUMBER_EDEFAULT = null;

  /**
   * The default value of the '{@link #getParentIndex() <em>Parent Index</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getParentIndex()
   * @generated
   * @ordered
   */
  protected static final int PARENT_INDEX_EDEFAULT = 0;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected TopicImpl()
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
    return ReviewsPackage.Literals.TOPIC;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getId()
  {
    return (Integer)eDynamicGet(ReviewsPackage.TOPIC__ID, ReviewsPackage.Literals.AUTHORABLE__ID, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setId(int newId)
  {
    eDynamicSet(ReviewsPackage.TOPIC__ID, ReviewsPackage.Literals.AUTHORABLE__ID, newId);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getText()
  {
    return (String)eDynamicGet(ReviewsPackage.TOPIC__TEXT, ReviewsPackage.Literals.AUTHORABLE__TEXT, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setText(String newText)
  {
    eDynamicSet(ReviewsPackage.TOPIC__TEXT, ReviewsPackage.Literals.AUTHORABLE__TEXT, newText);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isHeading()
  {
    return (Boolean)eDynamicGet(ReviewsPackage.TOPIC__HEADING, ReviewsPackage.Literals.TOPIC__HEADING, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setHeading(boolean newHeading)
  {
    eDynamicSet(ReviewsPackage.TOPIC__HEADING, ReviewsPackage.Literals.TOPIC__HEADING, newHeading);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ModelReference getModelReference()
  {
    return (ModelReference)eDynamicGet(ReviewsPackage.TOPIC__MODEL_REFERENCE, ReviewsPackage.Literals.TOPIC__MODEL_REFERENCE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setModelReference(ModelReference newModelReference)
  {
    eDynamicSet(ReviewsPackage.TOPIC__MODEL_REFERENCE, ReviewsPackage.Literals.TOPIC__MODEL_REFERENCE, newModelReference);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public TopicStatus getStatus()
  {
    return (TopicStatus)eDynamicGet(ReviewsPackage.TOPIC__STATUS, ReviewsPackage.Literals.TOPIC__STATUS, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setStatus(TopicStatus newStatus)
  {
    eDynamicSet(ReviewsPackage.TOPIC__STATUS, ReviewsPackage.Literals.TOPIC__STATUS, newStatus);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getAuthor()
  {
    return (String)eDynamicGet(ReviewsPackage.TOPIC__AUTHOR, ReviewsPackage.Literals.AUTHORABLE__AUTHOR, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAuthor(String newAuthor)
  {
    eDynamicSet(ReviewsPackage.TOPIC__AUTHOR, ReviewsPackage.Literals.AUTHORABLE__AUTHOR, newAuthor);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public long getCreationTime()
  {
    return (Long)eDynamicGet(ReviewsPackage.TOPIC__CREATION_TIME, ReviewsPackage.Literals.AUTHORABLE__CREATION_TIME, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setCreationTime(long newCreationTime)
  {
    eDynamicSet(ReviewsPackage.TOPIC__CREATION_TIME, ReviewsPackage.Literals.AUTHORABLE__CREATION_TIME, newCreationTime);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public long getEditTime()
  {
    return (Long)eDynamicGet(ReviewsPackage.TOPIC__EDIT_TIME, ReviewsPackage.Literals.AUTHORABLE__EDIT_TIME, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setEditTime(long newEditTime)
  {
    eDynamicSet(ReviewsPackage.TOPIC__EDIT_TIME, ReviewsPackage.Literals.AUTHORABLE__EDIT_TIME, newEditTime);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public TopicContainer getContainer()
  {
    return (TopicContainer)eDynamicGet(ReviewsPackage.TOPIC__CONTAINER, ReviewsPackage.Literals.TOPIC__CONTAINER, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetContainer(TopicContainer newContainer, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newContainer, ReviewsPackage.TOPIC__CONTAINER, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setContainer(TopicContainer newContainer)
  {
    eDynamicSet(ReviewsPackage.TOPIC__CONTAINER, ReviewsPackage.Literals.TOPIC__CONTAINER, newContainer);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Topic getParentHeading()
  {
    TopicContainer container = getContainer();
    while (container != null)
    {
      if (container instanceof Topic)
      {
        Topic topic = (Topic)container;

        if (topic.isHeading())
        {
          return topic;
        }

        container = topic.getContainer();
      }
      else
      {
        break;
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Topic getPreviousHeading()
  {
    TopicContainer container = getContainer();
    if (container == null)
    {
      return null;
    }

    EList<Topic> topics = container.getTopics();
    int index = topics.indexOf(this);
    if (index == -1)
    {
      return null;
    }

    for (int i = index - 1; i >= 0; --i)
    {
      Topic topic = topics.get(i);
      if (topic.isHeading())
      {
        return topic;
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Topic getNextHeading()
  {
    TopicContainer container = getContainer();
    if (container == null)
    {
      return null;
    }

    EList<Topic> topics = container.getTopics();
    int index = topics.indexOf(this);
    if (index == -1)
    {
      return null;
    }

    for (int i = index + 1, size = topics.size(); i < size; ++i)
    {
      Topic topic = topics.get(i);
      if (topic.isHeading())
      {
        return topic;
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public String getOutlineNumber()
  {
    StringBuilder builder = new StringBuilder();
    formatOutlineNumber(builder, this);
    return builder.toString();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public int getParentIndex()
  {
    TopicContainer container = getContainer();
    if (container == null)
    {
      return 1;
    }

    EList<Topic> topics = container.getTopics();
    int index = topics.indexOf(this);
    if (index == -1)
    {
      return 1;
    }

    int count = 1;

    for (int i = 0; i < index; ++i)
    {
      Topic topic = topics.get(i);
      if (topic.isHeading())
      {
        ++count;
      }
    }

    return count;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case ReviewsPackage.TOPIC__CONTAINER:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetContainer((TopicContainer)otherEnd, msgs);
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
    case ReviewsPackage.TOPIC__CONTAINER:
      return basicSetContainer(null, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs)
  {
    switch (eContainerFeatureID())
    {
    case ReviewsPackage.TOPIC__CONTAINER:
      return eInternalContainer().eInverseRemove(this, ReviewsPackage.TOPIC_CONTAINER__TOPICS, TopicContainer.class, msgs);
    }
    return super.eBasicRemoveFromContainerFeature(msgs);
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
    case ReviewsPackage.TOPIC__ID:
      return getId();
    case ReviewsPackage.TOPIC__TEXT:
      return getText();
    case ReviewsPackage.TOPIC__AUTHOR:
      return getAuthor();
    case ReviewsPackage.TOPIC__CREATION_TIME:
      return getCreationTime();
    case ReviewsPackage.TOPIC__EDIT_TIME:
      return getEditTime();
    case ReviewsPackage.TOPIC__HEADING:
      return isHeading();
    case ReviewsPackage.TOPIC__MODEL_REFERENCE:
      return getModelReference();
    case ReviewsPackage.TOPIC__STATUS:
      return getStatus();
    case ReviewsPackage.TOPIC__CONTAINER:
      return getContainer();
    case ReviewsPackage.TOPIC__PARENT_HEADING:
      return getParentHeading();
    case ReviewsPackage.TOPIC__PREVIOUS_HEADING:
      return getPreviousHeading();
    case ReviewsPackage.TOPIC__NEXT_HEADING:
      return getNextHeading();
    case ReviewsPackage.TOPIC__OUTLINE_NUMBER:
      return getOutlineNumber();
    case ReviewsPackage.TOPIC__PARENT_INDEX:
      return getParentIndex();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case ReviewsPackage.TOPIC__ID:
      setId((Integer)newValue);
      return;
    case ReviewsPackage.TOPIC__TEXT:
      setText((String)newValue);
      return;
    case ReviewsPackage.TOPIC__AUTHOR:
      setAuthor((String)newValue);
      return;
    case ReviewsPackage.TOPIC__CREATION_TIME:
      setCreationTime((Long)newValue);
      return;
    case ReviewsPackage.TOPIC__EDIT_TIME:
      setEditTime((Long)newValue);
      return;
    case ReviewsPackage.TOPIC__HEADING:
      setHeading((Boolean)newValue);
      return;
    case ReviewsPackage.TOPIC__MODEL_REFERENCE:
      setModelReference((ModelReference)newValue);
      return;
    case ReviewsPackage.TOPIC__STATUS:
      setStatus((TopicStatus)newValue);
      return;
    case ReviewsPackage.TOPIC__CONTAINER:
      setContainer((TopicContainer)newValue);
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
    case ReviewsPackage.TOPIC__ID:
      setId(ID_EDEFAULT);
      return;
    case ReviewsPackage.TOPIC__TEXT:
      setText(TEXT_EDEFAULT);
      return;
    case ReviewsPackage.TOPIC__AUTHOR:
      setAuthor(AUTHOR_EDEFAULT);
      return;
    case ReviewsPackage.TOPIC__CREATION_TIME:
      setCreationTime(CREATION_TIME_EDEFAULT);
      return;
    case ReviewsPackage.TOPIC__EDIT_TIME:
      setEditTime(EDIT_TIME_EDEFAULT);
      return;
    case ReviewsPackage.TOPIC__HEADING:
      setHeading(HEADING_EDEFAULT);
      return;
    case ReviewsPackage.TOPIC__MODEL_REFERENCE:
      setModelReference(MODEL_REFERENCE_EDEFAULT);
      return;
    case ReviewsPackage.TOPIC__STATUS:
      setStatus(STATUS_EDEFAULT);
      return;
    case ReviewsPackage.TOPIC__CONTAINER:
      setContainer((TopicContainer)null);
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
    case ReviewsPackage.TOPIC__ID:
      return getId() != ID_EDEFAULT;
    case ReviewsPackage.TOPIC__TEXT:
      return TEXT_EDEFAULT == null ? getText() != null : !TEXT_EDEFAULT.equals(getText());
    case ReviewsPackage.TOPIC__AUTHOR:
      return AUTHOR_EDEFAULT == null ? getAuthor() != null : !AUTHOR_EDEFAULT.equals(getAuthor());
    case ReviewsPackage.TOPIC__CREATION_TIME:
      return getCreationTime() != CREATION_TIME_EDEFAULT;
    case ReviewsPackage.TOPIC__EDIT_TIME:
      return getEditTime() != EDIT_TIME_EDEFAULT;
    case ReviewsPackage.TOPIC__HEADING:
      return isHeading() != HEADING_EDEFAULT;
    case ReviewsPackage.TOPIC__MODEL_REFERENCE:
      return MODEL_REFERENCE_EDEFAULT == null ? getModelReference() != null : !MODEL_REFERENCE_EDEFAULT.equals(getModelReference());
    case ReviewsPackage.TOPIC__STATUS:
      return getStatus() != STATUS_EDEFAULT;
    case ReviewsPackage.TOPIC__CONTAINER:
      return getContainer() != null;
    case ReviewsPackage.TOPIC__PARENT_HEADING:
      return getParentHeading() != null;
    case ReviewsPackage.TOPIC__PREVIOUS_HEADING:
      return getPreviousHeading() != null;
    case ReviewsPackage.TOPIC__NEXT_HEADING:
      return getNextHeading() != null;
    case ReviewsPackage.TOPIC__OUTLINE_NUMBER:
      return OUTLINE_NUMBER_EDEFAULT == null ? getOutlineNumber() != null : !OUTLINE_NUMBER_EDEFAULT.equals(getOutlineNumber());
    case ReviewsPackage.TOPIC__PARENT_INDEX:
      return getParentIndex() != PARENT_INDEX_EDEFAULT;
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
    if (baseClass == Authorable.class)
    {
      switch (derivedFeatureID)
      {
      case ReviewsPackage.TOPIC__ID:
        return ReviewsPackage.AUTHORABLE__ID;
      case ReviewsPackage.TOPIC__TEXT:
        return ReviewsPackage.AUTHORABLE__TEXT;
      case ReviewsPackage.TOPIC__AUTHOR:
        return ReviewsPackage.AUTHORABLE__AUTHOR;
      case ReviewsPackage.TOPIC__CREATION_TIME:
        return ReviewsPackage.AUTHORABLE__CREATION_TIME;
      case ReviewsPackage.TOPIC__EDIT_TIME:
        return ReviewsPackage.AUTHORABLE__EDIT_TIME;
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
    if (baseClass == Authorable.class)
    {
      switch (baseFeatureID)
      {
      case ReviewsPackage.AUTHORABLE__ID:
        return ReviewsPackage.TOPIC__ID;
      case ReviewsPackage.AUTHORABLE__TEXT:
        return ReviewsPackage.TOPIC__TEXT;
      case ReviewsPackage.AUTHORABLE__AUTHOR:
        return ReviewsPackage.TOPIC__AUTHOR;
      case ReviewsPackage.AUTHORABLE__CREATION_TIME:
        return ReviewsPackage.TOPIC__CREATION_TIME;
      case ReviewsPackage.AUTHORABLE__EDIT_TIME:
        return ReviewsPackage.TOPIC__EDIT_TIME;
      default:
        return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

  @Override
  public System getSystem()
  {
    TopicContainer container = getContainer();
    return container == null ? null : container.getSystem();
  }

  @Override
  public Review getReview()
  {
    TopicContainer container = getContainer();
    return container == null ? null : container.getReview();
  }

  @Override
  public boolean forEachTopic(Predicate<Topic> consumer)
  {
    if (!consumer.test(this))
    {
      return false;
    }

    return super.forEachTopic(consumer);
  }

  private static void formatOutlineNumber(StringBuilder builder, Topic heading)
  {
    Topic parentHeading = heading.getParentHeading();
    if (parentHeading != null)
    {
      formatOutlineNumber(builder, parentHeading);
    }

    StringUtil.appendSeparator(builder, '.');
    builder.append(heading.getParentIndex());
  }
} // TopicImpl
