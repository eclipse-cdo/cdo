/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.admin.catalog;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.server.internal.admin.catalog.CatalogFactory
 * @model kind="package"
 * @generated
 */
public interface CatalogPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "catalog";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.eclipse.org/emf/CDO/admin/RepositoryCatalog/4.3.0";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "catalog";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  CatalogPackage eINSTANCE = org.eclipse.emf.cdo.server.internal.admin.catalog.impl.CatalogPackageImpl.init();

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.server.internal.admin.catalog.impl.RepositoryCatalogImpl <em>Repository Catalog</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.server.internal.admin.catalog.impl.RepositoryCatalogImpl
   * @see org.eclipse.emf.cdo.server.internal.admin.catalog.impl.CatalogPackageImpl#getRepositoryCatalog()
   * @generated
   */
  int REPOSITORY_CATALOG = 0;

  /**
   * The feature id for the '<em><b>Repositories</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REPOSITORY_CATALOG__REPOSITORIES = 0;

  /**
   * The number of structural features of the '<em>Repository Catalog</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REPOSITORY_CATALOG_FEATURE_COUNT = 1;

  /**
   * The operation id for the '<em>Get Repository</em>' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REPOSITORY_CATALOG___GET_REPOSITORY__STRING = 0;

  /**
   * The number of operations of the '<em>Repository Catalog</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REPOSITORY_CATALOG_OPERATION_COUNT = 1;

  /**
   * The meta object id for the '{@link org.eclipse.emf.cdo.server.internal.admin.catalog.impl.RepositoryConfigurationImpl <em>Repository Configuration</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.cdo.server.internal.admin.catalog.impl.RepositoryConfigurationImpl
   * @see org.eclipse.emf.cdo.server.internal.admin.catalog.impl.CatalogPackageImpl#getRepositoryConfiguration()
   * @generated
   */
  int REPOSITORY_CONFIGURATION = 1;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REPOSITORY_CONFIGURATION__NAME = 0;

  /**
   * The feature id for the '<em><b>Config XML</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REPOSITORY_CONFIGURATION__CONFIG_XML = 1;

  /**
   * The number of structural features of the '<em>Repository Configuration</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REPOSITORY_CONFIGURATION_FEATURE_COUNT = 2;

  /**
   * The number of operations of the '<em>Repository Configuration</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REPOSITORY_CONFIGURATION_OPERATION_COUNT = 0;

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryCatalog <em>Repository Catalog</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Repository Catalog</em>'.
   * @see org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryCatalog
   * @generated
   */
  EClass getRepositoryCatalog();

  /**
   * Returns the meta object for the containment reference list '{@link org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryCatalog#getRepositories <em>Repositories</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Repositories</em>'.
   * @see org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryCatalog#getRepositories()
   * @see #getRepositoryCatalog()
   * @generated
   */
  EReference getRepositoryCatalog_Repositories();

  /**
   * Returns the meta object for the '{@link org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryCatalog#getRepository(java.lang.String) <em>Get Repository</em>}' operation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the '<em>Get Repository</em>' operation.
   * @see org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryCatalog#getRepository(java.lang.String)
   * @generated
   */
  EOperation getRepositoryCatalog__GetRepository__String();

  /**
   * Returns the meta object for class '{@link org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryConfiguration <em>Repository Configuration</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Repository Configuration</em>'.
   * @see org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryConfiguration
   * @generated
   */
  EClass getRepositoryConfiguration();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryConfiguration#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryConfiguration#getName()
   * @see #getRepositoryConfiguration()
   * @generated
   */
  EAttribute getRepositoryConfiguration_Name();

  /**
   * Returns the meta object for the attribute '{@link org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryConfiguration#getConfigXML <em>Config XML</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Config XML</em>'.
   * @see org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryConfiguration#getConfigXML()
   * @see #getRepositoryConfiguration()
   * @generated
   */
  EAttribute getRepositoryConfiguration_ConfigXML();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  CatalogFactory getCatalogFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each operation of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.server.internal.admin.catalog.impl.RepositoryCatalogImpl <em>Repository Catalog</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.server.internal.admin.catalog.impl.RepositoryCatalogImpl
     * @see org.eclipse.emf.cdo.server.internal.admin.catalog.impl.CatalogPackageImpl#getRepositoryCatalog()
     * @generated
     */
    EClass REPOSITORY_CATALOG = eINSTANCE.getRepositoryCatalog();

    /**
     * The meta object literal for the '<em><b>Repositories</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REPOSITORY_CATALOG__REPOSITORIES = eINSTANCE.getRepositoryCatalog_Repositories();

    /**
     * The meta object literal for the '<em><b>Get Repository</b></em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EOperation REPOSITORY_CATALOG___GET_REPOSITORY__STRING = eINSTANCE.getRepositoryCatalog__GetRepository__String();

    /**
     * The meta object literal for the '{@link org.eclipse.emf.cdo.server.internal.admin.catalog.impl.RepositoryConfigurationImpl <em>Repository Configuration</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.cdo.server.internal.admin.catalog.impl.RepositoryConfigurationImpl
     * @see org.eclipse.emf.cdo.server.internal.admin.catalog.impl.CatalogPackageImpl#getRepositoryConfiguration()
     * @generated
     */
    EClass REPOSITORY_CONFIGURATION = eINSTANCE.getRepositoryConfiguration();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REPOSITORY_CONFIGURATION__NAME = eINSTANCE.getRepositoryConfiguration_Name();

    /**
     * The meta object literal for the '<em><b>Config XML</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REPOSITORY_CONFIGURATION__CONFIG_XML = eINSTANCE.getRepositoryConfiguration_ConfigXML();

  }

} // CatalogPackage
