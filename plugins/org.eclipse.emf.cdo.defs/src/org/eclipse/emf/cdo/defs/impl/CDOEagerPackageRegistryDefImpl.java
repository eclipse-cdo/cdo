/*
 * Copyright (c) 2008-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.defs.impl;

import org.eclipse.emf.cdo.defs.CDODefsPackage;
import org.eclipse.emf.cdo.defs.CDOEagerPackageRegistryDef;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Eager Package Registry Def</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class CDOEagerPackageRegistryDefImpl extends CDOPackageRegistryDefImpl implements CDOEagerPackageRegistryDef
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected CDOEagerPackageRegistryDefImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return CDODefsPackage.Literals.CDO_EAGER_PACKAGE_REGISTRY_DEF;
  }

  /**
   * Creates a {@link Eager}.
   *
   * @return the CDO package registry
   */
  @Override
  protected Object createInstance()
  {
    // TODO: implement CDOEagerPackageRegistryDefImpl.createInstance()
    throw new UnsupportedOperationException();
    // return addPackages(CDOUtil.createEagerPackageRegistry());
  }

} // EagerPackageRegistryDefImpl
