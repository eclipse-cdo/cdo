/*
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipse.emf.cdo.releng.doc.article;

import org.eclipse.emf.cdo.releng.doc.article.util.HtmlWriter;

import java.io.IOException;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Embeddable Element</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.EmbeddableElement#getDocumentation <em>Documentation</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getEmbeddableElement()
 * @model abstract="true"
 * @generated
 */
public interface EmbeddableElement extends Identifiable
{
  /**
   * Returns the value of the '<em><b>Documentation</b></em>' container reference. It is bidirectional and its opposite
   * is '{@link org.eclipse.emf.cdo.releng.doc.article.Documentation#getEmbeddableElements <em>Embeddable Elements</em>}
   * '. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Documentation</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Documentation</em>' container reference.
   * @see #setDocumentation(Documentation)
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getEmbeddableElement_Documentation()
   * @see org.eclipse.emf.cdo.releng.doc.article.Documentation#getEmbeddableElements
   * @model opposite="embeddableElements" resolveProxies="false" required="true" transient="false"
   * @generated
   */
  Documentation getDocumentation();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.doc.article.EmbeddableElement#getDocumentation
   * <em>Documentation</em>}' container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Documentation</em>' container reference.
   * @see #getDocumentation()
   * @generated
   */
  void setDocumentation(Documentation value);

  void generate(HtmlWriter out, Embedding embedder) throws IOException;
} // EmbeddableElement
