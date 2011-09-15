/*
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Link</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Link#getTarget <em>Target</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getLink()
 * @model
 * @generated
 */
public interface Link extends BodyElement
{
  /**
   * Returns the value of the '<em><b>Target</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Target</em>' reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Target</em>' reference.
   * @see #setTarget(LinkTarget)
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getLink_Target()
   * @model resolveProxies="false"
   * @generated
   */
  LinkTarget getTarget();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.doc.article.Link#getTarget <em>Target</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Target</em>' reference.
   * @see #getTarget()
   * @generated
   */
  void setTarget(LinkTarget value);

} // Link
