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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Javadoc Group</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.JavadocGroup#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.JavadocGroup#getPackages <em>Packages</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getJavadocGroup()
 * @model
 * @generated
 */
public interface JavadocGroup extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getJavadocGroup_Name()
   * @model required="true"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.doc.article.JavadocGroup#getName <em>Name</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Packages</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.releng.doc.article.JavadocPackage}. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.releng.doc.article.JavadocPackage#getGroup <em>Group</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Packages</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Packages</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getJavadocGroup_Packages()
   * @see org.eclipse.emf.cdo.releng.doc.article.JavadocPackage#getGroup
   * @model opposite="group" containment="true" required="true"
   * @generated
   */
  EList<JavadocPackage> getPackages();

} // JavadocGroup
