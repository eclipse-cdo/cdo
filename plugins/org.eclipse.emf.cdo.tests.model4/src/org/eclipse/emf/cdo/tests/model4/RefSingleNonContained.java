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
package org.eclipse.emf.cdo.tests.model4;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Ref Single Non Contained</b></em>'. <!--
 * end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.RefSingleNonContained#getElement <em>Element</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefSingleNonContained()
 * @model
 * @generated
 */
public interface RefSingleNonContained extends EObject
{
  /**
   * Returns the value of the '<em><b>Element</b></em>' reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model4.SingleNonContainedElement#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc
   * -->
   * <p>
   * If the meaning of the '<em>Element</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Element</em>' reference.
   * @see #setElement(SingleNonContainedElement)
   * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefSingleNonContained_Element()
   * @see org.eclipse.emf.cdo.tests.model4.SingleNonContainedElement#getParent
   * @model opposite="parent"
   * @generated
   */
  SingleNonContainedElement getElement();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model4.RefSingleNonContained#getElement <em>Element</em>}' reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Element</em>' reference.
   * @see #getElement()
   * @generated
   */
  void setElement(SingleNonContainedElement value);

} // RefSingleNonContained
