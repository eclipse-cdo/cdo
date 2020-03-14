/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.spi.net4j;

import org.eclipse.net4j.ITransportConfig;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.INegotiator;

import org.eclipse.internal.net4j.TransportConfig;
import org.eclipse.internal.net4j.bundle.OM;

import java.util.HashSet;
import java.util.Set;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class Acceptor extends Container<IConnector> implements InternalAcceptor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_ACCEPTOR, Acceptor.class);

  private ITransportConfig config;

  private ConnectorPreparer connectorPreparer;

  private transient IListener connectorListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      removeConnector((IConnector)lifecycle);
    }
  };

  private Set<IConnector> acceptedConnectors = new HashSet<>(0);

  public Acceptor()
  {
  }

  @Override
  public synchronized ITransportConfig getConfig()
  {
    if (config == null)
    {
      config = new TransportConfig(this);
    }

    return config;
  }

  @Override
  public synchronized void setConfig(ITransportConfig config)
  {
    this.config = Net4jUtil.copyTransportConfig(this, config);
  }

  /**
   * @since 4.10
   */
  public ConnectorPreparer getConnectorPreparer()
  {
    return connectorPreparer;
  }

  /**
   * @since 4.10
   */
  public void setConnectorPreparer(ConnectorPreparer connectorPreparer)
  {
    this.connectorPreparer = connectorPreparer;
  }

  @Override
  public INegotiator getNegotiator()
  {
    return getConfig().getNegotiator();
  }

  @Override
  public void setNegotiator(INegotiator negotiator)
  {
    getConfig().setNegotiator(negotiator);
  }

  @Override
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

  @Override
  public IConnector[] getElements()
  {
    return getAcceptedConnectors();
  }

  public void prepareConnector(InternalConnector connector)
  {
    connector.setConfig(getConfig());

    if (connectorPreparer != null)
    {
      connectorPreparer.prepareConnector(connector);
    }
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
  public void close()
  {
    LifecycleUtil.deactivate(this, OMLogger.Level.DEBUG);
  }

  @Override
  public boolean isClosed()
  {
    return !isActive();
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (getConfig().getBufferProvider() == null)
    {
      throw new IllegalStateException("getConfig().getBufferProvider() == null"); //$NON-NLS-1$
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    for (IConnector connector : getAcceptedConnectors())
    {
      connector.close();
    }

    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   * @since 4.10
   */
  public interface ConnectorPreparer
  {
    public void prepareConnector(InternalConnector connector);
  }
}
