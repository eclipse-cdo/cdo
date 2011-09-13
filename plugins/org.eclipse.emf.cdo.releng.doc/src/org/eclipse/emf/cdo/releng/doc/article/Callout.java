/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Callout</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Callout#getSnippet <em>Snippet</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Callout#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getCallout()
 * @model
 * @generated
 */
public interface Callout extends EObject
{
  /**
   * Returns the value of the '<em><b>Snippet</b></em>' container reference. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.releng.doc.article.Snippet#getCallouts <em>Callouts</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Snippet</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Snippet</em>' container reference.
   * @see #setSnippet(Snippet)
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getCallout_Snippet()
   * @see org.eclipse.emf.cdo.releng.doc.article.Snippet#getCallouts
   * @model opposite="callouts" resolveProxies="false" required="true" transient="false"
   * @generated
   */
  Snippet getSnippet();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.doc.article.Callout#getSnippet <em>Snippet</em>}'
   * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Snippet</em>' container reference.
   * @see #getSnippet()
   * @generated
   */
  void setSnippet(Snippet value);

  /**
   * Returns the value of the '<em><b>Elements</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.releng.doc.article.BodyElement}. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.releng.doc.article.BodyElement#getCallout <em>Callout</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Elements</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Elements</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getCallout_Elements()
   * @see org.eclipse.emf.cdo.releng.doc.article.BodyElement#getCallout
   * @model opposite="callout" containment="true" required="true"
   * @generated
   */
  EList<BodyElement> getElements();

} // Callout
