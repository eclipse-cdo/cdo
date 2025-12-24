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

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>B</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.B#getOwnedC <em>Owned C</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getB()
 * @model
 * @generated
 */
public interface B extends EObject
{
  /**
   * Returns the value of the '<em><b>Owned C</b></em>' containment reference. <!-- begin-user-doc --> <!-- end-user-doc
   * --> <!-- begin-model-doc --> The style of the node. <!-- end-model-doc -->
   *
   * @return the value of the '<em>Owned C</em>' containment reference.
   * @see #setOwnedC(C)
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getB_OwnedC()
   * @model containment="true"
   * @generated
   */
  C getOwnedC();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model6.B#getOwnedC <em>Owned C</em>}' containment reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Owned C</em>' containment reference.
   * @see #getOwnedC()
   * @generated
   */
  void setOwnedC(C value);

} // B
