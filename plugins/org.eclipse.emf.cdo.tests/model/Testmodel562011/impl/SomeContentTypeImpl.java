/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package Testmodel562011.impl;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.emf.ecore.EClass;

import Testmodel562011.SomeContentType;
import Testmodel562011.Testmodel562011Package;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Some Content Type</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link Testmodel562011.impl.SomeContentTypeImpl#getSomeElem <em>Some Elem</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SomeContentTypeImpl extends CDOObjectImpl implements SomeContentType
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected SomeContentTypeImpl()
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
    return Testmodel562011Package.Literals.SOME_CONTENT_TYPE;
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
  public String getSomeElem()
  {
    return (String)eGet(Testmodel562011Package.Literals.SOME_CONTENT_TYPE__SOME_ELEM, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSomeElem(String newSomeElem)
  {
    eSet(Testmodel562011Package.Literals.SOME_CONTENT_TYPE__SOME_ELEM, newSomeElem);
  }

} // SomeContentTypeImpl
