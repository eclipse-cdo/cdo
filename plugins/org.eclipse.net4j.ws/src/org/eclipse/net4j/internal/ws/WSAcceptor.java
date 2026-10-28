/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.internal.ws;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.lifecycle.LifecycleState;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.ws.IWSAcceptor;

import org.eclipse.spi.net4j.Acceptor;
import org.eclipse.spi.net4j.MessageConnector.IMessageConnection;

/**
 * Provider-neutral acceptor that routes accepted message connections into Net4j.
 *
 * @author Eike Stepper
 */
public class WSAcceptor extends Acceptor implements IWSAcceptor
{
  /** Serializes admission with shutdown so a connection is either admitted or rejected as closing begins. */
  private final Object acceptLock = new Object();

  /** WS or WSS route type used by the provider for transport-specific upgrade handling. */
  private final String type;

  /** Route name published in the JVM-wide provider lookup registry while active. */
  private String name;

  /** Set under {@link #acceptLock} before deregistration and connector teardown begin. */
  private boolean closing;

  /**
   * Creates an acceptor whose provider type is used for WS or WSS routing.
   *
   * @param type the transport factory type associated with this acceptor
   */
  public WSAcceptor(String type)
  {
    this.type = type;
  }

  @Override
  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    checkInactive();
    this.name = name;
  }

  @Override
  public String getType()
  {
    return type;
  }

  @Override
  public void accept(IMessageConnection connection)
  {
    if (connection == null)
    {
      throw new IllegalArgumentException("connection == null"); //$NON-NLS-1$
    }

    boolean admitted;

    synchronized (acceptLock)
    {
      admitted = isActive() && !closing;
    }

    if (!admitted)
    {
      reject(connection);
      throw new IllegalStateException("Acceptor is not active"); //$NON-NLS-1$
    }

    WSServerConnector connector = null;
    boolean tracked = false;

    try
    {
      connector = new WSServerConnector(this, connection);
      prepareConnector(connector);

      synchronized (acceptLock)
      {
        checkAcceptanceOpen();
      }

      LifecycleUtil.activate(connector);

      synchronized (acceptLock)
      {
        checkAcceptanceOpen();
      }

      if (!isViable(connector))
      {
        throw new IllegalStateException("Accepted connector was terminated during activation"); //$NON-NLS-1$
      }

      addConnector(connector);

      tracked = isConnectorTracked(connector);
      if (!tracked || !isViable(connector))
      {
        throw new IllegalStateException("Accepted connector was terminated during registration"); //$NON-NLS-1$
      }

      synchronized (acceptLock)
      {
        checkAcceptanceOpen();
      }
    }
    catch (RuntimeException | Error ex)
    {
      if (connector != null)
      {
        try
        {
          if (tracked && isConnectorTracked(connector))
          {
            removeConnector(connector);
          }

          LifecycleUtil.deactivate(connector);
        }
        catch (RuntimeException | Error cleanupException)
        {
          ex.addSuppressed(cleanupException);
        }

        try
        {
          connector.abortAcceptedConnection();
        }
        catch (RuntimeException | Error cleanupException)
        {
          ex.addSuppressed(cleanupException);
        }
      }
      else
      {
        try
        {
          connection.abort();
        }
        catch (RuntimeException | Error cleanupException)
        {
          ex.addSuppressed(cleanupException);
        }
      }

      throw ex;
    }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();

    if (name == null || name.length() == 0)
    {
      throw new IllegalStateException("name is empty"); //$NON-NLS-1$
    }

    synchronized (acceptLock)
    {
      closing = false;
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    if (!WSAcceptorManager.INSTANCE.registerAcceptor(this))
    {
      super.doDeactivate();
      throw new IllegalStateException("A WebSocket acceptor named '" + name + "' is already registered"); //$NON-NLS-1$
    }
  }

  @Override
  protected void doBeforeDeactivate() throws Exception
  {
    synchronized (acceptLock)
    {
      closing = true;
    }

    WSAcceptorManager.INSTANCE.deregisterAcceptor(this);
    super.doBeforeDeactivate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    WSAcceptorManager.INSTANCE.deregisterAcceptor(this);

    for (IConnector acceptedConnector : getAcceptedConnectors())
    {
      if (acceptedConnector instanceof WSServerConnector)
      {
        WSServerConnector connector = (WSServerConnector)acceptedConnector;

        if (LifecycleUtil.getLifecycleState(connector) == LifecycleState.ACTIVATING)
        {
          connector.abortAcceptedConnection();
          removeConnector(connector);
        }
      }
    }

    super.doDeactivate();
  }

  private void checkAcceptanceOpen()
  {
    if (closing || !isActive())
    {
      throw new IllegalStateException("Acceptor is shutting down"); //$NON-NLS-1$
    }
  }

  private boolean isConnectorTracked(WSServerConnector connector)
  {
    for (IConnector acceptedConnector : getAcceptedConnectors())
    {
      if (acceptedConnector == connector)
      {
        return true;
      }
    }

    return false;
  }

  private static boolean isViable(WSServerConnector connector)
  {
    LifecycleState state = LifecycleUtil.getLifecycleState(connector);
    return state == LifecycleState.ACTIVE || state == LifecycleState.ACTIVATING && connector.isDeferredActivation();
  }

  private static void reject(IMessageConnection connection)
  {
    connection.abort();
  }
}
