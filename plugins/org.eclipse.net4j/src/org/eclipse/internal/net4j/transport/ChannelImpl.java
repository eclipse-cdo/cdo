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
import org.eclipse.net4j.transport.BufferHandler;
import org.eclipse.net4j.transport.BufferProvider;
import org.eclipse.net4j.transport.Channel;
import org.eclipse.net4j.transport.ChannelID;
import org.eclipse.net4j.transport.Connector;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.concurrent.AsynchronousWorkSerializer;
import org.eclipse.net4j.util.concurrent.SynchronousWorkSerializer;
import org.eclipse.net4j.util.concurrent.WorkSerializer;
import org.eclipse.net4j.util.lifecycle.AbstractLifecycle;
import org.eclipse.net4j.util.om.ContextTracer;

import org.eclipse.internal.net4j.bundle.Net4j;
import org.eclipse.internal.net4j.transport.BufferImpl.State;
import org.eclipse.internal.net4j.transport.tcp.ControlChannelImpl;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class ChannelImpl extends AbstractLifecycle implements Channel, BufferProvider
{
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_CHANNEL,
      ChannelImpl.class);

  private short channelIndex = BufferImpl.NO_CHANNEL;

  private AbstractConnector connector;

  /**
   * The external handler for buffers passed from the {@link #connector}.
   * <p>
   */
  private BufferHandler receiveHandler;

  private ExecutorService receiveExecutor;

  private WorkSerializer receiveSerializer;

  private Queue<Buffer> sendQueue;

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
    return BufferUtil.getBufferProvider(connector).getBufferCapacity();
  }

  public Buffer provideBuffer()
  {
    return BufferUtil.getBufferProvider(connector).provideBuffer();
  }

  public void retainBuffer(Buffer buffer)
  {
    BufferUtil.getBufferProvider(connector).retainBuffer(buffer);
  }

  public Queue<Buffer> getSendQueue()
  {
    return sendQueue;
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
      TRACER.trace(toString() + ": Handling buffer from client: " + buffer); //$NON-NLS-1$
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
      TRACER.trace(toString() + ": Handling buffer from multiplexer: " + buffer); //$NON-NLS-1$
    }

    receiveSerializer.addWork(new Runnable()
    {
      public void run()
      {
        receiveHandler.handleBuffer(buffer);
      }
    });
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

    if (!(this instanceof ControlChannelImpl))
    {
      Channel.REGISTRY.register(this);
    }
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    if (!(this instanceof ControlChannelImpl))
    {
      Channel.REGISTRY.deregister(getID());
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
  private final class ChannelIDImpl implements ChannelID
  {
    public Connector getConnector()
    {
      return connector;
    }

    public short getChannelIndex()
    {
      return channelIndex;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj instanceof ChannelID)
      {
        ChannelID that = (ChannelID)obj;
        return channelIndex == that.getChannelIndex()
            && ObjectUtil.equals(connector, that.getConnector());
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
}
