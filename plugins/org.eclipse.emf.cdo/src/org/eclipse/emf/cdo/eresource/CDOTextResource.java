/*
 * Copyright (c) 2011, 2012, 2014, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.lob.CDOClob;

import org.eclipse.net4j.util.io.EncodingProvider;

import java.io.Reader;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>CDO Text Resource</b></em>'.
 *
 * @since 4.1
 * @extends EncodingProvider
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.emf.cdo.eresource.CDOTextResource#getContents <em>Contents</em>}</li>
 *   <li>{@link org.eclipse.emf.cdo.eresource.CDOTextResource#getEncoding <em>Encoding</em>}</li>
 * </ul>
 *
 * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOTextResource()
 * @model superTypes="org.eclipse.emf.cdo.eresource.CDOFileResource&lt;org.eclipse.emf.cdo.etypes.Reader&gt;"
 * @generated not
 */
public interface CDOTextResource extends CDOFileResource<Reader>, EncodingProvider
{
  /**
   * Returns the value of the '<em><b>Contents</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Contents</em>' attribute isn't clear, there really should be more of a description
   * here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Contents</em>' attribute.
   * @see #setContents(CDOClob)
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOTextResource_Contents()
   * @model dataType="org.eclipse.emf.cdo.etypes.Clob" required="true" transient="true"
   *        annotation="http://www.eclipse.org/emf/CDO persistent='true'"
   * @generated
   */
  @Override
  CDOClob getContents();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.eresource.CDOTextResource#getContents <em>Contents</em>}' attribute.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param value the new value of the '<em>Contents</em>' attribute.
   * @see #getContents()
   * @generated
   */
  void setContents(CDOClob value);

  /**
   * Returns the value of the '<em><b>Encoding</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Encoding</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * @since 4.2
   * <!-- end-user-doc -->
   * @return the value of the '<em>Encoding</em>' attribute.
   * @see #setEncoding(String)
   * @see org.eclipse.emf.cdo.eresource.EresourcePackage#getCDOTextResource_Encoding()
   * @model
   * @generated
   */
  @Override
  String getEncoding();

  /**
   * Sets the value of the '{@link org.eclipse.emf.cdo.eresource.CDOTextResource#getEncoding <em>Encoding</em>}' attribute.
   * <!-- begin-user-doc -->
   * @since 4.2
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Encoding</em>' attribute.
   * @see #getEncoding()
   * @generated
   */
  void setEncoding(String value);

} // CDOTextResource
