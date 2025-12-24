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

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.internal.security.bundle.OM;
import org.eclipse.emf.cdo.security.FilterPermission;
import org.eclipse.emf.cdo.security.PermissionFilter;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Filter Permission</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.FilterPermissionImpl#getFilters <em>Filters</em>}</li>
 * </ul>
 *
 * @generated
 */
public class FilterPermissionImpl extends PermissionImpl implements FilterPermission
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, FilterPermissionImpl.class);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected FilterPermissionImpl()
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
    return SecurityPackage.Literals.FILTER_PERMISSION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  @SuppressWarnings("unchecked")
  public EList<PermissionFilter> getFilters()
  {
    return (EList<PermissionFilter>)eGet(SecurityPackage.Literals.FILTER_PERMISSION__FILTERS, true);
  }

  @Override
  public boolean isApplicable(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Checking {0} permission for {1}", getAccess(), revision); //$NON-NLS-1$
    }

    for (PermissionFilter filter : getFilters())
    {
      try
      {
        if (!filter.isApplicable(revision, revisionProvider, securityContext, 1))
        {
          return false;
        }
      }
      catch (Exception ex)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace(ex);
        }

        return false;
      }
    }

    return true;
  }

  @Override
  public boolean isImpacted(CommitImpactContext context)
  {
    for (PermissionFilter filter : getFilters())
    {
      if (filter.isImpacted(context))
      {
        return true;
      }
    }

    return false;
  }

} // FilterPermissionImpl
