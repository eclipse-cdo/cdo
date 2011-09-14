/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Javadoc Package</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.JavadocPackage#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.JavadocPackage#getGroup <em>Group</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getJavadocPackage()
 * @model
 * @generated
 */
public interface JavadocPackage extends EObject
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
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getJavadocPackage_Name()
   * @model required="true"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.doc.article.JavadocPackage#getName <em>Name</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Group</b></em>' container reference. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.releng.doc.article.JavadocGroup#getPackages <em>Packages</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Group</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Group</em>' container reference.
   * @see #setGroup(JavadocGroup)
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getJavadocPackage_Group()
   * @see org.eclipse.emf.cdo.releng.doc.article.JavadocGroup#getPackages
   * @model opposite="packages" required="true" transient="false"
   * @generated
   */
  JavadocGroup getGroup();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.doc.article.JavadocPackage#getGroup <em>Group</em>}'
   * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Group</em>' container reference.
   * @see #getGroup()
   * @generated
   */
  void setGroup(JavadocGroup value);

} // JavadocPackage
