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

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Holder</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.Holder#getHeld <em>Held</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.Holder#getOwned <em>Owned</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getHolder()
 * @model
 * @generated
 */
public interface Holder extends Holdable
{
  /**
   * Returns the value of the '<em><b>Held</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model6.Holdable}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Held</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Held</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getHolder_Held()
   * @model transient="true" derived="true"
   *        annotation="http://www.eclipse.org/emf/CDO persistent='true' filter='owned'"
   * @generated
   */
  EList<Holdable> getHeld();

  /**
   * Returns the value of the '<em><b>Owned</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model6.Holdable}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Owned</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Owned</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getHolder_Owned()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<Holdable> getOwned();

} // Holder
