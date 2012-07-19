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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Java Package</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.JavaPackage#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.JavaPackage#getPlugin <em>Plugin</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getJavaPackage()
 * @model
 * @generated
 */
public interface JavaPackage extends EObject
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
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getJavaPackage_Name()
   * @model required="true"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.doc.article.JavaPackage#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Plugin</b></em>' container reference. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.releng.doc.article.Plugin#getPackages <em>Packages</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Plugin</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Plugin</em>' container reference.
   * @see #setPlugin(Plugin)
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getJavaPackage_Plugin()
   * @see org.eclipse.emf.cdo.releng.doc.article.Plugin#getPackages
   * @model opposite="packages" required="true" transient="false"
   * @generated
   */
  Plugin getPlugin();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.doc.article.JavaPackage#getPlugin <em>Plugin</em>}'
   * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Plugin</em>' container reference.
   * @see #getPlugin()
   * @generated
   */
  void setPlugin(Plugin value);

} // JavaPackage
