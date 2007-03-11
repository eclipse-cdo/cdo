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
package org.eclipse.internal.net4j.transport;

import org.eclipse.net4j.transport.IAcceptor;
import org.eclipse.net4j.transport.IAcceptorAcceptedEvent;
import org.eclipse.net4j.transport.IBufferProvider;
import org.eclipse.net4j.transport.IConnector;
import org.eclipse.net4j.transport.IProtocolFactory;
import org.eclipse.net4j.transport.IProtocolFactoryID;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.internal.net4j.bundle.Net4j;
import org.eclipse.internal.net4j.util.container.LifecycleEventConverter;
import org.eclipse.internal.net4j.util.event.Event;
import org.eclipse.internal.net4j.util.lifecycle.Lifecycle;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public abstract class Acceptor extends Lifecycle implements IAcceptor, IContainer<IConnector>
{
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_ACCEPTOR, Acceptor.class);

  private String description;

  private IBufferProvider bufferProvider;

  private IRegistry<IProtocolFactoryID, IProtocolFactory> protocolFactoryRegistry;

  private ExecutorService receiveExecutor;

  private transient IListener lifecycleEventConverter = new LifecycleEventConverter(this)
  {
    @Override
    protected void removed(ILifecycleEvent e)
    {
      removeConnector((IConnector)e.getLifecycle());
      super.removed(e);
    }
  };

  private Set<IConnector> acceptedConnectors = new HashSet(0);

  public Acceptor()
  {
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
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

  public IRegistry<IProtocolFactoryID, IProtocolFactory> getProtocolFactoryRegistry()
  {
    return protocolFactoryRegistry;
  }

  public void setProtocolFactoryRegistry(IRegistry<IProtocolFactoryID, IProtocolFactory> protocolFactoryRegistry)
  {
    this.protocolFactoryRegistry = protocolFactoryRegistry;
  }

  public IConnector[] getAcceptedConnectors()
  {
    synchronized (acceptedConnectors)
    {
      return acceptedConnectors.toArray(new IConnector[acceptedConnectors.size()]);
    }
  }

  public IConnector[] getElements()
  {
    return getAcceptedConnectors();
  }

  protected void addConnector(Connector connector)
  {
    try
    {
      connector.activate();
      connector.addListener(lifecycleEventConverter);

      synchronized (acceptedConnectors)
      {
        acceptedConnectors.add(connector);
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("Added connector " + connector); //$NON-NLS-1$
      }

      fireEvent(new AcceptorAcceptedEventImpl(this, connector));
    }
    catch (Exception ex)
    {
      Net4j.LOG.error(ex);
    }
  }

  protected void removeConnector(IConnector connector)
  {
    connector.removeListener(lifecycleEventConverter);
    synchronized (acceptedConnectors)
    {
      acceptedConnectors.remove(connector);
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Removed connector " + connector); //$NON-NLS-1$
    }
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
      TRACER.trace("protocolFactoryRegistry == null"); //$NON-NLS-1$
    }

    if (receiveExecutor == null && TRACER.isEnabled())
    {
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

  /**
   * @author Eike Stepper
   */
  private static class AcceptorAcceptedEventImpl extends Event implements IAcceptorAcceptedEvent
  {
    private static final long serialVersionUID = 1L;

    private IConnector acceptedConnector;

    public AcceptorAcceptedEventImpl(INotifier notifier, IConnector acceptedConnector)
    {
      super(notifier);
      this.acceptedConnector = acceptedConnector;
    }

    public IConnector getConnector()
    {
      return acceptedConnector;
    }
  }
}
