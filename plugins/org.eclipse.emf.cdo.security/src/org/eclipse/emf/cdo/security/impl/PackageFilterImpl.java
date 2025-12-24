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
import org.eclipse.emf.cdo.security.PackageFilter;
import org.eclipse.emf.cdo.security.SecurityPackage;
import org.eclipse.emf.cdo.security.impl.PermissionImpl.CommitImpactContext;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Package Filter</b></em>'.
 * @since 4.3
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.PackageFilterImpl#getApplicablePackage <em>Applicable Package</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PackageFilterImpl extends PermissionFilterImpl implements PackageFilter
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PackageFilterImpl()
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
    return SecurityPackage.Literals.PACKAGE_FILTER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EPackage getApplicablePackage()
  {
    return (EPackage)eGet(SecurityPackage.Literals.PACKAGE_FILTER__APPLICABLE_PACKAGE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setApplicablePackage(EPackage newApplicablePackage)
  {
    eSet(SecurityPackage.Literals.PACKAGE_FILTER__APPLICABLE_PACKAGE, newApplicablePackage);
  }

  @Override
  protected boolean filter(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext, int level) throws Exception
  {
    EPackage actualPackage = revision.getEClass().getEPackage();
    EPackage applicablePackage = getApplicablePackage();
    return actualPackage == applicablePackage;
  }

  @Override
  public boolean isImpacted(CommitImpactContext context)
  {
    return false;
  }

  @Override
  public String format()
  {
    String label = "?";

    EPackage applicablePackage = getApplicablePackage();
    if (applicablePackage != null)
    {
      label = applicablePackage.getName();
    }

    return "package == " + label;
  }

} // PackageFilterImpl
