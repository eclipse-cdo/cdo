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

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.FeatureMap;

import Testmodel562011.DocumentRoot;
import Testmodel562011.SomeContentType;
import Testmodel562011.Testmodel562011Package;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Document Root</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link Testmodel562011.impl.DocumentRootImpl#getMixed <em>Mixed</em>}</li>
 *   <li>{@link Testmodel562011.impl.DocumentRootImpl#getXMLNSPrefixMap <em>XMLNS Prefix Map</em>}</li>
 *   <li>{@link Testmodel562011.impl.DocumentRootImpl#getXSISchemaLocation <em>XSI Schema Location</em>}</li>
 *   <li>{@link Testmodel562011.impl.DocumentRootImpl#getSomeContent <em>Some Content</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DocumentRootImpl extends CDOObjectImpl implements DocumentRoot
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected DocumentRootImpl()
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
    return Testmodel562011Package.Literals.DOCUMENT_ROOT;
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
  public FeatureMap getMixed()
  {
    return (FeatureMap)eGet(Testmodel562011Package.Literals.DOCUMENT_ROOT__MIXED, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public EMap<String, String> getXMLNSPrefixMap()
  {
    return (EMap<String, String>)eGet(Testmodel562011Package.Literals.DOCUMENT_ROOT__XMLNS_PREFIX_MAP, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public EMap<String, String> getXSISchemaLocation()
  {
    return (EMap<String, String>)eGet(Testmodel562011Package.Literals.DOCUMENT_ROOT__XSI_SCHEMA_LOCATION, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SomeContentType getSomeContent()
  {
    return (SomeContentType)eGet(Testmodel562011Package.Literals.DOCUMENT_ROOT__SOME_CONTENT, true);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void setSomeContent(SomeContentType newSomeContent)
  {
    eSet(Testmodel562011Package.Literals.DOCUMENT_ROOT__SOME_CONTENT, newSomeContent);
  }

} // DocumentRootImpl
