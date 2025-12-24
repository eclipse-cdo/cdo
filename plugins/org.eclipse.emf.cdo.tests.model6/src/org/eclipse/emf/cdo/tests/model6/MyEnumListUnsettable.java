/*
 * Copyright (c) 2012, 2015 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>My Enum List Unsettable</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.MyEnumListUnsettable#getMyEnum <em>My Enum</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getMyEnumListUnsettable()
 * @model
 * @generated
 */
public interface MyEnumListUnsettable extends EObject
{
  /**
   * Returns the value of the '<em><b>My Enum</b></em>' attribute list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model6.MyEnum}.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.tests.model6.MyEnum}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>My Enum</em>' attribute list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>My Enum</em>' attribute list.
   * @see org.eclipse.emf.cdo.tests.model6.MyEnum
   * @see #isSetMyEnum()
   * @see #unsetMyEnum()
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getMyEnumListUnsettable_MyEnum()
   * @model unsettable="true"
   * @generated
   */
  EList<MyEnum> getMyEnum();

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.tests.model6.MyEnumListUnsettable#getMyEnum <em>My Enum</em>}' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isSetMyEnum()
   * @see #getMyEnum()
   * @generated
   */
  void unsetMyEnum();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.tests.model6.MyEnumListUnsettable#getMyEnum <em>My Enum</em>}' attribute list is set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return whether the value of the '<em>My Enum</em>' attribute list is set.
   * @see #unsetMyEnum()
   * @see #getMyEnum()
   * @generated
   */
  boolean isSetMyEnum();

} // MyEnumListUnsettable
