/*
 * Copyright (c) 2012, 2013, 2016, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.Group;
import org.eclipse.emf.cdo.security.Permission;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.security.UserPassword;

import org.eclipse.net4j.util.StringUtil;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import java.util.HashSet;
import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>User</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getGroups <em>Groups</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getFirstName <em>First Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getLastName <em>Last Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getEmail <em>Email</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getDefaultAccessOverride <em>Default Access Override</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getDefaultAccess <em>Default Access</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#isLocked <em>Locked</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getPassword <em>Password</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getAllGroups <em>All Groups</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getAllRoles <em>All Roles</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getAllPermissions <em>All Permissions</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.UserImpl#getUnassignedRoles <em>Unassigned Roles</em>}</li>
 * </ul>
 *
 * @generated
 */
public class UserImpl extends AssigneeImpl implements User
{
  private EList<Group> allGroups = new DerivedList<Group>()
  {
    @Override
    protected InternalEObject getOwner()
    {
      return UserImpl.this;
    }

    @Override
    protected EStructuralFeature getFeature()
    {
      return SecurityPackage.Literals.USER__ALL_GROUPS;
    }

    @Override
    protected Object[] getData()
    {
      Set<Group> result = new HashSet<>();

      for (Group group : getGroups())
      {
        result.add(group);
        result.addAll(group.getAllInheritedGroups());
      }

      return result.toArray();
    }
  };

  private EList<Role> allRoles = new DerivedList<Role>()
  {
    @Override
    protected InternalEObject getOwner()
    {
      return UserImpl.this;
    }

    @Override
    protected EStructuralFeature getFeature()
    {
      return SecurityPackage.Literals.USER__ALL_ROLES;
    }

    @Override
    protected Object[] getData()
    {
      Set<Role> result = new HashSet<>();
      result.addAll(getRoles());

      for (Group group : getAllGroups())
      {
        result.addAll(group.getAllRoles());
      }

      return result.toArray();
    }
  };

  private EList<Permission> allPermissions = new DerivedList<Permission>()
  {
    @Override
    protected InternalEObject getOwner()
    {
      return UserImpl.this;
    }

    @Override
    protected EStructuralFeature getFeature()
    {
      return SecurityPackage.Literals.USER__ALL_PERMISSIONS;
    }

    @Override
    protected Object[] getData()
    {
      BasicEList<Permission> result = new BasicEList<>();

      for (Role role : getAllRoles())
      {
        result.addAll(role.getPermissions());
      }

      return result.toArray();
    }
  };

  private EList<Role> unassignedRoles = new DerivedList<Role>()
  {
    @Override
    protected InternalEObject getOwner()
    {
      return UserImpl.this;
    }

    @Override
    protected EStructuralFeature getFeature()
    {
      return SecurityPackage.Literals.USER__UNASSIGNED_ROLES;
    }

    @Override
    protected Object[] getData()
    {
      Set<Role> result = new HashSet<>();

      Realm realm = getRealm();
      if (realm != null)
      {
        result.addAll(realm.getAllRoles());
        result.removeAll(getAllRoles());
      }

      return result.toArray();
    }
  };

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected UserImpl()
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
    return SecurityPackage.Literals.USER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Group> getGroups()
  {
    return (EList<Group>)eGet(SecurityPackage.Literals.USER__GROUPS, true);
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
   * @generated NOT
   */
  @Override
  public EList<Role> getUnassignedRoles()
  {
    return unassignedRoles;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public String getLabel()
  {
    String label = getLastName();

    String firstName = getFirstName();
    if (!StringUtil.isEmpty(firstName))
    {
      label = firstName + " " + getLastName();
    }

    return label;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getFirstName()
  {
    return (String)eGet(SecurityPackage.Literals.USER__FIRST_NAME, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setFirstName(String newFirstName)
  {
    eSet(SecurityPackage.Literals.USER__FIRST_NAME, newFirstName);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getLastName()
  {
    return (String)eGet(SecurityPackage.Literals.USER__LAST_NAME, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setLastName(String newLastName)
  {
    eSet(SecurityPackage.Literals.USER__LAST_NAME, newLastName);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getEmail()
  {
    return (String)eGet(SecurityPackage.Literals.USER__EMAIL, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setEmail(String newEmail)
  {
    eSet(SecurityPackage.Literals.USER__EMAIL, newEmail);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Access getDefaultAccessOverride()
  {
    return (Access)eGet(SecurityPackage.Literals.USER__DEFAULT_ACCESS_OVERRIDE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setDefaultAccessOverride(Access newDefaultAccessOverride)
  {
    eSet(SecurityPackage.Literals.USER__DEFAULT_ACCESS_OVERRIDE, newDefaultAccessOverride);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public Access getDefaultAccess()
  {
    Access defaultAccessOverride = getDefaultAccessOverride();
    if (defaultAccessOverride != null)
    {
      return defaultAccessOverride;
    }

    Realm realm = getRealm();
    if (realm != null)
    {
      return realm.getDefaultAccess();
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean isLocked()
  {
    return (Boolean)eGet(SecurityPackage.Literals.USER__LOCKED, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setLocked(boolean newLocked)
  {
    eSet(SecurityPackage.Literals.USER__LOCKED, newLocked);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public UserPassword getPassword()
  {
    return (UserPassword)eGet(SecurityPackage.Literals.USER__PASSWORD, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setPassword(UserPassword newPassword)
  {
    eSet(SecurityPackage.Literals.USER__PASSWORD, newPassword);
  }

} // UserImpl
