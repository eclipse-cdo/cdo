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
import org.eclipse.net4j.transport.BufferHandler;
import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.transport.ChannelID;
import org.eclipse.net4j.transport.Connector;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.concurrent.IWorkSerializer;
import org.eclipse.net4j.util.lifecycle.LifecycleImpl;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.registry.IRegistry;

import org.eclipse.internal.net4j.bundle.Net4j;
import org.eclipse.internal.net4j.transport.BufferImpl.State;
import org.eclipse.internal.net4j.util.Value;
import org.eclipse.internal.net4j.util.concurrent.AsynchronousWorkSerializer;
import org.eclipse.internal.net4j.util.concurrent.SynchronousWorkSerializer;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class ChannelImpl extends LifecycleImpl implements Channel, BufferProvider
{
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_CHANNEL, ChannelImpl.class);

  private short channelIndex = BufferImpl.NO_CHANNEL;

  private AbstractConnector connector;

  /**
   * The external handler for buffers passed from the {@link #connector}.
   * <p>
   */
  private BufferHandler receiveHandler;

  private ExecutorService receiveExecutor;

  private IWorkSerializer receiveSerializer;

  private Queue<Buffer> sendQueue;

  public IRegistry<ChannelID, Channel> channelRegistry;

  public ChannelImpl(ExecutorService receiveExecutor)
  {
    this.receiveExecutor = receiveExecutor;
  }

  public ChannelID getID()
  {
    return new ChannelIDImpl();
  }

  public short getChannelIndex()
  {
    return channelIndex;
  }

  public void setChannelIndex(short channelIndex)
  {
    if (channelIndex == BufferImpl.NO_CHANNEL)
    {
      throw new IllegalArgumentException("channelIndex == INVALID_CHANNEL_ID"); //$NON-NLS-1$
    }

    this.channelIndex = channelIndex;
  }

  public Connector getConnector()
  {
    return connector;
  }

  public void setConnector(AbstractConnector connector)
  {
    this.connector = connector;
  }

  public short getBufferCapacity()
  {
    return connector.getBufferProvider().getBufferCapacity();
  }

  public Buffer provideBuffer()
  {
    return connector.getBufferProvider().provideBuffer();
  }

  public void retainBuffer(Buffer buffer)
  {
    connector.getBufferProvider().retainBuffer(buffer);
  }

  public Queue<Buffer> getSendQueue()
  {
    return sendQueue;
  }

  public IRegistry<ChannelID, Channel> getChannelRegistry()
  {
    return channelRegistry;
  }

  public void setChannelRegistry(IRegistry<ChannelID, Channel> channelRegistry)
  {
    this.channelRegistry = channelRegistry;
  }

  public BufferHandler getReceiveHandler()
  {
    return receiveHandler;
  }

  public void setReceiveHandler(BufferHandler receiveHandler)
  {
    this.receiveHandler = receiveHandler;
  }

  public ExecutorService getReceiveExecutor()
  {
    return receiveExecutor;
  }

  public boolean isInternal()
  {
    return false;
  }

  public void close()
  {
    deactivate();
  }

  public void sendBuffer(Buffer buffer)
  {
    handleBuffer(buffer);
  }

  public void handleBuffer(Buffer buffer)
  {
    State state = ((BufferImpl)buffer).getState();
    if (state != State.PUTTING)
    {
      Net4j.LOG.warn("Ignoring buffer in state == " + state + ": " + this); //$NON-NLS-1$ //$NON-NLS-2$
      return;
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Handling buffer from client: {0} --> {1}", buffer, this); //$NON-NLS-1$
    }

    if (sendQueue == null)
    {
      throw new IllegalStateException("sendQueue == null");
    }

    sendQueue.add(buffer);
    connector.multiplexBuffer(this);
  }

  public void handleBufferFromMultiplexer(final Buffer buffer)
  {
    if (receiveHandler == null)
    {
      Net4j.LOG.warn("Ignoring buffer because receiveHandler == null: " + this); //$NON-NLS-1$
      buffer.release();
      return;
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Handling buffer from multiplexer: {0} --> {1}", buffer, this); //$NON-NLS-1$
    }

    receiveSerializer.addWork(new ReceiverWork(buffer));
  }

  @Override
  public String toString()
  {
    return "Channel[" + connector + ", channelIndex=" + channelIndex + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  @Override
  protected void onAboutToActivate() throws Exception
  {
    super.onAboutToActivate();
    if (channelIndex == BufferImpl.NO_CHANNEL)
    {
      throw new IllegalStateException("channelIndex == INVALID_CHANNEL_ID"); //$NON-NLS-1$
    }

    if (connector == null)
    {
      throw new IllegalStateException("connector == null"); //$NON-NLS-1$
    }
  }

  @Override
  protected void onActivate() throws Exception
  {
    super.onActivate();
    sendQueue = new ConcurrentLinkedQueue();
    if (receiveExecutor == null)
    {
      receiveSerializer = new SynchronousWorkSerializer();
    }
    else
    {
      receiveSerializer = new AsynchronousWorkSerializer(receiveExecutor);
    }

    if (!isInternal() && channelRegistry != null)
    {
      channelRegistry.put(getID(), this);
      channelRegistry.commit();
    }
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    if (!isInternal() && channelRegistry != null)
    {
      channelRegistry.remove(getID());
      channelRegistry.commit();
    }

    receiveSerializer = null;
    if (sendQueue != null)
    {
      sendQueue.clear();
      sendQueue = null;
    }

    super.onDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  private final class ChannelIDImpl extends Value implements ChannelID
  {
    private static final long serialVersionUID = 1L;

    public ChannelIDImpl()
    {
    }

    public Connector getConnector()
    {
      return connector;
    }

    public short getChannelIndex()
    {
      return channelIndex;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException
    {
      return this;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj instanceof ChannelID)
      {
        ChannelID that = (ChannelID)obj;
        return channelIndex == that.getChannelIndex() && ObjectUtil.equals(connector, that.getConnector());
      }

      return false;
    }

    @Override
    public int hashCode()
    {
      return ObjectUtil.hashCode(connector) ^ channelIndex;
    }

    @Override
    public String toString()
    {
      return "ChannelID[" + connector + ", channelIndex=" + channelIndex + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ReceiverWork implements Runnable
  {
    private final Buffer buffer;

    private ReceiverWork(Buffer buffer)
    {
      this.buffer = buffer;
    }

    public void run()
    {
      receiveHandler.handleBuffer(buffer);
    }
  }
}
