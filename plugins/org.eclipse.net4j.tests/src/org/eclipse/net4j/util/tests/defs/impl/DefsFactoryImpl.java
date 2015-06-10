/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.util.tests.defs.impl;

import org.eclipse.net4j.util.tests.defs.DefsFactory;
import org.eclipse.net4j.util.tests.defs.DefsPackage;
import org.eclipse.net4j.util.tests.defs.TestDef;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 *
 * @generated
 */
public class DefsFactoryImpl extends EFactoryImpl implements DefsFactory
{
  /**
   * Creates the default factory implementation. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public static DefsFactory init()
  {
    try
    {
      DefsFactory theDefsFactory = (DefsFactory)EPackage.Registry.INSTANCE
          .getEFactory("http://www.eclipse.org/NET4J/defs/tests/1.0.0"); //$NON-NLS-1$
      if (theDefsFactory != null)
      {
        return theDefsFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new DefsFactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public DefsFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case DefsPackage.TEST_DEF:
      return createTestDef();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public TestDef createTestDef()
  {
    TestDefImpl testDef = new TestDefImpl();
    return testDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @generated
   */
  public DefsPackage getDefsPackage()
  {
    return (DefsPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   *
   * @deprecated
   * @generated
   */
  @Deprecated
  public static DefsPackage getPackage()
  {
    return DefsPackage.eINSTANCE;
  }

} // DefsFactoryImpl
