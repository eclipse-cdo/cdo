/*
 * Copyright (c) 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.security.LinkedFilter;
import org.eclipse.emf.cdo.security.PermissionFilter;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.impl.PermissionImpl.CommitImpactContext;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Reference Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.LinkedFilterImpl#getFilter <em>Filter</em>}</li>
 * </ul>
 *
 * @generated
 */
public class LinkedFilterImpl extends PermissionFilterImpl implements LinkedFilter
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected LinkedFilterImpl()
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
    return SecurityPackage.Literals.LINKED_FILTER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public PermissionFilter getFilter()
  {
    return (PermissionFilter)eGet(SecurityPackage.Literals.LINKED_FILTER__FILTER, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setFilter(PermissionFilter newFilter)
  {
    eSet(SecurityPackage.Literals.LINKED_FILTER__FILTER, newFilter);
  }

  @Override
  protected boolean filter(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext, int level) throws Exception
  {
    PermissionFilter filter = getFilter();
    return filter.isApplicable(revision, revisionProvider, securityContext, level + 1);
  }

  @Override
  public boolean isImpacted(CommitImpactContext context)
  {
    PermissionFilter filter = getFilter();
    return filter.isImpacted(context);
  }

  @Override
  public String format()
  {
    PermissionFilter filter = getFilter();
    if (filter == null)
    {
      return "?";
    }

    return filter.format();
  }
} // ReferenceFilterImpl
