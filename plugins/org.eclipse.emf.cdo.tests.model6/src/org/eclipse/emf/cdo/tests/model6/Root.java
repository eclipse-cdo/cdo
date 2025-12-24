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
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Root</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.Root#getListA <em>List A</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.Root#getListB <em>List B</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.Root#getListC <em>List C</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.Root#getListD <em>List D</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getRoot()
 * @model
 * @generated
 */
public interface Root extends EObject
{
  /**
   * Returns the value of the '<em><b>List A</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model6.BaseObject}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>List A</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>List A</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getRoot_ListA()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<BaseObject> getListA();

  /**
   * Returns the value of the '<em><b>List B</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model6.BaseObject}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>List B</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>List B</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getRoot_ListB()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<BaseObject> getListB();

  /**
   * Returns the value of the '<em><b>List C</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model6.BaseObject}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>List C</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>List C</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getRoot_ListC()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<BaseObject> getListC();

  /**
   * Returns the value of the '<em><b>List D</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model6.BaseObject}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>List D</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>List D</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getRoot_ListD()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<BaseObject> getListD();

} // Root
