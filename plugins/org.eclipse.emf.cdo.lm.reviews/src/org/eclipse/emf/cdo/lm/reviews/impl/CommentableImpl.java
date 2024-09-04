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

import org.eclipse.emf.cdo.etypes.impl.ModelElementImpl;
import org.eclipse.emf.cdo.lm.reviews.Comment;
import org.eclipse.emf.cdo.lm.reviews.CommentStatus;
import org.eclipse.emf.cdo.lm.reviews.Commentable;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;

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
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentableImpl#getReview <em>Review</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentableImpl#getComments <em>Comments</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentableImpl#getCommentCount <em>Comment Count</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentableImpl#getUnresolvedCount <em>Unresolved Count</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.CommentableImpl#getResolvedCount <em>Resolved Count</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class CommentableImpl extends ModelElementImpl implements Commentable
{
  /**
   * The default value of the '{@link #getCommentCount() <em>Comment Count</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCommentCount()
   * @generated
   * @ordered
   */
  protected static final int COMMENT_COUNT_EDEFAULT = 0;

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
  protected CommentableImpl()
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
    return ReviewsPackage.Literals.COMMENTABLE;
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
  @SuppressWarnings("unchecked")
  @Override
  public EList<Comment> getComments()
  {
    return (EList<Comment>)eDynamicGet(ReviewsPackage.COMMENTABLE__COMMENTS, ReviewsPackage.Literals.COMMENTABLE__COMMENTS, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public int getCommentCount()
  {
    return getCommentCount(this);
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
    case ReviewsPackage.COMMENTABLE__COMMENTS:
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
    case ReviewsPackage.COMMENTABLE__COMMENTS:
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
    case ReviewsPackage.COMMENTABLE__REVIEW:
      return getReview();
    case ReviewsPackage.COMMENTABLE__COMMENTS:
      return getComments();
    case ReviewsPackage.COMMENTABLE__COMMENT_COUNT:
      return getCommentCount();
    case ReviewsPackage.COMMENTABLE__UNRESOLVED_COUNT:
      return getUnresolvedCount();
    case ReviewsPackage.COMMENTABLE__RESOLVED_COUNT:
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
    case ReviewsPackage.COMMENTABLE__COMMENTS:
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
    case ReviewsPackage.COMMENTABLE__COMMENTS:
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
    case ReviewsPackage.COMMENTABLE__REVIEW:
      return getReview() != null;
    case ReviewsPackage.COMMENTABLE__COMMENTS:
      return !getComments().isEmpty();
    case ReviewsPackage.COMMENTABLE__COMMENT_COUNT:
      return getCommentCount() != COMMENT_COUNT_EDEFAULT;
    case ReviewsPackage.COMMENTABLE__UNRESOLVED_COUNT:
      return getUnresolvedCount() != UNRESOLVED_COUNT_EDEFAULT;
    case ReviewsPackage.COMMENTABLE__RESOLVED_COUNT:
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
    case ReviewsPackage.COMMENTABLE___GET_SYSTEM:
      return getSystem();
    }
    return super.eInvoke(operationID, arguments);
  }

  public static CommentStatistics getCommentStatistics(Commentable commentable)
  {
    CommentStatistics statistics = new CommentStatistics();
    updateCommentStatistics(statistics, commentable);
    return statistics;
  }

  static void updateCommentStatistics(CommentStatistics statistics, Commentable commentable)
  {
    if (commentable instanceof Comment)
    {
      Comment comment = (Comment)commentable;

      CommentStatus status = comment.getStatus();
      if (status == CommentStatus.NONE)
      {
        statistics.incNoneCount();
      }
      else if (status == CommentStatus.UNRESOLVED)
      {
        statistics.incUnresolvedCount();
      }
      else if (status == CommentStatus.RESOLVED)
      {
        statistics.incResolvedCount();
      }
    }

    for (Comment comment : commentable.getComments())
    {
      updateCommentStatistics(statistics, comment);
    }
  }

  static int getCommentCount(Commentable commentable)
  {
    CommentStatistics statistics = getCommentStatistics(commentable);
    return statistics.getTotalCount();
  }

  static int getUnresolvedCount(Commentable commentable)
  {
    CommentStatistics statistics = getCommentStatistics(commentable);
    return statistics.getUnresolvedCount();
  }

  static int getResolvedCount(Commentable commentable)
  {
    CommentStatistics statistics = getCommentStatistics(commentable);
    return statistics.getResolvedCount();
  }

  /**
   * @author Eike Stepper
   */
  public static final class CommentStatistics
  {
    private int noneCount;

    private int unresolvedCount;

    private int resolvedCount;

    private CommentStatistics()
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
