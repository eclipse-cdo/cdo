/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j;

import org.eclipse.net4j.internal.util.security.RandomizerFactory;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.internal.net4j.AcceptorFactory;
import org.eclipse.internal.net4j.BufferFactory;
import org.eclipse.internal.net4j.BufferPool;
import org.eclipse.internal.net4j.BufferProviderFactory;
import org.eclipse.internal.net4j.ConnectorFactory;
import org.eclipse.internal.net4j.ExecutorServiceFactory;
import org.eclipse.internal.net4j.Net4jTransportInjector;

import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public final class Net4jUtil
{
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

  public static IBufferProvider getBufferProvider(Object object)
  {
    if (object instanceof IBufferProvider)
    {
      return (IBufferProvider)object;
    }

    if (object == null)
    {
      throw new IllegalArgumentException("object == null"); //$NON-NLS-1$
    }

    throw new IllegalArgumentException("Unable to provide buffers: " + object); //$NON-NLS-1$
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
