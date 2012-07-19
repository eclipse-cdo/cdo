/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.doc.article;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Chapter</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Chapter#getArticle <em>Article</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getChapter()
 * @model
 * @generated
 */
public interface Chapter extends Body
{

  /**
   * Returns the value of the '<em><b>Article</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Article</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Article</em>' reference.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getChapter_Article()
   * @model resolveProxies="false" required="true" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Article getArticle();

  public String getName();
} // Chapter
