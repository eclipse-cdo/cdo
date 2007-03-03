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

import org.eclipse.net4j.transport.Acceptor;
import org.eclipse.net4j.transport.AcceptorConnectorsEvent;
import org.eclipse.net4j.transport.Buffer;
import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.transport.Connector;
import org.eclipse.net4j.transport.ProtocolFactory;
import org.eclipse.net4j.transport.ProtocolFactoryID;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.lifecycle.LifecycleListener;
import org.eclipse.net4j.util.lifecycle.LifecycleNotifier;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.internal.net4j.bundle.Net4j;
import org.eclipse.internal.net4j.util.event.EventImpl;
import org.eclipse.internal.net4j.util.event.NotifierImpl;
import org.eclipse.internal.net4j.util.lifecycle.LifecycleImpl;

import java.nio.channels.Channel;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public abstract class AbstractAcceptor extends LifecycleImpl implements Acceptor, INotifier.Introspection
{
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_ACCEPTOR, AbstractAcceptor.class);

  private String description;

  private BufferProvider bufferProvider;

  private IRegistry<ProtocolFactoryID, ProtocolFactory> protocolFactoryRegistry;

  /**
   * An optional executor to be used by the {@link Channel}s to process their
   * {@link ChannelImpl#receiveQueue} instead of the current thread. If not
   * <code>null</code> the calling thread of
   * {@link ChannelImpl#handleBufferFromMultiplexer(Buffer)} becomes decoupled.
   * <p>
   */
  private ExecutorService receiveExecutor;

  private NotifierImpl notifier = new NotifierImpl();

  private LifecycleListener connectorLifecycleListener = new LifecycleListener()
  {
    public void notifyLifecycleAboutToActivate(LifecycleNotifier notifier)
    {
      // Do nothing
    }

    public void notifyLifecycleActivated(LifecycleNotifier notifier)
    {
      // Do nothing
    }

    public void notifyLifecycleDeactivating(LifecycleNotifier notifier)
    {
      removeConnector((Connector)notifier);
    }
  };

  private Set<Connector> acceptedConnectors = new HashSet(0);

  public AbstractAcceptor()
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

  public BufferProvider getBufferProvider()
  {
    return bufferProvider;
  }

  public void setBufferProvider(BufferProvider bufferProvider)
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

  public IRegistry<ProtocolFactoryID, ProtocolFactory> getProtocolFactoryRegistry()
  {
    return protocolFactoryRegistry;
  }

  public void setProtocolFactoryRegistry(IRegistry<ProtocolFactoryID, ProtocolFactory> protocolFactoryRegistry)
  {
    this.protocolFactoryRegistry = protocolFactoryRegistry;
  }

  public void addListener(IListener listener)
  {
    notifier.addListener(listener);
  }

  public void removeListener(IListener listener)
  {
    notifier.removeListener(listener);
  }

  public IListener[] getListeners()
  {
    return notifier.getListeners();
  }

  public Connector[] getAcceptedConnectors()
  {
    synchronized (acceptedConnectors)
    {
      return acceptedConnectors.toArray(new Connector[acceptedConnectors.size()]);
    }
  }

  protected void addConnector(Connector connector)
  {
    try
    {
      LifecycleUtil.activate(connector);
      LifecycleUtil.addListener(connector, connectorLifecycleListener);

      synchronized (acceptedConnectors)
      {
        acceptedConnectors.add(connector);
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("Added connector " + connector); //$NON-NLS-1$
      }

      fireConnectorAccepted(connector);
    }
    catch (Exception ex)
    {
      Net4j.LOG.error(ex);
    }
  }

  protected void removeConnector(Connector connector)
  {
    LifecycleUtil.removeListener(connector, connectorLifecycleListener);
    synchronized (acceptedConnectors)
    {
      acceptedConnectors.remove(connector);
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Removed connector " + connector); //$NON-NLS-1$
    }
  }

  protected void fireConnectorAccepted(Connector connector)
  {
    notifier.fireEvent(new AcceptorConnectorsEventImpl(this, connector));
  }

  @Override
  protected void onAboutToActivate() throws Exception
  {
    super.onAboutToActivate();
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
  protected void onDeactivate() throws Exception
  {
    for (Connector connector : getAcceptedConnectors())
    {
      LifecycleUtil.deactivate(connector);
    }

    super.onDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  private static class AcceptorConnectorsEventImpl extends EventImpl implements AcceptorConnectorsEvent
  {
    private static final long serialVersionUID = 1L;

    private Connector acceptedConnector;

    public AcceptorConnectorsEventImpl(INotifier notifier, Connector acceptedConnector)
    {
      super(notifier);
      this.acceptedConnector = acceptedConnector;
    }

    public Connector getAcceptedConnector()
    {
      return acceptedConnector;
    }
  }
}
