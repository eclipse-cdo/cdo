/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.util;

import org.eclipse.emf.cdo.common.util.URIHandlerRegistry;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.util.URIHandlerFactory;

import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Eike Stepper
 */
public final class URIHandlerRegistryImpl implements URIHandlerRegistry, URIHandler
{
  public static final URIHandlerRegistryImpl INSTANCE = new URIHandlerRegistryImpl();

  private final Map<String, URIHandler> handlers = new HashMap<>();

  private boolean initialized;

  private URIHandlerRegistryImpl()
  {
  }

  private void initialize()
  {
    if (!initialized)
    {
      initialized = true;

      for (String scheme : IPluginContainer.INSTANCE.getFactoryTypes(URIHandlerFactory.PRODUCT_GROUP))
      {
        try
        {
          URIHandler handler = (URIHandler)IPluginContainer.INSTANCE.getElement(URIHandlerFactory.PRODUCT_GROUP, scheme, null);
          addURIHandler(scheme, handler);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }

      IPluginContainer.INSTANCE.getFactoryRegistry().addListener(new ContainerEventAdapter<Map.Entry<IFactoryKey, IFactory>>()
      {
        @Override
        protected void onAdded(IContainer<Entry<IFactoryKey, IFactory>> container, Map.Entry<IFactoryKey, IFactory> element)
        {
          String scheme = element.getKey().getType();
          URIHandler handler = (URIHandler)IPluginContainer.INSTANCE.getElement(URIHandlerFactory.PRODUCT_GROUP, scheme, null);
          addURIHandler(scheme, handler);
        }

        @Override
        protected void onRemoved(IContainer<Entry<IFactoryKey, IFactory>> container, Map.Entry<IFactoryKey, IFactory> element)
        {
          String scheme = element.getKey().getType();
          removeURIHandler(scheme);
        }
      });
    }
  }

  @Override
  public synchronized URIHandler addURIHandler(String scheme, URIHandler handler)
  {
    initialize();
    return handlers.put(scheme, handler);
  }

  @Override
  public synchronized URIHandler removeURIHandler(String scheme)
  {
    initialize();
    return handlers.remove(scheme);
  }

  @Override
  public synchronized URIHandler getURIHandler(String scheme)
  {
    initialize();
    return handlers.get(scheme);
  }

  private URIHandler getHandler(URI uri)
  {
    return getURIHandler(uri.scheme());
  }

  @Override
  public boolean canHandle(URI uri)
  {
    URIHandler handler = getHandler(uri);
    return handler != null && handler.canHandle(uri);
  }

  @Override
  public boolean exists(URI uri, Map<?, ?> options)
  {
    return getHandler(uri).exists(uri, options);
  }

  @Override
  public Map<String, ?> contentDescription(URI uri, Map<?, ?> options) throws IOException
  {
    return getHandler(uri).contentDescription(uri, options);
  }

  @Override
  public Map<String, ?> getAttributes(URI uri, Map<?, ?> options)
  {
    return getHandler(uri).getAttributes(uri, options);
  }

  @Override
  public void setAttributes(URI uri, Map<String, ?> attributes, Map<?, ?> options) throws IOException
  {
    getHandler(uri).setAttributes(uri, attributes, options);
  }

  @Override
  public InputStream createInputStream(URI uri, Map<?, ?> options) throws IOException
  {
    return getHandler(uri).createInputStream(uri, options);
  }

  @Override
  public OutputStream createOutputStream(URI uri, Map<?, ?> options) throws IOException
  {
    return getHandler(uri).createOutputStream(uri, options);
  }

  @Override
  public void delete(URI uri, Map<?, ?> options) throws IOException
  {
    getHandler(uri).delete(uri, options);
  }
}
