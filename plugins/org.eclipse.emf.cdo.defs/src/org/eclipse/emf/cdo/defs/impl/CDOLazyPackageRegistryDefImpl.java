/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.defs.CDOLazyPackageRegistryDef;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Lazy Package Registry Def</b></em>'. <!--
 * end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class CDOLazyPackageRegistryDefImpl extends CDOPackageRegistryDefImpl implements CDOLazyPackageRegistryDef
{
  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected CDOLazyPackageRegistryDefImpl()
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
    return CDODefsPackage.Literals.CDO_LAZY_PACKAGE_REGISTRY_DEF;
  }

  /**
   * Creates a {@link Lazy}.
   * 
   * @return the CDO package registry
   */
  @Override
  protected Object createInstance()
  {
    // TODO: implement CDOLazyPackageRegistryDefImpl.createInstance()
    throw new UnsupportedOperationException();
    // return addPackages(CDOUtil.createLazyPackageRegistry());
  }
} // LazyPackageRegistryDefImpl
