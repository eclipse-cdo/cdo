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
package org.eclipse.internal.net4j.connector;

import org.eclipse.net4j.ITransportConfig;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.ConnectorState;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.connector.IConnectorStateEvent;
import org.eclipse.net4j.protocol.ClientProtocolFactory;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.protocol.IProtocolProvider;
import org.eclipse.net4j.protocol.ServerProtocolFactory;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.factory.FactoryKey;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.MonitorUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.INegotiationContext;
import org.eclipse.net4j.util.security.NegotiationException;

import org.eclipse.internal.net4j.TransportConfig;
import org.eclipse.internal.net4j.bundle.OM;
import org.eclipse.internal.net4j.channel.Channel;

import org.eclipse.spi.net4j.InternalChannel;
import org.eclipse.spi.net4j.InternalConnector;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public abstract class Connector extends Container<IChannel> implements InternalConnector
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_CONNECTOR, Connector.class);

  private String userID;

  private ITransportConfig config;

  private INegotiationContext negotiationContext;

  private long channelTimeout = DEFAULT_CHANNEL_TIMEOUT;

  private transient InternalChannel[] channels = {};

  @ExcludeFromDump
  private transient Object channelsLock = new Object();

  private transient ConnectorState connectorState = ConnectorState.DISCONNECTED;

  @ExcludeFromDump
  private transient CountDownLatch finishedConnecting;

  @ExcludeFromDump
  private transient CountDownLatch finishedNegotiating;

  @ExcludeFromDump
  private NegotiationException negotiationException;

  public Connector()
  {
  }

  public ITransportConfig getConfig()
  {
    if (config == null)
    {
      config = new TransportConfig();
    }

    return config;
  }

  public void setConfig(ITransportConfig config)
  {
    this.config = config;
  }

  public INegotiationContext getNegotiationContext()
  {
    return negotiationContext;
  }

  public long getChannelTimeout()
  {
    if (channelTimeout == DEFAULT_CHANNEL_TIMEOUT)
    {
      return OM.BUNDLE.getDebugSupport().getDebugOption("channel.timeout", 10000);
    }

    return channelTimeout;
  }

  public void setChannelTimeout(long channelTimeout)
  {
    this.channelTimeout = channelTimeout;
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
        getConfig().getNegotiator().negotiate(negotiationContext);
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
    try
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Waiting for connection...");
      }

      do
      {
        if (finishedNegotiating == null)
        {
          break;
        }

        if (finishedNegotiating.await(Math.min(100L, timeout), TimeUnit.MILLISECONDS))
        {
          break;
        }

        if (MonitorUtil.isCanceled())
        {
          break;
        }

        timeout -= 100L;
      } while (timeout > 0);

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

  public final List<IChannel> getChannels()
  {
    List<IChannel> result = new ArrayList<IChannel>(0);
    synchronized (channelsLock)
    {
      for (int i = 0; i < channels.length; i++)
      {
        IChannel channel = channels[i];
        if (channel != null)
        {
          result.add(channel);
        }
      }
    }

    return result;
  }

  @Override
  public boolean isEmpty()
  {
    return getElements().length == 0;
  }

  public IChannel[] getElements()
  {
    List<IChannel> list = getChannels();
    return list.toArray(new IChannel[list.size()]);
  }

  public InternalChannel openChannel() throws ConnectorException
  {
    return openChannel((IProtocol)null);
  }

  public InternalChannel openChannel(String protocolID, Object infraStructure) throws ConnectorException
  {
    IProtocol protocol = createProtocol(protocolID, infraStructure);
    if (protocol == null)
    {
      throw new IllegalArgumentException("Unknown protocolID: " + protocolID);
    }

    return openChannel(protocol);
  }

  public InternalChannel openChannel(IProtocol protocol) throws ConnectorException
  {
    long openChannelTimeout = getChannelTimeout();
    long start = System.currentTimeMillis();
    if (!waitForConnection(openChannelTimeout))
    {
      throw new ConnectorException("Connector not connected");
    }

    final long elapsed = System.currentTimeMillis() - start;
    InternalChannel channel = createChannel();
    initChannel(channel, protocol);
    addChannelWithoutIndex(channel);

    try
    {
      try
      {
        registerChannelWithPeer(channel.getChannelIndex(), openChannelTimeout - elapsed, protocol);
      }
      catch (TimeoutRuntimeException ex)
      {
        // Adjust the message for the complete timeout time
        throw new TimeoutRuntimeException("Registration timeout  after " + openChannelTimeout + " milliseconds");
      }
    }
    catch (ConnectorException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new ConnectorException(ex);
    }

    return channel;
  }

  public InternalChannel inverseOpenChannel(short channelIndex, String protocolID)
  {
    IProtocol protocol = createProtocol(protocolID, null);

    InternalChannel channel = createChannel();
    initChannel(channel, protocol);
    channel.setChannelIndex(channelIndex);
    addChannelWithIndex(channel);
    return channel;
  }

  public final InternalChannel getChannel(short channelIndex)
  {
    int index = getChannelsArrayIndex(channelIndex);
    synchronized (channelsLock)
    {
      if (channels == null || index >= channels.length)
      {
        return null;
      }

      return channels[index];
    }
  }

  protected InternalChannel createChannel()
  {
    return new Channel();
  }

  private void initChannel(InternalChannel channel, IProtocol protocol)
  {
    channel.setChannelMultiplexer(this);
    channel.setReceiveExecutor(getConfig().getReceiveExecutor());
    if (protocol != null)
    {
      protocol.setChannel(channel);
      LifecycleUtil.activate(protocol);
      if (TRACER.isEnabled())
      {
        String protocolType = protocol == null ? null : protocol.getType();
        TRACER.format("Opening channel with protocol {0}", protocolType);
      }

      channel.setReceiveHandler(protocol);
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Opening channel without protocol");
      }
    }
  }

  private void addChannelWithIndex(InternalChannel channel)
  {
    short channelIndex = channel.getChannelIndex();
    int index = getChannelsArrayIndex(channelIndex);
    synchronized (channelsLock)
    {
      if (index >= channels.length)
      {
        InternalChannel[] newChannels = new InternalChannel[index + 1];
        System.arraycopy(channels, 0, newChannels, 0, channels.length);
        channels = newChannels;
      }

      channels[index] = channel;
    }

    LifecycleUtil.activate(channel);
    fireElementAddedEvent(channel);
  }

  private void addChannelWithoutIndex(InternalChannel channel)
  {
    final short INCREMENT = (short)(isClient() ? 1 : -1);
    short channelIndex = INCREMENT;
    synchronized (channelsLock)
    {
      for (;;)
      {
        int index = getChannelsArrayIndex(channelIndex);
        if (index >= channels.length)
        {
          channel.setChannelIndex(channelIndex);
          addChannelWithIndex(channel);
          return;
        }

        if (channels[index] == null)
        {
          channel.setChannelIndex(channelIndex);
          channels[index] = channel;

          LifecycleUtil.activate(channel);
          fireElementAddedEvent(channel);
          return;
        }

        channelIndex += INCREMENT;
      }
    }
  }

  public void closeChannel(IChannel channel) throws ConnectorException
  {
    InternalChannel internalChannel = (InternalChannel)channel;
    deregisterChannelFromPeer(internalChannel, getChannelTimeout());
    removeChannel(internalChannel, false);
  }

  public void inverseCloseChannel(short channelIndex) throws ConnectorException
  {
    InternalChannel channel = getChannel(channelIndex);
    if (channel != null && channel.isActive())
    {
      removeChannel(channel, true);
    }
  }

  private void removeChannel(InternalChannel channel, boolean inverse)
  {
    try
    {
      short channelIndex = channel.getChannelIndex();
      int index = getChannelsArrayIndex(channelIndex);
      synchronized (channelsLock)
      {
        if (index < channels.length)
        {
          if (channels[index] != channel)
          {
            throw new IllegalStateException("Wrong channel: " + channels[index]);
          }

          if (TRACER.isEnabled())
          {
            TRACER.trace("Removing " + channel);
          }

          if (index == channels.length - 1)
          {
            --index;
            while (index > 0 && channels[index] == null)
            {
              --index;
            }

            if (index == 0)
            {
              channels = new InternalChannel[0];
            }
            else
            {
              InternalChannel[] newChannels = new InternalChannel[index + 1];
              System.arraycopy(channels, 0, newChannels, 0, newChannels.length);
              channels = newChannels;
            }
          }
          else
          {
            channels[index] = null;
          }
        }
      }

      channel.finishDeactivate(inverse);
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);
      throw ex;
    }
  }

  private int getChannelsArrayIndex(short channelIndex)
  {
    if (channelIndex < 0)
    {
      return ~channelIndex << 1;
    }

    return (channelIndex << 1) - 1;
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
    if (getConfig().getNegotiator() == null)
    {
      setState(ConnectorState.CONNECTED);
    }
    else
    {
      setState(ConnectorState.NEGOTIATING);
    }
  }

  protected abstract INegotiationContext createNegotiationContext();

  protected NegotiationException getNegotiationException()
  {
    return negotiationException;
  }

  protected void setNegotiationException(NegotiationException negotiationException)
  {
    this.negotiationException = negotiationException;
  }

  protected IProtocol createProtocol(String type, Object infraStructure)
  {
    if (StringUtil.isEmpty(type))
    {
      return null;
    }

    IProtocolProvider protocolProvider = getConfig().getProtocolProvider();
    if (protocolProvider == null)
    {
      throw new ConnectorException("No protocol provider configured");
    }

    IProtocol protocol = protocolProvider.getProtocol(type);
    if (protocol == null)
    {
      throw new ConnectorException("Invalid protocol factory: " + type);
    }

    protocol.setBufferProvider(getConfig().getBufferProvider());
    protocol.setExecutorService(getConfig().getReceiveExecutor());
    if (infraStructure != null)
    {
      protocol.setInfraStructure(infraStructure);
    }

    return protocol;
  }

  protected IFactoryKey createProtocolFactoryKey(String type)
  {
    switch (getLocation())
    {
    case SERVER:
      return new FactoryKey(ServerProtocolFactory.PRODUCT_GROUP, type);
    case CLIENT:
      return new FactoryKey(ClientProtocolFactory.PRODUCT_GROUP, type);
    default:
      throw new IllegalStateException();
    }
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
    synchronized (channelsLock)
    {
      for (short i = 0; i < channels.length; i++)
      {
        InternalChannel channel = channels[i];
        if (channel != null)
        {
          LifecycleUtil.deactivate(channel);
        }
      }

      channels = new InternalChannel[0];
    }

    super.doDeactivate();
  }

  protected abstract void registerChannelWithPeer(short channelIndex, long timeout, IProtocol protocol)
      throws ConnectorException;

  protected abstract void deregisterChannelFromPeer(InternalChannel channel, long timeout) throws ConnectorException;

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
