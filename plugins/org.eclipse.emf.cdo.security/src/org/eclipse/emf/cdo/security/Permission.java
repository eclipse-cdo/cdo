/*
 * Copyright (c) 2012, 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Permission</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.Permission#getRole <em>Role</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.Permission#getAccess <em>Access</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getPermission()
 * @model abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface Permission extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Role</b></em>' container reference.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.security.Role#getPermissions <em>Permissions</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Role</em>' container reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Role</em>' container reference.
   * @see #setRole(Role)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getPermission_Role()
   * @see org.eclipse.emf.cdo.security.Role#getPermissions
   * @model opposite="permissions" required="true" transient="false"
   * @generated
   */
  Role getRole();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.Permission#getRole <em>Role</em>}' container reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Role</em>' container reference.
   * @see #getRole()
   * @generated
   */
  void setRole(Role value);

  /**
   * Returns the value of the '<em><b>Access</b></em>' attribute.
   * The default value is <code>"WRITE"</code>.
   * The literals are from the enumeration {@link org.eclipse.emf.cdo.security.Access}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Access</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Access</em>' attribute.
   * @see org.eclipse.emf.cdo.security.Access
   * @see #setAccess(Access)
   * @see org.eclipse.emf.cdo.security.SecurityPackage#getPermission_Access()
   * @model default="WRITE" required="true"
   * @generated
   */
  Access getAccess();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.security.Permission#getAccess <em>Access</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Access</em>' attribute.
   * @see org.eclipse.emf.cdo.security.Access
   * @see #getAccess()
   * @generated
   */
  void setAccess(Access value);

  boolean isApplicable(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext);

} // Permission
