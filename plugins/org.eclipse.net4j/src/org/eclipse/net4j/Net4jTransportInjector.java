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

import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.security.INegotiator;

import org.eclipse.internal.net4j.ExecutorServiceFactory;
import org.eclipse.internal.net4j.buffer.BufferProvider;
import org.eclipse.internal.net4j.buffer.BufferProviderFactory;

import org.eclipse.spi.net4j.InternalAcceptor;
import org.eclipse.spi.net4j.InternalConnector;

import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class Net4jTransportInjector implements IElementProcessor
{
  public static INegotiator serverNegotiator;

  public static INegotiator clientNegotiator;

  public Net4jTransportInjector()
  {
  }

  public Object process(IManagedContainer container, String productGroup, String factoryType, String description,
      Object element)
  {
    if (element instanceof InternalAcceptor)
    {
      InternalAcceptor acceptor = (InternalAcceptor)element;
      processAcceptor(container, factoryType, description, acceptor);
    }
    else if (element instanceof InternalConnector)
    {
      InternalConnector connector = (InternalConnector)element;
      processConnector(container, factoryType, description, connector);
    }

    return element;
  }

  protected void processAcceptor(IManagedContainer container, String factoryType, String description,
      InternalAcceptor acceptor)
  {
    if (acceptor.getBufferProvider() == null)
    {
      acceptor.setBufferProvider(getBufferProvider(container));
    }

    if (acceptor.getReceiveExecutor() == null)
    {
      acceptor.setReceiveExecutor(getExecutorService(container));
    }

    if (acceptor.getProtocolFactoryRegistry() == null)
    {
      acceptor.setProtocolFactoryRegistry(container.getFactoryRegistry());
    }

    if (acceptor.getProtocolPostProcessors() == null)
    {
      acceptor.setProtocolPostProcessors(container.getPostProcessors());
    }

    if (serverNegotiator != null && acceptor.getNegotiator() == null)
    {
      acceptor.setNegotiator(serverNegotiator);
    }
  }

  protected void processConnector(IManagedContainer container, String factoryType, String description,
      InternalConnector connector)
  {
    if (connector.getBufferProvider() == null)
    {
      connector.setBufferProvider(getBufferProvider(container));
    }

    if (connector.getReceiveExecutor() == null)
    {
      connector.setReceiveExecutor(getExecutorService(container));
    }

    if (connector.getProtocolFactoryRegistry() == null)
    {
      connector.setProtocolFactoryRegistry(container.getFactoryRegistry());
    }

    if (connector.getProtocolPostProcessors() == null)
    {
      connector.setProtocolPostProcessors(container.getPostProcessors());
    }

    if (clientNegotiator != null && connector.isClient() && connector.getNegotiator() == null)
    {
      connector.setNegotiator(clientNegotiator);
    }
  }

  protected BufferProvider getBufferProvider(IManagedContainer container)
  {
    return (BufferProvider)container.getElement(BufferProviderFactory.PRODUCT_GROUP, BufferProviderFactory.TYPE, null);
  }

  protected ExecutorService getExecutorService(IManagedContainer container)
  {
    return (ExecutorService)container.getElement(ExecutorServiceFactory.PRODUCT_GROUP, ExecutorServiceFactory.TYPE,
        null);
  }
}
