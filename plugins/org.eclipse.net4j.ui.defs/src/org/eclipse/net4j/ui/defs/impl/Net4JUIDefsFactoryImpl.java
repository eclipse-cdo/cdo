/*
 * Copyright (c) 2008, 2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.ui.defs.impl;

import org.eclipse.net4j.ui.defs.InteractiveCredentialsProviderDef;
import org.eclipse.net4j.ui.defs.Net4JUIDefsFactory;
import org.eclipse.net4j.ui.defs.Net4JUIDefsPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * @generated
 */
public class Net4JUIDefsFactoryImpl extends EFactoryImpl implements Net4JUIDefsFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public static Net4JUIDefsFactory init()
  {
    try
    {
      Net4JUIDefsFactory theNet4JUIDefsFactory = (Net4JUIDefsFactory)EPackage.Registry.INSTANCE
          .getEFactory("http://www.eclipse.org/NET4J/ui/defs/1.0.0");
      if (theNet4JUIDefsFactory != null)
      {
        return theNet4JUIDefsFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new Net4JUIDefsFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Net4JUIDefsFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
    case Net4JUIDefsPackage.INTERACTIVE_CREDENTIALS_PROVIDER_DEF:
      return createInteractiveCredentialsProviderDef();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public InteractiveCredentialsProviderDef createInteractiveCredentialsProviderDef()
  {
    InteractiveCredentialsProviderDefImpl interactiveCredentialsProviderDef = new InteractiveCredentialsProviderDefImpl();
    return interactiveCredentialsProviderDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public Net4JUIDefsPackage getNet4JUIDefsPackage()
  {
    return (Net4JUIDefsPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static Net4JUIDefsPackage getPackage()
  {
    return Net4JUIDefsPackage.eINSTANCE;
  }

} // Net4JUIDefsFactoryImpl
