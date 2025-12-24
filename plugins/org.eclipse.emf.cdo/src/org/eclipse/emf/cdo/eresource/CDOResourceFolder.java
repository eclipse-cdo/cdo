/*
 * Copyright (c) 2008-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.eresource;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>CDO Resource Folder</b></em>'.
 *
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.eresource.CDOResourceFolder#getNodes <em>Nodes</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResourceFolder()
 * @model
 * @generated
 */
public interface CDOResourceFolder extends CDOResourceNode
{
  /**
   * Returns the value of the '<em><b>Nodes</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.emf.cdo.eresource.CDOResourceNode}.
   * It is bidirectional and its opposite is '{@link org.eclipse.emf.cdo.eresource.CDOResourceNode#getFolder <em>Folder</em>}'.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Nodes</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Nodes</em>' containment reference list.
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResourceFolder_Nodes()
   * @see org.eclipse.emf.cdo.eresource.CDOResourceNode#getFolder
   * @model opposite="folder" containment="true"
   * @generated
   */
  EList<CDOResourceNode> getNodes();

  /**
   * @ADDED
   * @since 4.4
   */
  CDOResourceNode getNode(String name);

  /**
   * <!-- begin-user-doc -->
   *
   * @since 4.0 <!-- end-user-doc -->
   * @model
   * @generated
   */
  CDOResourceFolder addResourceFolder(String name);

  /**
   * <!-- begin-user-doc -->
   *
   * @since 4.0 <!-- end-user-doc -->
   * @model
   * @generated
   */
  CDOResource addResource(String name);

  /**
   * <!-- begin-user-doc -->
   * @since 4.2
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  CDOTextResource addTextResource(String name);

  /**
   * <!-- begin-user-doc -->
   * @since 4.2
   * <!-- end-user-doc -->
   * @model
   * @generated
   */
  CDOBinaryResource addBinaryResource(String name);

} // CDOResourceFolder
