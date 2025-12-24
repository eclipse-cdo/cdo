/*
 * Copyright (c) 2009, 2011-2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model2;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Transient Container</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.TransientContainer#getParent <em>Parent</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getTransientContainer()
 * @model
 * @generated
 */
public interface TransientContainer extends EObject
{
  /**
   * Returns the value of the '<em><b>Attr Before</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attr Before</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attr Before</em>' attribute.
   * @see #setAttrBefore(String)
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getTransientContainer_AttrBefore()
   * @model
   * @generated
   */
  String getAttrBefore();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.TransientContainer#getAttrBefore <em>Attr Before</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Attr Before</em>' attribute.
   * @see #getAttrBefore()
   * @generated
   */
  void setAttrBefore(String value);

  /**
   * Returns the value of the '<em><b>Parent</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model2.PersistentContainment#getChildren <em>Children</em>}'.
   * <!-- begin-user-doc
   * -->
   * <p>
   * If the meaning of the '<em>Parent</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Parent</em>' container reference.
   * @see #setParent(PersistentContainment)
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getTransientContainer_Parent()
   * @see org.eclipse.emf.cdo.tests.model2.PersistentContainment#getChildren
   * @model opposite="children"
   * @generated
   */
  PersistentContainment getParent();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.TransientContainer#getParent <em>Parent</em>}' container reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Parent</em>' container reference.
   * @see #getParent()
   * @generated
   */
  void setParent(PersistentContainment value);

  /**
   * Returns the value of the '<em><b>Attr After</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Attr After</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Attr After</em>' attribute.
   * @see #setAttrAfter(String)
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getTransientContainer_AttrAfter()
   * @model
   * @generated
   */
  String getAttrAfter();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.TransientContainer#getAttrAfter <em>Attr After</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Attr After</em>' attribute.
   * @see #getAttrAfter()
   * @generated
   */
  void setAttrAfter(String value);

} // TransientContainer
