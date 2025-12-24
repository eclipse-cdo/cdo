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
 * A representation of the model object '<em><b>Group</b></em>'.
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.Group#getUsers <em>Users</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Group#getInheritedGroups <em>Inherited Groups</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Group#getInheritingGroups <em>Inheriting Groups</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Group#getAllInheritedGroups <em>All Inherited Groups</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Group#getAllInheritingGroups <em>All Inheriting Groups</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Group#getAllRoles <em>All Roles</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getGroup()
 * @model annotation="http://www.eclipse.org/emf/2002/Ecore constraints='AcyclicInheritance'"
 * @generated
 */
public interface Group extends Assignee
{
  /**
   * @since 4.3
   */
  public static final String ADMINISTRATORS = "Administrators";

  /**
   * Returns the value of the '<em><b>Users</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.security.User}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.security.User#getGroups <em>Groups</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Users</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Users</em>' reference list.
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getGroup_Users()
   * @see org.eclipse.emf.cdo.security.User#getGroups
   * @model opposite="groups"
   * @generated
   */
  EList<User> getUsers();

  /**
   * Returns the value of the '<em><b>Inherited Groups</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.security.Group}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.security.Group#getInheritingGroups <em>Inheriting Groups</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Inherited Groups</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Inherited Groups</em>' reference list.
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getGroup_InheritedGroups()
   * @see org.eclipse.emf.cdo.security.Group#getInheritingGroups
   * @model opposite="inheritingGroups"
   * @generated
   */
  EList<Group> getInheritedGroups();

  /**
   * Returns the value of the '<em><b>Inheriting Groups</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.security.Group}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.security.Group#getInheritedGroups <em>Inherited Groups</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Inheriting Groups</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Inheriting Groups</em>' reference list.
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getGroup_InheritingGroups()
   * @see org.eclipse.emf.cdo.security.Group#getInheritedGroups
   * @model opposite="inheritedGroups"
   * @generated
   */
  EList<Group> getInheritingGroups();

  /**
   * Returns the value of the '<em><b>All Inheriting Groups</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.security.Group}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>All Inheriting Groups</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>All Inheriting Groups</em>' reference list.
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getGroup_AllInheritingGroups()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<Group> getAllInheritingGroups();

  /**
   * Returns the value of the '<em><b>All Inherited Groups</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.security.Group}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>All Inherited Groups</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>All Inherited Groups</em>' reference list.
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getGroup_AllInheritedGroups()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<Group> getAllInheritedGroups();

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
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getGroup_AllRoles()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<Role> getAllRoles();

} // Group
