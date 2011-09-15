/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.defs;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc --> The <b>Factory</b> for the model. It provides a create method for each non-abstract class of
 * the model. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.defs.CDODefsPackage
 * @generated
 */
public interface CDODefsFactory extends EFactory
{
  /**
   * The singleton instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  CDODefsFactory eINSTANCE = org.eclipse.emf.cdo.defs.impl.CDODefsFactoryImpl.init();

  /**
   * Returns a new object of class '<em>CDO View Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>CDO View Def</em>'.
   * @generated
   */
  CDOViewDef createCDOViewDef();

  /**
   * Returns a new object of class '<em>CDO Transaction Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>CDO Transaction Def</em>'.
   * @generated
   */
  CDOTransactionDef createCDOTransactionDef();

  /**
   * Returns a new object of class '<em>CDO Audit Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>CDO Audit Def</em>'.
   * @generated
   */
  CDOAuditDef createCDOAuditDef();

  /**
   * Returns a new object of class '<em>CDO Session Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>CDO Session Def</em>'.
   * @generated
   */
  CDOSessionDef createCDOSessionDef();

  /**
   * Returns a new object of class '<em>CDO Package Registry Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>CDO Package Registry Def</em>'.
   * @generated
   */
  CDOPackageRegistryDef createCDOPackageRegistryDef();

  /**
   * Returns a new object of class '<em>CDO Eager Package Registry Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @return a new object of class '<em>CDO Eager Package Registry Def</em>'.
   * @generated
   */
  CDOEagerPackageRegistryDef createCDOEagerPackageRegistryDef();

  /**
   * Returns a new object of class '<em>CDO Lazy Package Registry Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @return a new object of class '<em>CDO Lazy Package Registry Def</em>'.
   * @generated
   */
  CDOLazyPackageRegistryDef createCDOLazyPackageRegistryDef();

  /**
   * Returns a new object of class '<em>EDynamic Package Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>EDynamic Package Def</em>'.
   * @generated
   */
  EDynamicPackageDef createEDynamicPackageDef();

  /**
   * Returns a new object of class '<em>EGlobal Package Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>EGlobal Package Def</em>'.
   * @generated
   */
  EGlobalPackageDef createEGlobalPackageDef();

  /**
   * Returns a new object of class '<em>CDO Client Protocol Factory Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   * 
   * @return a new object of class '<em>CDO Client Protocol Factory Def</em>'.
   * @generated
   */
  CDOClientProtocolFactoryDef createCDOClientProtocolFactoryDef();

  /**
   * Returns a new object of class '<em>CDO Resource Def</em>'. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return a new object of class '<em>CDO Resource Def</em>'.
   * @generated
   */
  CDOResourceDef createCDOResourceDef();

  /**
   * Returns the package supported by this factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the package supported by this factory.
   * @generated
   */
  CDODefsPackage getCDODefsPackage();

} // CDODefsFactory
