/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.defs.impl;

//import org.eclipse.emf.cdo.defs.*;
import org.eclipse.emf.cdo.defs.CDOAuditDef;
import org.eclipse.emf.cdo.defs.CDOClientProtocolFactoryDef;
import org.eclipse.emf.cdo.defs.CDODefsFactory;
import org.eclipse.emf.cdo.defs.CDODefsPackage;
import org.eclipse.emf.cdo.defs.CDOEagerPackageRegistryDef;
import org.eclipse.emf.cdo.defs.CDOLazyPackageRegistryDef;
import org.eclipse.emf.cdo.defs.CDOPackageRegistryDef;
import org.eclipse.emf.cdo.defs.CDOResourceDef;
import org.eclipse.emf.cdo.defs.CDOSessionDef;
import org.eclipse.emf.cdo.defs.CDOTransactionDef;
import org.eclipse.emf.cdo.defs.CDOViewDef;
import org.eclipse.emf.cdo.defs.EDynamicPackageDef;
import org.eclipse.emf.cdo.defs.EGlobalPackageDef;
import org.eclipse.emf.cdo.defs.ResourceMode;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc --> An implementation of the model <b>Factory</b>. <!-- end-user-doc -->
 * @generated
 */
public class CDODefsFactoryImpl extends EFactoryImpl implements CDODefsFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public static CDODefsFactory init()
  {
    try
    {
      CDODefsFactory theCDODefsFactory = (CDODefsFactory)EPackage.Registry.INSTANCE
          .getEFactory("http://www.eclipse.org/emf/CDO/defs/1.0.0");
      if (theCDODefsFactory != null)
      {
        return theCDODefsFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new CDODefsFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDODefsFactoryImpl()
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
    case CDODefsPackage.CDO_VIEW_DEF:
      return createCDOViewDef();
    case CDODefsPackage.CDO_TRANSACTION_DEF:
      return createCDOTransactionDef();
    case CDODefsPackage.CDO_AUDIT_DEF:
      return createCDOAuditDef();
    case CDODefsPackage.CDO_SESSION_DEF:
      return createCDOSessionDef();
    case CDODefsPackage.CDO_PACKAGE_REGISTRY_DEF:
      return createCDOPackageRegistryDef();
    case CDODefsPackage.CDO_EAGER_PACKAGE_REGISTRY_DEF:
      return createCDOEagerPackageRegistryDef();
    case CDODefsPackage.CDO_LAZY_PACKAGE_REGISTRY_DEF:
      return createCDOLazyPackageRegistryDef();
    case CDODefsPackage.EDYNAMIC_PACKAGE_DEF:
      return createEDynamicPackageDef();
    case CDODefsPackage.EGLOBAL_PACKAGE_DEF:
      return createEGlobalPackageDef();
    case CDODefsPackage.CDO_CLIENT_PROTOCOL_FACTORY_DEF:
      return createCDOClientProtocolFactoryDef();
    case CDODefsPackage.CDO_RESOURCE_DEF:
      return createCDOResourceDef();
    default:
      throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object createFromString(EDataType eDataType, String initialValue)
  {
    switch (eDataType.getClassifierID())
    {
    case CDODefsPackage.RESOURCE_MODE:
      return createResourceModeFromString(eDataType, initialValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue)
  {
    switch (eDataType.getClassifierID())
    {
    case CDODefsPackage.RESOURCE_MODE:
      return convertResourceModeToString(eDataType, instanceValue);
    default:
      throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDOViewDef createCDOViewDef()
  {
    CDOViewDefImpl cdoViewDef = new CDOViewDefImpl();
    return cdoViewDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDOTransactionDef createCDOTransactionDef()
  {
    CDOTransactionDefImpl cdoTransactionDef = new CDOTransactionDefImpl();
    return cdoTransactionDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDOAuditDef createCDOAuditDef()
  {
    CDOAuditDefImpl cdoAuditDef = new CDOAuditDefImpl();
    return cdoAuditDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDOSessionDef createCDOSessionDef()
  {
    CDOSessionDefImpl cdoSessionDef = new CDOSessionDefImpl();
    return cdoSessionDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDOPackageRegistryDef createCDOPackageRegistryDef()
  {
    CDOPackageRegistryDefImpl cdoPackageRegistryDef = new CDOPackageRegistryDefImpl();
    return cdoPackageRegistryDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDOEagerPackageRegistryDef createCDOEagerPackageRegistryDef()
  {
    CDOEagerPackageRegistryDefImpl cdoEagerPackageRegistryDef = new CDOEagerPackageRegistryDefImpl();
    return cdoEagerPackageRegistryDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDOLazyPackageRegistryDef createCDOLazyPackageRegistryDef()
  {
    CDOLazyPackageRegistryDefImpl cdoLazyPackageRegistryDef = new CDOLazyPackageRegistryDefImpl();
    return cdoLazyPackageRegistryDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EDynamicPackageDef createEDynamicPackageDef()
  {
    EDynamicPackageDefImpl eDynamicPackageDef = new EDynamicPackageDefImpl();
    return eDynamicPackageDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public EGlobalPackageDef createEGlobalPackageDef()
  {
    EGlobalPackageDefImpl eGlobalPackageDef = new EGlobalPackageDefImpl();
    return eGlobalPackageDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDOClientProtocolFactoryDef createCDOClientProtocolFactoryDef()
  {
    CDOClientProtocolFactoryDefImpl cdoClientProtocolFactoryDef = new CDOClientProtocolFactoryDefImpl();
    return cdoClientProtocolFactoryDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDOResourceDef createCDOResourceDef()
  {
    CDOResourceDefImpl cdoResourceDef = new CDOResourceDefImpl();
    return cdoResourceDef;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public ResourceMode createResourceModeFromString(EDataType eDataType, String initialValue)
  {
    ResourceMode result = ResourceMode.get(initialValue);
    if (result == null)
      throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '"
          + eDataType.getName() + "'");
    return result;
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public String convertResourceModeToString(EDataType eDataType, Object instanceValue)
  {
    return instanceValue == null ? null : instanceValue.toString();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDODefsPackage getCDODefsPackage()
  {
    return (CDODefsPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static CDODefsPackage getPackage()
  {
    return CDODefsPackage.eINSTANCE;
  }

} // CDODefsFactoryImpl
