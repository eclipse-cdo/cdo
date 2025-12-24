/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model5;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>With Custom Type</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model5.WithCustomType#getValue <em>Value</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model5.Model5Package#getWithCustomType()
 * @model
 * @generated
 */
public interface WithCustomType extends EObject
{
  /**
   * Returns the value of the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Value</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Value</em>' attribute.
   * @see #setValue(CustomType)
   * @see org.eclipse.emf.cdo.tests.model5.Model5Package#getWithCustomType_Value()
   * @model dataType="org.eclipse.emf.cdo.tests.model5.CustomType"
   * @generated
   */
  CustomType getValue();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model5.WithCustomType#getValue <em>Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Value</em>' attribute.
   * @see #getValue()
   * @generated
   */
  void setValue(CustomType value);

} // WithCustomType
