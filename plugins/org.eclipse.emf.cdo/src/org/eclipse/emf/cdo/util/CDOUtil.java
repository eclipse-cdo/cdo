/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.eresource.CDOResourceFactory;
import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.ConnectorException;
import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.internal.cdo.CDOSessionImpl;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class CDOUtil
{
  private CDOUtil()
  {
  }

  public static CDOSession openSession(IConnector connector, String repositoryName, EPackage.Registry delegate)
      throws ConnectorException
  {
    CDOSessionImpl session = new CDOSessionImpl(delegate);
    session.setConnector(connector);
    session.setRepositoryName(repositoryName);
    LifecycleUtil.activate(session);
    return session;
  }

  public static CDOSession openSession(IConnector connector, String repositoryName) throws ConnectorException
  {
    return openSession(connector, repositoryName, null);
  }

  public static CDOView getView(ResourceSet resourceSet)
  {
    EList<Adapter> adapters = resourceSet.eAdapters();
    for (Adapter adapter : adapters)
    {
      if (adapter instanceof CDOView)
      {
        return (CDOView)adapter;
      }
    }

    return null;
  }

  public static void prepareResourceSet(ResourceSet resourceSet)
  {
    CDOResourceFactory factory = CDOResourceFactory.INSTANCE;
    Registry registry = resourceSet.getResourceFactoryRegistry();
    Map<String, Object> map = registry.getProtocolToFactoryMap();
    map.put(CDOProtocolConstants.PROTOCOL_NAME, factory);
  }

  public static String extractPath(URI uri)
  {
    if (!CDOProtocolConstants.PROTOCOL_NAME.equals(uri.scheme()))
    {
      return null;
    }

    if (uri.hasAuthority())
    {
      return null;
    }

    if (!uri.isHierarchical())
    {
      return null;
    }

    if (!uri.hasAbsolutePath())
    {
      return null;
    }

    return uri.path();
  }

  public static CDOID extractID(URI uri)
  {
    if (!CDOProtocolConstants.PROTOCOL_NAME.equals(uri.scheme()))
    {
      return null;
    }

    if (uri.hasAuthority())
    {
      return null;
    }

    if (!uri.isHierarchical())
    {
      return null;
    }

    if (uri.hasAbsolutePath())
    {
      return null;
    }

    try
    {
      String path = uri.path();
      return CDOIDImpl.parse(path);
    }
    catch (RuntimeException ex)
    {
      return null;
    }
  }

  public static URI createURI(String path)
  {
    return URI.createURI(CDOProtocolConstants.PROTOCOL_NAME + ":" + path);
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
}
