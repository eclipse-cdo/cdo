/*
 * Copyright (c) 2012, 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.security.Directory;
import org.eclipse.emf.cdo.security.Group;
import org.eclipse.emf.cdo.security.RealmUtil;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.SecurityItem;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Container</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.DirectoryImpl#getItems <em>Items</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.DirectoryImpl#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class DirectoryImpl extends SecurityItemImpl implements Directory
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected DirectoryImpl()
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
    return SecurityPackage.Literals.DIRECTORY;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<SecurityItem> getItems()
  {
    return (EList<SecurityItem>)eGet(SecurityPackage.Literals.DIRECTORY__ITEMS, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return (String)eGet(SecurityPackage.Literals.DIRECTORY__NAME, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    eSet(SecurityPackage.Literals.DIRECTORY__NAME, newName);
  }

  /**
   * @since 4.2
   */
  public Role getRole(String id)
  {
    return RealmUtil.findRole(getItems(), id);
  }

  /**
   * @since 4.2
   */
  public Group getGroup(String id)
  {
    return RealmUtil.findGroup(getItems(), id);
  }

  /**
   * @since 4.2
   */
  public User getUser(String id)
  {
    return RealmUtil.findUser(getItems(), id);
  }

  /**
   * @since 4.2
   */
  public Role addRole(String id)
  {
    Role role = SecurityFactory.eINSTANCE.createRole(id);
    getItems().add(role);
    return role;
  }

  /**
   * @since 4.2
   */
  public Group addGroup(String id)
  {
    Group group = SecurityFactory.eINSTANCE.createGroup(id);
    getItems().add(group);
    return group;
  }

  /**
   * @since 4.2
   */
  public User addUser(String id)
  {
    User user = SecurityFactory.eINSTANCE.createUser(id);
    getItems().add(user);
    return user;
  }

  /**
   * @since 4.2
   */
  public User addUser(String id, String password)
  {
    User user = SecurityFactory.eINSTANCE.createUser(id, password);
    getItems().add(user);
    return user;
  }

  /**
   * @since 4.3
   */
  public User setPassword(String id, String password)
  {
    return getRealm().setPassword(id, password);
  }

  /**
   * @since 4.2
   */
  public Role removeRole(String id)
  {
    return RealmUtil.removeRole(getItems(), id);
  }

  /**
   * @since 4.2
   */
  public Group removeGroup(String id)
  {
    return RealmUtil.removeGroup(getItems(), id);
  }

  /**
   * @since 4.2
   */
  public User removeUser(String id)
  {
    return RealmUtil.removeUser(getItems(), id);
  }

} // ContainerImpl
