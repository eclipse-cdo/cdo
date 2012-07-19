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

import com.sun.javadoc.Tag;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Callout</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Callout#getSnippet <em>Snippet</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getCallout()
 * @model
 * @generated
 */
public interface Callout extends BodyElementContainer
{
  /**
   * Returns the value of the '<em><b>Snippet</b></em>' container reference. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.releng.doc.article.Snippet#getCallouts <em>Callouts</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Snippet</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Snippet</em>' container reference.
   * @see #setSnippet(Snippet)
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getCallout_Snippet()
   * @see org.eclipse.emf.cdo.releng.doc.article.Snippet#getCallouts
   * @model opposite="callouts" resolveProxies="false" required="true" transient="false"
   * @generated
   */
  Snippet getSnippet();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.doc.article.Callout#getSnippet <em>Snippet</em>}'
   * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Snippet</em>' container reference.
   * @see #getSnippet()
   * @generated
   */
  void setSnippet(Snippet value);

  Tag getTag();

  int getIndex();
} // Callout
