/**
 */
package org.eclipse.emf.cdo.lm.reviews.impl;

import org.eclipse.emf.cdo.lm.reviews.ReviewTemplate;
import org.eclipse.emf.cdo.lm.reviews.ReviewType;
import org.eclipse.emf.cdo.lm.reviews.ReviewsPackage;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Review Template</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewTemplateImpl#getType <em>Type</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.impl.ReviewTemplateImpl#getReviewers <em>Reviewers</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ReviewTemplateImpl extends CommentableImpl implements ReviewTemplate
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
  @Override
  public ReviewType getType()
  {
    return (ReviewType)eDynamicGet(ReviewsPackage.REVIEW_TEMPLATE__TYPE, ReviewsPackage.Literals.REVIEW_TEMPLATE__TYPE, true, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setType(ReviewType newType)
  {
    eDynamicSet(ReviewsPackage.REVIEW_TEMPLATE__TYPE, ReviewsPackage.Literals.REVIEW_TEMPLATE__TYPE, newType);
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
    case ReviewsPackage.REVIEW_TEMPLATE__TYPE:
      return getType();
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
    case ReviewsPackage.REVIEW_TEMPLATE__TYPE:
      setType((ReviewType)newValue);
      return;
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
    case ReviewsPackage.REVIEW_TEMPLATE__TYPE:
      setType(TYPE_EDEFAULT);
      return;
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
    case ReviewsPackage.REVIEW_TEMPLATE__TYPE:
      return getType() != TYPE_EDEFAULT;
    case ReviewsPackage.REVIEW_TEMPLATE__REVIEWERS:
      return !getReviewers().isEmpty();
    }
    return super.eIsSet(featureID);
  }

} // ReviewTemplateImpl
