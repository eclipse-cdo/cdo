/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Install Task</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.P2Task#getInstallableUnits <em>Installable Units</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.P2Task#getP2Repositories <em>P2 Repositories</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.P2Task#isLicenseConfirmationDisabled <em>License Confirmation Disabled</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.P2Task#isMergeDisabled <em>Merge Disabled</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getP2Task()
 * @model
 * @generated
 */
public interface P2Task extends SetupTask
{
  /**
   * Returns the value of the '<em><b>Installable Units</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.InstallableUnit}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Installable Units</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Installable Units</em>' containment reference list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getP2Task_InstallableUnits()
   * @model containment="true" resolveProxies="true" required="true"
   * @generated
   */
  EList<InstallableUnit> getInstallableUnits();

  /**
   * Returns the value of the '<em><b>License Confirmation Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Disable License Confirmation</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>License Confirmation Disabled</em>' attribute.
   * @see #setLicenseConfirmationDisabled(boolean)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getP2Task_LicenseConfirmationDisabled()
   * @model
   * @generated
   */
  boolean isLicenseConfirmationDisabled();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.P2Task#isLicenseConfirmationDisabled <em>License Confirmation Disabled</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>License Confirmation Disabled</em>' attribute.
   * @see #isLicenseConfirmationDisabled()
   * @generated
   */
  void setLicenseConfirmationDisabled(boolean value);

  /**
   * Returns the value of the '<em><b>Merge Disabled</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Merge Disabled</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Merge Disabled</em>' attribute.
   * @see #setMergeDisabled(boolean)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getP2Task_MergeDisabled()
   * @model
   * @generated
   */
  boolean isMergeDisabled();

  /**
  	 * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.P2Task#isMergeDisabled <em>Merge Disabled</em>}' attribute.
  	 * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
  	 * @param value the new value of the '<em>Merge Disabled</em>' attribute.
  	 * @see #isMergeDisabled()
  	 * @generated
  	 */
  void setMergeDisabled(boolean value);

  /**
  	 * Returns the value of the '<em><b>P2 Repositories</b></em>' containment reference list.
  	 * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.P2Repository}.
  	 * <!-- begin-user-doc -->
         * <p>
         * If the meaning of the '<em>P2 Repositories</em>' containment reference list isn't clear,
         * there really should be more of a description here...
         * </p>
         * <!-- end-user-doc -->
  	 * @return the value of the '<em>P2 Repositories</em>' containment reference list.
  	 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getP2Task_P2Repositories()
  	 * @model containment="true" resolveProxies="true" required="true"
  	 * @generated
  	 */
  EList<P2Repository> getP2Repositories();

} // InstallTask
