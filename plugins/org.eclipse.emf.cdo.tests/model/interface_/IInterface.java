/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package interface_;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>IInterface</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link interface_.IInterface#getTest <em>Test</em>}</li>
 * </ul>
 * </p>
 *
 * @see interface_.InterfacePackage#getIInterface()
 * @model interface="true" abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface IInterface extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Test</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Test</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Test</em>' attribute.
   * @see #setTest(String)
   * @see interface_.InterfacePackage#getIInterface_Test()
   * @model
   * @generated
   */
  String getTest();

  /**
   * Sets the value of the '{@link interface_.IInterface#getTest <em>Test</em>}' attribute. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Test</em>' attribute.
   * @see #getTest()
   * @generated
   */
  void setTest(String value);

} // IInterface
