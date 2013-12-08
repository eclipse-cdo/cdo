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
package org.eclipse.emf.cdo.releng.setup.util;

import org.eclipse.emf.cdo.releng.internal.setup.Activator;
import org.eclipse.emf.cdo.releng.setup.InstallableUnit;
import org.eclipse.emf.cdo.releng.setup.P2Task;
import org.eclipse.emf.cdo.releng.setup.ScopeRoot;
import org.eclipse.emf.cdo.releng.setup.SetupConstants;
import org.eclipse.emf.cdo.releng.setup.SetupTask;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;

import org.eclipse.core.runtime.Plugin;

import java.util.HashSet;
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

  public static final ComposedAdapterFactory ADAPTER_FACTORY = new ComposedAdapterFactory(
      ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

  private static final URI REDIRECTED_CONFIGURATION_URI = getSetupURI();

  private EMFUtil()
  {
  }

  public static ResourceSet createResourceSet()
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    configureResourceSet(resourceSet);
    return resourceSet;
  }

  public static void configureResourceSet(ResourceSet resourceSet)
  {
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SetupResourceFactoryImpl());
    resourceSet.getURIConverter().getURIHandlers().add(4, new ECFURIHandlerImpl());

    if (!CONFIGURATION_URI.equals(REDIRECTED_CONFIGURATION_URI))
    {
      resourceSet.getURIConverter().getURIMap().put(CONFIGURATION_URI, REDIRECTED_CONFIGURATION_URI);
    }

    if (EXAMPLE_URI != null)
    {
      URI exampleURI = URI.createURI(EXAMPLE_URI);
      resourceSet.getURIConverter().getURIMap().put(EXAMPLE_PROXY_URI, exampleURI);
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

  private static URI getSetupURI()
  {
    String uri = System.getProperty(SetupConstants.PROP_SETUP_URI);
    if (uri == null || !uri.startsWith("file:"))
    {
      return CONFIGURATION_URI;
    }

    return URI.createURI(uri.replace('\\', '/'));
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
