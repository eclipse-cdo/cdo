/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Javadoc</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Javadoc#getGroups <em>Groups</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getJavadoc()
 * @model
 * @generated
 */
public interface Javadoc extends Category
{
  /**
   * Returns the value of the '<em><b>Groups</b></em>' reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.releng.doc.article.JavadocGroup}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Groups</em>' reference list isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Groups</em>' reference list.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getJavadoc_Groups()
   * @model required="true" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<JavadocGroup> getGroups();

} // Javadoc
