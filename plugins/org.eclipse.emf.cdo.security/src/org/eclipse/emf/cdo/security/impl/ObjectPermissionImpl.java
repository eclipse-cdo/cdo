/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.internal.security.PermissionUtil;
import org.eclipse.emf.cdo.security.ObjectPermission;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Object Permission</b></em>'.
 * @since 4.2
 * <!-- end-user-doc -->
 *
 * @generated
 */
@Deprecated
public abstract class ObjectPermissionImpl extends PermissionImpl implements ObjectPermission
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  protected ObjectPermissionImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Deprecated
  @Override
  protected EClass eStaticClass()
  {
    return SecurityPackage.Literals.OBJECT_PERMISSION;
  }

  @Deprecated
  protected CDOView getView(CDORevisionProvider revisionProvider)
  {
    return PermissionUtil.getView(revisionProvider);
  }

  /**
   * @ADDED
   */
  @Deprecated
  @Override
  public boolean isApplicable(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext)
  {
    CDOView view = getView(revisionProvider);
    CDOID id = revision.getID();

    CDOObject object = view.getObject(id);
    return isApplicable(object, securityContext);
  }

  @Deprecated
  protected abstract boolean isApplicable(CDOObject object, CDOBranchPoint securityContext);

} // ObjectPermissionImpl
