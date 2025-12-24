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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Child</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model5.Child#getParent <em>Parent</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model5.Child#getPreferredBy <em>Preferred By</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model5.Child#getName <em>Name</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model5.Model5Package#getChild()
 * @model
 * @generated
 */
public interface Child extends EObject
{
  /**
   * Returns the value of the '<em><b>Parent</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model5.Parent#getChildren <em>Children</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parent</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Parent</em>' container reference.
   * @see #setParent(Parent)
   * @see org.eclipse.emf.cdo.tests.model5.Model5Package#getChild_Parent()
   * @see org.eclipse.emf.cdo.tests.model5.Parent#getChildren
   * @model opposite="children" transient="false"
   * @generated
   */
  Parent getParent();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model5.Child#getParent <em>Parent</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Parent</em>' container reference.
   * @see #getParent()
   * @generated
   */
  void setParent(Parent value);

  /**
   * Returns the value of the '<em><b>Preferred By</b></em>' reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model5.Parent#getFavourite <em>Favourite</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Preferred By</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Preferred By</em>' reference.
   * @see #setPreferredBy(Parent)
   * @see org.eclipse.emf.cdo.tests.model5.Model5Package#getChild_PreferredBy()
   * @see org.eclipse.emf.cdo.tests.model5.Parent#getFavourite
   * @model opposite="favourite"
   * @generated
   */
  Parent getPreferredBy();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model5.Child#getPreferredBy <em>Preferred By</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Preferred By</em>' reference.
   * @see #getPreferredBy()
   * @generated
   */
  void setPreferredBy(Parent value);

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
   * @see org.eclipse.emf.cdo.tests.model5.Model5Package#getChild_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model5.Child#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

} // Child
