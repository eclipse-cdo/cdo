/*
 * Copyright (c) 2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Can Reference Legacy</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy#getSingleContainment <em>Single Containment</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy#getMultipleContainment <em>Multiple Containment</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy#getSingleReference <em>Single Reference</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy#getMultipleReference <em>Multiple Reference</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getCanReferenceLegacy()
 * @model
 * @generated
 */
public interface CanReferenceLegacy extends EObject
{
  /**
   * Returns the value of the '<em><b>Single Containment</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Single Containment</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Single Containment</em>' containment reference.
   * @see #setSingleContainment(EObject)
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getCanReferenceLegacy_SingleContainment()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EObject getSingleContainment();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy#getSingleContainment <em>Single Containment</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Single Containment</em>' containment reference.
   * @see #getSingleContainment()
   * @generated
   */
  void setSingleContainment(EObject value);

  /**
   * Returns the value of the '<em><b>Multiple Containment</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EObject}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Multiple Containment</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Multiple Containment</em>' containment reference list.
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getCanReferenceLegacy_MultipleContainment()
   * @model containment="true" resolveProxies="true"
   * @generated
   */
  EList<EObject> getMultipleContainment();

  /**
   * Returns the value of the '<em><b>Single Reference</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Single Reference</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Single Reference</em>' reference.
   * @see #setSingleReference(EObject)
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getCanReferenceLegacy_SingleReference()
   * @model
   * @generated
   */
  EObject getSingleReference();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model6.CanReferenceLegacy#getSingleReference <em>Single Reference</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Single Reference</em>' reference.
   * @see #getSingleReference()
   * @generated
   */
  void setSingleReference(EObject value);

  /**
   * Returns the value of the '<em><b>Multiple Reference</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.ecore.EObject}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Multiple Reference</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Multiple Reference</em>' reference list.
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getCanReferenceLegacy_MultipleReference()
   * @model
   * @generated
   */
  EList<EObject> getMultipleReference();

} // CanReferenceLegacy
