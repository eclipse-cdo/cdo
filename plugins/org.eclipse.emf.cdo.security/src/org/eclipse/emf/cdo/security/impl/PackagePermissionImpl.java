/*
 * Copyright (c) 2012, 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.security.PackagePermission;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Package Permission</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.PackagePermissionImpl#getApplicablePackage <em>Applicable Package</em>}</li>
 * </ul>
 *
 * @generated
 */
@Deprecated
public class PackagePermissionImpl extends PermissionImpl implements PackagePermission
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PackagePermissionImpl()
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
    return SecurityPackage.Literals.PACKAGE_PERMISSION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EPackage getApplicablePackage()
  {
    return (EPackage)eGet(SecurityPackage.Literals.PACKAGE_PERMISSION__APPLICABLE_PACKAGE, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setApplicablePackage(EPackage newApplicablePackage)
  {
    eSet(SecurityPackage.Literals.PACKAGE_PERMISSION__APPLICABLE_PACKAGE, newApplicablePackage);
  }

  @Override
  public boolean isApplicable(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext)
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

} // PackagePermissionImpl
