/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
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
import org.eclipse.net4j.transport.ConnectorCredentials;
import org.eclipse.net4j.transport.ConnectorException;
import org.eclipse.net4j.transport.Protocol;
import org.eclipse.net4j.transport.ProtocolFactory;
import org.eclipse.net4j.util.lifecycle.AbstractLifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleListener;
import org.eclipse.net4j.util.lifecycle.LifecycleNotifier;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.internal.net4j.bundle.Net4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
public abstract class AbstractConnector extends AbstractLifecycle implements Connector,
    BufferProvider
{
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_CONNECTOR,
      AbstractConnector.class);

  private static final ChannelImpl NULL_CHANNEL = new ChannelImpl(null)
  {
    @Override
    public String toString()
    {
      return "NullChannel"; //$NON-NLS-1$
    }
  };

  private static final int MIN_CONNECTOR_ID = 1;

  private static final int MAX_CONNECTOR_ID = Integer.MAX_VALUE;

  private static int nextConnectorID = MIN_CONNECTOR_ID;

  private int connectorID = getNextConnectorID();

  private ConnectorCredentials credentials;

  private IRegistry<String, ProtocolFactory> protocolFactoryRegistry;

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

  private State state = State.DISCONNECTED;

  /**
   * Don't initialize lazily to circumvent synchronization!
   */
  private Queue<StateListener> stateListeners = new ConcurrentLinkedQueue();

  /**
   * Don't initialize lazily to circumvent synchronization!
   */
  private Queue<ChannelListener> channelListeners = new ConcurrentLinkedQueue();

  /**
   * Is registered with each {@link Channel} of this {@link Connector}.
   * <p>
   */
  private LifecycleListener channelLifecycleListener = new ChannelLifecycleListener();

  private CountDownLatch finishedConnecting;

  private CountDownLatch finishedNegotiating;

  public AbstractConnector()
  {
  }

  public Integer getID()
  {
    return connectorID;
  }

  public abstract void multiplexBuffer(Channel channel);

  public ExecutorService getReceiveExecutor()
  {
    return receiveExecutor;
  }

  public void setReceiveExecutor(ExecutorService receiveExecutor)
  {
    this.receiveExecutor = receiveExecutor;
  }

  public IRegistry<String, ProtocolFactory> getProtocolFactoryRegistry()
  {
    return protocolFactoryRegistry;
  }

  public void setProtocolFactoryRegistry(IRegistry<String, ProtocolFactory> protocolFactoryRegistry)
  {
    this.protocolFactoryRegistry = protocolFactoryRegistry;
  }

  public void addStateListener(StateListener listener)
  {
    stateListeners.add(listener);
  }

  public void removeStateListener(StateListener listener)
  {
    stateListeners.remove(listener);
  }

  public void addChannelListener(ChannelListener listener)
  {
    channelListeners.add(listener);
  }

  public void removeChannelListener(ChannelListener listener)
  {
    channelListeners.remove(listener);
  }

  public BufferProvider getBufferProvider()
  {
    return bufferProvider;
  }

  public void setBufferProvider(BufferProvider bufferProvider)
  {
    this.bufferProvider = bufferProvider;
  }

  public short getBufferCapacity()
  {
    return bufferProvider.getBufferCapacity();
  }

  public Buffer provideBuffer()
  {
    return bufferProvider.provideBuffer();
  }

  public void retainBuffer(Buffer buffer)
  {
    bufferProvider.retainBuffer(buffer);
  }

  public boolean isClient()
  {
    return getType() == Type.CLIENT;
  }

  public boolean isServer()
  {
    return getType() == Type.SERVER;
  }

  public ConnectorCredentials getCredentials()
  {
    return credentials;
  }

  public void setCredentials(ConnectorCredentials credentials)
  {
    this.credentials = credentials;
  }

  public State getState()
  {
    return state;
  }

  public void setState(State newState) throws ConnectorException
  {
    State oldState = getState();
    if (newState != oldState)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace(this,
            "Setting state " + newState + " (was " + oldState.toString().toLowerCase() //$NON-NLS-1$ //$NON-NLS-2$
                + ")"); //$NON-NLS-1$
      }

      state = newState;
      fireStateChanged(newState, oldState);
      switch (newState)
      {
      case DISCONNECTED:
        REGISTRY.deregister(connectorID);
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
        if (getType() == Type.SERVER)
        {
          setState(State.NEGOTIATING);
        }
        break;

      case NEGOTIATING:
        finishedConnecting.countDown();
        setState(State.CONNECTED); // TODO Implement negotiation
        break;

      case CONNECTED:
        finishedConnecting.countDown(); // Just in case of suspicion
        finishedNegotiating.countDown();
        REGISTRY.register(this);
        break;

      }
    }
  }

  public boolean isConnected()
  {
    return getState() == State.CONNECTED;
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
    State state = getState();
    if (state == State.DISCONNECTED)
    {
      return false;
    }

    try
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace(this, "Waiting for connection..."); //$NON-NLS-1$
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
      TRACER.trace(this, "Opening channel " + channelIndex //$NON-NLS-1$
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
        TRACER.trace(this, "Invalid channelIndex " + channelIndex); //$NON-NLS-1$
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
      TRACER.trace(this, "Removing channel " + channelIndex); //$NON-NLS-1$
    }

    channels.set(channelIndex, NULL_CHANNEL);
  }

  protected Protocol createProtocol(String protocolID, Channel channel, Object protocolData)
  {
    if (protocolID == null || protocolID.length() == 0)
    {
      return null;
    }

    IRegistry<String, ProtocolFactory> registry = getProtocolFactoryRegistry();
    if (registry == null)
    {
      return null;
    }

    ProtocolFactory factory = registry.lookup(protocolID);
    if (factory == null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace(this, "Unknown protocol " + protocolID); //$NON-NLS-1$
      }

      return null;
    }

    return factory.createProtocol(channel, protocolData);
  }

  protected void fireChannelAboutToOpen(Channel channel)
  {
    for (ChannelListener listener : channelListeners)
    {
      try
      {
        listener.notifyChannelOpened(channel);
      }
      catch (Exception ex)
      {
        Net4j.LOG.error(ex);
      }
    }
  }

  protected void fireChannelOpened(Channel channel)
  {
    for (ChannelListener listener : channelListeners)
    {
      try
      {
        listener.notifyChannelOpened(channel);
      }
      catch (Exception ex)
      {
        Net4j.LOG.error(ex);
      }
    }
  }

  protected void fireChannelClosing(Channel channel)
  {
    for (ChannelListener listener : channelListeners)
    {
      try
      {
        listener.notifyChannelClosing(channel);
      }
      catch (Exception ex)
      {
        Net4j.LOG.error(ex);
      }
    }
  }

  protected void fireStateChanged(State newState, State oldState)
  {
    for (StateListener listener : stateListeners)
    {
      try
      {
        listener.notifyStateChanged(this, newState, oldState);
      }
      catch (Exception ex)
      {
        Net4j.LOG.error(ex);
      }
    }
  }

  @Override
  protected void onAboutToActivate() throws Exception
  {
    super.onAboutToActivate();
    if (bufferProvider == null)
    {
      throw new IllegalStateException("bufferProvider == null"); //$NON-NLS-1$
    }

    if (protocolFactoryRegistry == null && TRACER.isEnabled())
    {
      TRACER.trace(this, "No protocol factory registry!"); //$NON-NLS-1$
    }

    if (receiveExecutor == null && TRACER.isEnabled())
    {
      TRACER.trace(this, "No receive executor!"); //$NON-NLS-1$
    }
  }

  @Override
  protected void onActivate() throws Exception
  {
    super.onActivate();
    setState(State.CONNECTING);
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    setState(State.DISCONNECTED);
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

  protected abstract void registerChannelWithPeer(short channelIndex, String protocolID)
      throws ConnectorException;

  private int getNextConnectorID()
  {
    int id = nextConnectorID;
    if (nextConnectorID == MAX_CONNECTOR_ID)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace(this, "ID wrap around"); //$NON-NLS-1$
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
}
