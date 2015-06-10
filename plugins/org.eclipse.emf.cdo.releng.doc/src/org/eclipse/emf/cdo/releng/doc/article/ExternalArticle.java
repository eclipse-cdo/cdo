/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>External Article</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.ExternalArticle#getUrl <em>Url</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getExternalArticle()
 * @model
 * @generated
 */
public interface ExternalArticle extends Article
{
  /**
   * Returns the value of the '<em><b>Url</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Url</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Url</em>' attribute.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getExternalArticle_Url()
   * @model required="true" changeable="false" derived="true"
   * @generated
   */
  String getUrl();

} // ExternalArticle
