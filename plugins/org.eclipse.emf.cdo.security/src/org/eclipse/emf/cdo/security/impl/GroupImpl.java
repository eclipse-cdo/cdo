/*
 * Copyright (c) 2012, 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.security.Group;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.User;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import java.util.Set;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Group</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.GroupImpl#getUsers <em>Users</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.GroupImpl#getInheritedGroups <em>Inherited Groups</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.GroupImpl#getInheritingGroups <em>Inheriting Groups</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.GroupImpl#getAllInheritedGroups <em>All Inherited Groups</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.GroupImpl#getAllInheritingGroups <em>All Inheriting Groups</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.GroupImpl#getAllRoles <em>All Roles</em>}</li>
 * </ul>
 *
 * @generated
 */
public class GroupImpl extends AssigneeImpl implements Group
{
  private EList<Group> allInheritedGroups = new DerivedList.RecursionSafe<Group, Group>()
  {
    @Override
    protected InternalEObject getOwner()
    {
      return GroupImpl.this;
    }

    @Override
    protected EStructuralFeature getFeature()
    {
      return SecurityPackage.Literals.GROUP__ALL_INHERITED_GROUPS;
    }

    @Override
    protected void getData(Group group, Set<Object> visited, Set<Group> result)
    {
      if (visited.add(group))
      {
        result.add(group);

        for (Group inheritedGroup : group.getInheritedGroups())
        {
          getData(inheritedGroup, visited, result);
        }
      }
    }
  };

  private EList<Group> allInheritingGroups = new DerivedList.RecursionSafe<Group, Group>()
  {
    @Override
    protected InternalEObject getOwner()
    {
      return GroupImpl.this;
    }

    @Override
    protected EStructuralFeature getFeature()
    {
      return SecurityPackage.Literals.GROUP__ALL_INHERITING_GROUPS;
    }

    @Override
    protected void getData(Group group, Set<Object> visited, Set<Group> result)
    {
      if (visited.add(group))
      {
        result.add(group);

        for (Group inheritingGroup : group.getInheritingGroups())
        {
          getData(inheritingGroup, visited, result);
        }
      }
    }
  };

  private EList<Role> allRoles = new DerivedList.RecursionSafe<Role, Group>()
  {
    @Override
    protected InternalEObject getOwner()
    {
      return GroupImpl.this;
    }

    @Override
    protected EStructuralFeature getFeature()
    {
      return SecurityPackage.Literals.GROUP__ALL_ROLES;
    }

    @Override
    protected void getData(Group group, Set<Object> visited, Set<Role> result)
    {
      if (visited.add(group))
      {
        EList<Role> roles = group.getRoles();
        result.addAll(roles);

        for (Group inheritedGroup : group.getInheritedGroups())
        {
          getData(inheritedGroup, visited, result);
        }
      }
    }
  };

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected GroupImpl()
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
    return SecurityPackage.Literals.GROUP;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<User> getUsers()
  {
    return (EList<User>)eGet(SecurityPackage.Literals.GROUP__USERS, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Group> getInheritedGroups()
  {
    return (EList<Group>)eGet(SecurityPackage.Literals.GROUP__INHERITED_GROUPS, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Group> getInheritingGroups()
  {
    return (EList<Group>)eGet(SecurityPackage.Literals.GROUP__INHERITING_GROUPS, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public EList<Group> getAllInheritingGroups()
  {
    return allInheritingGroups;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public EList<Group> getAllInheritedGroups()
  {
    return allInheritedGroups;
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

} // GroupImpl
