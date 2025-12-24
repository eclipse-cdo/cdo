/*
 * Copyright (c) 2013, 2016 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.security.impl.PermissionImpl.CommitImpactContext;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Permission Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 *
 *
 * @see org.eclipse.emf.cdo.security.SecurityPackage#getPermissionFilter()
 * @model abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface PermissionFilter extends CDOObject
{
  boolean isApplicable(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext, int level) throws Exception;

  boolean isImpacted(CommitImpactContext context);

  String format();

} // PermissionFilter
