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
package org.eclipse.internal.net4j.acceptor;

import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.util.security.INegotiator;

import org.eclipse.internal.net4j.bundle.OM;

import org.eclipse.spi.net4j.InternalAcceptor;
import org.eclipse.spi.net4j.InternalConnector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public abstract class Acceptor extends Container<IConnector> implements InternalAcceptor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_ACCEPTOR, Acceptor.class);

  private INegotiator negotiator;

  private IBufferProvider bufferProvider;

  private IRegistry<IFactoryKey, IFactory> protocolFactoryRegistry;

  private List<IElementProcessor> protocolPostProcessors;

  private ExecutorService receiveExecutor;

  private transient IListener connectorListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      removeConnector((IConnector)lifecycle);
    }
  };

  private Set<IConnector> acceptedConnectors = new HashSet<IConnector>(0);

  public Acceptor()
  {
  }

  public INegotiator getNegotiator()
  {
    return negotiator;
  }

  public void setNegotiator(INegotiator negotiator)
  {
    this.negotiator = negotiator;
  }

  public IBufferProvider getBufferProvider()
  {
    return bufferProvider;
  }

  public void setBufferProvider(IBufferProvider bufferProvider)
  {
    this.bufferProvider = bufferProvider;
  }

  public ExecutorService getReceiveExecutor()
  {
    return receiveExecutor;
  }

  public void setReceiveExecutor(ExecutorService receiveExecutor)
  {
    this.receiveExecutor = receiveExecutor;
  }

  public IRegistry<IFactoryKey, IFactory> getProtocolFactoryRegistry()
  {
    return protocolFactoryRegistry;
  }

  public void setProtocolFactoryRegistry(IRegistry<IFactoryKey, IFactory> protocolFactoryRegistry)
  {
    this.protocolFactoryRegistry = protocolFactoryRegistry;
  }

  public List<IElementProcessor> getProtocolPostProcessors()
  {
    return protocolPostProcessors;
  }

  public void setProtocolPostProcessors(List<IElementProcessor> protocolPostProcessors)
  {
    this.protocolPostProcessors = protocolPostProcessors;
  }

  public IConnector[] getAcceptedConnectors()
  {
    synchronized (acceptedConnectors)
    {
      return acceptedConnectors.toArray(new IConnector[acceptedConnectors.size()]);
    }
  }

  @Override
  public boolean isEmpty()
  {
    return acceptedConnectors.isEmpty();
  }

  public IConnector[] getElements()
  {
    return getAcceptedConnectors();
  }

  public void prepareConnector(InternalConnector connector)
  {
    connector.setNegotiator(negotiator);
    connector.setBufferProvider(bufferProvider);
    connector.setReceiveExecutor(receiveExecutor);
    connector.setProtocolFactoryRegistry(protocolFactoryRegistry);
    connector.setProtocolPostProcessors(protocolPostProcessors);
  }

  public void addConnector(InternalConnector connector)
  {
    synchronized (acceptedConnectors)
    {
      acceptedConnectors.add(connector);
    }

    connector.addListener(connectorListener);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Added connector " + connector); //$NON-NLS-1$
    }

    fireElementAddedEvent(connector);
  }

  public void removeConnector(IConnector connector)
  {
    connector.removeListener(connectorListener);
    synchronized (acceptedConnectors)
    {
      acceptedConnectors.remove(connector);
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Removed connector " + connector); //$NON-NLS-1$
    }

    fireElementRemovedEvent(connector);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (bufferProvider == null)
    {
      throw new IllegalStateException("bufferProvider == null"); //$NON-NLS-1$
    }

    if (protocolFactoryRegistry == null && TRACER.isEnabled())
    {
      // Just a reminder during development
      TRACER.trace("factoryRegistry == null"); //$NON-NLS-1$
    }

    if (protocolPostProcessors == null && TRACER.isEnabled())
    {
      // Just a reminder during development
      TRACER.trace("protocolPostProcessors == null"); //$NON-NLS-1$
    }

    if (receiveExecutor == null && TRACER.isEnabled())
    {
      // Just a reminder during development
      TRACER.trace("receiveExecutor == null"); //$NON-NLS-1$
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (IConnector connector : getAcceptedConnectors())
    {
      LifecycleUtil.deactivate(connector);
    }

    super.doDeactivate();
  }
}
