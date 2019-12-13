/*
 * Copyright (c) 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.internal.security.PermissionUtil;
import org.eclipse.emf.cdo.internal.security.bundle.OM;
import org.eclipse.emf.cdo.security.PermissionFilter;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Permission Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 *
 * @generated
 */
public abstract class PermissionFilterImpl extends CDOObjectImpl implements PermissionFilter
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, PermissionFilterImpl.class);

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PermissionFilterImpl()
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
    return SecurityPackage.Literals.PERMISSION_FILTER;
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

  @Override
  public boolean isApplicable(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext, int level) throws Exception
  {
    String msg = StringUtil.NL;
    boolean tracing = TRACER.isEnabled();
    if (tracing)
    {
      msg = StringUtil.create(' ', level << 1) + getClass().getSimpleName() + ": " + format();
      TRACER.trace(msg);
    }

    boolean result = false;

    try
    {
      result = filter(revision, revisionProvider, securityContext, level);
    }
    catch (Exception ex)
    {
      if (tracing)
      {
        TRACER.trace(ex);
      }
    }
    finally
    {
      if (tracing)
      {
        TRACER.trace(msg + " = " + result);
      }
    }

    return result;
  }

  protected abstract boolean filter(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext, int level) throws Exception;

  /**
   * @since 4.3
   */
  protected final String getUser()
  {
    return PermissionUtil.getUser();
  }

} // PermissionFilterImpl
