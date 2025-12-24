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
package org.eclipse.emf.cdo.tests.model2;

import org.eclipse.emf.cdo.tests.model1.VAT;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Enum List Holder</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model2.EnumListHolder#getEnumList <em>Enum List</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getEnumListHolder()
 * @model
 * @generated
 */
public interface EnumListHolder extends EObject
{
  /**
   * Returns the value of the '<em><b>Enum List</b></em>' attribute list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model1.VAT}.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.tests.model1.VAT}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Enum List</em>' attribute list isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Enum List</em>' attribute list.
   * @see org.eclipse.emf.cdo.tests.model1.VAT
   * @see org.eclipse.emf.cdo.tests.model2.Model2Package#getEnumListHolder_EnumList()
   * @model
   * @generated
   */
  EList<VAT> getEnumList();

} // EnumListHolder
