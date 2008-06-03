/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.buffer.IBufferPool;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.security.RandomizerFactory;

import org.eclipse.internal.net4j.ExecutorServiceFactory;
import org.eclipse.internal.net4j.buffer.BufferFactory;
import org.eclipse.internal.net4j.buffer.BufferPool;
import org.eclipse.internal.net4j.buffer.BufferProviderFactory;

import org.eclipse.spi.net4j.AcceptorFactory;
import org.eclipse.spi.net4j.ConnectorFactory;

import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public final class Net4jUtil
{
  public static final String SCHEME_SEPARATOR = "://";

  public static final short DEFAULT_BUFFER_CAPACITY = 4096;

  private Net4jUtil()
  {
  }

  public static void prepareContainer(IManagedContainer container)
  {
    container.registerFactory(new ExecutorServiceFactory());
    container.registerFactory(new BufferProviderFactory());
    container.registerFactory(new RandomizerFactory());
    container.addPostProcessor(new Net4jTransportInjector());
  }

  public static ExecutorService getExecutorService(IManagedContainer container)
  {
    return ExecutorServiceFactory.get(container);
  }

  public static IBufferProvider getBufferProvider(IManagedContainer container)
  {
    return BufferProviderFactory.get(container);
  }

  public static IAcceptor getAcceptor(IManagedContainer container, String type, String description)
  {
    return (IAcceptor)container.getElement(AcceptorFactory.PRODUCT_GROUP, type, description);
  }

  public static IConnector getConnector(IManagedContainer container, String type, String description)
  {
    return (IConnector)container.getElement(ConnectorFactory.PRODUCT_GROUP, type, description);
  }

  public static IConnector getConnector(IManagedContainer container, String description)
  {
    int pos = description.indexOf(SCHEME_SEPARATOR);
    if (pos <= 0)
    {
      throw new IllegalArgumentException("Connector type (scheme) missing: " + description);
    }

    String factoryType = description.substring(0, pos);

    String connectorDescription = description.substring(pos + SCHEME_SEPARATOR.length());
    if (StringUtil.isEmpty(connectorDescription))
    {
      throw new IllegalArgumentException("Illegal connector description: " + description);
    }

    return (IConnector)container.getElement(ConnectorFactory.PRODUCT_GROUP, factoryType, connectorDescription);
  }

  public static IBufferProvider createBufferFactory(short bufferCapacity)
  {
    return new BufferFactory(bufferCapacity);
  }

  public static IBufferProvider createBufferFactory()
  {
    return createBufferFactory(DEFAULT_BUFFER_CAPACITY);
  }

  public static IBufferPool createBufferPool(IBufferProvider factory)
  {
    return new BufferPool(factory);
  }

  public static IBufferPool createBufferPool(short bufferCapacity)
  {
    return createBufferPool(createBufferFactory(bufferCapacity));
  }

  public static IBufferPool createBufferPool()
  {
    return createBufferPool(createBufferFactory());
  }

  public static long getProvidedBuffers(IBufferProvider bufferProvider)
  {
    if (bufferProvider instanceof IBufferProvider.Introspection)
    {
      return ((IBufferProvider.Introspection)bufferProvider).getProvidedBuffers();
    }

    return -1L;
  }

  public static long getRetainedBuffers(IBufferProvider bufferProvider)
  {
    if (bufferProvider instanceof IBufferProvider.Introspection)
    {
      return ((IBufferProvider.Introspection)bufferProvider).getRetainedBuffers();
    }

    return -1L;
  }

  public static int getPooledBuffers(IBufferPool bufferPool)
  {
    if (bufferPool instanceof IBufferPool.Introspection)
    {
      return ((IBufferPool.Introspection)bufferPool).getPooledBuffers();
    }

    return -1;
  }
}
