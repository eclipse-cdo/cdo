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

import com.sun.javadoc.SeeTag;

import java.io.PrintWriter;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Link Target</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.LinkTarget#getDefaultLabel <em>Default Label</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.LinkTarget#getTooltip <em>Tooltip</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getLinkTarget()
 * @model abstract="true"
 * @generated
 */
public interface LinkTarget extends Identifiable
{
  /**
   * Returns the value of the '<em><b>Default Label</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Default Label</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Default Label</em>' attribute.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getLinkTarget_DefaultLabel()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  String getDefaultLabel();

  /**
   * Returns the value of the '<em><b>Tooltip</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Label</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Tooltip</em>' attribute.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getLinkTarget_Tooltip()
   * @model required="true" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  String getTooltip();

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @model required="true" sourceRequired="true"
   * @generated
   */
  String linkFrom(StructuralElement source);

  void generateLink(PrintWriter out, StructuralElement linkSource, SeeTag tag);

} // LinkTarget
