/*
 * Copyright (c) 2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.admin.catalog.impl;

import org.eclipse.emf.cdo.server.internal.admin.catalog.CatalogPackage;
import org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryCatalog;
import org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryConfiguration;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import java.lang.reflect.InvocationTargetException;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Repository Catalog</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.server.internal.admin.catalog.impl.RepositoryCatalogImpl#getRepositories <em>Repositories</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RepositoryCatalogImpl extends CDOObjectImpl implements RepositoryCatalog
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected RepositoryCatalogImpl()
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
    return CatalogPackage.Literals.REPOSITORY_CATALOG;
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
  @SuppressWarnings("unchecked")
  public EList<RepositoryConfiguration> getRepositories()
  {
    return (EList<RepositoryConfiguration>)eGet(CatalogPackage.Literals.REPOSITORY_CATALOG__REPOSITORIES, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated NOT
   */
  @Override
  public RepositoryConfiguration getRepository(String name)
  {
    for (RepositoryConfiguration repository : getRepositories())
    {
      if (ObjectUtil.equals(repository.getName(), name))
      {
        return repository;
      }
    }

    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException
  {
    switch (operationID)
    {
    case CatalogPackage.REPOSITORY_CATALOG___GET_REPOSITORY__STRING:
      return getRepository((String)arguments.get(0));
    }
    return super.eInvoke(operationID, arguments);
  }

} // RepositoryCatalogImpl
