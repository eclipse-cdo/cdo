/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.security.impl;

import org.eclipse.emf.cdo.lm.security.LMSecurityFactory;
import org.eclipse.emf.cdo.lm.security.LMSecurityPackage;
import org.eclipse.emf.cdo.lm.security.ModuleFilter;
import org.eclipse.emf.cdo.lm.security.ModuleTypeFilter;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class LMSecurityFactoryImpl extends EFactoryImpl implements LMSecurityFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static LMSecurityFactory init()
  {
    try
    {
      LMSecurityFactory theLMSecurityFactory = (LMSecurityFactory)EPackage.Registry.INSTANCE.getEFactory(LMSecurityPackage.eNS_URI);
      if (theLMSecurityFactory != null)
      {
        return theLMSecurityFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new LMSecurityFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LMSecurityFactoryImpl()
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
    case LMSecurityPackage.MODULE_FILTER:
      return createModuleFilter();
    case LMSecurityPackage.MODULE_TYPE_FILTER:
      return createModuleTypeFilter();
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
  public ModuleFilter createModuleFilter()
  {
    ModuleFilterImpl moduleFilter = new ModuleFilterImpl();
    return moduleFilter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public ModuleTypeFilter createModuleTypeFilter()
  {
    ModuleTypeFilterImpl moduleTypeFilter = new ModuleTypeFilterImpl();
    return moduleTypeFilter;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public LMSecurityPackage getLMSecurityPackage()
  {
    return (LMSecurityPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static LMSecurityPackage getPackage()
  {
    return LMSecurityPackage.eINSTANCE;
  }

} // LMSecurityFactoryImpl
