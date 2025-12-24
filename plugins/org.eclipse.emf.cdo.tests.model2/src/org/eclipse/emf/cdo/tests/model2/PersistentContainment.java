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

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Persistent Containment</b></em>'. <!--
 * end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.PersistentContainment#getAttrBefore <em>Attr Before</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.PersistentContainment#getChildren <em>Children</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model2.PersistentContainment#getAttrAfter <em>Attr After</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getPersistentContainment()
 * @model
 * @generated
 */
public interface PersistentContainment extends EObject
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
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getPersistentContainment_AttrBefore()
   * @model
   * @generated
   */
  String getAttrBefore();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.PersistentContainment#getAttrBefore <em>Attr Before</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Attr Before</em>' attribute.
   * @see #getAttrBefore()
   * @generated
   */
  void setAttrBefore(String value);

  /**
   * Returns the value of the '<em><b>Children</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model2.TransientContainer}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model2.TransientContainer#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Children</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Children</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getPersistentContainment_Children()
   * @see org.eclipse.emf.cdo.tests.model2.TransientContainer#getParent
   * @model opposite="parent" containment="true" ordered="false"
   * @generated
   */
  EList<TransientContainer> getChildren();

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
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getPersistentContainment_AttrAfter()
   * @model
   * @generated
   */
  String getAttrAfter();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model2.PersistentContainment#getAttrAfter <em>Attr After</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Attr After</em>' attribute.
   * @see #getAttrAfter()
   * @generated
   */
  void setAttrAfter(String value);

} // PersistentContainment
