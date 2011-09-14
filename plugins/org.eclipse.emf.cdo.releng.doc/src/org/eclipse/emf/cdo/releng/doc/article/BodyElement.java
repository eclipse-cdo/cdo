/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article;

import org.eclipse.emf.ecore.EObject;

import com.sun.javadoc.Tag;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Body Element</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.BodyElement#getContainer <em>Container</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.BodyElement#getTag <em>Tag</em>}</li>
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
   * Returns the value of the '<em><b>Container</b></em>' container reference. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.releng.doc.article.BodyElementContainer#getElements <em>Elements</em>}'. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Container</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Container</em>' container reference.
   * @see #setContainer(BodyElementContainer)
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getBodyElement_Container()
   * @see org.eclipse.emf.cdo.releng.doc.article.BodyElementContainer#getElements
   * @model opposite="elements" resolveProxies="false" transient="false"
   * @generated
   */
  BodyElementContainer getContainer();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.doc.article.BodyElement#getContainer <em>Container</em>}'
   * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Container</em>' container reference.
   * @see #getContainer()
   * @generated
   */
  void setContainer(BodyElementContainer value);

  /**
   * Returns the value of the '<em><b>Tag</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Tag</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Tag</em>' attribute.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getBodyElement_Tag()
   * @model dataType="org.eclipse.emf.cdo.releng.doc.article.Tag" transient="true" changeable="false" volatile="true"
   * @generated
   */
  Tag getTag();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @model
   * @generated
   */
  String getHtml(StructuralElement linkSource);

} // BodyElement
