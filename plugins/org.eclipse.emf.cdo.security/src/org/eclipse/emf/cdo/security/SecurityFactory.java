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
package org.eclipse.emf.cdo.security;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.security.SecurityPackage
 * @generated
 */
public interface SecurityFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  SecurityFactory eINSTANCE = org.eclipse.emf.cdo.security.impl.SecurityFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Realm</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Realm</em>'.
   * @generated
   */
  Realm createRealm();

  /**
   * @since 4.2
   */
  Realm createRealm(String name);

  /**
   * @since 4.2
   */
  Realm createRealm(String name, Access defaultAccess);

  /**
   * Returns a new object of class '<em>Directory</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Directory</em>'.
   * @generated
   */
  Directory createDirectory();

  /**
   * @since 4.2
   */
  Directory createDirectory(String name);

  /**
   * Returns a new object of class '<em>Role</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Role</em>'.
   * @generated
   */
  Role createRole();

  /**
   * @since 4.2
   */
  Role createRole(String id);

  /**
   * Returns a new object of class '<em>Group</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Group</em>'.
   * @generated
   */
  Group createGroup();

  /**
   * @since 4.2
   */
  Group createGroup(String id);

  /**
   * Returns a new object of class '<em>User</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>User</em>'.
   * @generated
   */
  User createUser();

  /**
   * @since 4.2
   */
  User createUser(String id);

  /**
   * @since 4.2
   */
  User createUser(String id, String password);

  /**
   * Returns a new object of class '<em>User Password</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>User Password</em>'.
   * @generated
   */
  UserPassword createUserPassword();

  /**
   * Returns a new object of class '<em>Class Permission</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Class Permission</em>'.
   * @generated
   */
  ClassPermission createClassPermission();

  /**
   * @since 4.2
   */
  ClassPermission createClassPermission(EClass eClass, Access access);

  /**
   * Returns a new object of class '<em>Package Permission</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Package Permission</em>'.
   * @generated
   */
  PackagePermission createPackagePermission();

  /**
   * @since 4.2
   */
  PackagePermission createPackagePermission(EPackage ePackage, Access access);

  /**
   * Returns a new object of class '<em>Resource Permission</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Resource Permission</em>'.
   * @generated
   */
  ResourcePermission createResourcePermission();

  /**
   * @since 4.2
   */
  ResourcePermission createResourcePermission(String pattern, Access access);

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  SecurityPackage getSecurityPackage();

} // SecurityFactory
