/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.doc.article;

import com.sun.javadoc.Doc;

import java.io.IOException;
import java.io.PrintWriter;

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

  /**
   * Returns the value of the '<em><b>Doc</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Doc</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Doc</em>' attribute.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getEmbeddableElement_Doc()
   * @model dataType="org.eclipse.emf.cdo.releng.doc.article.Doc" required="true" transient="true" changeable="false"
   *        volatile="true"
   * @generated
   */
  Doc getDoc();

  void generate(PrintWriter out, Embedding embedder) throws IOException;
} // EmbeddableElement
