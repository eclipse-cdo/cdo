/*
 * Copyright (c) 2008-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.channel.ChannelException;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.channel.IChannelMultiplexer;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.protocol.IProtocol2;
import org.eclipse.net4j.protocol.IProtocolProvider;
import org.eclipse.net4j.protocol.ProtocolVersionException;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.IExecutorServiceProvider;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.factory.FactoryKey;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.security.INegotiationContext;

import org.eclipse.internal.net4j.TransportConfig;
import org.eclipse.internal.net4j.bundle.OM;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class ChannelMultiplexer extends Container<IChannel> implements InternalChannelMultiplexer, IExecutorServiceProvider, InverseCloseable
{
  private static final ThreadLocal<Boolean> INVERSE_CLOSING = new ThreadLocal<>();

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_CONNECTOR, ChannelMultiplexer.class);

  private ITransportConfig config;

  private long openChannelTimeout = IChannelMultiplexer.DEFAULT_OPEN_CHANNEL_TIMEOUT;

  private ConcurrentMap<Short, IChannel> channels = new ConcurrentHashMap<>();

  @ExcludeFromDump
  private transient Set<Short> channelIDs = new HashSet<>();

  @ExcludeFromDump
  private transient int lastChannelID;

  public ChannelMultiplexer()
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
    checkInactive();
    this.config = Net4jUtil.copyTransportConfig(this, config);
  }

  @Override
  public ExecutorService getExecutorService()
  {
    return ConcurrencyUtil.getExecutorService(config);
  }

  @Override
  public long getOpenChannelTimeout()
  {
    if (openChannelTimeout == IChannelMultiplexer.DEFAULT_OPEN_CHANNEL_TIMEOUT)
    {
      return OM.BUNDLE.getDebugSupport().getDebugOption("open.channel.timeout", 10000); //$NON-NLS-1$
    }

    return openChannelTimeout;
  }

  @Override
  public void setOpenChannelTimeout(long openChannelTimeout)
  {
    this.openChannelTimeout = openChannelTimeout;
  }

  public final InternalChannel getChannel(short channelID)
  {
    return (InternalChannel)channels.get(channelID);
  }

  @Override
  public final Collection<IChannel> getChannels()
  {
    return channels.values();
  }

  @Override
  public boolean isEmpty()
  {
    return channels.isEmpty();
  }

  @Override
  public IChannel[] getElements()
  {
    List<IChannel> list = new ArrayList<>(getChannels());
    return list.toArray(new IChannel[list.size()]);
  }

  @Override
  public InternalChannel openChannel() throws ChannelException
  {
    return openChannel((IProtocol<?>)null);
  }

  @Override
  public InternalChannel openChannel(String protocolID, Object infraStructure) throws ChannelException
  {
    IProtocol<?> protocol = createProtocol(protocolID, infraStructure);
    if (protocol == null)
    {
      throw new IllegalArgumentException("Unknown protocolID: " + protocolID); //$NON-NLS-1$
    }

    return openChannel(protocol);
  }

  @Override
  public InternalChannel openChannel(IProtocol<?> protocol) throws ChannelException
  {
    long start = System.currentTimeMillis();
    doBeforeOpenChannel(protocol);

    InternalChannel channel = createChannel();
    initChannel(channel, protocol);

    short channelID = getNextChannelID();
    channel.setID(channelID);
    addChannel(channel);

    try
    {
      try
      {
        long timeout = getOpenChannelTimeout() - System.currentTimeMillis() + start;
        if (timeout <= 0)
        {
          throw new TimeoutRuntimeException();
        }

        registerChannelWithPeer(channelID, timeout, protocol);
      }
      catch (TimeoutRuntimeException ex)
      {
        // Adjust the message for the complete timeout time
        String message = "Channel registration timeout after " + getOpenChannelTimeout() + " milliseconds";
        throw new TimeoutRuntimeException(message, ex);
      }
    }
    catch (ChannelException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new ChannelException(ex);
    }

    return channel;
  }

  /**
   * @deprecated Use {@link #inverseOpenChannel(short, String, int)}.
   */
  @Deprecated
  public InternalChannel inverseOpenChannel(short channelID, String protocolID)
  {
    return inverseOpenChannel(channelID, protocolID, IProtocol2.UNSPECIFIED_VERSION);
  }

  /**
   * @since 4.2
   */
  public InternalChannel inverseOpenChannel(short channelID, String protocolID, int protocolVersion)
  {
    CONTEXT_MULTIPLEXER.set(this);

    try
    {
      IProtocol<?> protocol = createProtocol(protocolID, null);
      ProtocolVersionException.checkVersion(protocol, protocolVersion);

      InternalChannel channel = createChannel();
      initChannel(channel, protocol);
      channel.setID(channelID);

      addChannel(channel);
      return channel;
    }
    finally
    {
      CONTEXT_MULTIPLEXER.remove();
    }
  }

  @Override
  public void closeChannel(InternalChannel channel) throws ChannelException
  {
    InternalChannel internalChannel = channel;

    if (INVERSE_CLOSING.get() == null)
    {
      deregisterChannelFromPeer(internalChannel);
    }

    removeChannel(internalChannel);
  }

  public void inverseCloseChannel(short channelID) throws ChannelException
  {
    InternalChannel channel = getChannel(channelID);
    INVERSE_CLOSING.set(Boolean.TRUE);

    try
    {
      LifecycleUtil.deactivate(channel);
    }
    finally
    {
      INVERSE_CLOSING.remove();
    }
  }

  @Override
  public void inverseClose()
  {
    INVERSE_CLOSING.set(Boolean.TRUE);

    try
    {
      LifecycleUtil.deactivateNoisy(this);
    }
    finally
    {
      INVERSE_CLOSING.remove();
    }
  }

  protected InternalChannel createChannel()
  {
    return new Channel();
  }

  protected void initChannel(InternalChannel channel, IProtocol<?> protocol)
  {
    channel.setMultiplexer(this);
    if (protocol != null)
    {
      protocol.setChannel(channel);
      LifecycleUtil.activate(protocol);
      if (TRACER.isEnabled())
      {
        String protocolType = protocol.getType();
        TRACER.format("Opening channel with protocol {0}", protocolType); //$NON-NLS-1$
      }

      channel.setReceiveHandler(protocol);
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Opening channel without protocol"); //$NON-NLS-1$
      }
    }
  }

  @SuppressWarnings("unchecked")
  protected <INFRA_STRUCTURE> IProtocol<INFRA_STRUCTURE> createProtocol(String type, INFRA_STRUCTURE infraStructure)
  {
    if (StringUtil.isEmpty(type))
    {
      return null;
    }

    IProtocolProvider protocolProvider = getConfig().getProtocolProvider();
    if (protocolProvider == null)
    {
      throw new ChannelException("No protocol provider configured"); //$NON-NLS-1$
    }

    IProtocol<INFRA_STRUCTURE> protocol = (IProtocol<INFRA_STRUCTURE>)protocolProvider.getProtocol(type);
    if (protocol == null)
    {
      throw new ChannelException("Invalid protocol factory: " + type); //$NON-NLS-1$
    }

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

  protected void doBeforeOpenChannel(IProtocol<?> protocol)
  {
    // Do nothing
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    IChannel[] channels;
    synchronized (channelIDs)
    {
      channels = getElements();
    }

    for (IChannel channel : channels)
    {
      LifecycleUtil.deactivate(channel);
    }

    synchronized (channelIDs)
    {
      this.channels.clear();
    }

    super.doDeactivate();
  }

  protected abstract INegotiationContext createNegotiationContext();

  protected abstract void registerChannelWithPeer(short channelID, long timeout, IProtocol<?> protocol) throws ChannelException;

  protected abstract void deregisterChannelFromPeer(InternalChannel channel) throws ChannelException;

  private short getNextChannelID()
  {
    synchronized (channelIDs)
    {
      int start = lastChannelID;
      int maxValue = Short.MAX_VALUE;
      for (;;)
      {
        ++lastChannelID;
        if (lastChannelID == start)
        {
          throw new ChannelException("Too many channels"); //$NON-NLS-1$
        }

        if (lastChannelID > maxValue)
        {
          lastChannelID = 1;
        }

        short id = (short)(isClient() ? lastChannelID : -lastChannelID);
        if (channelIDs.add(id))
        {
          return id;
        }
      }
    }
  }

  private void addChannel(InternalChannel channel)
  {
    short channelID = channel.getID();
    if (channelID == RESERVED_CHANNEL || channelID == IBuffer.NO_CHANNEL)
    {
      throw new ChannelException("Invalid channel ID: " + channelID); //$NON-NLS-1$
    }

    channels.put(channelID, channel);
    LifecycleUtil.activate(channel);
    fireElementAddedEvent(channel);
  }

  private void removeChannel(InternalChannel channel)
  {
    try
    {
      short channelID = channel.getID();
      boolean removed;
      synchronized (channelIDs)
      {
        removed = channels.remove(channelID) != null;
        if (removed)
        {
          channelIDs.remove(channelID);
        }
      }

      if (removed)
      {
        fireElementRemovedEvent(channel);
      }
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);
      throw ex;
    }
  }
}
