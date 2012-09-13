/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.transfer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;

/**
 * @author Eike Stepper
 */
public class ResourceFactoryRegistryWithoutDefaults extends ResourceFactoryRegistryImpl
{
  public ResourceFactoryRegistryWithoutDefaults()
  {
    getProtocolToFactoryMap().putAll(Resource.Factory.Registry.INSTANCE.getProtocolToFactoryMap());
    getExtensionToFactoryMap().putAll(Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap());
    getContentTypeToFactoryMap().putAll(Resource.Factory.Registry.INSTANCE.getContentTypeToFactoryMap());

    getExtensionToFactoryMap().remove(Resource.Factory.Registry.DEFAULT_EXTENSION);
    getContentTypeToFactoryMap().remove(Resource.Factory.Registry.DEFAULT_CONTENT_TYPE_IDENTIFIER);
  }

  @Override
  protected Factory delegatedGetFactory(URI uri, String contentTypeIdentifier)
  {
    return null;
  }
}