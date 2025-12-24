/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package reference;

import org.eclipse.emf.cdo.CDOObject;

import interface_.IInterface;

/**
 * <!-- begin-user-doc --> A representation of the model object '<em><b>Reference</b></em>'. <!-- end-user-doc -->
 * <p>
 * The following features are supported:
 * <ul>
 * <li>{@link reference.Reference#getRef <em>Ref</em>}</li>
 * </ul>
 * </p>
 *
 * @see reference.ReferencePackage#getReference()
 * @model
 * @extends CDOObject
 * @generated
 */
public interface Reference extends CDOObject
{
  /**
   * Returns the value of the '<em><b>Ref</b></em>' reference. <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Ref</em>' reference isn't clear, there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   *
   * @return the value of the '<em>Ref</em>' reference.
   * @see #setRef(IInterface)
   * @see reference.ReferencePackage#getReference_Ref()
   * @model
   * @generated
   */
  IInterface getRef();

  /**
   * Sets the value of the '{@link reference.Reference#getRef <em>Ref</em>}' reference. <!-- begin-user-doc --> <!--
   * end-user-doc -->
   *
   * @param value
   *          the new value of the '<em>Ref</em>' reference.
   * @see #getRef()
   * @generated
   */
  void setRef(IInterface value);

} // Reference
