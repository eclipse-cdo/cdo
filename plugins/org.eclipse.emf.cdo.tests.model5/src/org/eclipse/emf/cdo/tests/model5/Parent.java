/*
 * Copyright (c) 2012, 2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model5;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Parent</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model5.Parent#getChildren <em>Children</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model5.Parent#getFavourite <em>Favourite</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model5.Parent#getName <em>Name</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model5.Model5Package#getParent()
 * @model
 * @generated
 */
public interface Parent extends EObject
{
  /**
   * Returns the value of the '<em><b>Children</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model5.Child}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model5.Child#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Children</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Children</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model5.Model5Package#getParent_Children()
   * @see org.eclipse.emf.cdo.tests.model5.Child#getParent
   * @model opposite="parent" containment="true"
   * @generated
   */
  EList<Child> getChildren();

  /**
   * Returns the value of the '<em><b>Favourite</b></em>' reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model5.Child#getPreferredBy <em>Preferred By</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Favourite</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Favourite</em>' reference.
   * @see #setFavourite(Child)
   * @see org.eclipse.emf.cdo.tests.model5.Model5Package#getParent_Favourite()
   * @see org.eclipse.emf.cdo.tests.model5.Child#getPreferredBy
   * @model opposite="preferredBy"
   * @generated
   */
  Child getFavourite();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model5.Parent#getFavourite <em>Favourite</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Favourite</em>' reference.
   * @see #getFavourite()
   * @generated
   */
  void setFavourite(Child value);

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.tests.model5.Model5Package#getParent_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model5.Parent#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

} // Parent
