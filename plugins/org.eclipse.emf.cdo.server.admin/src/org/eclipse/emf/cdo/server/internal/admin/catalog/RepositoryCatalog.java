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

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Repository Catalog</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryCatalog#getRepositories <em>Repositories</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.server.internal.admin.catalog.CatalogPackage#getRepositoryCatalog()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface RepositoryCatalog extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Repositories</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryConfiguration}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Repositories</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Repositories</em>' containment reference list.
   * @see org.eclipse.emf.cdo.server.internal.admin.catalog.CatalogPackage#getRepositoryCatalog_Repositories()
   * @model containment="true"
   * @generated
   */
  EList<RepositoryConfiguration> getRepositories();

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  RepositoryConfiguration getRepository(String name);

} // RepositoryCatalog
