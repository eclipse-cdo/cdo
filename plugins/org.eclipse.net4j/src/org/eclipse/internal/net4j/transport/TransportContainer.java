/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.transport;

import org.eclipse.net4j.transport.IBufferProvider;
import org.eclipse.net4j.transport.ITransportContainer;
import org.eclipse.net4j.transport.TransportUtil;
import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.internal.net4j.bundle.Net4j;
import org.eclipse.internal.net4j.util.container.ManagedContainer;

import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author Eike Stepper
 */
public class TransportContainer extends ManagedContainer implements ITransportContainer
{
  public static final String EXECUTOR_SERVICE_GROUP = Net4j.BUNDLE_ID + ".executorServices";

  public static final String BUFFER_PROVIDER_GROUP = Net4j.BUNDLE_ID + ".bufferProviders";

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

  private short bufferCapacity;

  public TransportContainer(short bufferCapacity)
  {
    this.bufferCapacity = bufferCapacity;
    putElement(BUFFER_PROVIDER_GROUP, null, null, createBufferProvider());
    putElement(EXECUTOR_SERVICE_GROUP, null, null, createExecutorService());
    addPostProcessor(new PostProcessor());
  }

  public TransportContainer()
  {
    this(DEFAULT_BUFFER_CAPACITY);
  }

  public short getBufferCapacity()
  {
    return bufferCapacity;
  }

  public BufferProvider getBufferProvider()
  {
    return (BufferProvider)getElement(BUFFER_PROVIDER_GROUP, null, null);
  }

  public ExecutorService getExecutorService()
  {
    return (ExecutorService)getElement(EXECUTOR_SERVICE_GROUP, null, null);
  }

  public Acceptor getAcceptor(String type, String description)
  {
    return (Acceptor)getElement(AcceptorFactory.ACCEPTOR_GROUP, type, description);
  }

  public Connector getConnector(String type, String description)
  {
    return (Connector)getElement(ConnectorFactory.CONNECTOR_GROUP, type, description);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("TransportContainer[{0}]", bufferCapacity);
  }

  protected IBufferProvider createBufferProvider()
  {
    return TransportUtil.createBufferPool(getBufferCapacity());
  }

  protected ExecutorService createExecutorService()
  {
    return Executors.newCachedThreadPool(THREAD_FACTORY);
  }

  /**
   * @author Eike Stepper
   */
  private final class PostProcessor implements IElementProcessor
  {
    public Object process(final IManagedContainer container, final String productGroup, final String factoryType,
        final String description, final Object element)
    {
      if (element instanceof Acceptor)
      {
        Acceptor acceptor = (Acceptor)element;
        acceptor.setBufferProvider(getBufferProvider());
        acceptor.setReceiveExecutor(getExecutorService());
        acceptor.setFactoryRegistry(getFactoryRegistry());
      }
      else if (element instanceof Connector)
      {
        Connector connector = (Connector)element;
        connector.setBufferProvider(getBufferProvider());
        connector.setReceiveExecutor(getExecutorService());
        connector.setFactoryRegistry(getFactoryRegistry());
      }

      return element;
    }
  }
}
