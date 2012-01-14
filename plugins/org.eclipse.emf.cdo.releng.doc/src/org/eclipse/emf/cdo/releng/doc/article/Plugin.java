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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Plugin</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Plugin#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Plugin#getPackages <em>Packages</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Plugin#getLabel <em>Label</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Plugin#getExtensionPoints <em>Extension Points</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getPlugin()
 * @model
 * @generated
 */
public interface Plugin extends EObject
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
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getPlugin_Name()
   * @model required="true"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.doc.article.Plugin#getName <em>Name</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Packages</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.releng.doc.article.JavaPackage}. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.releng.doc.article.JavaPackage#getPlugin <em>Plugin</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Packages</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Packages</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getPlugin_Packages()
   * @see org.eclipse.emf.cdo.releng.doc.article.JavaPackage#getPlugin
   * @model opposite="plugin" containment="true" required="true"
   * @generated
   */
  EList<JavaPackage> getPackages();

  /**
   * Returns the value of the '<em><b>Label</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Label</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Label</em>' attribute.
   * @see #setLabel(String)
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getPlugin_Label()
   * @model required="true"
   * @generated
   */
  String getLabel();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.doc.article.Plugin#getLabel <em>Label</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Label</em>' attribute.
   * @see #getLabel()
   * @generated
   */
  void setLabel(String value);

  /**
   * Returns the value of the '<em><b>Extension Points</b></em>' containment reference list. The list contents are of
   * type {@link org.eclipse.emf.cdo.releng.doc.article.ExtensionPoint}. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.releng.doc.article.ExtensionPoint#getPlugin <em>Plugin</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Extension Points</em>' containment reference list isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Extension Points</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getPlugin_ExtensionPoints()
   * @see org.eclipse.emf.cdo.releng.doc.article.ExtensionPoint#getPlugin
   * @model opposite="plugin" containment="true"
   * @generated
   */
  EList<ExtensionPoint> getExtensionPoints();

} // Plugin
