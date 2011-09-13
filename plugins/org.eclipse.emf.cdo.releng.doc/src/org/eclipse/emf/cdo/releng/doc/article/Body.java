/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Body</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Body#getElements <em>Elements</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Body#getHtml <em>Html</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Body#getCategory <em>Category</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getBody()
 * @model abstract="true"
 * @generated
 */
public interface Body extends StructuralElement
{
  /**
   * Returns the value of the '<em><b>Elements</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.releng.doc.article.BodyElement}. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.releng.doc.article.BodyElement#getBody <em>Body</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Elements</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Elements</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getBody_Elements()
   * @see org.eclipse.emf.cdo.releng.doc.article.BodyElement#getBody
   * @model opposite="body" containment="true"
   * @generated
   */
  EList<BodyElement> getElements();

  /**
   * Returns the value of the '<em><b>Html</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Html</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Html</em>' attribute.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getBody_Html()
   * @model changeable="false" derived="true"
   * @generated
   */
  String getHtml();

  /**
   * Returns the value of the '<em><b>Category</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Category</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Category</em>' reference.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getBody_Category()
   * @model resolveProxies="false" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Category getCategory();

} // Body
