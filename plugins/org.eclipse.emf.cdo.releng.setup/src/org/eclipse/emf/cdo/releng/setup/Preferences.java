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
 * A representation of the model object '<em><b>Preferences</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Preferences#getInstallFolder <em>Install Folder</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Preferences#getBundlePoolFolder <em>Bundle Pool Folder</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Preferences#getBundlePoolFolderTP <em>Bundle Pool Folder TP</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.Preferences#getAcceptedLicenses <em>Accepted Licenses</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getPreferences()
 * @model
 * @generated
 */
public interface Preferences extends ScopeRoot
{
  /**
   * Returns the value of the '<em><b>Install Folder</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Install Folder</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Install Folder</em>' attribute.
   * @see #setInstallFolder(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getPreferences_InstallFolder()
   * @model required="true"
   * @generated
   */
  String getInstallFolder();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.Preferences#getInstallFolder <em>Install Folder</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Install Folder</em>' attribute.
   * @see #getInstallFolder()
   * @generated
   */
  void setInstallFolder(String value);

  /**
   * Returns the value of the '<em><b>Bundle Pool Folder</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Bundle Pool Folder</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Bundle Pool Folder</em>' attribute.
   * @see #setBundlePoolFolder(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getPreferences_BundlePoolFolder()
   * @model
   * @generated
   */
  String getBundlePoolFolder();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.Preferences#getBundlePoolFolder <em>Bundle Pool Folder</em>}' attribute.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @param value the new value of the '<em>Bundle Pool Folder</em>' attribute.
   * @see #getBundlePoolFolder()
   * @generated
   */
  void setBundlePoolFolder(String value);

  /**
   * Returns the value of the '<em><b>Bundle Pool Folder TP</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Bundle Pool Folder TP</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Bundle Pool Folder TP</em>' attribute.
   * @see #setBundlePoolFolderTP(String)
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getPreferences_BundlePoolFolderTP()
   * @model
   * @generated
   */
  String getBundlePoolFolderTP();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.releng.setup.Preferences#getBundlePoolFolderTP <em>Bundle Pool Folder TP</em>}' attribute.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @param value the new value of the '<em>Bundle Pool Folder TP</em>' attribute.
   * @see #getBundlePoolFolderTP()
   * @generated
   */
  void setBundlePoolFolderTP(String value);

  /**
   * Returns the value of the '<em><b>Accepted Licenses</b></em>' attribute list.
   * The list contents are of type {@link org.eclipse.emf.cdo.releng.setup.LicenseInfo}.
   * <!-- begin-user-doc -->
        	 * <p>
        	 * If the meaning of the '<em>Accepted Licenses</em>' attribute list isn't clear,
        	 * there really should be more of a description here...
        	 * </p>
        	 * <!-- end-user-doc -->
   * @return the value of the '<em>Accepted Licenses</em>' attribute list.
   * @see org.eclipse.emf.cdo.releng.setup.SetupPackage#getPreferences_AcceptedLicenses()
   * @model dataType="org.eclipse.emf.cdo.releng.setup.LicenseInfo"
   * @generated
   */
  EList<LicenseInfo> getAcceptedLicenses();

} // Preferences
