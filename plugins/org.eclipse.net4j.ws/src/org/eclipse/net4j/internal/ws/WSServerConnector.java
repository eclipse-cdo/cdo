/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.internal.ws;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.connector.IServerConnector;
import org.eclipse.net4j.ws.IWSAcceptor;

import org.eclipse.spi.net4j.MessageConnector;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A provider-neutral server connector that binds one accepted message connection to its owning WS acceptor.
 * <p>
 * The provider has already accepted and upgraded the transport before this connector is created. Activating the
 * connector starts Net4j negotiation and message processing; deactivation closes the connection through
 * {@link MessageConnector}. Failed acceptance or activation aborts the transferred connection, with the wrapper
 * ensuring repeated cleanup paths invoke provider abort at most once.
 *
 * @author Eike Stepper
 */
public class WSServerConnector extends MessageConnector implements IServerConnector
{
  /** Acceptor whose configuration and accepted-connector registry govern this connection. */
  private final WSAcceptor acceptor;

  /** Delegating guard that makes overlapping acceptor and connector cleanup idempotent. */
  private final AbortOnceMessageConnection messageConnection;

  /**
   * Creates a server connector for an accepted message connection.
   *
   * @param acceptor the WS acceptor that owns this connector
   * @param connection the provider-created connection transferred to this connector
   * @throws IllegalArgumentException if either argument is {@code null}
   */
  public WSServerConnector(WSAcceptor acceptor, IMessageConnection connection)
  {
    if (acceptor == null || connection == null)
    {
      throw new IllegalArgumentException("acceptor and connection must be non-null"); //$NON-NLS-1$
    }

    this.acceptor = acceptor;
    messageConnection = new AbortOnceMessageConnection(connection);

    setLocation(Location.SERVER);
    setConnection(messageConnection);
  }

  @Override
  public IWSAcceptor getAcceptor()
  {
    return acceptor;
  }

  void abortAcceptedConnection()
  {
    messageConnection.abort();
    terminatePendingActivation();
  }

  @Override
  protected void onActivationFailed()
  {
    acceptor.removeConnector(this);
  }

  /**
   * Makes repeated cleanup requests harmless while preserving the provider's abort operation.
   *
   * @author Eike Stepper
   */
  private static final class AbortOnceMessageConnection implements IMessageConnection
  {
    private final IMessageConnection delegate;

    private final AtomicBoolean aborted = new AtomicBoolean();

    public AbortOnceMessageConnection(IMessageConnection delegate)
    {
      this.delegate = delegate;
    }

    @Override
    public void start(Listener listener)
    {
      delegate.start(listener);
    }

    @Override
    public boolean send(IBuffer buffer, SendCallback callback)
    {
      return delegate.send(buffer, callback);
    }

    @Override
    public void request(long messages)
    {
      delegate.request(messages);
    }

    @Override
    public void closeGracefully()
    {
      delegate.closeGracefully();
    }

    @Override
    public void abort()
    {
      if (aborted.compareAndSet(false, true))
      {
        delegate.abort();
      }
    }
  }
}
