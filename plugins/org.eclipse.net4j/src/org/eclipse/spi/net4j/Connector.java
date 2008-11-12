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
package org.eclipse.spi.net4j;

import org.eclipse.net4j.ITransportConfig;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.ConnectorState;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.connector.IConnectorStateEvent;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.INegotiationContext;
import org.eclipse.net4j.util.security.INegotiator;
import org.eclipse.net4j.util.security.NegotiationException;

import org.eclipse.internal.net4j.TransportConfig;
import org.eclipse.internal.net4j.bundle.OM;

import java.text.MessageFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class Connector extends ChannelMultiplexer implements InternalConnector
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_CONNECTOR, Connector.class);

  private String userID;

  private ITransportConfig config;

  private transient ConnectorState connectorState = ConnectorState.DISCONNECTED;

  @ExcludeFromDump
  private transient CountDownLatch finishedConnecting;

  @ExcludeFromDump
  private transient CountDownLatch finishedNegotiating;

  @ExcludeFromDump
  private transient INegotiationContext negotiationContext;

  @ExcludeFromDump
  private transient NegotiationException negotiationException;

  public Connector()
  {
  }

  @Override
  public synchronized ITransportConfig getConfig()
  {
    if (config == null)
    {
      config = new TransportConfig();
    }

    return config;
  }

  @Override
  public synchronized void setConfig(ITransportConfig config)
  {
    checkInactive();
    this.config = config;
  }

  public INegotiator getNegotiator()
  {
    return getConfig().getNegotiator();
  }

  public void setNegotiator(INegotiator negotiator)
  {
    getConfig().setNegotiator(negotiator);
  }

  public INegotiationContext getNegotiationContext()
  {
    return negotiationContext;
  }

  public boolean isClient()
  {
    return getLocation() == Location.CLIENT;
  }

  public boolean isServer()
  {
    return getLocation() == Location.SERVER;
  }

  public String getUserID()
  {
    return userID;
  }

  public void setUserID(String userID)
  {
    checkState(getState() != ConnectorState.CONNECTED, "Connector is already connected");
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting userID {0} for {1}", userID, this);
    }

    this.userID = userID;
  }

  public ConnectorState getState()
  {
    return connectorState;
  }

  public void setState(ConnectorState newState) throws ConnectorException
  {
    ConnectorState oldState = getState();
    if (newState != oldState)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Setting state {0} (was {1}) for {2}", newState, oldState.toString().toLowerCase(), this);
      }

      connectorState = newState;
      switch (newState)
      {
      case DISCONNECTED:
        if (finishedConnecting != null)
        {
          finishedConnecting.countDown();
          finishedConnecting = null;
        }

        if (finishedNegotiating != null)
        {
          finishedNegotiating.countDown();
          finishedNegotiating = null;
        }
        break;

      case CONNECTING:
        finishedConnecting = new CountDownLatch(1);
        finishedNegotiating = new CountDownLatch(1);
        // The concrete implementation must advance state to NEGOTIATING or CONNECTED
        break;

      case NEGOTIATING:
        finishedConnecting.countDown();
        negotiationContext = createNegotiationContext();
        getNegotiator().negotiate(negotiationContext);
        break;

      case CONNECTED:
        negotiationContext = null;
        deferredActivate();
        finishedConnecting.countDown();
        finishedNegotiating.countDown();
        break;
      }

      fireEvent(new ConnectorStateEvent(this, oldState, newState));
    }
  }

  public boolean isDisconnected()
  {
    return connectorState == ConnectorState.DISCONNECTED;
  }

  public boolean isConnecting()
  {
    return connectorState == ConnectorState.CONNECTING;
  }

  public boolean isNegotiating()
  {
    return connectorState == ConnectorState.NEGOTIATING;
  }

  public boolean isConnected()
  {
    if (negotiationException != null)
    {
      throw new ConnectorException("Connector negotiation failed", negotiationException);
    }

    return connectorState == ConnectorState.CONNECTED;
  }

  public void connectAsync() throws ConnectorException
  {
    try
    {
      activate();
    }
    catch (ConnectorException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new ConnectorException(ex);
    }
  }

  public boolean waitForConnection(long timeout) throws ConnectorException
  {
    final long MAX_POLL_INTERVAL = 100L;
    boolean withTimeout = timeout != NO_TIMEOUT;

    try
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Waiting for connection...");
      }

      for (;;)
      {
        long t = MAX_POLL_INTERVAL;
        if (withTimeout)
        {
          t = Math.min(MAX_POLL_INTERVAL, timeout);
          timeout -= MAX_POLL_INTERVAL;
        }

        if (t <= 0)
        {
          break;
        }

        if (finishedNegotiating == null)
        {
          break;
        }

        if (finishedNegotiating.await(t, TimeUnit.MILLISECONDS))
        {
          break;
        }
      }

      return isConnected();
    }
    catch (InterruptedException ex)
    {
      return false;
    }
  }

  public boolean connect(long timeout) throws ConnectorException
  {
    connectAsync();
    return waitForConnection(timeout);
  }

  public boolean connect() throws ConnectorException
  {
    return connect(NO_TIMEOUT);
  }

  public ConnectorException disconnect()
  {
    Exception ex = deactivate();
    if (ex == null)
    {
      return null;
    }

    if (ex instanceof ConnectorException)
    {
      return (ConnectorException)ex;
    }

    return new ConnectorException(ex);
  }

  public short getBufferCapacity()
  {
    return getConfig().getBufferProvider().getBufferCapacity();
  }

  public IBuffer provideBuffer()
  {
    return getConfig().getBufferProvider().provideBuffer();
  }

  public void retainBuffer(IBuffer buffer)
  {
    getConfig().getBufferProvider().retainBuffer(buffer);
  }

  protected void leaveConnecting()
  {
    if (getNegotiator() == null)
    {
      setState(ConnectorState.CONNECTED);
    }
    else
    {
      setState(ConnectorState.NEGOTIATING);
    }
  }

  @Override
  protected abstract INegotiationContext createNegotiationContext();

  protected NegotiationException getNegotiationException()
  {
    return negotiationException;
  }

  protected void setNegotiationException(NegotiationException negotiationException)
  {
    this.negotiationException = negotiationException;
  }

  @Override
  protected boolean isDeferredActivation()
  {
    return true;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (getConfig().getBufferProvider() == null)
    {
      throw new IllegalStateException("getConfig().getBufferProvider() == null");
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    setState(ConnectorState.CONNECTING);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    setState(ConnectorState.DISCONNECTED);
    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  private static class ConnectorStateEvent extends Event implements IConnectorStateEvent
  {
    private static final long serialVersionUID = 1L;

    private ConnectorState oldState;

    private ConnectorState newState;

    public ConnectorStateEvent(INotifier notifier, ConnectorState oldState, ConnectorState newState)
    {
      super(notifier);
      this.oldState = oldState;
      this.newState = newState;
    }

    public IConnector getConnector()
    {
      return (IConnector)getSource();
    }

    public ConnectorState getOldState()
    {
      return oldState;
    }

    public ConnectorState getNewState()
    {
      return newState;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("ConnectorStateEvent[source={0}, oldState={1}, newState={2}]", getSource(),
          getOldState(), getNewState());
    }
  }
}
