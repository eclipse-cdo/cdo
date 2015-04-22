/*
 * Copyright (c) 2008-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.defs.util;

//import org.eclipse.emf.cdo.defs.*;
import org.eclipse.emf.cdo.defs.CDOAuditDef;
import org.eclipse.emf.cdo.defs.CDOClientProtocolFactoryDef;
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
import org.eclipse.emf.cdo.defs.EPackageDef;

import org.eclipse.net4j.defs.ClientProtocolFactoryDef;
import org.eclipse.net4j.defs.ProtocolProviderDef;
import org.eclipse.net4j.util.defs.Def;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc --> The <b>Adapter Factory</b> for the model. It provides an adapter <code>createXXX</code>
 * method for each class of the model. <!-- end-user-doc -->
 * @see org.eclipse.emf.cdo.defs.CDODefsPackage
 * @generated
 */
public class CDODefsAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected static CDODefsPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  public CDODefsAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = CDODefsPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object.
   * <!-- begin-user-doc --> This implementation
   * returns <code>true</code> if the object is either the model's package or is an instance object of the model. <!--
   * end-user-doc -->
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject)object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @generated
   */
  protected CDODefsSwitch<Adapter> modelSwitch = new CDODefsSwitch<Adapter>()
  {
    @Override
    public Adapter caseCDOViewDef(CDOViewDef object)
    {
      return createCDOViewDefAdapter();
    }

    @Override
    public Adapter caseCDOTransactionDef(CDOTransactionDef object)
    {
      return createCDOTransactionDefAdapter();
    }

    @Override
    public Adapter caseCDOAuditDef(CDOAuditDef object)
    {
      return createCDOAuditDefAdapter();
    }

    @Override
    public Adapter caseCDOSessionDef(CDOSessionDef object)
    {
      return createCDOSessionDefAdapter();
    }

    @Override
    public Adapter caseCDOPackageRegistryDef(CDOPackageRegistryDef object)
    {
      return createCDOPackageRegistryDefAdapter();
    }

    @Override
    public Adapter caseCDOEagerPackageRegistryDef(CDOEagerPackageRegistryDef object)
    {
      return createCDOEagerPackageRegistryDefAdapter();
    }

    @Override
    public Adapter caseCDOLazyPackageRegistryDef(CDOLazyPackageRegistryDef object)
    {
      return createCDOLazyPackageRegistryDefAdapter();
    }

    @Override
    public Adapter caseEPackageDef(EPackageDef object)
    {
      return createEPackageDefAdapter();
    }

    @Override
    public Adapter caseEDynamicPackageDef(EDynamicPackageDef object)
    {
      return createEDynamicPackageDefAdapter();
    }

    @Override
    public Adapter caseEGlobalPackageDef(EGlobalPackageDef object)
    {
      return createEGlobalPackageDefAdapter();
    }

    @Override
    public Adapter caseCDOClientProtocolFactoryDef(CDOClientProtocolFactoryDef object)
    {
      return createCDOClientProtocolFactoryDefAdapter();
    }

    @Override
    public Adapter caseCDOResourceDef(CDOResourceDef object)
    {
      return createCDOResourceDefAdapter();
    }

    @Override
    public Adapter caseDef(Def object)
    {
      return createDefAdapter();
    }

    @Override
    public Adapter caseProtocolProviderDef(ProtocolProviderDef object)
    {
      return createProtocolProviderDefAdapter();
    }

    @Override
    public Adapter caseClientProtocolFactoryDef(ClientProtocolFactoryDef object)
    {
      return createClientProtocolFactoryDefAdapter();
    }

    @Override
    public Adapter defaultCase(EObject object)
    {
      return createEObjectAdapter();
    }
  };

  /**
   * Creates an adapter for the <code>target</code>.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * @param target the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.defs.CDOViewDef <em>CDO View Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.defs.CDOViewDef
   * @generated
   */
  public Adapter createCDOViewDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.defs.CDOTransactionDef <em>CDO Transaction Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.defs.CDOTransactionDef
   * @generated
   */
  public Adapter createCDOTransactionDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.defs.CDOAuditDef <em>CDO Audit Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.defs.CDOAuditDef
   * @generated
   */
  public Adapter createCDOAuditDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.defs.CDOSessionDef <em>CDO Session Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.defs.CDOSessionDef
   * @generated
   */
  public Adapter createCDOSessionDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.defs.CDOPackageRegistryDef <em>CDO Package Registry Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.defs.CDOPackageRegistryDef
   * @generated
   */
  public Adapter createCDOPackageRegistryDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.defs.CDOEagerPackageRegistryDef <em>CDO Eager Package Registry Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that
   * we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.defs.CDOEagerPackageRegistryDef
   * @generated
   */
  public Adapter createCDOEagerPackageRegistryDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.defs.CDOLazyPackageRegistryDef <em>CDO Lazy Package Registry Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that
   * we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.defs.CDOLazyPackageRegistryDef
   * @generated
   */
  public Adapter createCDOLazyPackageRegistryDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.defs.EPackageDef <em>EPackage Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.defs.EPackageDef
   * @generated
   */
  public Adapter createEPackageDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.defs.EDynamicPackageDef <em>EDynamic Package Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.defs.EDynamicPackageDef
   * @generated
   */
  public Adapter createEDynamicPackageDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.defs.EGlobalPackageDef <em>EGlobal Package Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.defs.EGlobalPackageDef
   * @generated
   */
  public Adapter createEGlobalPackageDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.defs.CDOClientProtocolFactoryDef <em>CDO Client Protocol Factory Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so
   * that we can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.defs.CDOClientProtocolFactoryDef
   * @generated
   */
  public Adapter createCDOClientProtocolFactoryDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.emf.cdo.defs.CDOResourceDef <em>CDO Resource Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can easily
   * ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.emf.cdo.defs.CDOResourceDef
   * @generated
   */
  public Adapter createCDOResourceDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.util.defs.Def <em>Def</em>}'. <!--
   * begin-user-doc --> This default implementation returns null so that we can easily ignore cases; it's useful to
   * ignore a case when inheritance will catch all the cases anyway. <!-- end-user-doc -->
   *
   * @return the new adapter.
   * @see org.eclipse.net4j.util.defs.Def
   * @generated
   */
  public Adapter createDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.defs.ProtocolProviderDef <em>Protocol Provider Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we can
   * easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.defs.ProtocolProviderDef
   * @generated
   */
  public Adapter createProtocolProviderDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link org.eclipse.net4j.defs.ClientProtocolFactoryDef <em>Client Protocol Factory Def</em>}'.
   * <!-- begin-user-doc --> This default implementation returns null so that we
   * can easily ignore cases; it's useful to ignore a case when inheritance will catch all the cases anyway. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @see org.eclipse.net4j.defs.ClientProtocolFactoryDef
   * @generated
   */
  public Adapter createClientProtocolFactoryDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case.
   * <!-- begin-user-doc --> This default implementation returns null. <!--
   * end-user-doc -->
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} // CDODefsAdapterFactory
