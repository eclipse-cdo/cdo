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

import org.eclipse.emf.cdo.lm.impl.BaselineImpl;
import org.eclipse.emf.cdo.lm.reviews.Comment;
import org.eclipse.emf.cdo.lm.reviews.Commentable;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewType;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Review</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewImpl#getComments <em>Comments</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewImpl#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewImpl#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewImpl#getReviewers <em>Reviewers</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class ReviewImpl extends BaselineImpl implements Review
{
  /**
   * The default value of the '{@link #getType() <em>Type</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getType()
   * @generated
   * @ordered
   */
  protected static final ReviewType TYPE_EDEFAULT = ReviewType.DELIVERY;

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

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public EList<Comment> getComments()
  {
    return (EList<Comment>)eDynamicGet(ReviewsPackage.REVIEW__COMMENTS, ReviewsPackage.Literals.COMMENTABLE__COMMENTS, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ReviewType getType()
  {
    // TODO: implement this method to return the 'Type' attribute
    // Ensure that you remove @generated or mark it @generated NOT
    throw new UnsupportedOperationException();
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
  @SuppressWarnings("unchecked")
  @Override
  public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
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
    case ReviewsPackage.REVIEW__COMMENTS:
      return getComments();
    case ReviewsPackage.REVIEW__TYPE:
      return getType();
    case ReviewsPackage.REVIEW__AUTHOR:
      return getAuthor();
    case ReviewsPackage.REVIEW__REVIEWERS:
      return getReviewers();
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
    case ReviewsPackage.REVIEW__COMMENTS:
      getComments().clear();
      getComments().addAll((Collection<? extends Comment>)newValue);
      return;
    case ReviewsPackage.REVIEW__AUTHOR:
      setAuthor((String)newValue);
      return;
    case ReviewsPackage.REVIEW__REVIEWERS:
      getReviewers().clear();
      getReviewers().addAll((Collection<? extends String>)newValue);
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
    case ReviewsPackage.REVIEW__COMMENTS:
      getComments().clear();
      return;
    case ReviewsPackage.REVIEW__AUTHOR:
      setAuthor(AUTHOR_EDEFAULT);
      return;
    case ReviewsPackage.REVIEW__REVIEWERS:
      getReviewers().clear();
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
    case ReviewsPackage.REVIEW__COMMENTS:
      return !getComments().isEmpty();
    case ReviewsPackage.REVIEW__TYPE:
      return getType() != TYPE_EDEFAULT;
    case ReviewsPackage.REVIEW__AUTHOR:
      return AUTHOR_EDEFAULT == null ? getAuthor() != null : !AUTHOR_EDEFAULT.equals(getAuthor());
    case ReviewsPackage.REVIEW__REVIEWERS:
      return !getReviewers().isEmpty();
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
    if (baseClass == Commentable.class)
    {
      switch (derivedFeatureID)
      {
      case ReviewsPackage.REVIEW__COMMENTS:
        return ReviewsPackage.COMMENTABLE__COMMENTS;
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
    if (baseClass == Commentable.class)
    {
      switch (baseFeatureID)
      {
      case ReviewsPackage.COMMENTABLE__COMMENTS:
        return ReviewsPackage.REVIEW__COMMENTS;
      default:
        return -1;
      }
    }
    return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
  }

} // ReviewImpl
