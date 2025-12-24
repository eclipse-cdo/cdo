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

import org.eclipse.emf.cdo.etypes.impl.ModelElementImpl;
import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.reviews.Comment;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;
import org.eclipse.emf.cdo.lm.reviews.TopicContainer;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import java.lang.reflect.InvocationTargetException;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Comment</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentImpl#getText <em>Text</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentImpl#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentImpl#getCreationTime <em>Creation Time</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentImpl#getEditTime <em>Edit Time</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentImpl#getContainer <em>Container</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentImpl#getReview <em>Review</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentImpl#getReplyTo <em>Reply To</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CommentImpl extends ModelElementImpl implements Comment
{
  /**
   * The default value of the '{@link #getId() <em>Id</em>}' attribute.
   * <!-- begin-user-doc -->
   * @since 1.2
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
   * @since 1.2
   * <!-- end-user-doc -->
   * @see #getCreationTime()
   * @generated
   * @ordered
   */
  protected static final long CREATION_TIME_EDEFAULT = 0L;

  /**
   * The default value of the '{@link #getEditTime() <em>Edit Time</em>}' attribute.
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @see #getEditTime()
   * @generated
   * @ordered
   */
  protected static final long EDIT_TIME_EDEFAULT = 0L;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected CommentImpl()
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
    return ReviewsPackage.Literals.COMMENT;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public int getId()
  {
    return (Integer)eDynamicGet(ReviewsPackage.COMMENT__ID, ReviewsPackage.Literals.AUTHORABLE__ID, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setId(int newId)
  {
    eDynamicSet(ReviewsPackage.COMMENT__ID, ReviewsPackage.Literals.AUTHORABLE__ID, newId);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public TopicContainer getContainer()
  {
    return (TopicContainer)eDynamicGet(ReviewsPackage.COMMENT__CONTAINER, ReviewsPackage.Literals.COMMENT__CONTAINER, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetContainer(TopicContainer newContainer, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newContainer, ReviewsPackage.COMMENT__CONTAINER, msgs);
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
    eDynamicSet(ReviewsPackage.COMMENT__CONTAINER, ReviewsPackage.Literals.COMMENT__CONTAINER, newContainer);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getAuthor()
  {
    return (String)eDynamicGet(ReviewsPackage.COMMENT__AUTHOR, ReviewsPackage.Literals.AUTHORABLE__AUTHOR, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAuthor(String newAuthor)
  {
    eDynamicSet(ReviewsPackage.COMMENT__AUTHOR, ReviewsPackage.Literals.AUTHORABLE__AUTHOR, newAuthor);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public long getCreationTime()
  {
    return (Long)eDynamicGet(ReviewsPackage.COMMENT__CREATION_TIME, ReviewsPackage.Literals.AUTHORABLE__CREATION_TIME, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setCreationTime(long newCreationTime)
  {
    eDynamicSet(ReviewsPackage.COMMENT__CREATION_TIME, ReviewsPackage.Literals.AUTHORABLE__CREATION_TIME, newCreationTime);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public long getEditTime()
  {
    return (Long)eDynamicGet(ReviewsPackage.COMMENT__EDIT_TIME, ReviewsPackage.Literals.AUTHORABLE__EDIT_TIME, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setEditTime(long newEditTime)
  {
    eDynamicSet(ReviewsPackage.COMMENT__EDIT_TIME, ReviewsPackage.Literals.AUTHORABLE__EDIT_TIME, newEditTime);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Comment getReplyTo()
  {
    return (Comment)eDynamicGet(ReviewsPackage.COMMENT__REPLY_TO, ReviewsPackage.Literals.COMMENT__REPLY_TO, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 1.2
   * <!-- end-user-doc -->
   * @generated
   */
  public Comment basicGetReplyTo()
  {
    return (Comment)eDynamicGet(ReviewsPackage.COMMENT__REPLY_TO, ReviewsPackage.Literals.COMMENT__REPLY_TO, false, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setReplyTo(Comment newReplyTo)
  {
    eDynamicSet(ReviewsPackage.COMMENT__REPLY_TO, ReviewsPackage.Literals.COMMENT__REPLY_TO, newReplyTo);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getText()
  {
    return (String)eDynamicGet(ReviewsPackage.COMMENT__TEXT, ReviewsPackage.Literals.AUTHORABLE__TEXT, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setText(String newText)
  {
    eDynamicSet(ReviewsPackage.COMMENT__TEXT, ReviewsPackage.Literals.AUTHORABLE__TEXT, newText);
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
    case ReviewsPackage.COMMENT__CONTAINER:
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
    case ReviewsPackage.COMMENT__CONTAINER:
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
    case ReviewsPackage.COMMENT__CONTAINER:
      return eInternalContainer().eInverseRemove(this, ReviewsPackage.TOPIC_CONTAINER__COMMENTS, TopicContainer.class, msgs);
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
    case ReviewsPackage.COMMENT__ID:
      return getId();
    case ReviewsPackage.COMMENT__TEXT:
      return getText();
    case ReviewsPackage.COMMENT__AUTHOR:
      return getAuthor();
    case ReviewsPackage.COMMENT__CREATION_TIME:
      return getCreationTime();
    case ReviewsPackage.COMMENT__EDIT_TIME:
      return getEditTime();
    case ReviewsPackage.COMMENT__CONTAINER:
      return getContainer();
    case ReviewsPackage.COMMENT__REVIEW:
      return getReview();
    case ReviewsPackage.COMMENT__REPLY_TO:
      if (resolve)
      {
        return getReplyTo();
      }
      return basicGetReplyTo();
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
    case ReviewsPackage.COMMENT__ID:
      setId((Integer)newValue);
      return;
    case ReviewsPackage.COMMENT__TEXT:
      setText((String)newValue);
      return;
    case ReviewsPackage.COMMENT__AUTHOR:
      setAuthor((String)newValue);
      return;
    case ReviewsPackage.COMMENT__CREATION_TIME:
      setCreationTime((Long)newValue);
      return;
    case ReviewsPackage.COMMENT__EDIT_TIME:
      setEditTime((Long)newValue);
      return;
    case ReviewsPackage.COMMENT__CONTAINER:
      setContainer((TopicContainer)newValue);
      return;
    case ReviewsPackage.COMMENT__REPLY_TO:
      setReplyTo((Comment)newValue);
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
    case ReviewsPackage.COMMENT__ID:
      setId(ID_EDEFAULT);
      return;
    case ReviewsPackage.COMMENT__TEXT:
      setText(TEXT_EDEFAULT);
      return;
    case ReviewsPackage.COMMENT__AUTHOR:
      setAuthor(AUTHOR_EDEFAULT);
      return;
    case ReviewsPackage.COMMENT__CREATION_TIME:
      setCreationTime(CREATION_TIME_EDEFAULT);
      return;
    case ReviewsPackage.COMMENT__EDIT_TIME:
      setEditTime(EDIT_TIME_EDEFAULT);
      return;
    case ReviewsPackage.COMMENT__CONTAINER:
      setContainer((TopicContainer)null);
      return;
    case ReviewsPackage.COMMENT__REPLY_TO:
      setReplyTo((Comment)null);
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
    case ReviewsPackage.COMMENT__ID:
      return getId() != ID_EDEFAULT;
    case ReviewsPackage.COMMENT__TEXT:
      return TEXT_EDEFAULT == null ? getText() != null : !TEXT_EDEFAULT.equals(getText());
    case ReviewsPackage.COMMENT__AUTHOR:
      return AUTHOR_EDEFAULT == null ? getAuthor() != null : !AUTHOR_EDEFAULT.equals(getAuthor());
    case ReviewsPackage.COMMENT__CREATION_TIME:
      return getCreationTime() != CREATION_TIME_EDEFAULT;
    case ReviewsPackage.COMMENT__EDIT_TIME:
      return getEditTime() != EDIT_TIME_EDEFAULT;
    case ReviewsPackage.COMMENT__CONTAINER:
      return getContainer() != null;
    case ReviewsPackage.COMMENT__REVIEW:
      return getReview() != null;
    case ReviewsPackage.COMMENT__REPLY_TO:
      return basicGetReplyTo() != null;
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
    case ReviewsPackage.COMMENT___GET_SYSTEM:
      return getSystem();
    }
    return super.eInvoke(operationID, arguments);
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

} // CommentImpl
