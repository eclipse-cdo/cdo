/*
 * Copyright (c) 2008-2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.util.defs.impl;

import org.eclipse.net4j.util.defs.Net4jUtilDefsPackage;
import org.eclipse.net4j.util.defs.User;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>User</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.net4j.util.defs.impl.UserImpl#getUserID <em>User ID</em>}</li>
 *   <li>{@link org.eclipse.net4j.util.defs.impl.UserImpl#getPassword <em>Password</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class UserImpl extends EObjectImpl implements User
{
  /**
   * The default value of the '{@link #getUserID() <em>User ID</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getUserID()
   * @generated
   * @ordered
   */
  protected static final String USER_ID_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getUserID() <em>User ID</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getUserID()
   * @generated
   * @ordered
   */
  protected String userID = USER_ID_EDEFAULT;

  /**
   * The default value of the '{@link #getPassword() <em>Password</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getPassword()
   * @generated
   * @ordered
   */
  protected static final String PASSWORD_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getPassword() <em>Password</em>}' attribute.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @see #getPassword()
   * @generated
   * @ordered
   */
  protected String password = PASSWORD_EDEFAULT;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected UserImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Net4jUtilDefsPackage.Literals.USER;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String getUserID()
  {
    return userID;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setUserID(String newUserID)
  {
    String oldUserID = userID;
    userID = newUserID;
    if (eNotificationRequired())
    {
      eNotify(new ENotificationImpl(this, Notification.SET, Net4jUtilDefsPackage.USER__USER_ID, oldUserID, userID));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String getPassword()
  {
    return password;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public void setPassword(String newPassword)
  {
    String oldPassword = password;
    password = newPassword;
    if (eNotificationRequired())
    {
      eNotify(
          new ENotificationImpl(this, Notification.SET, Net4jUtilDefsPackage.USER__PASSWORD, oldPassword, password));
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case Net4jUtilDefsPackage.USER__USER_ID:
      return getUserID();
    case Net4jUtilDefsPackage.USER__PASSWORD:
      return getPassword();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Net4jUtilDefsPackage.USER__USER_ID:
      setUserID((String)newValue);
      return;
    case Net4jUtilDefsPackage.USER__PASSWORD:
      setPassword((String)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case Net4jUtilDefsPackage.USER__USER_ID:
      setUserID(USER_ID_EDEFAULT);
      return;
    case Net4jUtilDefsPackage.USER__PASSWORD:
      setPassword(PASSWORD_EDEFAULT);
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case Net4jUtilDefsPackage.USER__USER_ID:
      return USER_ID_EDEFAULT == null ? userID != null : !USER_ID_EDEFAULT.equals(userID);
    case Net4jUtilDefsPackage.USER__PASSWORD:
      return PASSWORD_EDEFAULT == null ? password != null : !PASSWORD_EDEFAULT.equals(password);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
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
    result.append(" (userID: ");
    result.append(userID);
    result.append(", password: ");
    result.append(password);
    result.append(')');
    return result.toString();
  }

} // UserImpl
