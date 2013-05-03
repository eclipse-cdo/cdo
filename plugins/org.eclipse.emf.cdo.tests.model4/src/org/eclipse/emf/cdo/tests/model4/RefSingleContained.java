/*
 * Copyright (c) 2008, 2009, 2011-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model4;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Ref Single Contained</b></em>'. <!--
 * end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model4.RefSingleContained#getElement <em>Element</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefSingleContained()
 * @model
 * @generated
 */
public interface RefSingleContained extends EObject
{
  /**
   * Returns the value of the '<em><b>Element</b></em>' containment reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.tests.model4.SingleContainedElement#getParent <em>Parent</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Element</em>' containment reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Element</em>' containment reference.
   * @see #setElement(SingleContainedElement)
   * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefSingleContained_Element()
   * @see org.eclipse.emf.cdo.tests.model4.SingleContainedElement#getParent
   * @model opposite="parent" containment="true"
   * @generated
   */
  SingleContainedElement getElement();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model4.RefSingleContained#getElement <em>Element</em>}' containment reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Element</em>' containment reference.
   * @see #getElement()
   * @generated
   */
  void setElement(SingleContainedElement value);

} // RefSingleContained
