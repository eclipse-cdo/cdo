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
import org.eclipse.emf.cdo.security.ClassPermission;
import org.eclipse.emf.cdo.security.SecurityPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Class Permission</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.security.impl.ClassPermissionImpl#getApplicableClass <em>Applicable Class</em>}</li>
 * </ul>
 *
 * @generated
 */
@Deprecated
public class ClassPermissionImpl extends PermissionImpl implements ClassPermission
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ClassPermissionImpl()
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
    return SecurityPackage.Literals.CLASS_PERMISSION;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EClass getApplicableClass()
  {
    return (EClass)eGet(SecurityPackage.Literals.CLASS_PERMISSION__APPLICABLE_CLASS, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setApplicableClass(EClass newApplicableClass)
  {
    eSet(SecurityPackage.Literals.CLASS_PERMISSION__APPLICABLE_CLASS, newApplicableClass);
  }

  @Override
  public boolean isApplicable(CDORevision revision, CDORevisionProvider revisionProvider, CDOBranchPoint securityContext)
  {
    EClass actualClass = revision.getEClass();
    EClass applicableClass = getApplicableClass();
    return actualClass == applicableClass;
  }

  @Override
  public boolean isImpacted(CommitImpactContext context)
  {
    return false;
  }

} // ClassPermissionImpl
