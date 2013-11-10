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
  private EMFUtil()
  {
  }

  public static ResourceSet createResourceSet()
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("xmi", new SetupResourceFactoryImpl());
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
}
