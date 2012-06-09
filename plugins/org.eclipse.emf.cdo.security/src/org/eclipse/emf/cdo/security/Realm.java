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

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Realm</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.Realm#getItems <em>Items</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Realm#getAllUsers <em>All Users</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Realm#getAllGroups <em>All Groups</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Realm#getAllRoles <em>All Roles</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Realm#getAllChecks <em>All Checks</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Realm#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getRealm()
 * @model
 * @generated
 */
public interface Realm extends SecurityElement
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
   * Returns the value of the '<em><b>All Checks</b></em>' reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.security.Check}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>All Checks</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>All Checks</em>' reference list.
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getRealm_AllChecks()
   * @model transient="true" changeable="false" volatile="true" derived="true"
   * @generated
   */
  EList<Check> getAllChecks();

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

} // SecurityRealm
