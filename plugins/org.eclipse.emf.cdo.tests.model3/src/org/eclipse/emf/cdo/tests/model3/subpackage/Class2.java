/*
 * Copyright (c) 2008-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3.subpackage;

import org.eclipse.emf.cdo.tests.model3.Class1;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Class2</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.subpackage.Class2#getClass1 <em>Class1</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model3.subpackage.SubpackagePackage#getClass2()
 * @model
 * @generated
 */
public interface Class2 extends EObject
{
  /**
   * Returns the value of the '<em><b>Class1</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model3.Class1}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model3.Class1#getClass2 <em>Class2</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Class1</em>' reference list isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Class1</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model3.subpackage.SubpackagePackage#getClass2_Class1()
   * @see org.eclipse.emf.cdo.tests.model3.Class1#getClass2
   * @model opposite="class2"
   * @generated
   */
  EList<Class1> getClass1();

} // Class2
