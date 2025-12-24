/*
 * Copyright (c) 2012, 2013, 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Realm</b></em>'.
 * @extends SecurityItemContainer
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.Realm#getItems <em>Items</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Realm#getAllUsers <em>All Users</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Realm#getAllGroups <em>All Groups</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Realm#getAllRoles <em>All Roles</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Realm#getAllPermissions <em>All Permissions</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Realm#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Realm#getDefaultAccess <em>Default Access</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Realm#getDefaultUserDirectory <em>Default User Directory</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Realm#getDefaultGroupDirectory <em>Default Group Directory</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Realm#getDefaultRoleDirectory <em>Default Role Directory</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getRealm()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='HasAdministrator'"
 * @generated
 */
public interface Realm extends SecurityElement, SecurityItemContainer
{
  /**
   * Returns the value of the '<em><b>Items</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.security.SecurityItem}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Items</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Items</em>' containment reference list.
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getRealm_Items()
   * @model containment="true"
   * @generated
   */
  EList<SecurityItem> getItems();

  /**
   * Returns the value of the '<em><b>All Users</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.security.User}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>All Users</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>All Users</em>' reference list.
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getRealm_AllUsers()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<User> getAllUsers();

  /**
   * Returns the value of the '<em><b>All Groups</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.security.Group}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>All Groups</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>All Groups</em>' reference list.
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getRealm_AllGroups()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<Group> getAllGroups();

  /**
   * Returns the value of the '<em><b>All Roles</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.security.Role}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>All Roles</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>All Roles</em>' reference list.
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getRealm_AllRoles()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<Role> getAllRoles();

  /**
   * Returns the value of the '<em><b>All Permissions</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.security.Permission}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>All Permissions</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>All Permissions</em>' reference list.
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getRealm_AllPermissions()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<Permission> getAllPermissions();

  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getRealm_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.Realm#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Default Access</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * <p>The minimum level of access for all users on all objects in the repository.
   * This is not the default level of access for a user on an object that is not otherwise covered by some permission rule; all permissions are implicitly elevated to this level.
   * </p><p>
   * This default/minimum can be overridden, even to a lesser access level, for specific {@linkplain User#setDefaultAccessOverride(Access) users} on a case-by-case basis.</p>
   * <!-- end-model-doc -->
   * @return the value of the '<em>Default Access</em>' attribute.
   * @see #setDefaultAccess(Access)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getRealm_DefaultAccess()
   * @model dataType="org.eclipse.emf.cdo.security.AccessObject"
   * @generated
   */
  Access getDefaultAccess();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.Realm#getDefaultAccess <em>Default Access</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Default Access</em>' attribute.
   * @see #getDefaultAccess()
   * @generated
   */
  void setDefaultAccess(Access value);

  /**
   * Returns the value of the '<em><b>Default User Directory</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Default User Directory</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * @since 4.2
   * <!-- end-user-doc -->
   * @return the value of the '<em>Default User Directory</em>' reference.
   * @see #setDefaultUserDirectory(Directory)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getRealm_DefaultUserDirectory()
   * @model
   * @generated
   */
  Directory getDefaultUserDirectory();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.Realm#getDefaultUserDirectory <em>Default User Directory</em>}' reference.
   * <!-- begin-user-doc -->
   * @since 4.2
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Default User Directory</em>' reference.
   * @see #getDefaultUserDirectory()
   * @generated
   */
  void setDefaultUserDirectory(Directory value);

  /**
   * Returns the value of the '<em><b>Default Group Directory</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Default Group Directory</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * @since 4.2
   * <!-- end-user-doc -->
   * @return the value of the '<em>Default Group Directory</em>' reference.
   * @see #setDefaultGroupDirectory(Directory)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getRealm_DefaultGroupDirectory()
   * @model
   * @generated
   */
  Directory getDefaultGroupDirectory();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.Realm#getDefaultGroupDirectory <em>Default Group Directory</em>}' reference.
   * <!-- begin-user-doc -->
   * @since 4.2
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Default Group Directory</em>' reference.
   * @see #getDefaultGroupDirectory()
   * @generated
   */
  void setDefaultGroupDirectory(Directory value);

  /**
   * Returns the value of the '<em><b>Default Role Directory</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Default Role Directory</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * @since 4.2
   * <!-- end-user-doc -->
   * @return the value of the '<em>Default Role Directory</em>' reference.
   * @see #setDefaultRoleDirectory(Directory)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getRealm_DefaultRoleDirectory()
   * @model
   * @generated
   */
  Directory getDefaultRoleDirectory();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.Realm#getDefaultRoleDirectory <em>Default Role Directory</em>}' reference.
   * <!-- begin-user-doc -->
   * @since 4.2
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Default Role Directory</em>' reference.
   * @see #getDefaultRoleDirectory()
   * @generated
   */
  void setDefaultRoleDirectory(Directory value);

} // SecurityRealm
