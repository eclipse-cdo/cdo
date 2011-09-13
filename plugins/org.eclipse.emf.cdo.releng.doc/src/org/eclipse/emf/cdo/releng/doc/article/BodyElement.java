/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Body Element</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.BodyElement#getBody <em>Body</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.BodyElement#getHtml <em>Html</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.BodyElement#getCallout <em>Callout</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getBodyElement()
 * @model abstract="true"
 * @generated
 */
public interface BodyElement extends EObject
{
  /**
   * Returns the value of the '<em><b>Body</b></em>' container reference. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.releng.doc.article.Body#getElements <em>Elements</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Body</em>' container reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Body</em>' container reference.
   * @see #setBody(Body)
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getBodyElement_Body()
   * @see org.eclipse.emf.cdo.releng.doc.article.Body#getElements
   * @model opposite="elements" resolveProxies="false" transient="false"
   * @generated
   */
  Body getBody();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.doc.article.BodyElement#getBody <em>Body</em>}' container
   * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Body</em>' container reference.
   * @see #getBody()
   * @generated
   */
  void setBody(Body value);

  /**
   * Returns the value of the '<em><b>Html</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Html</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Html</em>' attribute.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getBodyElement_Html()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  String getHtml();

  /**
   * Returns the value of the '<em><b>Callout</b></em>' container reference. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.releng.doc.article.Callout#getElements <em>Elements</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Callout</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Callout</em>' container reference.
   * @see #setCallout(Callout)
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getBodyElement_Callout()
   * @see org.eclipse.emf.cdo.releng.doc.article.Callout#getElements
   * @model opposite="elements" resolveProxies="false" transient="false"
   * @generated
   */
  Callout getCallout();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.doc.article.BodyElement#getCallout <em>Callout</em>}'
   * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Callout</em>' container reference.
   * @see #getCallout()
   * @generated
   */
  void setCallout(Callout value);

} // BodyElement
