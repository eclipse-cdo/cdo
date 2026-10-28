/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2016, 2019-2022, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.spi.net4j;

import org.eclipse.net4j.ITransportConfig;
import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleState;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.registry.HashMapRegistry;
import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.util.security.INegotiator;

import org.eclipse.internal.net4j.TransportConfig;
import org.eclipse.internal.net4j.bundle.OM;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Manages a listening Net4j transport endpoint and the connectors accepted through it. The acceptor applies transport configuration and
 * negotiation settings to incoming connections.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class Acceptor extends Container<IConnector> implements InternalAcceptor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_ACCEPTOR, Acceptor.class);

  private final IRegistry<String, Object> properties = new HashMapRegistry.AutoCommit<>();

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

  private final Map<IConnector, Object> connectorRegistrations = new HashMap<>();

  private final Map<IConnector, ConnectorEventQueue> connectorEventQueues = new HashMap<>();

  private boolean shuttingDown;

  public Acceptor()
  {
  }

  @Override
  public final IRegistry<String, Object> properties()
  {
    return properties;
  }

  @Override
  public boolean needsBufferProvider()
  {
    return true;
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
    synchronized (acceptedConnectors)
    {
      return acceptedConnectors.isEmpty();
    }
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

  /**
   * Adds an active connector or one undergoing deferred activation to this acceptor and publishes its addition. A connector that
   * terminates during registration is removed again after its lifecycle listener has been installed, so a missed early lifecycle event
   * cannot leave stale accepted state.
   *
   * @param connector the connector to track
   */
  public void addConnector(InternalConnector connector)
  {
    boolean deferredActivation = LifecycleUtil.isDeferredActivation(connector);
    if (!isEligibleForTracking(connector, deferredActivation))
    {
      return;
    }

    Object registration = new Object();
    ConnectorEventQueue eventQueue;
    boolean publish;

    synchronized (acceptedConnectors)
    {
      if (shuttingDown || !isEligibleForTracking(connector, deferredActivation) || !acceptedConnectors.add(connector))
      {
        return;
      }

      try
      {
        connector.addListener(connectorListener);
      }
      catch (RuntimeException | Error ex)
      {
        acceptedConnectors.remove(connector);
        connector.removeListener(connectorListener);
        throw ex;
      }

      if (!isEligibleForTracking(connector, deferredActivation))
      {
        acceptedConnectors.remove(connector);
        connector.removeListener(connectorListener);
        return;
      }

      connectorRegistrations.put(connector, registration);
      eventQueue = connectorEventQueues.computeIfAbsent(connector, key -> new ConnectorEventQueue());
      eventQueue.events.add(IContainerDelta.Kind.ADDED);
      publish = eventQueue.startPublishing();
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Added connector " + connector); //$NON-NLS-1$
    }

    try
    {
      if (publish)
      {
        publishConnectorEvents(connector, eventQueue);
      }
    }
    catch (RuntimeException | Error ex)
    {
      removeConnector(connector, registration);
      throw ex;
    }

    if (!isEligibleForTracking(connector, deferredActivation))
    {
      removeConnector(connector, registration);
    }
  }

  /**
   * Removes a connector that is currently tracked by this acceptor and publishes one removal event. Repeated calls are harmless and do
   * not publish duplicate removal events.
   *
   * @param connector the connector to remove
   */
  public void removeConnector(IConnector connector)
  {
    removeConnector(connector, null);
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

    synchronized (acceptedConnectors)
    {
      shuttingDown = false;
    }

    if (needsBufferProvider())
    {
      checkState(getConfig().getBufferProvider(), "getConfig().getBufferProvider()"); //$NON-NLS-1$
    }
  }

  @Override
  protected void doBeforeDeactivate() throws Exception
  {
    synchronized (acceptedConnectors)
    {
      shuttingDown = true;
    }

    super.doBeforeDeactivate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    IConnector[] connectors;

    synchronized (acceptedConnectors)
    {
      shuttingDown = true;
      connectors = acceptedConnectors.toArray(new IConnector[acceptedConnectors.size()]);
    }

    for (IConnector connector : connectors)
    {
      connector.close();
    }

    super.doDeactivate();
  }

  private void removeConnector(IConnector connector, Object expectedRegistration)
  {
    ConnectorEventQueue eventQueue;
    boolean publish;

    synchronized (acceptedConnectors)
    {
      if (expectedRegistration != null && connectorRegistrations.get(connector) != expectedRegistration)
      {
        return;
      }

      if (!acceptedConnectors.remove(connector))
      {
        return;
      }

      connectorRegistrations.remove(connector);

      eventQueue = connectorEventQueues.get(connector);
      if (eventQueue == null)
      {
        throw new IllegalStateException("Missing connector event queue"); //$NON-NLS-1$
      }

      connector.removeListener(connectorListener);
      eventQueue.events.add(IContainerDelta.Kind.REMOVED);
      publish = eventQueue.startPublishing();
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Removed connector " + connector); //$NON-NLS-1$
    }

    if (publish)
    {
      publishConnectorEvents(connector, eventQueue);
    }
  }

  private void publishConnectorEvents(IConnector connector, ConnectorEventQueue eventQueue)
  {
    Throwable failure = null;

    for (;;)
    {
      IContainerDelta.Kind kind;

      synchronized (acceptedConnectors)
      {
        kind = eventQueue.events.poll();
        if (kind == null)
        {
          eventQueue.publishing = false;

          if (!acceptedConnectors.contains(connector))
          {
            connectorEventQueues.remove(connector, eventQueue);
          }

          break;
        }
      }

      try
      {
        if (kind == IContainerDelta.Kind.ADDED)
        {
          fireElementAddedEvent(connector);
        }
        else
        {
          fireElementRemovedEvent(connector);
        }
      }
      catch (RuntimeException | Error ex)
      {
        if (failure == null)
        {
          failure = ex;
        }
        else
        {
          failure.addSuppressed(ex);
        }
      }
    }

    if (failure instanceof Error)
    {
      throw (Error)failure;
    }

    if (failure != null)
    {
      throw (RuntimeException)failure;
    }
  }

  private static boolean isEligibleForTracking(InternalConnector connector, boolean deferredActivation)
  {
    LifecycleState state = LifecycleUtil.getLifecycleState(connector);
    return state == LifecycleState.ACTIVE || state == LifecycleState.ACTIVATING && deferredActivation;
  }

  /**
   * @author Eike Stepper
   * @since 4.10
   */
  public interface ConnectorPreparer
  {
    public void prepareConnector(InternalConnector connector);
  }

  /**
   * Serializes one connector's container events while allowing listeners to reenter acceptor lifecycle operations.
   *
   * @author Eike Stepper
   */
  private static final class ConnectorEventQueue
  {
    private final Queue<IContainerDelta.Kind> events = new ArrayDeque<>();

    private boolean publishing;

    private boolean startPublishing()
    {
      if (publishing)
      {
        return false;
      }

      publishing = true;
      return true;
    }
  }
}
