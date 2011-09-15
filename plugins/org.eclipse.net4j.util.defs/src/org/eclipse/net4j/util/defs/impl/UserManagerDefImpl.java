/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.net4j.util.defs.UserManagerDef;
import org.eclipse.net4j.util.security.UserManager;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>User Manager Def</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.net4j.util.defs.impl.UserManagerDefImpl#getUser <em>User</em>}</li>
 * </ul>
 * </p>
 * 
 * @generated
 */
public class UserManagerDefImpl extends DefImpl implements UserManagerDef
{
  /**
   * The cached value of the '{@link #getUser() <em>User</em>}' reference list. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * 
   * @see #getUser()
   * @generated
   * @ordered
   */
  protected EList<User> user;

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected UserManagerDefImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return Net4jUtilDefsPackage.Literals.USER_MANAGER_DEF;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EList<User> getUser()
  {
    if (user == null)
    {
      user = new EObjectResolvingEList.Unsettable<User>(User.class, this, Net4jUtilDefsPackage.USER_MANAGER_DEF__USER);
    }
    return user;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public void unsetUser()
  {
    if (user != null)
    {
      ((InternalEList.Unsettable<?>)user).unset();
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public boolean isSetUser()
  {
    return user != null && ((InternalEList.Unsettable<?>)user).isSet();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
    case Net4jUtilDefsPackage.USER_MANAGER_DEF__USER:
      return getUser();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
    case Net4jUtilDefsPackage.USER_MANAGER_DEF__USER:
      getUser().clear();
      getUser().addAll((Collection<? extends User>)newValue);
      return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
    case Net4jUtilDefsPackage.USER_MANAGER_DEF__USER:
      unsetUser();
      return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
    case Net4jUtilDefsPackage.USER_MANAGER_DEF__USER:
      return isSetUser();
    }
    return super.eIsSet(featureID);
  }

  /**
   * @ADDED
   */
  @Override
  protected Object createInstance()
  {
    UserManager userManager = new UserManager();
    return userManager;

  }

} // UserManagerDefImpl
