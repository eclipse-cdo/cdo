/*
 * Copyright (c) 2008-2012, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.spi.net4j;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.channel.ChannelException;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.ConnectorState;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.connector.IConnectorStateEvent;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.INegotiationContext;
import org.eclipse.net4j.util.security.INegotiator;
import org.eclipse.net4j.util.security.NegotiationException;

import org.eclipse.internal.net4j.bundle.OM;

import java.text.MessageFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class Connector extends ChannelMultiplexer implements InternalConnector
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_CONNECTOR, Connector.class);

  private String userID;

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
  public INegotiator getNegotiator()
  {
    return getConfig().getNegotiator();
  }

  @Override
  public void setNegotiator(INegotiator negotiator)
  {
    getConfig().setNegotiator(negotiator);
  }

  public INegotiationContext getNegotiationContext()
  {
    return negotiationContext;
  }

  @Override
  public boolean isClient()
  {
    return getLocation() == Location.CLIENT;
  }

  @Override
  public boolean isServer()
  {
    return getLocation() == Location.SERVER;
  }

  @Override
  public String getUserID()
  {
    return userID;
  }

  public void setUserID(String userID)
  {
    checkState(getState() != ConnectorState.CONNECTED, "Connector is already connected"); //$NON-NLS-1$
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting userID {0} for {1}", userID, this); //$NON-NLS-1$
    }

    this.userID = userID;
  }

  @Override
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
        TRACER.format("Setting state {0} (was {1}) for {2}", newState, oldState.toString().toLowerCase(), this); //$NON-NLS-1$
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
        negotiationContext = createNegotiationContext();
        finishedConnecting.countDown();
        getNegotiator().negotiate(negotiationContext);
        break;

      case CONNECTED:
        negotiationContext = null;
        deferredActivate(true);
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

  @Override
  public boolean isConnected()
  {
    if (negotiationException != null)
    {
      throw new ConnectorException("Connector negotiation failed", negotiationException); //$NON-NLS-1$
    }

    return connectorState == ConnectorState.CONNECTED;
  }

  @Override
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

  /**
   * @since 4.0
   */
  @Override
  public void waitForConnection(long timeout) throws ConnectorException
  {
    long totalTimeout = timeout;
    final long MAX_POLL_INTERVAL = 100L;
    boolean withTimeout = timeout != NO_TIMEOUT;

    try
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Waiting for connection..."); //$NON-NLS-1$
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

      if (!isConnected())
      {
        throw new ConnectorException("Connection timeout after " + totalTimeout + " milliseconds");
      }
    }
    catch (ConnectorException ex)
    {
      setState(ConnectorState.DISCONNECTED);
      throw ex;
    }
    catch (Exception ex)
    {
      setState(ConnectorState.DISCONNECTED);
      throw new ConnectorException(ex);
    }
  }

  /**
   * @since 4.0
   */
  @Override
  public void connect(long timeout) throws ConnectorException
  {
    connectAsync();
    waitForConnection(timeout);
  }

  /**
   * @since 4.0
   */
  @Override
  public void connect() throws ConnectorException
  {
    connect(NO_TIMEOUT);
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
  public short getBufferCapacity()
  {
    return getConfig().getBufferProvider().getBufferCapacity();
  }

  @Override
  public IBuffer provideBuffer()
  {
    return getConfig().getBufferProvider().provideBuffer();
  }

  @Override
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
  protected void initChannel(InternalChannel channel, IProtocol<?> protocol)
  {
    super.initChannel(channel, protocol);
    channel.setUserID(getUserID());
  }

  @Override
  protected void deregisterChannelFromPeer(InternalChannel channel) throws ChannelException
  {
  }

  @Override
  public Location getLocation()
  {
    return null;
  }

  @Override
  public String getURL()
  {
    return null;
  }

  /**
   * @since 4.1
   */
  @Override
  public boolean isDeferredActivation()
  {
    return true;
  }

  @Override
  protected void doBeforeOpenChannel(IProtocol<?> protocol)
  {
    super.doBeforeOpenChannel(protocol);
    long timeout = getOpenChannelTimeout();
    waitForConnection(timeout);
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();

    if (needsBufferProvider())
    {
      checkState(getConfig().getBufferProvider(), "getConfig().getBufferProvider()"); //$NON-NLS-1$
    }

    if (userID != null && getConfig().getNegotiator() == null)
    {
      throw new IllegalStateException("A user ID on this connector requires a negotiator"); //$NON-NLS-1$
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

    public ConnectorStateEvent(IConnector source, ConnectorState oldState, ConnectorState newState)
    {
      super(source);
      this.oldState = oldState;
      this.newState = newState;
    }

    @Override
    public IConnector getSource()
    {
      return (IConnector)super.getSource();
    }

    @Override
    public ConnectorState getOldState()
    {
      return oldState;
    }

    @Override
    public ConnectorState getNewState()
    {
      return newState;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("ConnectorStateEvent[source={0}, oldState={1}, newState={2}]", getSource(), //$NON-NLS-1$
          getOldState(), getNewState());
    }
  }
}
