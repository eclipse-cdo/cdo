/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j;

import org.eclipse.net4j.ConnectorException;
import org.eclipse.net4j.ConnectorLocation;
import org.eclipse.net4j.ConnectorState;
import org.eclipse.net4j.IBuffer;
import org.eclipse.net4j.IBufferProvider;
import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.IConnectorChannelsEvent;
import org.eclipse.net4j.IConnectorCredentials;
import org.eclipse.net4j.IConnectorStateEvent;
import org.eclipse.net4j.IProtocol;
import org.eclipse.net4j.internal.util.container.LifecycleEventConverter;
import org.eclipse.net4j.internal.util.container.SingleDeltaContainerEvent;
import org.eclipse.net4j.internal.util.event.Event;
import org.eclipse.net4j.internal.util.factory.FactoryKey;
import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.internal.net4j.bundle.OM;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public abstract class Connector extends Lifecycle implements IConnector
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_CONNECTOR, Connector.class);

  private String userID;

  private IConnectorCredentials credentials;

  private IRegistry<IFactoryKey, IFactory> factoryRegistry;

  private IBufferProvider bufferProvider;

  /**
   * An optional executor to be used by the {@link IChannel}s to process their
   * {@link Channel#receiveQueue} instead of the current thread. If not
   * <code>null</code> the sender and the receiver peers become decoupled.
   * <p>
   */
  private ExecutorService receiveExecutor;

  private List<Channel> channels = new ArrayList(0);

  private ConnectorState connectorState = ConnectorState.DISCONNECTED;

  /**
   * Is registered with each {@link IChannel} of this {@link IConnector}.
   * <p>
   */
  private transient IListener channelListener = new LifecycleEventConverter(this)
  {
    @Override
    protected IContainerEvent createContainerEvent(IContainer container, Object element, Kind kind)
    {
      if (kind == IContainerDelta.Kind.REMOVED)
      {
        removeChannel((Channel)element, false);
      }

      return new ConnectorChannelsEvent((IConnector)container, (IChannel)element, kind);
    }
  };

  private transient CountDownLatch finishedConnecting;

  private transient CountDownLatch finishedNegotiating;

  public Connector()
  {
  }

  public abstract void multiplexBuffer(IChannel channel);

  public ExecutorService getReceiveExecutor()
  {
    return receiveExecutor;
  }

  public void setReceiveExecutor(ExecutorService receiveExecutor)
  {
    this.receiveExecutor = receiveExecutor;
  }

  public IRegistry<IFactoryKey, IFactory> getFactoryRegistry()
  {
    return factoryRegistry;
  }

  public void setFactoryRegistry(IRegistry<IFactoryKey, IFactory> factoryRegistry)
  {
    this.factoryRegistry = factoryRegistry;
  }

  public IBufferProvider getBufferProvider()
  {
    return bufferProvider;
  }

  public void setBufferProvider(IBufferProvider bufferProvider)
  {
    this.bufferProvider = bufferProvider;
  }

  public boolean isClient()
  {
    return getLocation() == ConnectorLocation.CLIENT;
  }

  public boolean isServer()
  {
    return getLocation() == ConnectorLocation.SERVER;
  }

  public String getUserID()
  {
    return userID;
  }

  public void setUserID(String userID)
  {
    this.userID = userID;
  }

  public IConnectorCredentials getCredentials()
  {
    return credentials;
  }

  public void setCredentials(IConnectorCredentials credentials)
  {
    this.credentials = credentials;
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
        TRACER.trace("Setting state " + newState + " (was " + oldState.toString().toLowerCase() //$NON-NLS-1$ //$NON-NLS-2$
            + ")"); //$NON-NLS-1$
      }

      connectorState = newState;
      fireEvent(new ConnectorStateEvent(this, oldState, newState));
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
        if (isServer())
        {
          setState(ConnectorState.NEGOTIATING);
        }
        break;

      case NEGOTIATING:
        finishedConnecting.countDown();
        setState(ConnectorState.CONNECTED); // TODO Implement negotiation
        break;

      case CONNECTED:
        finishedConnecting.countDown(); // Just in case of suspicion
        finishedNegotiating.countDown();
        break;

      }
    }
  }

  public boolean isConnected()
  {
    return getState() == ConnectorState.CONNECTED;
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
    ConnectorState connectorState = getState();
    if (connectorState == ConnectorState.DISCONNECTED)
    {
      return false;
    }

    try
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Waiting for connection..."); //$NON-NLS-1$
      }

      return finishedNegotiating.await(timeout, TimeUnit.MILLISECONDS);
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

  public IChannel[] getChannels()
  {
    List<IChannel> result = new ArrayList(channels.size());
    synchronized (channels)
    {
      for (Channel channel : channels)
      {
        if (channel != null)
        {
          result.add(channel);
        }
      }
    }

    return result.toArray(new IChannel[result.size()]);
  }

  public boolean isEmpty()
  {
    return getElements().length == 0;
  }

  public IChannel[] getElements()
  {
    return getChannels();
  }

  public IChannel openChannel() throws ConnectorException
  {
    return openChannel((IProtocol)null);
  }

  public IChannel openChannel(String protocolID) throws ConnectorException
  {
    IProtocol protocol = createProtocol(protocolID);
    if (protocol == null)
    {
      throw new IllegalArgumentException("Unknown protocolID: " + protocolID);
    }

    return openChannel(protocol);
  }

  public IChannel openChannel(IProtocol protocol) throws ConnectorException
  {
    waitForConnection(Long.MAX_VALUE);
    short channelIndex = findFreeChannelIndex();
    Channel channel = createChannel(channelIndex, protocol);
    registerChannelWithPeer(channelIndex, protocol);

    try
    {
      channel.activate();
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

  public Channel createChannel(short channelIndex, String protocolID)
  {
    IProtocol protocol = createProtocol(protocolID);
    return createChannel(channelIndex, protocol);
  }

  public Channel createChannel(short channelIndex, IProtocol protocol)
  {
    Channel channel = new Channel(receiveExecutor);
    if (protocol != null)
    {
      protocol.setChannel(channel);
      LifecycleUtil.activate(protocol);
      if (TRACER.isEnabled())
      {
        TRACER.format(
            "Opening channel {0} with protocol {1}", channelIndex, protocol == null ? null : protocol.getType()); //$NON-NLS-1$
      }
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Opening channel {0} without protocol", channelIndex); //$NON-NLS-1$
      }
    }

    channel.setChannelIndex(channelIndex);
    channel.setConnector(this);
    channel.setReceiveHandler(protocol);
    channel.addListener(channelListener);
    addChannel(channel);
    return channel;
  }

  public Channel getChannel(short channelIndex)
  {
    try
    {
      return channels.get(channelIndex);
    }
    catch (IndexOutOfBoundsException ex)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Invalid channelIndex " + channelIndex); //$NON-NLS-1$
      }

      return null;
    }
  }

  protected List<Queue<IBuffer>> getChannelBufferQueues()
  {
    final List<Queue<IBuffer>> result = new ArrayList(channels.size());
    synchronized (channels)
    {
      for (final Channel channel : channels)
      {
        if (channel != null && channel.isActive())
        {
          Queue<IBuffer> bufferQueue = channel.getSendQueue();
          result.add(bufferQueue);
        }
      }
    }

    return result;
  }

  protected short findFreeChannelIndex()
  {
    synchronized (channels)
    {
      int size = channels.size();
      for (short i = 0; i < size; i++)
      {
        if (channels.get(i) == null)
        {
          return i;
        }
      }

      return (short)size;
    }
  }

  protected void addChannel(Channel channel)
  {
    synchronized (channels)
    {
      short channelIndex = channel.getChannelIndex();
      while (channelIndex >= channels.size())
      {
        channels.add(null);
      }

      channels.set(channelIndex, channel);
    }
  }

  protected boolean removeChannel(Channel channel, boolean actively)
  {
    synchronized (channels)
    {
      int channelIndex = channel.getChannelIndex();
      if (channelIndex < channels.size() && channels.get(channelIndex) == channel)
      {
        channel.removeListener(channelListener);
        if (TRACER.isEnabled())
        {
          TRACER.trace("Removing channel " + channelIndex); //$NON-NLS-1$
        }

        channels.set(channelIndex, null);
        return true;
      }
      else
      {
        return false;
      }
    }
  }

  protected IProtocol createProtocol(String type)
  {
    if (StringUtil.isEmpty(type) || factoryRegistry == null)
    {
      return null;
    }

    IFactoryKey key = createProtocolFactoryKey(type);
    IFactory<IProtocol> factory = factoryRegistry.get(key);
    if (factory == null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Unknown protocol " + type); //$NON-NLS-1$
      }

      return null;
    }

    return factory.create(null);
  }

  protected IFactoryKey createProtocolFactoryKey(String type)
  {
    switch (getLocation())
    {
    case SERVER:
      return new FactoryKey(ServerProtocolFactory.SERVER_PROTOCOL_GROUP, type);
    case CLIENT:
      return new FactoryKey(ClientProtocolFactory.CLIENT_PROTOCOL_GROUP, type);
    default:
      throw new IllegalStateException();
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

    if (factoryRegistry == null && TRACER.isEnabled())
    {
      // Just a reminder during development
      TRACER.trace("No factoryRegistry!"); //$NON-NLS-1$
    }

    if (receiveExecutor == null && TRACER.isEnabled())
    {
      // Just a reminder during development
      TRACER.trace("No receiveExecutor!"); //$NON-NLS-1$
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
    for (short i = 0; i < channels.size(); i++)
    {
      Channel channel = channels.get(i);
      if (channel != null)
      {
        LifecycleUtil.deactivate(channel);
      }
    }

    channels.clear();
    super.doDeactivate();
  }

  protected abstract void registerChannelWithPeer(short channelIndex, IProtocol protocol) throws ConnectorException;

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
  }

  /**
   * @author Eike Stepper
   */
  private static class ConnectorChannelsEvent extends SingleDeltaContainerEvent<IChannel> implements
      IConnectorChannelsEvent
  {
    private static final long serialVersionUID = 1L;

    public ConnectorChannelsEvent(IConnector connector, IChannel channel, Kind kind)
    {
      super(connector, channel, kind);
    }

    public IConnector getConnector()
    {
      return (IConnector)getContainer();
    }

    public IChannel getChannel()
    {
      return getDeltaElement();
    }
  }
}
