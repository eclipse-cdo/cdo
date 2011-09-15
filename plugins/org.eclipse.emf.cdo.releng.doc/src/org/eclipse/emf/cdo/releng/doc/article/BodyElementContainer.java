/*
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Body Element Container</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.BodyElementContainer#getElements <em>Elements</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getBodyElementContainer()
 * @model abstract="true"
 * @generated
 */
public interface BodyElementContainer extends EObject
{
  /**
   * Returns the value of the '<em><b>Elements</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.releng.doc.article.BodyElement}. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.releng.doc.article.BodyElement#getContainer <em>Container</em>}'. <!-- begin-user-doc
   * -->
   * <p>
   * If the meaning of the '<em>Elements</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Elements</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getBodyElementContainer_Elements()
   * @see org.eclipse.emf.cdo.releng.doc.article.BodyElement#getContainer
   * @model opposite="container" containment="true"
   * @generated
   */
  EList<BodyElement> getElements();

} // BodyElementContainer
