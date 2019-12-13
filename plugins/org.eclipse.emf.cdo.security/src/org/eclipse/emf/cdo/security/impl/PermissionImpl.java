/*
 * Copyright (c) 2012-2014, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.security.PermissionUtil;
import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.Permission;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Permission</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.PermissionImpl#getRole <em>Role</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.PermissionImpl#getAccess <em>Access</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class PermissionImpl extends CDOObjectImpl implements Permission
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PermissionImpl()
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
    return SecurityPackage.Literals.PERMISSION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected int eStaticFeatureCount()
  {
    return 0;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Role getRole()
  {
    return (Role)eGet(SecurityPackage.Literals.PERMISSION__ROLE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setRole(Role newRole)
  {
    eSet(SecurityPackage.Literals.PERMISSION__ROLE, newRole);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Access getAccess()
  {
    return (Access)eGet(SecurityPackage.Literals.PERMISSION__ACCESS, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setAccess(Access newAccess)
  {
    eSet(SecurityPackage.Literals.PERMISSION__ACCESS, newAccess);
  }

  /**
   * @since 4.3
   */
  protected final String getUser()
  {
    return PermissionUtil.getUser();
  }

  /**
   * Should be overridden in subclasses to do proper impact analysis!
   *
   * @since 4.3
   */
  public boolean isImpacted(CommitImpactContext context)
  {
    return true;
  }

  /**
   * A description of the current commit that is used to {@link PermissionImpl#isImpacted(CommitImpactContext) determine}
   * the impact of this commit on the configured {@link Permission permissions}.
   *
   * @author Eike Stepper
   * @since 4.3
   */
  public interface CommitImpactContext
  {
    /**
     * Returns the revisions of the new objects of the current commit.
     * <p>
     * Read only!
     */
    public CDORevision[] getNewObjects();

    /**
     * Returns the revisions of the changed objects of the current commit.
     * <p>
     * Read only!
     */
    public CDORevision[] getDirtyObjects();

    /**
     * Returns the revision deltas of the changed objects of the current commit.
     * <p>
     * Read only!
     */
    public CDORevisionDelta[] getDirtyObjectDeltas();

    /**
     * Returns the CDOIDs of the detached objects of the current commit.
     * <p>
     * Read only!
     */
    public CDOID[] getDetachedObjects();
  }

} // PermissionImpl
