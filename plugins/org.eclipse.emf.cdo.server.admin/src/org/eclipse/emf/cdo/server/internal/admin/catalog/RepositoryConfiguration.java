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
import org.eclipse.emf.cdo.common.lob.CDOClob;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Admin Repository</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryConfiguration#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryConfiguration#getConfigXML <em>Config XML</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.cdo.server.internal.admin.catalog.CatalogPackage#getRepositoryConfiguration()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface RepositoryConfiguration extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.server.internal.admin.catalog.CatalogPackage#getRepositoryConfiguration_Name()
   * @model required="true"
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryConfiguration#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Config XML</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Config XML</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Config XML</em>' attribute.
   * @see #setConfigXML(CDOClob)
   * @see org.eclipse.emf.cdo.server.internal.admin.catalog.CatalogPackage#getRepositoryConfiguration_ConfigXML()
   * @model dataType="org.eclipse.emf.cdo.etypes.Clob" required="true"
   * @generated
   */
  CDOClob getConfigXML();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.server.internal.admin.catalog.RepositoryConfiguration#getConfigXML <em>Config XML</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Config XML</em>' attribute.
   * @see #getConfigXML()
   * @generated
   */
  void setConfigXML(CDOClob value);

} // RepositoryConfiguration
