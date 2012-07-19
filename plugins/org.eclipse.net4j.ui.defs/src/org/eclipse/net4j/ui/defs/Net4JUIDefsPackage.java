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
package org.eclipse.net4j.ui.defs;

import org.eclipse.net4j.util.defs.Net4jUtilDefsPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

/**
 * <!-- begin-user-doc --> The <b>Package</b> for the model. It contains accessors for the meta objects to represent
 * <ul>
 * <li>each class,</li>
 * <li>each feature of each class,</li>
 * <li>each enum,</li>
 * <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.net4j.ui.defs.Net4JUIDefsFactory
 * @model kind="package"
 * @generated
 */
public interface Net4JUIDefsPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "defs"; //$NON-NLS-1$

  /**
   * The package namespace URI.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/NET4J/ui/defs/1.0.0"; //$NON-NLS-1$

  /**
   * The package namespace name.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "net4j.ui.defs"; //$NON-NLS-1$

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  Net4JUIDefsPackage eINSTANCE = org.eclipse.net4j.ui.defs.impl.Net4JUIDefsPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.net4j.ui.defs.impl.InteractiveCredentialsProviderDefImpl <em>Interactive Credentials Provider Def</em>}' class.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @see org.eclipse.net4j.ui.defs.impl.InteractiveCredentialsProviderDefImpl
   * @see org.eclipse.net4j.ui.defs.impl.Net4JUIDefsPackageImpl#getInteractiveCredentialsProviderDef()
   * @generated
   */
  int INTERACTIVE_CREDENTIALS_PROVIDER_DEF = 0;

  /**
   * The number of structural features of the '<em>Interactive Credentials Provider Def</em>' class.
   * <!-- begin-user-doc
   * --> <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTERACTIVE_CREDENTIALS_PROVIDER_DEF_FEATURE_COUNT = Net4jUtilDefsPackage.DEF_FEATURE_COUNT + 0;

  /**
   * Returns the meta object for class '{@link org.eclipse.net4j.ui.defs.InteractiveCredentialsProviderDef <em>Interactive Credentials Provider Def</em>}'.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the meta object for class '<em>Interactive Credentials Provider Def</em>'.
   * @see org.eclipse.net4j.ui.defs.InteractiveCredentialsProviderDef
   * @generated
   */
  EClass getInteractiveCredentialsProviderDef();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  Net4JUIDefsFactory getNet4JUIDefsFactory();

  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.net4j.ui.defs.impl.InteractiveCredentialsProviderDefImpl <em>Interactive Credentials Provider Def</em>}' class.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see org.eclipse.net4j.ui.defs.impl.InteractiveCredentialsProviderDefImpl
     * @see org.eclipse.net4j.ui.defs.impl.Net4JUIDefsPackageImpl#getInteractiveCredentialsProviderDef()
     * @generated
     */
    EClass INTERACTIVE_CREDENTIALS_PROVIDER_DEF = eINSTANCE.getInteractiveCredentialsProviderDef();

  }

} // Net4JUIDefsPackage
