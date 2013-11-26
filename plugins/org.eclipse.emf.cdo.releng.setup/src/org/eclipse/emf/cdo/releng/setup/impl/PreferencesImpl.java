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
package org.eclipse.emf.cdo.releng.setup.impl;

import org.eclipse.emf.cdo.releng.setup.LicenseInfo;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.ScopeRoot;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTaskScope;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Preferences</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.PreferencesImpl#getInstallFolder <em>Install Folder</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.PreferencesImpl#getBundlePoolFolder <em>Bundle Pool Folder</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.PreferencesImpl#getAcceptedLicenses <em>Accepted Licenses</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PreferencesImpl extends SetupTaskContainerImpl implements Preferences
{
  /**
   * The default value of the '{@link #getInstallFolder() <em>Install Folder</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInstallFolder()
   * @generated
   * @ordered
   */
  protected static final String INSTALL_FOLDER_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getInstallFolder() <em>Install Folder</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInstallFolder()
   * @generated
   * @ordered
   */
  protected String installFolder = INSTALL_FOLDER_EDEFAULT;

  /**
   * The default value of the '{@link #getBundlePoolFolder() <em>Bundle Pool Folder</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBundlePoolFolder()
   * @generated
   * @ordered
   */
  protected static final String BUNDLE_POOL_FOLDER_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getBundlePoolFolder() <em>Bundle Pool Folder</em>}' attribute.
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @see #getBundlePoolFolder()
   * @generated
   * @ordered
   */
  protected String bundlePoolFolder = BUNDLE_POOL_FOLDER_EDEFAULT;

  /**
   * The cached value of the '{@link #getAcceptedLicenses() <em>Accepted Licenses</em>}' attribute list.
   * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
   * @see #getAcceptedLicenses()
   * @generated
   * @ordered
   */
  protected EList<LicenseInfo> acceptedLicenses;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PreferencesImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return SetupPackage.Literals.PREFERENCES;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getInstallFolder()
  {
    return installFolder;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setInstallFolder(String newInstallFolder)
  {
    String oldInstallFolder = installFolder;
    installFolder = newInstallFolder;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.PREFERENCES__INSTALL_FOLDER, oldInstallFolder,
          installFolder));
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getBundlePoolFolder()
  {
    return bundlePoolFolder;
  }

  /**
   * <!-- begin-user-doc -->
  	 * <!-- end-user-doc -->
   * @generated
   */
  public void setBundlePoolFolder(String newBundlePoolFolder)
  {
    String oldBundlePoolFolder = bundlePoolFolder;
    bundlePoolFolder = newBundlePoolFolder;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.PREFERENCES__BUNDLE_POOL_FOLDER,
          oldBundlePoolFolder, bundlePoolFolder));
    }
  }

  /**
   * <!-- begin-user-doc -->
       * <!-- end-user-doc -->
   * @generated
   */
  public EList<LicenseInfo> getAcceptedLicenses()
  {
    if (acceptedLicenses == null)
    {
      acceptedLicenses = new EDataTypeUniqueEList<LicenseInfo>(LicenseInfo.class, this,
          SetupPackage.PREFERENCES__ACCEPTED_LICENSES);
    }
    return acceptedLicenses;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public SetupTaskScope getScope()
  {
    return SetupTaskScope.USER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public ScopeRoot getParentScopeRoot()
  {
    return null;
  }

  /**
  	 * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
  	 * @generated
  	 */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case SetupPackage.PREFERENCES__INSTALL_FOLDER:
      return getInstallFolder();
    case SetupPackage.PREFERENCES__BUNDLE_POOL_FOLDER:
      return getBundlePoolFolder();
    case SetupPackage.PREFERENCES__ACCEPTED_LICENSES:
      return getAcceptedLicenses();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case SetupPackage.PREFERENCES__INSTALL_FOLDER:
      setInstallFolder((String)newValue);
      return;
    case SetupPackage.PREFERENCES__BUNDLE_POOL_FOLDER:
      setBundlePoolFolder((String)newValue);
      return;
    case SetupPackage.PREFERENCES__ACCEPTED_LICENSES:
      getAcceptedLicenses().clear();
      getAcceptedLicenses().addAll((Collection<? extends LicenseInfo>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.PREFERENCES__INSTALL_FOLDER:
      setInstallFolder(INSTALL_FOLDER_EDEFAULT);
      return;
    case SetupPackage.PREFERENCES__BUNDLE_POOL_FOLDER:
      setBundlePoolFolder(BUNDLE_POOL_FOLDER_EDEFAULT);
      return;
    case SetupPackage.PREFERENCES__ACCEPTED_LICENSES:
      getAcceptedLicenses().clear();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case SetupPackage.PREFERENCES__INSTALL_FOLDER:
      return INSTALL_FOLDER_EDEFAULT == null ? installFolder != null : !INSTALL_FOLDER_EDEFAULT.equals(installFolder);
    case SetupPackage.PREFERENCES__BUNDLE_POOL_FOLDER:
      return BUNDLE_POOL_FOLDER_EDEFAULT == null ? bundlePoolFolder != null : !BUNDLE_POOL_FOLDER_EDEFAULT
          .equals(bundlePoolFolder);
    case SetupPackage.PREFERENCES__ACCEPTED_LICENSES:
      return acceptedLicenses != null && !acceptedLicenses.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
    {
      return super.toString();
    }

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (installFolder: ");
    result.append(installFolder);
    result.append(", bundlePoolFolder: ");
    result.append(bundlePoolFolder);
    result.append(", acceptedLicenses: ");
    result.append(acceptedLicenses);
    result.append(')');
    return result.toString();
  }

} // PreferencesImpl
