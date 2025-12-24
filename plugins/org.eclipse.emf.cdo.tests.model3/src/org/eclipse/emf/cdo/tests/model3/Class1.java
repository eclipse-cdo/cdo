/*
 * Copyright (c) 2008-2012, 2015, 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3;

import org.eclipse.emf.cdo.tests.model3.subpackage.Class2;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Class1</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.Class1#getClass2 <em>Class2</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.Class1#getAdditionalValue <em>Additional Value</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getClass1()
 * @model
 * @generated
 */
public interface Class1 extends EObject
{
  /**
   * Returns the value of the '<em><b>Class2</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model3.subpackage.Class2}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model3.subpackage.Class2#getClass1 <em>Class1</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Class2</em>' reference list isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Class2</em>' reference list.
   * @see #isSetClass2()
   * @see #unsetClass2()
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getClass1_Class2()
   * @see org.eclipse.emf.cdo.tests.model3.subpackage.Class2#getClass1
   * @model opposite="class1" unsettable="true"
   * @generated
   */
  EList<Class2> getClass2();

  /**
   * Unsets the value of the '{@link org.eclipse.emf.cdo.tests.model3.Class1#getClass2 <em>Class2</em>}' reference list.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see #isSetClass2()
   * @see #getClass2()
   * @generated
   */
  void unsetClass2();

  /**
   * Returns whether the value of the '{@link org.eclipse.emf.cdo.tests.model3.Class1#getClass2 <em>Class2</em>}' reference list is set.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return whether the value of the '<em>Class2</em>' reference list is set.
   * @see #unsetClass2()
   * @see #getClass2()
   * @generated
   */
  boolean isSetClass2();

  /**
   * Returns the value of the '<em><b>Additional Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Additional Value</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Additional Value</em>' attribute.
   * @see #setAdditionalValue(String)
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getClass1_AdditionalValue()
   * @model
   * @generated
   */
  String getAdditionalValue();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model3.Class1#getAdditionalValue <em>Additional Value</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Additional Value</em>' attribute.
   * @see #getAdditionalValue()
   * @generated
   */
  void setAdditionalValue(String value);

} // Class1
