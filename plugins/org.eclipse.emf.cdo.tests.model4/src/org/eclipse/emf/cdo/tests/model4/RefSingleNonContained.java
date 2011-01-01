/******
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Ref Single Non Contained</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model4.RefSingleNonContained#getElement <em>Element</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefSingleNonContained()
 * @model
 * @generated
 */
public interface RefSingleNonContained extends EObject
{
  /**
   * Returns the value of the '<em><b>Element</b></em>' reference. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.tests.model4.SingleNonContainedElement#getParent <em>Parent</em>}'. <!-- begin-user-doc
   * -->
   * <p>
   * If the meaning of the '<em>Element</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Element</em>' reference.
   * @see #setElement(SingleNonContainedElement)
   * @see org.eclipse.emf.cdo.tests.model4.model4Package#getRefSingleNonContained_Element()
   * @see org.eclipse.emf.cdo.tests.model4.SingleNonContainedElement#getParent
   * @model opposite="parent"
   * @generated
   */
  SingleNonContainedElement getElement();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model4.RefSingleNonContained#getElement <em>Element</em>}'
   * reference. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Element</em>' reference.
   * @see #getElement()
   * @generated
   */
  void setElement(SingleNonContainedElement value);

} // RefSingleNonContained
