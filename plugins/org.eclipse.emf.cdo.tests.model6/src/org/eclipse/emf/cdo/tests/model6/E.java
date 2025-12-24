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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>E</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.E#getOwnedAs <em>Owned As</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getE()
 * @model
 * @generated
 */
public interface E extends EObject
{
  /**
   * Returns the value of the '<em><b>Owned As</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.tests.model6.A}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Owned As</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Owned As</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getE_OwnedAs()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<A> getOwnedAs();

} // E
