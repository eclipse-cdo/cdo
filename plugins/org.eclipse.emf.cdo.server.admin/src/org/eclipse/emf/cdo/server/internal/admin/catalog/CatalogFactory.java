/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.admin.catalog;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.server.internal.admin.catalog.CatalogPackage
 * @generated
 */
public interface CatalogFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  CatalogFactory eINSTANCE = org.eclipse.emf.cdo.server.internal.admin.catalog.impl.CatalogFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Repository Catalog</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Repository Catalog</em>'.
   * @generated
   */
  RepositoryCatalog createRepositoryCatalog();

  /**
   * Returns a new object of class '<em>Repository Configuration</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Repository Configuration</em>'.
   * @generated
   */
  RepositoryConfiguration createRepositoryConfiguration();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  CatalogPackage getCatalogPackage();

} // CatalogFactory
