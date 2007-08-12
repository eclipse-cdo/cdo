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
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.internal.protocol.CDOIDImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.CDOProtocolConstants;

import org.eclipse.net4j.ConnectorException;
import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.internal.cdo.CDOSessionImpl;
import org.eclipse.emf.internal.cdo.CDOStateMachine;
import org.eclipse.emf.internal.cdo.CDOViewImpl;
import org.eclipse.emf.internal.cdo.InternalCDOObject;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.protocol.CDOClientProtocolFactory;
import org.eclipse.emf.internal.cdo.util.FSMUtil;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class CDOUtil
{
  public static final String EXT_POINT_NAME = "persistent_package";

  private static Map<String, CDOPackageType> packageTypes;

  public static final String CDO_VERSION_SUFFIX = "-CDO";

  private CDOUtil()
  {
  }

  /**
   * Can only be used with Eclipse running!
   */
  public static synchronized Map<String, CDOPackageType> getPackageTypes()
  {
    if (packageTypes == null)
    {
      packageTypes = new HashMap();
      IExtensionRegistry registry = Platform.getExtensionRegistry();

      // Collect native packages
      for (IConfigurationElement element : registry.getConfigurationElementsFor(OM.BUNDLE_ID, EXT_POINT_NAME))
      {
        String uri = element.getAttribute("uri");
        if (!StringUtil.isEmpty(uri) && !uri.equals(EresourcePackage.eINSTANCE.getNsURI()))
        {
          packageTypes.put(uri, CDOPackageType.NATIVE);
        }
      }

      // Collect legacy and converted packages
      for (IConfigurationElement element : registry.getConfigurationElementsFor(EcorePlugin.getPlugin().getBundle()
          .getSymbolicName(), EcorePlugin.GENERATED_PACKAGE_PPID))
      {
        String uri = element.getAttribute("uri");
        if (!StringUtil.isEmpty(uri) && !packageTypes.containsKey(uri)
            && !uri.equals(EresourcePackage.eINSTANCE.getNsURI()))
        {
          String bundleName = element.getContributor().getName();
          Bundle bundle = Platform.getBundle(bundleName);
          String version = (String)bundle.getHeaders().get(Constants.BUNDLE_VERSION);
          if (version.endsWith(CDOUtil.CDO_VERSION_SUFFIX))
          {
            packageTypes.put(uri, CDOPackageType.CONVERTED);
          }
          else
          {
            packageTypes.put(uri, CDOPackageType.LEGACY);
          }
        }
      }
    }

    return packageTypes;
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

  public static void prepareContainer(IManagedContainer container)
  {
    container.registerFactory(new CDOClientProtocolFactory());
  }

  public static String extractResourcePath(URI uri)
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

  public static CDOID extractResourceID(URI uri)
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

  public static URI createResourceURI(String path)
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

  /**
   * Returns a self-contained copy of the eObject with all proxies resolved.
   * 
   * @param eObject
   *          the object to copy.
   * @return the copy.
   * @see EcoreUtil#copy(EObject)
   */
  @Deprecated
  public static EObject copy(EObject eObject, CDOView view)
  {
    Copier copier = new CDOCopier(view);
    EObject result = copier.copy(eObject);
    copier.copyReferences();
    return result;
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
   * @author Eike Stepper
   */
  @Deprecated
  public static final class CDOCopier extends Copier
  {
    private static final long serialVersionUID = 1L;

    private CDOView view;

    public CDOCopier(CDOView view)
    {
      this.view = view;
    }

    @Override
    protected void copyReference(EReference eReference, EObject eObject, EObject copyEObject)
    {
      resolve(eReference, eObject);
      super.copyReference(eReference, eObject, copyEObject);
    }

    @Override
    protected void copyContainment(EReference eReference, EObject eObject, EObject copyEObject)
    {
      resolve(eReference, eObject);
      super.copyContainment(eReference, eObject, copyEObject);
    }

    protected void resolve(EReference eReference, EObject eObject)
    {
      if (eObject.eIsSet(eReference))
      {
        if (eReference.isMany())
        {
          @SuppressWarnings("unchecked")
          List<EObject> list = (List<EObject>)eObject.eGet(eReference);
          for (EObject element : list)
          {
            if (element.eIsProxy())
            {
              EcoreUtil.resolve(element, view.getResourceSet());
            }
          }
        }
        else
        {
          EObject childEObject = (EObject)eObject.eGet(eReference);
          if (childEObject.eIsProxy())
          {
            EcoreUtil.resolve(childEObject, view.getResourceSet());
          }
        }
      }
    }
  }
}
