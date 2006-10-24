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
import org.eclipse.net4j.util.registry.IRegistry;

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
  private static final ChannelImpl NULL_CHANNEL = new ChannelImpl(null)
  {
    @Override
    public String toString()
    {
      return "NullChannel"; //$NON-NLS-1$
    }
  };

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
      System.out.println(toString() + ": Setting state " + newState + " (was " //$NON-NLS-1$ //$NON-NLS-2$
          + oldState.toString().toLowerCase() + ")"); //$NON-NLS-1$
      state = newState;
      fireStateChanged(newState, oldState);

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
      System.out.println(toString() + ": Waiting for connection..."); //$NON-NLS-1$
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
    final List<Channel> result = new ArrayList<Channel>();
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
    short channelID = findFreeChannelID();
    ChannelImpl channel = createChannel(channelID, protocolID);
    registerChannelWithPeer(channelID, protocolID);

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

  public ChannelImpl createChannel(short channelID, String protocolID)
  {
    ChannelImpl channel = new ChannelImpl(receiveExecutor);
    Protocol protocol = createProtocol(protocolID, channel);
    System.out.println(toString() + ": Opening channel " + channelID //$NON-NLS-1$
        + (protocol == null ? " without protocol" : " with protocol " + protocolID)); //$NON-NLS-1$ //$NON-NLS-2$

    channel.setChannelID(channelID);
    channel.setConnector(this);
    channel.setReceiveHandler(protocol);
    channel.addLifecycleListener(channelLifecycleListener);
    addChannel(channel);
    return channel;
  }

  public ChannelImpl getChannel(short channelID)
  {
    try
    {
      ChannelImpl channel = channels.get(channelID);
      if (channel == null || channel == NULL_CHANNEL)
      {
        throw new NullPointerException();
      }

      return channel;
    }
    catch (IndexOutOfBoundsException ex)
    {
      System.out.println(toString() + ": Invalid channelID " + channelID); //$NON-NLS-1$
      return null;
    }
  }

  protected List<Queue<Buffer>> getChannelBufferQueues()
  {
    final List<Queue<Buffer>> result = new ArrayList();
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

  protected short findFreeChannelID()
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
    short channelID = channel.getChannelID();
    while (channelID >= channels.size())
    {
      channels.add(NULL_CHANNEL);
    }

    channels.set(channelID, channel);
  }

  protected void removeChannel(ChannelImpl channel)
  {
    channel.removeLifecycleListener(channelLifecycleListener);
    int channelID = channel.getChannelID();

    System.out.println(toString() + ": Removing channel " + channelID); //$NON-NLS-1$
    channels.set(channelID, NULL_CHANNEL);
  }

  protected Protocol createProtocol(String protocolID, Channel channel)
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
      System.out.println(toString() + ": Unknown protocol " + protocolID); //$NON-NLS-1$
      return null;
    }

    return factory.createProtocol(channel);
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
        ex.printStackTrace();
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
        ex.printStackTrace();
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
        ex.printStackTrace();
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
        ex.printStackTrace();
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

    if (protocolFactoryRegistry == null)
    {
      System.out.println(toString() + ": (INFO) protocolFactoryRegistry == null"); //$NON-NLS-1$
    }

    if (receiveExecutor == null)
    {
      System.out.println(toString() + ": (INFO) receiveExecutor == null"); //$NON-NLS-1$
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

  protected abstract void registerChannelWithPeer(short channelID, String protocolID)
      throws ConnectorException;

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
