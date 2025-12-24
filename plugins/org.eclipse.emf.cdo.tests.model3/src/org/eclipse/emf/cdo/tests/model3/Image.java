/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.model3;

import org.eclipse.emf.cdo.common.lob.CDOBlob;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Image</b></em>'. <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.Image#getWidth <em>Width</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.Image#getHeight <em>Height</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.tests.model3.Image#getData <em>Data</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getImage()
 * @model
 * @generated
 */
public interface Image extends EObject
{
  /**
   * Returns the value of the '<em><b>Width</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Width</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Width</em>' attribute.
   * @see #setWidth(int)
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getImage_Width()
   * @model
   * @generated
   */
  int getWidth();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model3.Image#getWidth <em>Width</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Width</em>' attribute.
   * @see #getWidth()
   * @generated
   */
  void setWidth(int value);

  /**
   * Returns the value of the '<em><b>Height</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Height</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Height</em>' attribute.
   * @see #setHeight(int)
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getImage_Height()
   * @model
   * @generated
   */
  int getHeight();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model3.Image#getHeight <em>Height</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Height</em>' attribute.
   * @see #getHeight()
   * @generated
   */
  void setHeight(int value);

  /**
   * Returns the value of the '<em><b>Data</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Data</em>' attribute isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Data</em>' attribute.
   * @see #setData(CDOBlob)
   * @see org.eclipse.emf.cdo.tests.model3.Model3Package#getImage_Data()
   * @model dataType="org.eclipse.emf.cdo.etypes.Blob"
   * @generated
   */
  CDOBlob getData();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.tests.model3.Image#getData <em>Data</em>}' attribute. <!--
   * begin-user-doc --> <!-- end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Data</em>' attribute.
   * @see #getData()
   * @generated
   */
  void setData(CDOBlob value);

} // Image
