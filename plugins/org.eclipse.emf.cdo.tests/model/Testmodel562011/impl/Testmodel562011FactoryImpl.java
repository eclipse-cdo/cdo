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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import Testmodel562011.DocumentRoot;
import Testmodel562011.SomeContentType;
import Testmodel562011.Testmodel562011Factory;
import Testmodel562011.Testmodel562011Package;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class Testmodel562011FactoryImpl extends EFactoryImpl implements Testmodel562011Factory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static Testmodel562011Factory init()
  {
    try
    {
      Testmodel562011Factory theTestmodel562011Factory = (Testmodel562011Factory)EPackage.Registry.INSTANCE.getEFactory(Testmodel562011Package.eNS_URI);
      if (theTestmodel562011Factory != null)
      {
        return theTestmodel562011Factory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new Testmodel562011FactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Testmodel562011FactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case Testmodel562011Package.DOCUMENT_ROOT:
      return createDocumentRoot();
    case Testmodel562011Package.SOME_CONTENT_TYPE:
      return createSomeContentType();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public DocumentRoot createDocumentRoot()
  {
    DocumentRootImpl documentRoot = new DocumentRootImpl();
    return documentRoot;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public SomeContentType createSomeContentType()
  {
    SomeContentTypeImpl someContentType = new SomeContentTypeImpl();
    return someContentType;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Testmodel562011Package getTestmodel562011Package()
  {
    return (Testmodel562011Package)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static Testmodel562011Package getPackage()
  {
    return Testmodel562011Package.eINSTANCE;
  }

} // Testmodel562011FactoryImpl
