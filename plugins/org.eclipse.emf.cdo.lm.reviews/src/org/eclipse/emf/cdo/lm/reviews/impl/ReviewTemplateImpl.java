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

import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.SystemElement;
import org.eclipse.emf.cdo.lm.reviews.Review;
import org.eclipse.emf.cdo.lm.reviews.ReviewTemplate;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Review Template</b></em>'.
 * @noextend This class is not intended to be subclassed by clients.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewTemplateImpl#getReviewers <em>Reviewers</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ReviewTemplateImpl extends TopicContainerImpl implements ReviewTemplate
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ReviewTemplateImpl()
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
    return ReviewsPackage.Literals.REVIEW_TEMPLATE;
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
    return (EList<String>)eDynamicGet(ReviewsPackage.REVIEW_TEMPLATE__REVIEWERS, ReviewsPackage.Literals.REVIEW_TEMPLATE__REVIEWERS, true, true);
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
      case ReviewsPackage.REVIEW_TEMPLATE__REVIEWERS:
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
      case ReviewsPackage.REVIEW_TEMPLATE__REVIEWERS:
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
      case ReviewsPackage.REVIEW_TEMPLATE__REVIEWERS:
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
      case ReviewsPackage.REVIEW_TEMPLATE__REVIEWERS:
        return !getReviewers().isEmpty();
    }
    return super.eIsSet(featureID);
  }

  @Override
  public System getSystem()
  {
    EObject container = eContainer();
    while (container != null)
    {
      if (container instanceof SystemElement)
      {
        return ((SystemElement)container).getSystem();
      }

      container = container.eContainer();
    }

    return null;
  }

  @Override
  public Review getReview()
  {
    return null;
  }

} // ReviewTemplateImpl
