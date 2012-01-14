/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model1;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Address</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.Address#getName <em>Name</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.Address#getStreet <em>Street</em>}</li>
 * <li>{@link org.eclipse.emf.cdo.tests.model1.Address#getCity <em>City</em>}</li>
 * </ul>
 * </p>
 * 
 * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getAddress()
 * @model
 * @generated
 */
public interface Address extends EObject
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  String copyright = "Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.\r\nAll rights reserved. This program and the accompanying materials\r\nare made available under the terms of the Eclipse Public License v1.0\r\nwhich accompanies this distribution, and is available at\r\nhttp://www.eclipse.org/legal/epl-v10.html\r\n\r\nContributors:\r\n   Eike Stepper - initial API and implementation";

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getAddress_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model1.Address#getName <em>Name</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Street</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Street</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Street</em>' attribute.
   * @see #setStreet(String)
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getAddress_Street()
   * @model
   * @generated
   */
  String getStreet();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model1.Address#getStreet <em>Street</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>Street</em>' attribute.
   * @see #getStreet()
   * @generated
   */
  void setStreet(String value);

  /**
   * Returns the value of the '<em><b>City</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>City</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>City</em>' attribute.
   * @see #setCity(String)
   * @see org.eclipse.emf.cdo.tests.model1.Model1Package#getAddress_City()
   * @model
   * @generated
   */
  String getCity();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model1.Address#getCity <em>City</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   * 
   * @param value
   *          the new value of the '<em>City</em>' attribute.
   * @see #getCity()
   * @generated
   */
  void setCity(String value);

} // Address
