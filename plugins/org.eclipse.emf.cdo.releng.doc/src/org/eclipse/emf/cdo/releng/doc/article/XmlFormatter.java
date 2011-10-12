/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article;

import java.io.File;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Xml Formatter</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.XmlFormatter#getFile <em>File</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getXmlFormatter()
 * @model
 * @generated
 */
public interface XmlFormatter extends Formatter
{

  /**
   * Returns the value of the '<em><b>File</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>File</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>File</em>' attribute.
   * @see #setFile(File)
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getXmlFormatter_File()
   * @model dataType="org.eclipse.emf.cdo.releng.doc.article.File"
   * @generated
   */
  File getFile();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.doc.article.XmlFormatter#getFile <em>File</em>}'
   * attribute. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>File</em>' attribute.
   * @see #getFile()
   * @generated
   */
  void setFile(File value);
} // XmlFormatter
