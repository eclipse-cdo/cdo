/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.security.AndFilter;
import org.eclipse.emf.cdo.security.PermissionFilter;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>And Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class AndFilterImpl extends CombinedFilterImpl implements AndFilter
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected AndFilterImpl()
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
    return SecurityPackage.Literals.AND_FILTER;
  }

  @Override
  protected boolean filter(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext,
      int level) throws Exception
  {
    ++level;
    for (PermissionFilter operand : getOperands())
    {
      if (!operand.isApplicable(revision, revisionProvider, securityContext, level))
      {
        return false;
      }
    }

    return true;
  }

  @Override
  protected String formatOperator()
  {
    return "And";
  }

} // AndFilterImpl
