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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.core.runtime.Plugin;

/**
 * @author Eike Stepper
 */
public final class EMFUtil extends Plugin
{
  public static final URI SETUP_URI = getSetupURI();

  public static final String EXAMPLE_URI = System.getProperty("example.uri");

  public static final URI EXAMPLE_PROXY_URI = URI.createURI("file:/example.setup");

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

  public static Resource loadResourceSafe(ResourceSet resourceSet, URI uri)
  {
    try
    {
      return resourceSet.getResource(uri, true);
    }
    catch (Throwable ex)
    {
      return resourceSet.getResource(uri, false);
    }
  }

  private static URI getSetupURI()
  {
    String uri = System.getProperty("setup.uri");
    if (uri == null || !uri.startsWith("file:"))
    {
      uri = "http://git.eclipse.org/c/cdo/cdo.git/plain/plugins/org.eclipse.emf.cdo.releng.setup/Configuration.setup";
    }

    return URI.createURI(uri.replace('\\', '/'));
  }
}
