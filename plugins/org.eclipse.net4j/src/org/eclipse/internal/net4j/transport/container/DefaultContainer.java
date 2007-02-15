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
package org.eclipse.internal.net4j.transport.container;

import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.transport.container.ContainerAdapterFactory;
import org.eclipse.net4j.transport.container.ContainerUtil;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.internal.net4j.util.registry.HashMapRegistry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author Eike Stepper
 */
public class DefaultContainer extends ContainerImpl
{
  public static final short DEFAULT_BUFFER_CAPACITY = 4096;

  public static final ThreadFactory THREAD_FACTORY = new ThreadFactory()
  {
    public Thread newThread(Runnable r)
    {
      Thread thread = new Thread(r);
      thread.setDaemon(true);
      return thread;
    }
  };

  public DefaultContainer()
  {
    this(createAdapterFactoryRegistry(), DEFAULT_BUFFER_CAPACITY);
  }

  public DefaultContainer(short bufferCapacity)
  {
    this(createAdapterFactoryRegistry(), bufferCapacity);
  }

  public DefaultContainer(IRegistry<String, ContainerAdapterFactory> adapterFactoryRegistry)
  {
    this(adapterFactoryRegistry, DEFAULT_BUFFER_CAPACITY);
  }

  public DefaultContainer(IRegistry<String, ContainerAdapterFactory> adapterFactoryRegistry, short bufferCapacity)
  {
    super(adapterFactoryRegistry);
    setExecutorService(createExecutorService());
    setBufferProvider(createBufferProvider(bufferCapacity));
    setAcceptorFactoryRegistry(createAcceptorFactoryRegistry());
    setConnectorFactoryRegistry(createConnectorFactoryRegistry());
    setProtocolFactoryRegistry(createProtocolFactoryRegistry());
    setAcceptorRegistry(createAcceptorRegistry());
    setConnectorRegistry(createConnectorRegistry());
    setChannelRegistry(createChannelRegistry());
  }

  protected static IRegistry<String, ContainerAdapterFactory> createAdapterFactoryRegistry()
  {
    return new HashMapRegistry();
  }

  protected HashMapRegistry createAcceptorFactoryRegistry()
  {
    return new HashMapRegistry();
  }

  protected HashMapRegistry createConnectorFactoryRegistry()
  {
    return new HashMapRegistry();
  }

  protected HashMapRegistry createProtocolFactoryRegistry()
  {
    return new HashMapRegistry();
  }

  protected HashMapRegistry createAcceptorRegistry()
  {
    return new HashMapRegistry();
  }

  protected HashMapRegistry createConnectorRegistry()
  {
    return new HashMapRegistry();
  }

  protected HashMapRegistry createChannelRegistry()
  {
    return new HashMapRegistry();
  }

  protected ExecutorService createExecutorService()
  {
    return Executors.newCachedThreadPool(THREAD_FACTORY);
  }

  protected BufferProvider createBufferProvider(short bufferCapacity)
  {
    return ContainerUtil.createBufferPool(bufferCapacity);
  }
}
