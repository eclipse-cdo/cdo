/*
 * Copyright (c) 2010-2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model4;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Multi Non Contained Unsettable Element</b></em>
 * '. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.MultiNonContainedUnsettableElement#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.MultiNonContainedUnsettableElement#getParent <em>Parent</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model4.model4Package#getMultiNonContainedUnsettableElement()
 * @model
 * @generated
 */
public interface MultiNonContainedUnsettableElement extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.tests.model4.model4Package#getMultiNonContainedUnsettableElement_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model4.MultiNonContainedUnsettableElement#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Parent</b></em>' reference. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedUnsettable#getElements <em>Elements</em>}'. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parent</em>' reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Parent</em>' reference.
   * @see #isSetParent()
   * @see #unsetParent()
   * @see #setParent(RefMultiNonContainedUnsettable)
   * @see org.eclipse.emf.cdo.tests.model4.model4Package#getMultiNonContainedUnsettableElement_Parent()
   * @see org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedUnsettable#getElements
   * @model opposite="elements" unsettable="true"
   * @generated
   */
  RefMultiNonContainedUnsettable getParent();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model4.MultiNonContainedUnsettableElement#getParent <em>Parent</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Parent</em>' reference.
   * @see #isSetParent()
   * @see #unsetParent()
   * @see #getParent()
   * @generated
   */
  void setParent(RefMultiNonContainedUnsettable value);

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.tests.model4.MultiNonContainedUnsettableElement#getParent <em>Parent</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isSetParent()
   * @see #getParent()
   * @see #setParent(RefMultiNonContainedUnsettable)
   * @generated
   */
  void unsetParent();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.tests.model4.MultiNonContainedUnsettableElement#getParent <em>Parent</em>}' reference is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Parent</em>' reference is set.
   * @see #unsetParent()
   * @see #getParent()
   * @see #setParent(RefMultiNonContainedUnsettable)
   * @generated
   */
  boolean isSetParent();

} // MultiNonContainedUnsettableElement
