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
package org.eclipse.emf.cdo.lm.reviews.impl;

import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.reviews.Comment;
import org.eclipse.emf.cdo.lm.reviews.CommentStatus;
import org.eclipse.emf.cdo.lm.reviews.Commentable;
import org.eclipse.emf.cdo.lm.reviews.Heading;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Comment</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentImpl#getCommentable <em>Commentable</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentImpl#getParentHeading <em>Parent Heading</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentImpl#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentImpl#getText <em>Text</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentImpl#getStatus <em>Status</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CommentImpl extends CommentableImpl implements Comment
{
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
   * The default value of the '{@link #getText() <em>Text</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getText()
   * @generated
   * @ordered
   */
  protected static final String TEXT_EDEFAULT = null;

  /**
   * The default value of the '{@link #getStatus() <em>Status</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getStatus()
   * @generated
   * @ordered
   */
  protected static final CommentStatus STATUS_EDEFAULT = CommentStatus.NONE;

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
  public Commentable getCommentable()
  {
    return (Commentable)eDynamicGet(ReviewsPackage.COMMENT__COMMENTABLE, ReviewsPackage.Literals.COMMENT__COMMENTABLE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetCommentable(Commentable newCommentable, NotificationChain msgs)
  {
    msgs = eBasicSetContainer((InternalEObject)newCommentable, ReviewsPackage.COMMENT__COMMENTABLE, msgs);
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setCommentable(Commentable newCommentable)
  {
    eDynamicSet(ReviewsPackage.COMMENT__COMMENTABLE, ReviewsPackage.Literals.COMMENT__COMMENTABLE, newCommentable);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Heading getParentHeading()
  {
    Commentable commentable = getCommentable();
    while (commentable != null)
    {
      if (commentable instanceof Heading)
      {
        return (Heading)commentable;
      }

      if (commentable instanceof Comment)
      {
        Comment comment = (Comment)commentable;
        commentable = comment.getCommentable();
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
   * @generated
   */
  @Override
  public String getAuthor()
  {
    return (String)eDynamicGet(ReviewsPackage.COMMENT__AUTHOR, ReviewsPackage.Literals.COMMENT__AUTHOR, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAuthor(String newAuthor)
  {
    eDynamicSet(ReviewsPackage.COMMENT__AUTHOR, ReviewsPackage.Literals.COMMENT__AUTHOR, newAuthor);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getText()
  {
    return (String)eDynamicGet(ReviewsPackage.COMMENT__TEXT, ReviewsPackage.Literals.COMMENT__TEXT, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setText(String newText)
  {
    eDynamicSet(ReviewsPackage.COMMENT__TEXT, ReviewsPackage.Literals.COMMENT__TEXT, newText);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public CommentStatus getStatus()
  {
    return (CommentStatus)eDynamicGet(ReviewsPackage.COMMENT__STATUS, ReviewsPackage.Literals.COMMENT__STATUS, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setStatus(CommentStatus newStatus)
  {
    eDynamicSet(ReviewsPackage.COMMENT__STATUS, ReviewsPackage.Literals.COMMENT__STATUS, newStatus);
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
    case ReviewsPackage.COMMENT__COMMENTABLE:
      if (eInternalContainer() != null)
      {
        msgs = eBasicRemoveFromContainer(msgs);
      }
      return basicSetCommentable((Commentable)otherEnd, msgs);
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
    case ReviewsPackage.COMMENT__COMMENTABLE:
      return basicSetCommentable(null, msgs);
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
    case ReviewsPackage.COMMENT__COMMENTABLE:
      return eInternalContainer().eInverseRemove(this, ReviewsPackage.COMMENTABLE__COMMENTS, Commentable.class, msgs);
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
    case ReviewsPackage.COMMENT__COMMENTABLE:
      return getCommentable();
    case ReviewsPackage.COMMENT__PARENT_HEADING:
      return getParentHeading();
    case ReviewsPackage.COMMENT__AUTHOR:
      return getAuthor();
    case ReviewsPackage.COMMENT__TEXT:
      return getText();
    case ReviewsPackage.COMMENT__STATUS:
      return getStatus();
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
    case ReviewsPackage.COMMENT__COMMENTABLE:
      setCommentable((Commentable)newValue);
      return;
    case ReviewsPackage.COMMENT__AUTHOR:
      setAuthor((String)newValue);
      return;
    case ReviewsPackage.COMMENT__TEXT:
      setText((String)newValue);
      return;
    case ReviewsPackage.COMMENT__STATUS:
      setStatus((CommentStatus)newValue);
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
    case ReviewsPackage.COMMENT__COMMENTABLE:
      setCommentable((Commentable)null);
      return;
    case ReviewsPackage.COMMENT__AUTHOR:
      setAuthor(AUTHOR_EDEFAULT);
      return;
    case ReviewsPackage.COMMENT__TEXT:
      setText(TEXT_EDEFAULT);
      return;
    case ReviewsPackage.COMMENT__STATUS:
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
    case ReviewsPackage.COMMENT__COMMENTABLE:
      return getCommentable() != null;
    case ReviewsPackage.COMMENT__PARENT_HEADING:
      return getParentHeading() != null;
    case ReviewsPackage.COMMENT__AUTHOR:
      return AUTHOR_EDEFAULT == null ? getAuthor() != null : !AUTHOR_EDEFAULT.equals(getAuthor());
    case ReviewsPackage.COMMENT__TEXT:
      return TEXT_EDEFAULT == null ? getText() != null : !TEXT_EDEFAULT.equals(getText());
    case ReviewsPackage.COMMENT__STATUS:
      return getStatus() != STATUS_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  @Override
  public System getSystem()
  {
    Commentable commentable = getCommentable();
    return commentable == null ? null : commentable.getSystem();
  }

  @Override
  public Review getReview()
  {
    Commentable commentable = getCommentable();
    return commentable == null ? null : commentable.getReview();
  }

} // CommentImpl
