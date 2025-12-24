/*
 * Copyright (c) 2012, 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security;

import org.eclipse.emf.cdo.expressions.Expression;

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
   * @deprecated As of 4.3 use {@link #createFilterPermission()} and {@link #createClassFilter()}.
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Class Permission</em>'.
   * @generated
   */
  @Deprecated
  ClassPermission createClassPermission();

  /**
   * @since 4.2
   * @deprecated As of 4.3 use {@link #createFilterPermission(Access, PermissionFilter...)} and {@link #createClassFilter(EClass)}.
   */
  @Deprecated
  ClassPermission createClassPermission(EClass eClass, Access access);

  /**
   * Returns a new object of class '<em>Package Permission</em>'.
   * <!-- begin-user-doc -->
   * @deprecated As of 4.3 use {@link #createFilterPermission()} and {@link #createPackageFilter()}.
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Package Permission</em>'.
   * @generated
   */
  @Deprecated
  PackagePermission createPackagePermission();

  /**
   * @since 4.2
   * @deprecated As of 4.3 use {@link #createFilterPermission(Access, PermissionFilter...)} and {@link #createPackageFilter(EPackage)}.
   */
  @Deprecated
  PackagePermission createPackagePermission(EPackage ePackage, Access access);

  /**
   * Returns a new object of class '<em>Resource Permission</em>'.
   * <!-- begin-user-doc -->
   * @deprecated As of 4.3 use {@link #createFilterPermission()} and {@link #createResourceFilter()}.
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Resource Permission</em>'.
   * @generated
   */
  @Deprecated
  ResourcePermission createResourcePermission();

  /**
   * @since 4.2
   * @deprecated As of 4.3 use {@link #createFilterPermission()} and {@link #createResourceFilter(String)}.
   */
  @Deprecated
  ResourcePermission createResourcePermission(String pattern, Access access);

  /**
   * Returns a new object of class '<em>Filter Permission</em>'.
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Filter Permission</em>'.
   * @generated
   */
  FilterPermission createFilterPermission();

  /**
   * Returns a new object of class '<em>Linked Filter</em>'.
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Linked Filter</em>'.
   * @generated
   */
  LinkedFilter createLinkedFilter();

  /**
   * @since 4.3
   */
  FilterPermission createFilterPermission(Access access, PermissionFilter... filters);

  /**
   * Returns a new object of class '<em>Package Filter</em>'.
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Package Filter</em>'.
   * @generated
   */
  PackageFilter createPackageFilter();

  /**
   * @since 4.3
   */
  PackageFilter createPackageFilter(EPackage ePackage);

  /**
   * Returns a new object of class '<em>Class Filter</em>'.
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Class Filter</em>'.
   * @generated
   */
  ClassFilter createClassFilter();

  /**
   * @since 4.3
   */
  ClassFilter createClassFilter(EClass eClass);

  /**
   * Returns a new object of class '<em>Resource Filter</em>'.
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Resource Filter</em>'.
   * @generated
   */
  ResourceFilter createResourceFilter();

  /**
   * @since 4.3
   */
  ResourceFilter createResourceFilter(String path);

  /**
   * @since 4.3
   */
  ResourceFilter createResourceFilter(String path, PatternStyle PatternStyle);

  /**
   * @since 4.3
   */
  ResourceFilter createResourceFilter(String path, PatternStyle PatternStyle, boolean includeParents);

  /**
   * Returns a new object of class '<em>Expression Filter</em>'.
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Expression Filter</em>'.
   * @generated
   */
  ExpressionFilter createExpressionFilter();

  /**
   * @since 4.3
   */
  ExpressionFilter createExpressionFilter(Expression expression);

  /**
   * Returns a new object of class '<em>Not Filter</em>'.
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Not Filter</em>'.
   * @generated
   */
  NotFilter createNotFilter();

  /**
   * Returns a new object of class '<em>And Filter</em>'.
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @return a new object of class '<em>And Filter</em>'.
   * @generated
   */
  AndFilter createAndFilter();

  /**
   * Returns a new object of class '<em>Or Filter</em>'.
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Or Filter</em>'.
   * @generated
   */
  OrFilter createOrFilter();

  /**
   * @since 4.3
   */
  NotFilter createNotFilter(PermissionFilter operand);

  /**
   * Returns a new object of class '<em>And Filter</em>'.
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @return a new object of class '<em>And Filter</em>'.
   * @generated NOT
   */
  AndFilter createAndFilter(PermissionFilter... operands);

  /**
   * Returns a new object of class '<em>Or Filter</em>'.
   * <!-- begin-user-doc -->
   * @since 4.3
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Or Filter</em>'.
   * @generated NOT
   */
  OrFilter createOrFilter(PermissionFilter... operands);

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  SecurityPackage getSecurityPackage();

} // SecurityFactory
