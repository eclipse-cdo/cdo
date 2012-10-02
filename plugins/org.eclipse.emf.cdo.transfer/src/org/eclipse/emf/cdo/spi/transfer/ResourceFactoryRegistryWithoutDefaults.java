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

import org.eclipse.emf.cdo.transfer.CDOTransferElement;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;

/**
 * A {@link Registry resource factory registry} that does <b>not</b> delegate and does <b>not</b> recognize default extensions or default content types.
 * This registry can be used to determine whether a {@link CDOTransferElement transfer element} can be loaded as an EMF model {@link Resource resource} or not.
 *
 * @author Eike Stepper
 */
public class ResourceFactoryRegistryWithoutDefaults extends ResourceFactoryRegistryImpl
{
  public ResourceFactoryRegistryWithoutDefaults()
  {
    getProtocolToFactoryMap().putAll(Registry.INSTANCE.getProtocolToFactoryMap());
    getExtensionToFactoryMap().putAll(Registry.INSTANCE.getExtensionToFactoryMap());
    getContentTypeToFactoryMap().putAll(Registry.INSTANCE.getContentTypeToFactoryMap());

    getExtensionToFactoryMap().remove(Registry.DEFAULT_EXTENSION);
    getContentTypeToFactoryMap().remove(Registry.DEFAULT_CONTENT_TYPE_IDENTIFIER);
  }

  @Override
  protected Factory delegatedGetFactory(URI uri, String contentTypeIdentifier)
  {
    return null;
  }
}
