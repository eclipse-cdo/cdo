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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Identifiable</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.releng.doc.article.Identifiable#getId <em>Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getIdentifiable()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface Identifiable extends EObject
{
  /**
   * Returns the value of the '<em><b>Id</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Id</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Id</em>' attribute.
   * @see org.eclipse.emf.cdo.releng.doc.article.ArticlePackage#getIdentifiable_Id()
   * @model required="true" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Object getId();

} // Identifiable
