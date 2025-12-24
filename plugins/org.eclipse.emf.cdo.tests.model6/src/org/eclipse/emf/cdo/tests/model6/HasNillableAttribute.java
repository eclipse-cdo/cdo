/*
 * Copyright (c) 2013, 2015 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Has Nillable Attribute</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.HasNillableAttribute#getNillable <em>Nillable</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getHasNillableAttribute()
 * @model
 * @generated
 */
public interface HasNillableAttribute extends EObject
{
  /**
   * Returns the value of the '<em><b>Nillable</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Nillable</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Nillable</em>' attribute.
   * @see #isSetNillable()
   * @see #unsetNillable()
   * @see #setNillable(String)
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getHasNillableAttribute_Nillable()
   * @model unsettable="true" dataType="org.eclipse.emf.cdo.tests.model6.MyString"
   * @generated
   */
  String getNillable();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model6.HasNillableAttribute#getNillable <em>Nillable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Nillable</em>' attribute.
   * @see #isSetNillable()
   * @see #unsetNillable()
   * @see #getNillable()
   * @generated
   */
  void setNillable(String value);

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.tests.model6.HasNillableAttribute#getNillable <em>Nillable</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isSetNillable()
   * @see #getNillable()
   * @see #setNillable(String)
   * @generated
   */
  void unsetNillable();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.tests.model6.HasNillableAttribute#getNillable <em>Nillable</em>}' attribute is set.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return whether the value of the '<em>Nillable</em>' attribute is set.
   * @see #unsetNillable()
   * @see #getNillable()
   * @see #setNillable(String)
   * @generated
   */
  boolean isSetNillable();

} // HasNillableAttribute
