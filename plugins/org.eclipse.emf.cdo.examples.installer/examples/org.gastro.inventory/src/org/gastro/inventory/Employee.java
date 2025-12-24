/*
 * Copyright (c) 2009, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *
 *  Initial Publication:
 *    Eclipse Magazin - http://www.eclipse-magazin.de
 */
package org.gastro.inventory;

import org.eclipse.emf.cdo.CDOObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Employee</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link org.gastro.inventory.Employee#getDepartment <em>Department</em>}</li>
 * <li>{@link org.gastro.inventory.Employee#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.gastro.inventory.InventoryPackage#getEmployee()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface Employee extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Department</b></em>' container reference. It is bidirectional and its opposite is
   * '{@link org.gastro.inventory.Department#getEmployees <em>Employees</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Department</em>' reference isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Department</em>' container reference.
   * @see #setDepartment(Department)
   * @see org.gastro.inventory.InventoryPackage#getEmployee_Department()
   * @see org.gastro.inventory.Department#getEmployees
   * @model opposite="employees" transient="false"
   * @generated
   */
  Department getDepartment();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Employee#getDepartment <em>Department</em>}' reference. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Department</em>' reference.
   * @see #getDepartment()
   * @generated
   */
  void setDepartment(Department value);

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.gastro.inventory.InventoryPackage#getEmployee_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.gastro.inventory.Employee#getName <em>Name</em>}' attribute. <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

} // Employee
