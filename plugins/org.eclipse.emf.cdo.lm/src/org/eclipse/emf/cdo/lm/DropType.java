/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Drop
 * Type</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.lm.DropType#getProcess <em>Process</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.DropType#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.lm.DropType#isRelease <em>Release</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.lm.LMPackage#getDropType()
 * @model
 * @generated
 */
public interface DropType extends ProcessElement
{
  /**
   * Returns the value of the '<em><b>Process</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.lm.Process#getDropTypes <em>Drop Types</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the value of the '<em>Process</em>' container reference.
   * @see #setProcess(org.eclipse.emf.cdo.lm.Process)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getDropType_Process()
   * @see org.eclipse.emf.cdo.lm.Process#getDropTypes
   * @model opposite="dropTypes" required="true" transient="false"
   * @generated
   */
  @Override
  org.eclipse.emf.cdo.lm.Process getProcess();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.DropType#getProcess <em>Process</em>}' container reference.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Process</em>' container reference.
   * @see #getProcess()
   * @generated
   */
  void setProcess(org.eclipse.emf.cdo.lm.Process value);

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getDropType_Name()
   * @model required="true"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.DropType#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Release</b></em>' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @return the value of the '<em>Release</em>' attribute.
   * @see #setRelease(boolean)
   * @see org.eclipse.emf.cdo.lm.LMPackage#getDropType_Release()
   * @model required="true"
   * @generated
   */
  boolean isRelease();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.lm.DropType#isRelease <em>Release</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Release</em>' attribute.
   * @see #isRelease()
   * @generated
   */
  void setRelease(boolean value);

} // DropType
