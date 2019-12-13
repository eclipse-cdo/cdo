/*
 * Copyright (c) 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.security.Assignee;
import org.eclipse.emf.cdo.security.Permission;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Role</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.RoleImpl#getId <em>Id</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.RoleImpl#getPermissions <em>Permissions</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.RoleImpl#getAssignees <em>Assignees</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RoleImpl extends SecurityItemImpl implements Role
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected RoleImpl()
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
    return SecurityPackage.Literals.ROLE;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Assignee> getAssignees()
  {
    return (EList<Assignee>)eGet(SecurityPackage.Literals.ROLE__ASSIGNEES, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getId()
  {
    return (String)eGet(SecurityPackage.Literals.ROLE__ID, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setId(String newId)
  {
    eSet(SecurityPackage.Literals.ROLE__ID, newId);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<Permission> getPermissions()
  {
    return (EList<Permission>)eGet(SecurityPackage.Literals.ROLE__PERMISSIONS, true);
  }

} // RoleImpl
