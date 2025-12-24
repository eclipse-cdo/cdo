/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Containment Object</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model6.ContainmentObject#getContainmentOptional <em>Containment Optional</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model6.ContainmentObject#getContainmentList <em>Containment List</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getContainmentObject()
 * @model
 * @generated
 */
public interface ContainmentObject extends BaseObject
{
  /**
   * Returns the value of the '<em><b>Containment Optional</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Containment Optional</em>' containment reference isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Containment Optional</em>' containment reference.
   * @see #setContainmentOptional(BaseObject)
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getContainmentObject_ContainmentOptional()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  BaseObject getContainmentOptional();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model6.ContainmentObject#getContainmentOptional <em>Containment Optional</em>}' containment reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Containment Optional</em>' containment reference.
   * @see #getContainmentOptional()
   * @generated
   */
  void setContainmentOptional(BaseObject value);

  /**
   * Returns the value of the '<em><b>Containment List</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model6.BaseObject}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Containment List</em>' containment reference list isn't clear, there really should be
   * more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Containment List</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getContainmentObject_ContainmentList()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<BaseObject> getContainmentList();

} // ContainmentObject
