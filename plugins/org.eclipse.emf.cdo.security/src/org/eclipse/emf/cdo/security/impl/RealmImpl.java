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

import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.Group;
import org.eclipse.emf.cdo.security.Permission;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.RealmUtil;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityItem;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Realm</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.RealmImpl#getItems <em>Items</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.RealmImpl#getAllUsers <em>All Users</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.RealmImpl#getAllGroups <em>All Groups</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.RealmImpl#getAllRoles <em>All Roles</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.RealmImpl#getAllPermissions <em>All Permissions</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.RealmImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.RealmImpl#getDefaultAccess <em>Default Access</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RealmImpl extends SecurityElementImpl implements Realm
{
  private EList<User> allUsers = new CachedList<User>()
  {
    @Override
    protected InternalEObject getOwner()
    {
      return RealmImpl.this;
    }

    @Override
    protected EStructuralFeature getFeature()
    {
      return SecurityPackage.Literals.REALM__ALL_USERS;
    }

    @Override
    protected Object[] getData()
    {
      EList<SecurityItem> items = getItems();
      return RealmUtil.allUsers(items).toArray();
    }
  };

  private EList<Group> allGroups = new CachedList<Group>()
  {
    @Override
    protected InternalEObject getOwner()
    {
      return RealmImpl.this;
    }

    @Override
    protected EStructuralFeature getFeature()
    {
      return SecurityPackage.Literals.REALM__ALL_GROUPS;
    }

    @Override
    protected Object[] getData()
    {
      EList<SecurityItem> items = getItems();
      return RealmUtil.allGroups(items).toArray();
    }
  };

  private EList<Role> allRoles = new CachedList<Role>()
  {
    @Override
    protected InternalEObject getOwner()
    {
      return RealmImpl.this;
    }

    @Override
    protected EStructuralFeature getFeature()
    {
      return SecurityPackage.Literals.REALM__ALL_ROLES;
    }

    @Override
    protected Object[] getData()
    {
      EList<SecurityItem> items = getItems();
      return RealmUtil.allRoles(items).toArray();
    }
  };

  private EList<Permission> allPermissions = new CachedList<Permission>()
  {
    @Override
    protected InternalEObject getOwner()
    {
      return RealmImpl.this;
    }

    @Override
    protected EStructuralFeature getFeature()
    {
      return SecurityPackage.Literals.REALM__ALL_PERMISSIONS;
    }

    @Override
    protected Object[] getData()
    {
      EList<SecurityItem> items = getItems();
      return RealmUtil.allPermissions(items).toArray();
    }
  };

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected RealmImpl()
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
    return SecurityPackage.Literals.REALM;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  public EList<SecurityItem> getItems()
  {
    return (EList<SecurityItem>)eGet(SecurityPackage.Literals.REALM__ITEMS, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<User> getAllUsers()
  {
    return allUsers;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<Group> getAllGroups()
  {
    return allGroups;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<Role> getAllRoles()
  {
    return allRoles;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  public EList<Permission> getAllPermissions()
  {
    return allPermissions;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return (String)eGet(SecurityPackage.Literals.REALM__NAME, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    eSet(SecurityPackage.Literals.REALM__NAME, newName);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Access getDefaultAccess()
  {
    return (Access)eGet(SecurityPackage.Literals.REALM__DEFAULT_ACCESS, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setDefaultAccess(Access newDefaultAccess)
  {
    eSet(SecurityPackage.Literals.REALM__DEFAULT_ACCESS, newDefaultAccess);
  }

  @Override
  public Realm getRealm()
  {
    return this;
  }

} // SecurityRealmImpl
