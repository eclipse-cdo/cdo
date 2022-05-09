/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.assembly.impl;

import org.eclipse.emf.cdo.lm.assembly.Assembly;
import org.eclipse.emf.cdo.lm.assembly.AssemblyFactory;
import org.eclipse.emf.cdo.lm.assembly.AssemblyModule;
import org.eclipse.emf.cdo.lm.assembly.AssemblyPackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!--
 * end-user-doc -->
 * @generated
 */
public class AssemblyFactoryImpl extends EFactoryImpl implements AssemblyFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc --> <!--
   * end-user-doc -->
   * @generated
   */
  public static AssemblyFactory init()
  {
    try
    {
      AssemblyFactory theAssemblyFactory = (AssemblyFactory)EPackage.Registry.INSTANCE.getEFactory(AssemblyPackage.eNS_URI);
      if (theAssemblyFactory != null)
      {
        return theAssemblyFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new AssemblyFactoryImpl();
  }

  /**
   * Creates an instance of the factory. <!-- begin-user-doc --> <!-- end-user-doc
   * -->
   *
   * @generated
   */
  public AssemblyFactoryImpl()
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
    case AssemblyPackage.ASSEMBLY:
      return createAssembly();
    case AssemblyPackage.ASSEMBLY_MODULE:
      return createAssemblyModule();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Assembly createAssembly()
  {
    AssemblyImpl assembly = new AssemblyImpl();
    return assembly;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public AssemblyModule createAssemblyModule()
  {
    AssemblyModuleImpl assemblyModule = new AssemblyModuleImpl();
    return assemblyModule;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public AssemblyPackage getAssemblyPackage()
  {
    return (AssemblyPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static AssemblyPackage getPackage()
  {
    return AssemblyPackage.eINSTANCE;
  }

} // AssemblyFactoryImpl
