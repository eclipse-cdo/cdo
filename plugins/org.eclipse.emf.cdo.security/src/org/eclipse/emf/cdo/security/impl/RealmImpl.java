/*
 * Copyright (c) 2012, 2013, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.security.Directory;
import org.eclipse.emf.cdo.security.Group;
import org.eclipse.emf.cdo.security.Permission;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.RealmUtil;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.SecurityItem;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.security.UserPassword;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.SecurityUtil;

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
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.RealmImpl#getItems <em>Items</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.RealmImpl#getAllUsers <em>All Users</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.RealmImpl#getAllGroups <em>All Groups</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.RealmImpl#getAllRoles <em>All Roles</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.RealmImpl#getAllPermissions <em>All Permissions</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.RealmImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.RealmImpl#getDefaultAccess <em>Default Access</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.RealmImpl#getDefaultUserDirectory <em>Default User Directory</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.RealmImpl#getDefaultGroupDirectory <em>Default Group Directory</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.RealmImpl#getDefaultRoleDirectory <em>Default Role Directory</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RealmImpl extends SecurityElementImpl implements Realm
{
  private EList<User> allUsers = new DerivedList<User>()
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

  private EList<Group> allGroups = new DerivedList<Group>()
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

  private EList<Role> allRoles = new DerivedList<Role>()
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

  private EList<Permission> allPermissions = new DerivedList<Permission>()
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
  @Override
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
  @Override
  public EList<User> getAllUsers()
  {
    return allUsers;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public EList<Group> getAllGroups()
  {
    return allGroups;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public EList<Role> getAllRoles()
  {
    return allRoles;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public EList<Permission> getAllPermissions()
  {
    return allPermissions;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return (String)eGet(SecurityPackage.Literals.REALM__NAME, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    eSet(SecurityPackage.Literals.REALM__NAME, newName);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Access getDefaultAccess()
  {
    return (Access)eGet(SecurityPackage.Literals.REALM__DEFAULT_ACCESS, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDefaultAccess(Access newDefaultAccess)
  {
    eSet(SecurityPackage.Literals.REALM__DEFAULT_ACCESS, newDefaultAccess);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.2
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Directory getDefaultUserDirectory()
  {
    return (Directory)eGet(SecurityPackage.Literals.REALM__DEFAULT_USER_DIRECTORY, true);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.2
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDefaultUserDirectory(Directory newDefaultUserDirectory)
  {
    eSet(SecurityPackage.Literals.REALM__DEFAULT_USER_DIRECTORY, newDefaultUserDirectory);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.2
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Directory getDefaultGroupDirectory()
  {
    return (Directory)eGet(SecurityPackage.Literals.REALM__DEFAULT_GROUP_DIRECTORY, true);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.2
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDefaultGroupDirectory(Directory newDefaultGroupDirectory)
  {
    eSet(SecurityPackage.Literals.REALM__DEFAULT_GROUP_DIRECTORY, newDefaultGroupDirectory);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.2
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Directory getDefaultRoleDirectory()
  {
    return (Directory)eGet(SecurityPackage.Literals.REALM__DEFAULT_ROLE_DIRECTORY, true);
  }

  /**
   * <!-- begin-user-doc -->
   * @since 4.2
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDefaultRoleDirectory(Directory newDefaultRoleDirectory)
  {
    eSet(SecurityPackage.Literals.REALM__DEFAULT_ROLE_DIRECTORY, newDefaultRoleDirectory);
  }

  /**
   * @since 4.2
   */
  @Override
  public Role getRole(String id)
  {
    return RealmUtil.findRole(getItems(), id);
  }

  /**
   * @since 4.2
   */
  @Override
  public Group getGroup(String id)
  {
    return RealmUtil.findGroup(getItems(), id);
  }

  /**
   * @since 4.2
   */
  @Override
  public User getUser(String id)
  {
    return RealmUtil.findUser(getItems(), id);
  }

  /**
   * @since 4.2
   */
  protected EList<SecurityItem> getRoleItems()
  {
    Directory directory = getDefaultRoleDirectory();
    return directory != null ? directory.getItems() : getItems();
  }

  /**
   * @since 4.2
   */
  protected EList<SecurityItem> getGroupItems()
  {
    Directory directory = getDefaultGroupDirectory();
    return directory != null ? directory.getItems() : getItems();
  }

  /**
   * @since 4.2
   */
  protected EList<SecurityItem> getUserItems()
  {
    Directory directory = getDefaultUserDirectory();
    return directory != null ? directory.getItems() : getItems();
  }

  /**
   * @since 4.2
   */
  @Override
  public Role addRole(String id)
  {
    Role role = SecurityFactory.eINSTANCE.createRole(id);

    EList<SecurityItem> items = getRoleItems();
    items.add(role);
    return role;
  }

  /**
   * @since 4.2
   */
  @Override
  public Group addGroup(String id)
  {
    Group group = SecurityFactory.eINSTANCE.createGroup(id);

    EList<SecurityItem> items = getGroupItems();
    items.add(group);
    return group;
  }

  /**
   * @since 4.2
   */
  @Override
  public User addUser(String id)
  {
    User user = SecurityFactory.eINSTANCE.createUser(id);
    return addUser(user);
  }

  /**
   * @since 4.2
   */
  @Override
  public User addUser(String id, String password)
  {
    User user = SecurityFactory.eINSTANCE.createUser(id, password);
    return addUser(user);
  }

  /**
   * @since 4.3
   */
  @Override
  public User addUser(IPasswordCredentials credentials)
  {
    return addUser(credentials.getUserID(), SecurityUtil.toString(credentials.getPassword()));
  }

  /**
   * @since 4.2
   */
  protected User addUser(User user)
  {
    EList<SecurityItem> items = getUserItems();
    items.add(user);
    return user;
  }

  /**
   * @since 4.3
   */
  @Override
  public User setPassword(String id, String password)
  {
    UserPassword userPassword = null;
    if (!StringUtil.isEmpty(password))
    {
      userPassword = SecurityFactory.eINSTANCE.createUserPassword();
      userPassword.setEncrypted(password);
    }

    User user = getUser(id);
    user.setPassword(userPassword);
    return user;
  }

  /**
   * @since 4.2
   */
  @Override
  public Role removeRole(String id)
  {
    EList<SecurityItem> items = getRoleItems();
    Role role = RealmUtil.removeRole(items, id);
    if (role == null)
    {
      EList<SecurityItem> realmItems = getItems();
      if (items != realmItems)
      {
        role = RealmUtil.removeRole(realmItems, id);
      }
    }

    return role;
  }

  /**
   * @since 4.2
   */
  @Override
  public Group removeGroup(String id)
  {
    EList<SecurityItem> items = getGroupItems();
    Group group = RealmUtil.removeGroup(items, id);
    if (group == null)
    {
      EList<SecurityItem> realmItems = getItems();
      if (items != realmItems)
      {
        group = RealmUtil.removeGroup(realmItems, id);
      }
    }

    return group;
  }

  /**
   * @since 4.2
   */
  @Override
  public User removeUser(String id)
  {
    EList<SecurityItem> items = getUserItems();
    User user = RealmUtil.removeUser(items, id);
    if (user == null)
    {
      EList<SecurityItem> realmItems = getItems();
      if (items != realmItems)
      {
        user = RealmUtil.removeUser(realmItems, id);
      }
    }

    return user;
  }

  @Override
  public Realm getRealm()
  {
    return this;
  }

} // SecurityRealmImpl
