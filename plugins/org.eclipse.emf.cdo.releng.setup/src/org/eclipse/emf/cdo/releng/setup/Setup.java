/*
 * Copyright (c) 2013, 2014 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Workspace</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Setup#getBranch <em>Branch</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Setup#getEclipseVersion <em>Eclipse Version</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Setup#getPreferences <em>Preferences</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getSetup()
 * @model
 * @generated
 */
public interface Setup extends EObject
{
  /**
   * Returns the value of the '<em><b>Branch</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Branch</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Branch</em>' reference.
   * @see #setBranch(Branch)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getSetup_Branch()
   * @model required="true"
   * @generated
   */
  Branch getBranch();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.Setup#getBranch <em>Branch</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Branch</em>' reference.
   * @see #getBranch()
   * @generated
   */
  void setBranch(Branch value);

  /**
   * Returns the value of the '<em><b>Eclipse Version</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Eclipse Version</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Eclipse Version</em>' reference.
   * @see #setEclipseVersion(Eclipse)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getSetup_EclipseVersion()
   * @model required="true"
   * @generated
   */
  Eclipse getEclipseVersion();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.Setup#getEclipseVersion <em>Eclipse Version</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Eclipse Version</em>' reference.
   * @see #getEclipseVersion()
   * @generated
   */
  void setEclipseVersion(Eclipse value);

  /**
   * Returns the value of the '<em><b>Preferences</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Preferences</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Preferences</em>' reference.
   * @see #setPreferences(Preferences)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getSetup_Preferences()
   * @model required="true"
   * @generated
   */
  Preferences getPreferences();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.Setup#getPreferences <em>Preferences</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Preferences</em>' reference.
   * @see #getPreferences()
   * @generated
   */
  void setPreferences(Preferences value);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  EList<SetupTask> getSetupTasks(boolean filterRestrictions, Trigger trigger);

} // Workspace
