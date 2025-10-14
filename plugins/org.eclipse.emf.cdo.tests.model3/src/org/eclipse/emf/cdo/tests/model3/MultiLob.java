/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3;

import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Multi Lob</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.MultiLob#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.MultiLob#getBlobs <em>Blobs</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.MultiLob#getClobs <em>Clobs</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getMultiLob()
 * @model
 * @generated
 */
public interface MultiLob extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getMultiLob_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model3.MultiLob#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Blobs</b></em>' attribute list.
   * The list contents are of type {@link org.eclipse.emf.cdo.common.lob.CDOBlob}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Blobs</em>' attribute list.
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getMultiLob_Blobs()
   * @model dataType="org.eclipse.emf.cdo.etypes.Blob"
   * @generated
   */
  EList<CDOBlob> getBlobs();

  /**
   * Returns the value of the '<em><b>Clobs</b></em>' attribute list.
   * The list contents are of type {@link org.eclipse.emf.cdo.common.lob.CDOClob}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Clobs</em>' attribute list.
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getMultiLob_Clobs()
   * @model dataType="org.eclipse.emf.cdo.etypes.Clob"
   * @generated
   */
  EList<CDOClob> getClobs();

} // MultiLob
