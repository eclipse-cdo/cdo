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

import org.eclipse.net4j.transport.Buffer;
import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.transport.Connector;
import org.eclipse.net4j.transport.ConnectorChannelsEvent;
import org.eclipse.net4j.transport.ConnectorCredentials;
import org.eclipse.net4j.transport.ConnectorException;
import org.eclipse.net4j.transport.ConnectorLocation;
import org.eclipse.net4j.transport.ConnectorState;
import org.eclipse.net4j.transport.ConnectorStateEvent;
import org.eclipse.net4j.transport.Protocol;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public abstract class AbstractConnector extends LifecycleImpl implements Connector, INotifier.Introspection
{
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_CONNECTOR, AbstractConnector.class);

  private static final ChannelImpl NULL_CHANNEL = new NullChannel();

  private static final int MIN_CONNECTOR_ID = 1;

  private static final int MAX_CONNECTOR_ID = Integer.MAX_VALUE;

  private static int nextConnectorID = MIN_CONNECTOR_ID;

  private int connectorID = getNextConnectorID();

  private String userID;

  private String description;

  private ConnectorCredentials credentials;

  private IRegistry<ProtocolFactoryID, ProtocolFactory> protocolFactoryRegistry;

  private BufferProvider bufferProvider;

  /**
   * An optional executor to be used by the {@link Channel}s to process their
   * {@link ChannelImpl#receiveQueue} instead of the current thread. If not
   * <code>null</code> the sender and the receiver peers become decoupled.
   * <p>
   */
  private ExecutorService receiveExecutor;

  /**
   * TODO synchronize on channels?
   */
  private List<ChannelImpl> channels = new ArrayList(0);

  private ConnectorState connectorState = ConnectorState.DISCONNECTED;

  private transient NotifierImpl notifier = new NotifierImpl();

  /**
   * Is registered with each {@link Channel} of this {@link Connector}.
   * <p>
   */
  private transient LifecycleListener channelLifecycleListener = new ChannelLifecycleListener();

  private transient CountDownLatch finishedConnecting;

  private transient CountDownLatch finishedNegotiating;

  public AbstractConnector()
  {
  }

  public Integer getID()
  {
    return connectorID;
  }

  public abstract void multiplexBuffer(Channel channel);

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

  public BufferProvider getBufferProvider()
  {
    return bufferProvider;
  }

  public void setBufferProvider(BufferProvider bufferProvider)
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

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public ConnectorCredentials getCredentials()
  {
    return credentials;
  }

  public void setCredentials(ConnectorCredentials credentials)
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
      fireStateChanged(oldState, newState);
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

  public Channel[] getChannels()
  {
    final List<Channel> result = new ArrayList(channels.size());
    synchronized (channels)
    {
      for (final ChannelImpl channel : channels)
      {
        if (channel != NULL_CHANNEL)
        {
          result.add(channel);
        }
      }
    }

    return result.toArray(new Channel[result.size()]);
  }

  public Channel openChannel() throws ConnectorException
  {
    return openChannel(null);
  }

  public Channel openChannel(String protocolID) throws ConnectorException
  {
    return openChannel(protocolID, null);
  }

  public Channel openChannel(String protocolID, Object protocolData) throws ConnectorException
  {
    waitForConnection(Long.MAX_VALUE);
    short channelIndex = findFreeChannelIndex();
    ChannelImpl channel = createChannel(channelIndex, protocolID, protocolData);
    registerChannelWithPeer(channelIndex, protocolID);

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

  public ChannelImpl createChannel(short channelIndex, String protocolID, Object protocolData)
  {
    ChannelImpl channel = new ChannelImpl(receiveExecutor);
    Protocol protocol = createProtocol(protocolID, channel, protocolData);
    if (TRACER.isEnabled())
    {
      TRACER.trace("Opening channel " + channelIndex //$NON-NLS-1$
          + (protocol == null ? " without protocol" : " with protocol " + protocolID)); //$NON-NLS-1$ //$NON-NLS-2$
    }

    channel.setChannelIndex(channelIndex);
    channel.setConnector(this);
    channel.setReceiveHandler(protocol);
    channel.addLifecycleListener(channelLifecycleListener);
    addChannel(channel);
    return channel;
  }

  public ChannelImpl getChannel(short channelIndex)
  {
    try
    {
      ChannelImpl channel = channels.get(channelIndex);
      if (channel == NULL_CHANNEL)
      {
        channel = null;
      }

      return channel;
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

  protected List<Queue<Buffer>> getChannelBufferQueues()
  {
    final List<Queue<Buffer>> result = new ArrayList(channels.size());
    synchronized (channels)
    {
      for (final ChannelImpl channel : channels)
      {
        if (channel != NULL_CHANNEL && channel.isActive())
        {
          Queue<Buffer> bufferQueue = channel.getSendQueue();
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
        if (channels.get(i) == NULL_CHANNEL)
        {
          return i;
        }
      }

      return (short)size;
    }
  }

  protected void addChannel(ChannelImpl channel)
  {
    short channelIndex = channel.getChannelIndex();
    while (channelIndex >= channels.size())
    {
      channels.add(NULL_CHANNEL);
    }

    channels.set(channelIndex, channel);
  }

  protected void removeChannel(ChannelImpl channel)
  {
    channel.removeLifecycleListener(channelLifecycleListener);
    int channelIndex = channel.getChannelIndex();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Removing channel " + channelIndex); //$NON-NLS-1$
    }

    channels.set(channelIndex, NULL_CHANNEL);
  }

  protected Protocol createProtocol(String protocolID, Channel channel, Object protocolData)
  {
    if (protocolID == null || protocolID.length() == 0 || protocolFactoryRegistry == null)
    {
      return null;
    }

    ProtocolFactoryID protocolFactoryID = ProtocolFactoryIDImpl.create(getLocation(), protocolID);
    ProtocolFactory factory = protocolFactoryRegistry.get(protocolFactoryID);

    if (factory == null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Unknown protocol " + protocolID); //$NON-NLS-1$
      }

      return null;
    }

    return factory.createProtocol(channel, protocolData);
  }

  protected void fireStateChanged(ConnectorState oldState, ConnectorState newState)
  {
    notifier.fireEvent(new ConnectorStateEventImpl(this, oldState, newState));
  }

  protected void fireChannelAboutToOpen(Channel channel)
  {
    notifier.fireEvent(new ConnectorChannelsEventImpl(this, channel, ConnectorChannelsEvent.Type.ABOUT_TO_OPEN));
  }

  protected void fireChannelOpened(Channel channel)
  {
    notifier.fireEvent(new ConnectorChannelsEventImpl(this, channel, ConnectorChannelsEvent.Type.OPENED));
  }

  protected void fireChannelClosing(Channel channel)
  {
    notifier.fireEvent(new ConnectorChannelsEventImpl(this, channel, ConnectorChannelsEvent.Type.CLOSING));
  }

  @Override
  protected void onAboutToActivate() throws Exception
  {
    super.onAboutToActivate();
    if (description == null)
    {
      throw new IllegalStateException("description == null"); //$NON-NLS-1$
    }
    else
    {
      userID = DescriptionUtil.getElement(description, 1);
    }

    if (bufferProvider == null)
    {
      throw new IllegalStateException("bufferProvider == null"); //$NON-NLS-1$
    }

    if (protocolFactoryRegistry == null && TRACER.isEnabled())
    {
      // Just a reminder during development
      TRACER.trace("No receive protocolFactoryRegistry!"); //$NON-NLS-1$
    }

    if (receiveExecutor == null && TRACER.isEnabled())
    {
      // Just a reminder during development
      TRACER.trace("No receiveExecutor!"); //$NON-NLS-1$
    }
  }

  @Override
  protected void onActivate() throws Exception
  {
    super.onActivate();
    setState(ConnectorState.CONNECTING);
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    setState(ConnectorState.DISCONNECTED);
    for (short i = 0; i < channels.size(); i++)
    {
      ChannelImpl channel = channels.get(i);
      if (channel != null)
      {
        LifecycleUtil.deactivate(channel);
      }
    }

    channels.clear();
    super.onDeactivate();
  }

  protected abstract void registerChannelWithPeer(short channelIndex, String protocolID) throws ConnectorException;

  private static int getNextConnectorID()
  {
    int id = nextConnectorID;
    if (nextConnectorID == MAX_CONNECTOR_ID)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("ID wrap around"); //$NON-NLS-1$
      }

      nextConnectorID = MIN_CONNECTOR_ID;
    }
    else
    {
      ++nextConnectorID;
    }

    return id;
  }

  /**
   * @author Eike Stepper
   */
  private static final class NullChannel extends ChannelImpl
  {
    private NullChannel()
    {
      super(null);
    }

    @Override
    public boolean isInternal()
    {
      return true;
    }

    @Override
    public String toString()
    {
      return "NullChannel"; //$NON-NLS-1$
    }
  }

  /**
   * Is registered with each {@link Channel} of this {@link Connector}.
   * <p>
   * 
   * @author Eike Stepper
   */
  private final class ChannelLifecycleListener implements LifecycleListener
  {
    public void notifyLifecycleAboutToActivate(LifecycleNotifier notifier)
    {
      ChannelImpl channel = (ChannelImpl)notifier;
      fireChannelAboutToOpen(channel);
    }

    public void notifyLifecycleActivated(LifecycleNotifier notifier)
    {
      ChannelImpl channel = (ChannelImpl)notifier;
      fireChannelOpened(channel);
    }

    public void notifyLifecycleDeactivating(LifecycleNotifier notifier)
    {
      ChannelImpl channel = (ChannelImpl)notifier;
      fireChannelClosing(channel);
      removeChannel(channel);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class ConnectorStateEventImpl extends EventImpl implements ConnectorStateEvent
  {
    private static final long serialVersionUID = 1L;

    private ConnectorState oldState;

    private ConnectorState newState;

    public ConnectorStateEventImpl(INotifier notifier, ConnectorState oldState, ConnectorState newState)
    {
      super(notifier);
      this.oldState = oldState;
      this.newState = newState;
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
  private static class ConnectorChannelsEventImpl extends EventImpl implements ConnectorChannelsEvent
  {
    private static final long serialVersionUID = 1L;

    private Channel channel;

    private ConnectorChannelsEvent.Type type;

    public ConnectorChannelsEventImpl(INotifier notifier, Channel channel, Type type)
    {
      super(notifier);
      this.channel = channel;
      this.type = type;
    }

    public Channel getChannel()
    {
      return channel;
    }

    public ConnectorChannelsEvent.Type getType()
    {
      return type;
    }
  }
}
