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
import org.eclipse.emf.cdo.releng.setup.SetupConstants;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;

import org.eclipse.core.runtime.Plugin;

/**
 * @author Eike Stepper
 */
public final class EMFUtil extends Plugin
{
  public static final URI SETUP_URI = getSetupURI();

  public static final String EXAMPLE_URI = System.getProperty(SetupConstants.PROP_EXAMPLE_URI);

  public static final URI EXAMPLE_PROXY_URI = URI.createURI("file:/example.setup");

  public static final ComposedAdapterFactory ADAPTER_FACTORY = new ComposedAdapterFactory(
      EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry());

  private EMFUtil()
  {
  }

  public static ResourceSet createResourceSet()
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SetupResourceFactoryImpl());

    if (EXAMPLE_URI != null)
    {
      URI exampleURI = URI.createURI(EXAMPLE_URI);
      resourceSet.getURIConverter().getURIMap().put(EXAMPLE_PROXY_URI, exampleURI);
    }

    return resourceSet;
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
      uri = "http://git.eclipse.org/c/cdo/cdo.git/plain/plugins/org.eclipse.emf.cdo.releng.setup/Configuration.setup";
    }

    return URI.createURI(uri.replace('\\', '/'));
  }
}
