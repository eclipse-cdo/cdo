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

import org.eclipse.emf.common.util.EList;

import java.io.File;
import java.util.List;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Documentation</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Documentation#getContext <em>Context</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Documentation#getEmbeddableElements <em>Embeddable Elements</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Documentation#getDependencies <em>Dependencies</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Documentation#getProject <em>Project</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Documentation#getPlugins <em>Plugins</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getDocumentation()
 * @model
 * @generated
 */
public interface Documentation extends StructuralElement
{
  /**
   * Returns the value of the '<em><b>Context</b></em>' container reference. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.releng.doc.article.Context#getDocumentations <em>Documentations</em>}'. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Context</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Context</em>' container reference.
   * @see #setContext(Context)
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getDocumentation_Context()
   * @see org.eclipse.emf.cdo.releng.doc.article.Context#getDocumentations
   * @model opposite="documentations" resolveProxies="false" required="true" transient="false"
   * @generated
   */
  Context getContext();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.doc.article.Documentation#getContext <em>Context</em>}'
   * container reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Context</em>' container reference.
   * @see #getContext()
   * @generated
   */
  void setContext(Context value);

  /**
   * Returns the value of the '<em><b>Embeddable Elements</b></em>' containment reference list. The list contents are of
   * type {@link org.eclipse.emf.cdo.releng.doc.article.EmbeddableElement}. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.releng.doc.article.EmbeddableElement#getDocumentation <em>Documentation</em>}'. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Embeddable Elements</em>' containment reference list isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Embeddable Elements</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getDocumentation_EmbeddableElements()
   * @see org.eclipse.emf.cdo.releng.doc.article.EmbeddableElement#getDocumentation
   * @model opposite="documentation" containment="true"
   * @generated
   */
  EList<EmbeddableElement> getEmbeddableElements();

  /**
   * Returns the value of the '<em><b>Dependencies</b></em>' reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.releng.doc.article.Documentation}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Dependencies</em>' reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Dependencies</em>' reference list.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getDocumentation_Dependencies()
   * @model resolveProxies="false"
   * @generated
   */
  EList<Documentation> getDependencies();

  /**
   * Returns the value of the '<em><b>Project</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Project</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Project</em>' attribute.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getDocumentation_Project()
   * @model required="true" changeable="false"
   * @generated
   */
  String getProject();

  /**
   * Returns the value of the '<em><b>Plugins</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.releng.doc.article.Plugin}. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Plugins</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Plugins</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getDocumentation_Plugins()
   * @model containment="true" changeable="false" derived="true"
   * @generated
   */
  EList<Plugin> getPlugins();

  boolean isAnalyzed();

  File getProjectFolder();

  StructuralElement getDefaultElement();

  void setDefaultElement(StructuralElement defaultElement);

  List<StructuralElement> getNavElements();

  void registerElement(StructuralElement element);
} // Documentation
