/*
 * Copyright (c) 2012, 2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>User</b></em>'.
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.User#getGroups <em>Groups</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.User#getLabel <em>Label</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.User#getFirstName <em>First Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.User#getLastName <em>Last Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.User#getEmail <em>Email</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.User#getDefaultAccessOverride <em>Default Access Override</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.User#getDefaultAccess <em>Default Access</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.User#isLocked <em>Locked</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.User#getPassword <em>Password</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.User#getAllGroups <em>All Groups</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.User#getAllRoles <em>All Roles</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.User#getAllPermissions <em>All Permissions</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.User#getUnassignedRoles <em>Unassigned Roles</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getUser()
 * @model
 * @generated
 */
public interface User extends Assignee
{
  /**
   * @since 4.3
   */
  public static final String ADMINISTRATOR = "Administrator";

  /**
   * @since 4.6
   */
  public static final String ADMINISTRATOR_DEFAULT_PASSWORD = "0000";

  /**
   * Returns the value of the '<em><b>Groups</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.security.Group}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.security.Group#getUsers <em>Users</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Groups</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Groups</em>' reference list.
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getUser_Groups()
   * @see org.eclipse.emf.cdo.security.Group#getUsers
   * @model opposite="users"
   * @generated
   */
  EList<Group> getGroups();

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
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getUser_AllGroups()
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
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getUser_AllRoles()
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
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getUser_AllPermissions()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<Permission> getAllPermissions();

  /**
   * Returns the value of the '<em><b>Unassigned Roles</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.security.Role}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Unassigned Roles</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Unassigned Roles</em>' reference list.
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getUser_UnassignedRoles()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<Role> getUnassignedRoles();

  /**
   * Returns the value of the '<em><b>Label</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Label</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Label</em>' attribute.
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getUser_Label()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  String getLabel();

  /**
   * Returns the value of the '<em><b>First Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>First Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>First Name</em>' attribute.
   * @see #setFirstName(String)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getUser_FirstName()
   * @model
   * @generated
   */
  String getFirstName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.User#getFirstName <em>First Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>First Name</em>' attribute.
   * @see #getFirstName()
   * @generated
   */
  void setFirstName(String value);

  /**
   * Returns the value of the '<em><b>Last Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Last Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Last Name</em>' attribute.
   * @see #setLastName(String)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getUser_LastName()
   * @model
   * @generated
   */
  String getLastName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.User#getLastName <em>Last Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Last Name</em>' attribute.
   * @see #getLastName()
   * @generated
   */
  void setLastName(String value);

  /**
   * Returns the value of the '<em><b>Email</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Email</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Email</em>' attribute.
   * @see #setEmail(String)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getUser_Email()
   * @model
   * @generated
   */
  String getEmail();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.User#getEmail <em>Email</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Email</em>' attribute.
   * @see #getEmail()
   * @generated
   */
  void setEmail(String value);

  /**
   * Returns the value of the '<em><b>Default Access Override</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * <p>Overrides the {@link Realm}'s {@linkplain Realm#getDefaultAccess() default access permission} for this user.
   * When this attribute is set, its value establishes the minimum level of access for the user on all objects in the repository.  In particular, it is not the access permission that applies to objects that are not otherwise covered by some permission rule for the user.  All permissions are implicitly elevated at least to this override for the user.</p>
   * <!-- end-model-doc -->
   * @return the value of the '<em>Default Access Override</em>' attribute.
   * @see #setDefaultAccessOverride(Access)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getUser_DefaultAccessOverride()
   * @model dataType="org.eclipse.emf.cdo.security.AccessObject"
   * @generated
   */
  Access getDefaultAccessOverride();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.User#getDefaultAccessOverride <em>Default Access Override</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Default Access Override</em>' attribute.
   * @see #getDefaultAccessOverride()
   * @generated
   */
  void setDefaultAccessOverride(Access value);

  /**
   * Returns the value of the '<em><b>Default Access</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * <!-- begin-model-doc -->
   * <p>The minimum level of access for the user on all objects in the repository.
   * This is either the default set for {@linkplain Realm#getDefaultAccess() all users} in the {@link Realm} or an {@linkplain #getDefaultAccessOverride() override} set specifically on this user.</p>
   * <!-- end-model-doc -->
   * @return the value of the '<em>Default Access</em>' attribute.
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getUser_DefaultAccess()
   * @model dataType="org.eclipse.emf.cdo.security.AccessObject" transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  Access getDefaultAccess();

  /**
   * Returns the value of the '<em><b>Locked</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Locked</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Locked</em>' attribute.
   * @see #setLocked(boolean)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getUser_Locked()
   * @model
   * @generated
   */
  boolean isLocked();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.User#isLocked <em>Locked</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Locked</em>' attribute.
   * @see #isLocked()
   * @generated
   */
  void setLocked(boolean value);

  /**
   * Returns the value of the '<em><b>Password</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Password</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Password</em>' containment reference.
   * @see #setPassword(UserPassword)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getUser_Password()
   * @model containment="true"
   * @generated
   */
  UserPassword getPassword();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.User#getPassword <em>Password</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Password</em>' containment reference.
   * @see #getPassword()
   * @generated
   */
  void setPassword(UserPassword value);

} // User
