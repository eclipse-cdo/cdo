/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model6;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>D</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model6.D#getData <em>Data</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getD()
 * @model
 * @generated
 */
public interface D extends EObject
{
  /**
   * Returns the value of the '<em><b>Data</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Data</em>' containment reference isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Data</em>' containment reference.
   * @see #setData(EObject)
   * @see org.eclipse.emf.cdo.tests.model6.Model6Package#getD_Data()
   * @model containment="true"
   * @generated
   */
  EObject getData();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model6.D#getData <em>Data</em>}' containment reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Data</em>' containment reference.
   * @see #getData()
   * @generated
   */
  void setData(EObject value);

} // D
