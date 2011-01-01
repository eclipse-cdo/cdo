/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.eresource;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>CDO Resource Folder</b></em>'. <!-- end-user-doc
 * -->
 * 
 * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResourceFolder()
 * @model
 * @generated
 * @since 2.0
 */
public interface CDOResourceFolder extends CDOResourceNode
{
  /**
   * Returns the value of the '<em><b>Nodes</b></em>' containment reference list. The list contents are of type
   * {@link org.eclipse.emf.cdo.eresource.CDOResourceNode}. It is bidirectional and its opposite is '
   * {@link org.eclipse.emf.cdo.eresource.CDOResourceNode#getFolder <em>Folder</em>}'. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Nodes</em>' containment reference list isn't clear, there really should be more of a
   * description here...
   * </p>
   * <!-- end-user-doc -->
   * 
   * @return the value of the '<em>Nodes</em>' containment reference list.
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOResourceFolder_Nodes()
   * @see org.eclipse.emf.cdo.eresource.CDOResourceNode#getFolder
   * @model opposite="folder" containment="true"
   * @generated
   */
  EList<CDOResourceNode> getNodes();

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

} // CDOResourceFolder
