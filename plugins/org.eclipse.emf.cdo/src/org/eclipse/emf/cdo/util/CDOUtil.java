/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/213402
 **************************************************************************/
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOSessionConfiguration;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.CDOViewSet;
import org.eclipse.emf.cdo.CDOXATransaction;

import org.eclipse.emf.internal.cdo.CDOSessionConfigurationImpl;
import org.eclipse.emf.internal.cdo.CDOStateMachine;
import org.eclipse.emf.internal.cdo.CDOViewImpl;
import org.eclipse.emf.internal.cdo.CDOViewSetImpl;
import org.eclipse.emf.internal.cdo.CDOXATransactionImpl;
import org.eclipse.emf.internal.cdo.InternalCDOObject;
import org.eclipse.emf.internal.cdo.protocol.CDOClientProtocolFactory;
import org.eclipse.emf.internal.cdo.util.CDOPackageRegistryImpl;
import org.eclipse.emf.internal.cdo.util.FSMUtil;
import org.eclipse.emf.internal.cdo.util.ProxyResolverURIResourceMap;

import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EStringToStringMapEntryImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import java.util.Iterator;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class CDOUtil
{
  public static final String CDO_VERSION_SUFFIX = "-CDO";

  private CDOUtil()
  {
  }

  public static CDOSessionConfiguration createSessionConfiguration()
  {
    return new CDOSessionConfigurationImpl();
  }

  /**
   * @since 2.0
   */
  public static CDOPackageRegistry createEagerPackageRegistry()
  {
    return new CDOPackageRegistryImpl.Eager();
  }

  /**
   * @since 2.0
   */
  public static CDOPackageRegistry createLazyPackageRegistry()
  {
    return new CDOPackageRegistryImpl.Lazy();
  }

  /**
   * @since 2.0
   */
  public static CDOXATransaction createXATransaction(CDOViewSet viewSet)
  {
    CDOXATransaction xaTransaction = new CDOXATransactionImpl();
    if (viewSet != null)
    {
      xaTransaction.add(viewSet);
    }
    return xaTransaction;
  }

  /**
   * @since 2.0
   */
  public static CDOXATransaction createXATransaction()
  {
    return createXATransaction(null);
  }

  /**
   * @since 2.0
   */
  public static CDOXATransaction getXATransaction(CDOViewSet viewSet)
  {
    EList<Adapter> adapters = viewSet.eAdapters();
    for (Adapter adapter : adapters)
    {
      if (adapter instanceof CDOXATransactionImpl.CDOXAInternalAdapter)
      {
        return ((CDOXATransactionImpl.CDOXAInternalAdapter)adapter).getCDOXA();
      }
    }
    return null;
  }

  /**
   * @since 2.0
   */
  public static CDOViewSet getViewSet(ResourceSet resourceSet)
  {
    EList<Adapter> adapters = resourceSet.eAdapters();
    for (Adapter adapter : adapters)
    {
      if (adapter instanceof CDOViewSet)
      {
        return (CDOViewSet)adapter;
      }
    }
    return null;
  }

  /**
   * @since 2.0
   */
  public static CDOViewSet prepareResourceSet(ResourceSet resourceSet)
  {
    CDOViewSetImpl viewSet = null;

    synchronized (resourceSet)
    {
      viewSet = (CDOViewSetImpl)getViewSet(resourceSet);

      if (viewSet == null)
      {
        if (resourceSet instanceof ResourceSetImpl)
        {
          Map<URI, Resource> resourceMap = null;
          ResourceSetImpl rs = (ResourceSetImpl)resourceSet;
          resourceMap = rs.getURIResourceMap();
          rs.setURIResourceMap(new ProxyResolverURIResourceMap(null, resourceMap));
        }
        else
        {
          throw new ImplementationError("Not a " + ResourceSetImpl.class.getName() + ": "
              + resourceSet.getClass().getName());
        }
        viewSet = new CDOViewSetImpl();
        resourceSet.eAdapters().add(viewSet);
      }
    }
    return viewSet;
  }

  /**
   * @since 2.0
   */
  public static void prepareContainer(IManagedContainer container)
  {
    container.registerFactory(new CDOClientProtocolFactory());
  }

  public static EPackage createEPackage(String name, String nsPrefix, String nsURI)
  {
    EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
    ePackage.setName(name);
    ePackage.setNsPrefix(nsPrefix);
    ePackage.setNsURI(nsURI);
    return ePackage;
  }

  public static EClass createEClass(EPackage ePackage, String name, boolean isAbstract, boolean isInterface)
  {
    EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName(name);
    eClass.setAbstract(isAbstract);
    eClass.setInterface(isInterface);
    ePackage.getEClassifiers().add(eClass);
    return eClass;
  }

  public static EAttribute createEAttribute(EClass eClass, String name, EClassifier type)
  {
    EAttribute eAttribute = EcoreFactory.eINSTANCE.createEAttribute();
    eAttribute.setName(name);
    eAttribute.setEType(type);
    eClass.getEStructuralFeatures().add(eAttribute);
    return eAttribute;
  }

  public static EReference createEReference(EClass eClass, String name, EClassifier type, boolean isRequired,
      boolean isMany)
  {
    EReference eReference = EcoreFactory.eINSTANCE.createEReference();
    eReference.setName(name);
    eReference.setEType(type);
    eReference.setLowerBound(isRequired ? 1 : 0);
    eReference.setUpperBound(isMany ? -1 : 0);
    eClass.getEStructuralFeatures().add(eReference);
    return eReference;
  }

  public static void load(EObject eObject, CDOView view)
  {
    InternalCDOObject cdoObject = FSMUtil.adapt(eObject, view);
    CDOStateMachine.INSTANCE.read(cdoObject);

    for (Iterator<InternalCDOObject> it = FSMUtil.iterator(cdoObject.eContents(), (CDOViewImpl)view); it.hasNext();)
    {
      InternalCDOObject content = it.next();
      load(content, view);
    }
  }

  /**
   * @since 2.0
   */
  public static EObject getEObject(EObject object)
  {
    if (object instanceof InternalCDOObject)
    {
      return ((InternalCDOObject)object).cdoInternalInstance();
    }

    return object;
  }

  /**
   * @since 2.0
   */
  public static CDOObject getCDOObject(EObject object)
  {
    if (object instanceof CDOObject)
    {
      return (CDOObject)object;
    }

    return (CDOObject)FSMUtil.getLegacyWrapper((InternalEObject)object);
  }

  /**
   * @since 2.0
   */
  public static CDOObject getCDOObject(EModelElement object, CDOView view)
  {
    return FSMUtil.adaptMeta((InternalEObject)object, view);
  }

  /**
   * @since 2.0
   */
  public static CDOObject getCDOObject(EGenericType object, CDOView view)
  {
    return FSMUtil.adaptMeta((InternalEObject)object, view);
  }

  /**
   * @since 2.0
   */
  public static CDOObject getCDOObject(EStringToStringMapEntryImpl object, CDOView view)
  {
    return FSMUtil.adaptMeta(object, view);
  }
}
