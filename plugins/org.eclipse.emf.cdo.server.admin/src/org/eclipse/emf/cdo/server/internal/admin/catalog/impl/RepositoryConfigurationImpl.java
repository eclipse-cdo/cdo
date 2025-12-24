/*
 * Copyright (c) 2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.admin.catalog.impl;

import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.server.internal.admin.catalog.CatalogPackage;
import org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryConfiguration;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Admin Repository</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.server.internal.admin.catalog.impl.RepositoryConfigurationImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.server.internal.admin.catalog.impl.RepositoryConfigurationImpl#getConfigXML <em>Config XML</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RepositoryConfigurationImpl extends CDOObjectImpl implements RepositoryConfiguration
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected RepositoryConfigurationImpl()
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
    return CatalogPackage.Literals.REPOSITORY_CONFIGURATION;
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

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String getName()
  {
    return (String)eGet(CatalogPackage.Literals.REPOSITORY_CONFIGURATION__NAME, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setName(String newName)
  {
    eSet(CatalogPackage.Literals.REPOSITORY_CONFIGURATION__NAME, newName);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public CDOClob getConfigXML()
  {
    return (CDOClob)eGet(CatalogPackage.Literals.REPOSITORY_CONFIGURATION__CONFIG_XML, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setConfigXML(CDOClob newConfigXML)
  {
    eSet(CatalogPackage.Literals.REPOSITORY_CONFIGURATION__CONFIG_XML, newConfigXML);
  }

} // RepositoryConfigurationImpl
