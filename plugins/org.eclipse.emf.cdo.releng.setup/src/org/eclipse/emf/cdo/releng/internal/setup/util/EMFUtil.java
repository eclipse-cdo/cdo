/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup.util;

import org.eclipse.emf.cdo.releng.internal.setup.AbstractSetupTaskContext;
import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.setup.InstallableUnit;
import org.eclipse.emf.cdo.releng.setup.P2Task;
import org.eclipse.emf.cdo.releng.setup.ScopeRoot;
import org.eclipse.emf.cdo.releng.setup.Setup;
import org.eclipse.emf.cdo.releng.setup.SetupConstants;
import org.eclipse.emf.cdo.releng.setup.SetupPackage;
import org.eclipse.emf.cdo.releng.setup.SetupTask;
import org.eclipse.emf.cdo.releng.setup.util.SetupResource;
import org.eclipse.emf.cdo.releng.setup.util.SetupResourceFactoryImpl;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;

import org.eclipse.core.runtime.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class EMFUtil extends Plugin
{
  public static final URI CONFIGURATION_URI = URI
      .createURI("http://git.eclipse.org/c/cdo/cdo.git/plain/plugins/org.eclipse.emf.cdo.releng.setup/Configuration.setup");

  public static final String EXAMPLE_URI = System.getProperty(SetupConstants.PROP_EXAMPLE_URI);

  public static final URI EXAMPLE_PROXY_URI = URI.createURI("file:/example.setup");

  private static final URI REDIRECTED_CONFIGURATION_URI = getSetupURI();

  private static final URI BRANCH_URI = getBranchURI();

  private static final URI REDIRECTED_BRANCH_URI = getRedirectedBranchURI();

  private EMFUtil()
  {
  }

  public static ComposedAdapterFactory createAdapterFactory()
  {
    return new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
  }

  public static ResourceSet createResourceSet()
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    configureResourceSet(resourceSet);
    return resourceSet;
  }

  public static void configureResourceSet(ResourceSet resourceSet)
  {
    Map<String, Object> extensionToFactoryMap = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
    extensionToFactoryMap.put("xmi", new SetupResourceFactoryImpl());

    URIConverter uriConverter = resourceSet.getURIConverter();
    Map<URI, URI> uriMap = uriConverter.getURIMap();
    uriMap.put(URI.createFileURI(Activator.OLD_PREFERENCES.getAbsolutePath()), SetupConstants.PREFERENCES_URI);

    EList<URIHandler> uriHandlers = uriConverter.getURIHandlers();
    uriHandlers.add(4, new ECFURIHandlerImpl());

    if (!CONFIGURATION_URI.equals(REDIRECTED_CONFIGURATION_URI))
    {
      uriMap.put(CONFIGURATION_URI, REDIRECTED_CONFIGURATION_URI);
    }

    if (BRANCH_URI != null && REDIRECTED_BRANCH_URI != null && !BRANCH_URI.equals(REDIRECTED_BRANCH_URI)
        && uriConverter.exists(REDIRECTED_BRANCH_URI, null))
    {
      uriMap.put(BRANCH_URI, REDIRECTED_BRANCH_URI);
    }

    if (EXAMPLE_URI != null)
    {
      URI exampleURI = URI.createURI(EXAMPLE_URI);
      uriMap.put(EXAMPLE_PROXY_URI, exampleURI);
    }
  }

  public static SetupResource loadResourceSafely(ResourceSet resourceSet, URI uri)
  {
    try
    {
      return (SetupResource)resourceSet.getResource(uri, true);
    }
    catch (Throwable ex)
    {
      Activator.log(ex);
      return (SetupResource)resourceSet.getResource(uri, false);
    }
  }

  public static void saveEObject(EObject eObject)
  {
    try
    {
      XMLResource xmlResource = (XMLResource)eObject.eResource();
      xmlResource.getEObjectToExtensionMap().clear();
      xmlResource.save(null);
    }
    catch (IOException ex)
    {
      Activator.log(ex);
    }
  }

  private static URI getSetupURI()
  {
    String uri = System.getProperty(SetupConstants.PROP_SETUP_URI);
    if (uri == null || !uri.startsWith("file:"))
    {
      return CONFIGURATION_URI;
    }

    return URI.createURI(uri.replace('\\', '/'));
  }

  private static URI getBranchURI()
  {
    try
    {
      if (SetupConstants.SETUP_IDE)
      {
        ResourceSet resourceSet = new ResourceSetImpl();
        Map<String, Object> extensionToFactoryMap = resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap();
        extensionToFactoryMap.put("xmi", new SetupResourceFactoryImpl());

        File branchDir = AbstractSetupTaskContext.getCurrentBranchDir().getCanonicalFile();
        URI uri = AbstractSetupTaskContext.getSetupURI(branchDir);
        SetupResource resource = EMFUtil.loadResourceSafely(resourceSet, uri);
        Setup setup = (Setup)resource.getContents().get(0);
        URI branchURI = EcoreUtil.getURI((EObject)setup.eGet(SetupPackage.Literals.SETUP__BRANCH)).trimFragment();
        return branchURI;
      }
    }
    catch (Throwable ex)
    {
      // Ignore
    }

    return null;
  }

  private static URI getRedirectedBranchURI()
  {
    String uri = System.getProperty(SetupConstants.PROP_SETUP_BRANCH_URI);
    if (uri == null)
    {
      return null;
    }

    return URI.createURI(uri);
  }

  public static Set<String> getInstallableUnitIDs(ScopeRoot scope, boolean includeParentScopes)
  {
    Set<String> ids = new HashSet<String>();

    EList<SetupTask> setupTasks = getSetupTasks(scope, includeParentScopes);
    for (SetupTask setupTask : setupTasks)
    {
      if (setupTask instanceof P2Task)
      {
        P2Task p2Task = (P2Task)setupTask;
        EList<InstallableUnit> ius = p2Task.getInstallableUnits();
        for (InstallableUnit iu : ius)
        {
          ids.add(iu.getID());
        }
      }
    }

    return ids;
  }

  public static EList<SetupTask> getSetupTasks(ScopeRoot scope, boolean includeParentScopes)
  {
    EList<SetupTask> setupTasks = new BasicEList<SetupTask>();

    if (scope != null)
    {
      collectSetupTasks(scope, includeParentScopes, setupTasks);
    }

    return setupTasks;
  }

  private static void collectSetupTasks(ScopeRoot scope, boolean includeParentScopes, EList<SetupTask> setupTasks)
  {
    if (includeParentScopes)
    {
      ScopeRoot parentScope = scope.getParentScopeRoot();
      if (parentScope != null)
      {
        collectSetupTasks(parentScope, true, setupTasks);
      }
    }

    setupTasks.addAll(scope.getSetupTasks());
  }
}
