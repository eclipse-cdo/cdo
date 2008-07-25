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

import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.ConnectorLocation;
import org.eclipse.net4j.connector.ConnectorState;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.connector.IConnectorStateEvent;
import org.eclipse.net4j.protocol.ClientProtocolFactory;
import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.protocol.ServerProtocolFactory;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.RWLock;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.LifecycleEventConverter;
import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.INotifier;
import org.eclipse.net4j.util.factory.FactoryKey;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.MonitorUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.util.security.INegotiationContext;
import org.eclipse.net4j.util.security.INegotiator;

import org.eclipse.internal.net4j.bundle.OM;
import org.eclipse.internal.net4j.channel.Channel;

import org.eclipse.spi.net4j.InternalChannel;
import org.eclipse.spi.net4j.InternalConnector;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Eike Stepper
 */
public abstract class Connector extends Container<IChannel> implements InternalConnector
{
  public static final long NO_OPEN_CHANNEL_TIMEOUT = Long.MAX_VALUE;

  /**
   * Indicates to use the timeout that is configured via debug property <code>open.channel.timeout</code> (see .options
   * file) which has a default of 10 seconds.
   */
  public static final long DEFAULT_OPEN_CHANNEL_TIMEOUT = -1L;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_CONNECTOR, Connector.class);

  private String userID;

  private IRegistry<IFactoryKey, IFactory> protocolFactoryRegistry;

  private List<IElementProcessor> protocolPostProcessors;

  private INegotiator negotiator;

  private INegotiationContext negotiationContext;

  private IBufferProvider bufferProvider;

  /**
   * An optional executor to be used by the {@link IChannel}s to process their receive queues instead of the current
   * thread. If not <code>null</code> the sender and the receiver peers become decoupled.
   * <p>
   */
  private ExecutorService receiveExecutor;

  private long openChannelTimeout = DEFAULT_OPEN_CHANNEL_TIMEOUT;;

  private transient int nextChannelID;

  private transient List<InternalChannel> channels = new ArrayList<InternalChannel>(0);

  private transient RWLock channelsLock = new RWLock(2500);

  private transient ConnectorState connectorState = ConnectorState.DISCONNECTED;

  /**
   * Is registered with each {@link IChannel} of this {@link IConnector}.
   */
  private transient IListener channelListener = new LifecycleEventConverter<IChannel>(this)
  {
    @Override
    protected IContainerEvent<IChannel> createContainerEvent(IContainer<IChannel> container, IChannel element, Kind kind)
    {
      return newContainerEvent(element, kind);
    }
  };

  private transient CountDownLatch finishedConnecting;

  private transient CountDownLatch finishedNegotiating;

  public Connector()
  {
  }

  public ExecutorService getReceiveExecutor()
  {
    return receiveExecutor;
  }

  public void setReceiveExecutor(ExecutorService receiveExecutor)
  {
    this.receiveExecutor = receiveExecutor;
  }

  public IRegistry<IFactoryKey, IFactory> getProtocolFactoryRegistry()
  {
    return protocolFactoryRegistry;
  }

  public void setProtocolFactoryRegistry(IRegistry<IFactoryKey, IFactory> protocolFactoryRegistry)
  {
    this.protocolFactoryRegistry = protocolFactoryRegistry;
  }

  public List<IElementProcessor> getProtocolPostProcessors()
  {
    return protocolPostProcessors;
  }

  public void setProtocolPostProcessors(List<IElementProcessor> protocolPostProcessors)
  {
    this.protocolPostProcessors = protocolPostProcessors;
  }

  public IBufferProvider getBufferProvider()
  {
    return bufferProvider;
  }

  public void setBufferProvider(IBufferProvider bufferProvider)
  {
    this.bufferProvider = bufferProvider;
  }

  public INegotiator getNegotiator()
  {
    return negotiator;
  }

  public void setNegotiator(INegotiator negotiator)
  {
    this.negotiator = negotiator;
  }

  public INegotiationContext getNegotiationContext()
  {
    return negotiationContext;
  }

  public long getOpenChannelTimeout()
  {
    if (openChannelTimeout == DEFAULT_OPEN_CHANNEL_TIMEOUT)
    {
      return OM.BUNDLE.getDebugSupport().getDebugOption("open.channel.timeout", 10000);
    }

    return openChannelTimeout;
  }

  public void setOpenChannelTimeout(long openChannelTimeout)
  {
    this.openChannelTimeout = openChannelTimeout;
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
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting userID {0} for {1}", userID, this); //$NON-NLS-1$ 
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
        finishedConnecting.countDown();
        negotiationContext = createNegotiationContext();
        negotiator.negotiate(negotiationContext);
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
        TRACER.trace("Waiting for connection..."); //$NON-NLS-1$
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

  public IChannel[] getChannels()
  {
    final List<IChannel> result = new ArrayList<IChannel>(0);
    channelsLock.read(new Runnable()
    {
      public void run()
      {
        for (InternalChannel channel : channels)
        {
          if (channel != null)
          {
            result.add(channel);
          }
        }
      }
    });

    return result.toArray(new IChannel[result.size()]);
  }

  @Override
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

  public IChannel openChannel(String protocolID, Object infraStructure) throws ConnectorException
  {
    IProtocol protocol = createProtocol(protocolID, infraStructure);
    if (protocol == null)
    {
      throw new IllegalArgumentException("Unknown protocolID: " + protocolID);
    }

    return openChannel(protocol);
  }

  public IChannel openChannel(final IProtocol protocol) throws ConnectorException
  {
    long openChannelTimeout = getOpenChannelTimeout();
    long start = System.currentTimeMillis();
    if (!waitForConnection(openChannelTimeout))
    {
      throw new ConnectorException("Connector not connected");
    }

    final long elapsed = System.currentTimeMillis() - start;
    int channelID = getNextChannelID();
    InternalChannel channel = createChannel(channelID, protocol);

    try
    {
      try
      {
        registerChannelWithPeer(channelID, channel.getChannelIndex(), protocol, openChannelTimeout - elapsed);
      }
      catch (TimeoutRuntimeException ex)
      {
        // Adjust the message for the complete timeout time
        throw new TimeoutRuntimeException("Registration timeout  after " + openChannelTimeout + " milliseconds");
      }

      channel.activate();
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new ConnectorException(ex);
    }

    return channel;
  }

  public InternalChannel createChannel(int channelID, short channelIndex, String protocolID)
  {
    IProtocol protocol = createProtocol(protocolID, null);
    return createAndAddChannel(channelID, channelIndex, protocol);
  }

  protected InternalChannel createChannelWithoutChannelIndex(int channelID, IProtocol protocol)
  {
    InternalChannel channel = createChannel();
    channel.setChannelID(channelID);

    if (protocol != null)
    {
      protocol.setChannel(channel);
      LifecycleUtil.activate(protocol);
      if (TRACER.isEnabled())
      {
        String protocolType = protocol == null ? null : protocol.getType();
        TRACER.format("Opening channel ID {0} with protocol {1}", channelID, protocolType); //$NON-NLS-1$
      }
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Opening channel ID {0} without protocol", channelID); //$NON-NLS-1$
      }
    }

    channel.setReceiveHandler(protocol);
    channel.addListener(channelListener);
    return channel;
  }

  public InternalChannel createAndAddChannel(int channelID, short channelIndex, IProtocol protocol)
  {
    InternalChannel channel = createChannelWithoutChannelIndex(channelID, protocol);
    channel.setChannelIndex(channelIndex);
    addChannelWithIndex(channel);
    return channel;
  }

  public InternalChannel createChannel(int channelID, IProtocol protocol)
  {
    InternalChannel channel = createChannelWithoutChannelIndex(channelID, protocol);
    addChannelWithoutIndex(channel);
    return channel;
  }

  protected InternalChannel createChannel()
  {
    InternalChannel channel = createChannelInstance();
    channel.setChannelMultiplexer(this);
    channel.setReceiveExecutor(receiveExecutor);
    return channel;
  }

  protected InternalChannel createChannelInstance()
  {
    return new Channel();
  }

  public InternalChannel getChannel(final short channelIndex)
  {
    return channelsLock.read(new Callable<InternalChannel>()
    {
      public InternalChannel call() throws Exception
      {
        return channels.get(channelIndex);
      }
    });
  }

  protected int getNextChannelID()
  {
    return nextChannelID++;
  }

  protected void addChannelWithIndex(final InternalChannel channel)
  {
    channelsLock.write(new Runnable()
    {
      public void run()
      {
        short channelIndex = channel.getChannelIndex();
        while (channelIndex >= channels.size())
        {
          channels.add(null);
        }

        channels.set(channelIndex, channel);
      }
    });
  }

  protected void addChannelWithoutIndex(final InternalChannel channel)
  {
    channelsLock.write(new Runnable()
    {
      public void run()
      {
        int size = channels.size();
        for (short i = 0; i < size; i++)
        {
          if (channels.get(i) == null)
          {
            channels.set(i, channel);
            channel.setChannelIndex(i);
            return;
          }
        }

        channel.setChannelIndex((short)size);
        channels.add(channel);
      }
    });
  }

  /**
   * @return <code>true</code> if the channel was removed, <code>false</code> otherwise.
   */
  public boolean removeChannel(final IChannel channel)
  {
    if (channel == null)
    {
      throw new IllegalArgumentException("channel == null");
    }

    if (!isConnected())
    {
      return false;
    }

    final int channelIndex = channel.getChannelIndex();
    boolean removed = false;
    try
    {
      removed = channelsLock.write(new Callable<Boolean>()
      {
        public Boolean call() throws Exception
        {
          if (channelIndex < channels.size() && channels.get(channelIndex) == channel)
          {
            if (TRACER.isEnabled())
            {
              TRACER.trace("Removing channel " + channelIndex); //$NON-NLS-1$
            }

            channels.set(channelIndex, null);
            return true;
          }

          return false;
        }
      });

      if (removed)
      {
        channel.removeListener(channelListener);
        channel.close();
      }
    }
    catch (RuntimeException ex)
    {
      Exception unwrapped = WrappedException.unwrap(ex);
      if (unwrapped instanceof TimeoutException)
      {
        if (channelIndex < channels.size())
        {
          InternalChannel c = channels.get(channelIndex);
          if (c != null && c.isActive())
          {
            throw ex;
          }
        }
      }
      else
      {
        throw ex;
      }
    }

    return removed;
  }

  public void inverseRemoveChannel(int channelID, short channelIndex)
  {
    try
    {
      InternalChannel channel = getChannel(channelIndex);
      if (channel != null)
      {
        if (channel.getChannelID() != channelID)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Ignoring concurrent atempt to remove channel {0} (channelID={1}", channelIndex, channelID);
          }
        }
        else
        {
          removeChannel(channel);
        }
      }
    }
    catch (RuntimeException ex)
    {
      OM.LOG.warn(ex);
    }
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

  protected abstract INegotiationContext createNegotiationContext();

  /**
   * TODO Use IProtocolProvider and make the protocols real container elements, so that the post processors can reach
   * them. The protocol description can be used to store unique protocol IDs so that always new protocols are created in
   * the container.
   */
  protected IProtocol createProtocol(String type, Object infraStructure)
  {
    if (StringUtil.isEmpty(type))
    {
      return null;
    }

    IRegistry<IFactoryKey, IFactory> registry = getProtocolFactoryRegistry();
    if (registry == null)
    {
      throw new ConnectorException("No protocol registry configured");
    }

    // Get protocol factory
    IFactoryKey key = createProtocolFactoryKey(type);
    IFactory factory = registry.get(key);
    if (factory == null)
    {
      throw new ConnectorException("Unknown protocol: " + type); //$NON-NLS-1$
    }

    // Create protocol
    String description = null;
    IProtocol protocol = (IProtocol)factory.create(description);
    if (protocol == null)
    {
      throw new ConnectorException("Invalid protocol factory: " + type); //$NON-NLS-1$
    }

    protocol.setBufferProvider(bufferProvider);
    protocol.setExecutorService(receiveExecutor);
    if (infraStructure != null)
    {
      protocol.setInfraStructure(infraStructure);
    }

    // Post process protocol
    List<IElementProcessor> processors = getProtocolPostProcessors();
    if (processors != null)
    {
      for (IElementProcessor processor : processors)
      {
        protocol = (IProtocol)processor.process(null, key.getProductGroup(), key.getType(), description, protocol);
      }
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
    if (bufferProvider == null)
    {
      throw new IllegalStateException("bufferProvider == null"); //$NON-NLS-1$
    }

    if (protocolFactoryRegistry == null && TRACER.isEnabled())
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
    channelsLock.write(new Runnable()
    {
      public void run()
      {
        for (short i = 0; i < channels.size(); i++)
        {
          InternalChannel channel = channels.get(i);
          if (channel != null)
          {
            LifecycleUtil.deactivate(channel);
          }
        }

        channels.clear();
      }
    });

    super.doDeactivate();
  }

  protected abstract void registerChannelWithPeer(int channelID, short channelIndex, IProtocol protocol, long timeout)
      throws ConnectorException;

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
