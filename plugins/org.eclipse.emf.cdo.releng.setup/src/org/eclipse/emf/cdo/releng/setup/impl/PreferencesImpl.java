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

import java.util.Collection;
import org.eclipse.emf.cdo.releng.setup.LinkLocation;
import org.eclipse.emf.cdo.releng.setup.Preferences;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Preferences</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.PreferencesImpl#getUserName <em>User Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.PreferencesImpl#getInstallFolder <em>Install Folder</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.PreferencesImpl#getGitPrefix <em>Git Prefix</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.releng.setup.impl.PreferencesImpl#getLinkLocations <em>Link Locations</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class PreferencesImpl extends ToolInstallationImpl implements Preferences
{
  /**
   * The default value of the '{@link #getUserName() <em>User Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUserName()
   * @generated
   * @ordered
   */
  protected static final String USER_NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getUserName() <em>User Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUserName()
   * @generated
   * @ordered
   */
  protected String userName = USER_NAME_EDEFAULT;

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
   * The default value of the '{@link #getGitPrefix() <em>Git Prefix</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getGitPrefix()
   * @generated
   * @ordered
   */
  protected static final String GIT_PREFIX_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getGitPrefix() <em>Git Prefix</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getGitPrefix()
   * @generated
   * @ordered
   */
  protected String gitPrefix = GIT_PREFIX_EDEFAULT;

  /**
   * The cached value of the '{@link #getLinkLocations() <em>Link Locations</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLinkLocations()
   * @generated
   * @ordered
   */
  protected EList<LinkLocation> linkLocations;

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
  public String getUserName()
  {
    return userName;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setUserName(String newUserName)
  {
    String oldUserName = userName;
    userName = newUserName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.PREFERENCES__USER_NAME, oldUserName, userName));
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
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.PREFERENCES__INSTALL_FOLDER, oldInstallFolder,
          installFolder));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getGitPrefix()
  {
    return gitPrefix;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setGitPrefix(String newGitPrefix)
  {
    String oldGitPrefix = gitPrefix;
    gitPrefix = newGitPrefix;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, SetupPackage.PREFERENCES__GIT_PREFIX, oldGitPrefix,
          gitPrefix));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<LinkLocation> getLinkLocations()
  {
    if (linkLocations == null)
    {
      linkLocations = new EObjectContainmentEList<LinkLocation>(LinkLocation.class, this,
          SetupPackage.PREFERENCES__LINK_LOCATIONS);
    }
    return linkLocations;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
    case SetupPackage.PREFERENCES__LINK_LOCATIONS:
      return ((InternalEList<?>)getLinkLocations()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
    case SetupPackage.PREFERENCES__USER_NAME:
      return getUserName();
    case SetupPackage.PREFERENCES__INSTALL_FOLDER:
      return getInstallFolder();
    case SetupPackage.PREFERENCES__GIT_PREFIX:
      return getGitPrefix();
    case SetupPackage.PREFERENCES__LINK_LOCATIONS:
      return getLinkLocations();
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
    case SetupPackage.PREFERENCES__USER_NAME:
      setUserName((String)newValue);
      return;
    case SetupPackage.PREFERENCES__INSTALL_FOLDER:
      setInstallFolder((String)newValue);
      return;
    case SetupPackage.PREFERENCES__GIT_PREFIX:
      setGitPrefix((String)newValue);
      return;
    case SetupPackage.PREFERENCES__LINK_LOCATIONS:
      getLinkLocations().clear();
      getLinkLocations().addAll((Collection<? extends LinkLocation>)newValue);
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
    case SetupPackage.PREFERENCES__USER_NAME:
      setUserName(USER_NAME_EDEFAULT);
      return;
    case SetupPackage.PREFERENCES__INSTALL_FOLDER:
      setInstallFolder(INSTALL_FOLDER_EDEFAULT);
      return;
    case SetupPackage.PREFERENCES__GIT_PREFIX:
      setGitPrefix(GIT_PREFIX_EDEFAULT);
      return;
    case SetupPackage.PREFERENCES__LINK_LOCATIONS:
      getLinkLocations().clear();
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
    case SetupPackage.PREFERENCES__USER_NAME:
      return USER_NAME_EDEFAULT == null ? userName != null : !USER_NAME_EDEFAULT.equals(userName);
    case SetupPackage.PREFERENCES__INSTALL_FOLDER:
      return INSTALL_FOLDER_EDEFAULT == null ? installFolder != null : !INSTALL_FOLDER_EDEFAULT.equals(installFolder);
    case SetupPackage.PREFERENCES__GIT_PREFIX:
      return GIT_PREFIX_EDEFAULT == null ? gitPrefix != null : !GIT_PREFIX_EDEFAULT.equals(gitPrefix);
    case SetupPackage.PREFERENCES__LINK_LOCATIONS:
      return linkLocations != null && !linkLocations.isEmpty();
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
      return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (userName: ");
    result.append(userName);
    result.append(", installFolder: ");
    result.append(installFolder);
    result.append(", gitPrefix: ");
    result.append(gitPrefix);
    result.append(')');
    return result.toString();
  }

} // PreferencesImpl
