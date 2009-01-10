/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.session.CDOPackageType;
import org.eclipse.emf.cdo.session.CDOPackageTypeRegistry;
import org.eclipse.emf.cdo.util.CDOFactory;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.util.ModelUtil;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.registry.HashMapRegistry;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.dynamichelpers.ExtensionTracker;
import org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler;
import org.eclipse.core.runtime.dynamichelpers.IExtensionTracker;
import org.eclipse.core.runtime.dynamichelpers.IFilter;

import org.osgi.framework.Bundle;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class CDOPackageTypeRegistryImpl extends HashMapRegistry<String, CDOPackageType> implements
    CDOPackageTypeRegistry
{
  public static final CDOPackageTypeRegistryImpl INSTANCE = new CDOPackageTypeRegistryImpl();

  private static final String ECORE_ID = "org.eclipse.emf.ecore";

  private Object extensionTracker;

  private CDOPackageTypeRegistryImpl()
  {
    activate();
  }

  public void register(EPackage ePackage)
  {
    put(ePackage.getNsURI(), getPackageType(ePackage));
  }

  public void registerLegacy(String packageURI)
  {
    put(packageURI, CDOPackageType.LEGACY);
  }

  public void registerNative(String packageURI)
  {
    put(packageURI, CDOPackageType.NATIVE);
  }

  public void reset()
  {
    deactivate();
    activate();
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    initPackageTypes();
    if (OMPlatform.INSTANCE.isOSGiRunning())
    {
      try
      {
        connectExtensionTracker();
      }
      catch (Throwable t)
      {
        OM.LOG.error(t);
      }
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    if (OMPlatform.INSTANCE.isOSGiRunning())
    {
      try
      {
        disconnectExtensionTracker();
      }
      catch (Throwable t)
      {
        OM.LOG.error(t);
      }
    }

    clear();
    super.doDeactivate();
  }

  private void initPackageTypes()
  {
    for (Object object : EPackage.Registry.INSTANCE.values())
    {
      if (object instanceof EPackage)
      {
        EPackage ePackage = (EPackage)object;
        register(ePackage);
      }
    }

    if (OMPlatform.INSTANCE.isOSGiRunning())
    {
      IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(ECORE_ID,
          EcorePlugin.GENERATED_PACKAGE_PPID);
      addPackageTypes(elements);
    }
  }

  private void addPackageTypes(IConfigurationElement[] elements)
  {
    Map<String, CDOPackageType> bundles = new HashMap<String, CDOPackageType>();
    for (IConfigurationElement element : elements)
    {
      String uri = element.getAttribute("uri");
      if (!StringUtil.isEmpty(uri) && !uri.equals(EresourcePackage.eINSTANCE.getNsURI()) && !containsKey(uri))
      {
        String bundleName = element.getContributor().getName();
        CDOPackageType packageType = bundles.get(bundleName);
        if (packageType == null)
        {
          Bundle bundle = Platform.getBundle(bundleName);
          packageType = getBundleType(bundle);
          bundles.put(bundleName, packageType);
        }

        put(uri, packageType);
      }
    }
  }

  private CDOPackageType getBundleType(Bundle bundle)
  {
    if (bundle.getEntry("META-INF/CDO.MF") != null)
    {
      return CDOPackageType.NATIVE;
    }

    return CDOPackageType.LEGACY;
  }

  private void connectExtensionTracker()
  {
    ExtensionTracker extensionTracker = new ExtensionTracker();
    extensionTracker.registerHandler(new IExtensionChangeHandler()
    {
      public void addExtension(IExtensionTracker tracker, IExtension extension)
      {
        IConfigurationElement[] elements = extension.getConfigurationElements();
        addPackageTypes(elements);
      }

      public void removeExtension(IExtension extension, Object[] objects)
      {
      }
    }, createExtensionPointFilter());

    this.extensionTracker = extensionTracker;
  }

  private void disconnectExtensionTracker()
  {
    ExtensionTracker extensionTracker = (ExtensionTracker)this.extensionTracker;
    extensionTracker.close();
  }

  private IFilter createExtensionPointFilter()
  {
    final IExtensionPoint xpt = Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.emf.ecore",
        EcorePlugin.GENERATED_PACKAGE_PPID);
    return new IFilter()
    {
      public boolean matches(IExtensionPoint target)
      {
        return xpt.equals(target);
      }
    };
  }

  public static CDOPackageType getPackageType(EPackage ePackage)
  {
    if (ePackage.getClass() == EPackageImpl.class)
    {
      EFactory factory = ePackage.getEFactoryInstance();
      if (factory instanceof CDOFactory)
      {
        return CDOPackageType.NATIVE;
      }

      return CDOPackageType.LEGACY;
    }

    EPackage topLevelPackage = ModelUtil.getTopLevelPackage(ePackage);
    EClass eClass = getAnyConcreteEClass(topLevelPackage);
    if (eClass == null)
    {
      return CDOPackageType.LEGACY;
    }

    EObject testObject = EcoreUtil.create(eClass);
    if (testObject instanceof CDOObject)
    {
      return CDOPackageType.NATIVE;
    }

    return CDOPackageType.LEGACY;
  }

  private static EClass getAnyConcreteEClass(EPackage ePackage)
  {
    for (EClassifier classifier : ePackage.getEClassifiers())
    {
      if (classifier instanceof EClass)
      {
        EClass eClass = (EClass)classifier;
        if (!(eClass.isAbstract() || eClass.isInterface()))
        {
          return eClass;
        }
      }
    }

    for (EPackage subpackage : ePackage.getESubpackages())
    {
      EClass eClass = getAnyConcreteEClass(subpackage);
      if (eClass != null)
      {
        return eClass;
      }
    }

    return null;
  }
}
