/**
 */
package org.eclipse.emf.cdo.lm.reviews;

import org.eclipse.emf.cdo.etypes.ModelElement;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Commentable</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.reviews.Commentable#getComments <em>Comments</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getCommentable()
 * @model abstract="true"
 * @generated
 */
public interface Commentable extends ModelElement
{
  /**
   * Returns the value of the '<em><b>Comments</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.lm.reviews.Comment}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.reviews.Comment#getCommentable <em>Commentable</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Comments</em>' containment reference list.
   * @see org.eclipse.emf.cdo.lm.reviews.ReviewsPackage#getCommentable_Comments()
   * @see org.eclipse.emf.cdo.lm.reviews.Comment#getCommentable
   * @model opposite="commentable" containment="true"
   * @generated
   */
  EList<Comment> getComments();

} // Commentable
