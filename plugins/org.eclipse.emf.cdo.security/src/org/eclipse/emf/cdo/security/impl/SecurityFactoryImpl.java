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
package org.eclipse.emf.cdo.security.impl;

//import org.eclipse.emf.cdo.security.*;
import org.eclipse.emf.cdo.security.Directory;
import org.eclipse.emf.cdo.security.Group;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.security.UserPassword;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SecurityFactoryImpl extends EFactoryImpl implements SecurityFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static SecurityFactory init()
  {
    try
    {
      SecurityFactory theSecurityFactory = (SecurityFactory)EPackage.Registry.INSTANCE
          .getEFactory("http://www.eclipse.org/emf/CDO/security/4.1.0"); //$NON-NLS-1$ 
      if (theSecurityFactory != null)
      {
        return theSecurityFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new SecurityFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SecurityFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case SecurityPackage.REALM:
      return (EObject)createRealm();
    case SecurityPackage.DIRECTORY:
      return (EObject)createDirectory();
    case SecurityPackage.ROLE:
      return (EObject)createRole();
    case SecurityPackage.GROUP:
      return (EObject)createGroup();
    case SecurityPackage.USER:
      return (EObject)createUser();
    case SecurityPackage.USER_PASSWORD:
      return (EObject)createUserPassword();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Realm createRealm()
  {
    RealmImpl realm = new RealmImpl();
    return realm;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Directory createDirectory()
  {
    DirectoryImpl directory = new DirectoryImpl();
    return directory;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Role createRole()
  {
    RoleImpl role = new RoleImpl();
    return role;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Group createGroup()
  {
    GroupImpl group = new GroupImpl();
    return group;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public User createUser()
  {
    UserImpl user = new UserImpl();
    return user;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public UserPassword createUserPassword()
  {
    UserPasswordImpl userPassword = new UserPasswordImpl();
    return userPassword;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SecurityPackage getSecurityPackage()
  {
    return (SecurityPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static SecurityPackage getPackage()
  {
    return SecurityPackage.eINSTANCE;
  }

} // SecurityFactoryImpl
