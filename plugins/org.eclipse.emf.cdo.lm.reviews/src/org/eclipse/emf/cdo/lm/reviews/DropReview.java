/**
 */
package org.eclipse.emf.cdo.lm.reviews;

import org.eclipse.emf.cdo.lm.DropType;
import org.eclipse.emf.cdo.lm.FixedBaseline;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Drop Review</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.DropReview#getTargetTimeStamp <em>Target Time Stamp</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.DropReview#getDropType <em>Drop Type</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getDropReview()
 * @model
 * @generated
 */
public interface DropReview extends Review, FixedBaseline
{
  /**
   * Returns the value of the '<em><b>Target Time Stamp</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Target Time Stamp</em>' attribute.
   * @see #setTargetTimeStamp(long)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getDropReview_TargetTimeStamp()
   * @model required="true"
   * @generated
   */
  long getTargetTimeStamp();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.DropReview#getTargetTimeStamp <em>Target Time Stamp</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Target Time Stamp</em>' attribute.
   * @see #getTargetTimeStamp()
   * @generated
   */
  void setTargetTimeStamp(long value);

  /**
   * Returns the value of the '<em><b>Drop Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Drop Type</em>' reference.
   * @see #setDropType(DropType)
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getDropReview_DropType()
   * @model required="true"
   * @generated
   */
  DropType getDropType();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.reviews.DropReview#getDropType <em>Drop Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Drop Type</em>' reference.
   * @see #getDropType()
   * @generated
   */
  void setDropType(DropType value);

} // DropReview
