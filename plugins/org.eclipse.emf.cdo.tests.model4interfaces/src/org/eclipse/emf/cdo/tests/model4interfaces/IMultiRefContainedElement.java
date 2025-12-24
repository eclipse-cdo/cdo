/*
 * Copyright (c) 2008, 2009, 2011-2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model4interfaces;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>IMulti Ref Contained Element</b></em>'. <!--
 * end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainedElement#getParent <em>Parent</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage#getIMultiRefContainedElement()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface IMultiRefContainedElement extends EObject
{
  /**
   * Returns the value of the '<em><b>Parent</b></em>' container reference. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainer#getElements <em>Elements</em>}'. <!--
   * begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parent</em>' container reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Parent</em>' container reference.
   * @see #setParent(IMultiRefContainer)
   * @see org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage#getIMultiRefContainedElement_Parent()
   * @see org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainer#getElements
   * @model opposite="elements" transient="false"
   * @generated
   */
  IMultiRefContainer getParent();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model4interfaces.IMultiRefContainedElement#getParent <em>Parent</em>}' container reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Parent</em>' container reference.
   * @see #getParent()
   * @generated
   */
  void setParent(IMultiRefContainer value);

} // IMultiRefContainedElement
