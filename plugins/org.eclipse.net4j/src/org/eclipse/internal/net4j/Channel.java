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

import org.eclipse.net4j.BufferState;
import org.eclipse.net4j.IBuffer;
import org.eclipse.net4j.IBufferHandler;
import org.eclipse.net4j.IBufferProvider;
import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.IChannelID;
import org.eclipse.net4j.IConnector;
import org.eclipse.net4j.internal.util.concurrent.AsynchronousWorkSerializer;
import org.eclipse.net4j.internal.util.concurrent.SynchronousWorkSerializer;
import org.eclipse.net4j.internal.util.lifecycle.Lifecycle;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.concurrent.IWorkSerializer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.internal.net4j.bundle.OM;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class Channel extends Lifecycle implements IChannel, IBufferProvider
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_CHANNEL, Channel.class);

  private short channelIndex = Buffer.NO_CHANNEL;

  private Connector connector;

  /**
   * The external handler for buffers passed from the {@link #connector}.
   * <p>
   */
  private IBufferHandler receiveHandler;

  private ExecutorService receiveExecutor;

  private IWorkSerializer receiveSerializer;

  private Queue<IBuffer> sendQueue;

  public Channel(ExecutorService receiveExecutor)
  {
    this.receiveExecutor = receiveExecutor;
  }

  public IChannelID getID()
  {
    return new ChannelIDImpl();
  }

  public short getChannelIndex()
  {
    return channelIndex;
  }

  public void setChannelIndex(short channelIndex)
  {
    if (channelIndex == Buffer.NO_CHANNEL)
    {
      throw new IllegalArgumentException("channelIndex == INVALID_CHANNEL_ID"); //$NON-NLS-1$
    }

    this.channelIndex = channelIndex;
  }

  public Connector getConnector()
  {
    return connector;
  }

  public void setConnector(Connector connector)
  {
    this.connector = connector;
  }

  public short getBufferCapacity()
  {
    return connector.getBufferProvider().getBufferCapacity();
  }

  public IBuffer provideBuffer()
  {
    return connector.getBufferProvider().provideBuffer();
  }

  public void retainBuffer(IBuffer buffer)
  {
    connector.getBufferProvider().retainBuffer(buffer);
  }

  public Queue<IBuffer> getSendQueue()
  {
    return sendQueue;
  }

  public IBufferHandler getReceiveHandler()
  {
    return receiveHandler;
  }

  public void setReceiveHandler(IBufferHandler receiveHandler)
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

  public void sendBuffer(IBuffer buffer)
  {
    handleBuffer(buffer);
  }

  public void handleBuffer(IBuffer buffer)
  {
    BufferState state = ((Buffer)buffer).getState();
    if (state != BufferState.PUTTING)
    {
      OM.LOG.warn("Ignoring buffer in state == " + state + ": " + this); //$NON-NLS-1$ //$NON-NLS-2$
      return;
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Handling buffer from client: {0} --> {1}", buffer, this); //$NON-NLS-1$
    }

    if (sendQueue == null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Ignoring buffer because sendQueue == null: " + this); //$NON-NLS-1$
      }

      buffer.release();
    }
    else
    {
      sendQueue.add(buffer);
      connector.multiplexBuffer(this);
    }
  }

  public void handleBufferFromMultiplexer(final IBuffer buffer)
  {
    if (receiveHandler != null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Handling buffer from multiplexer: {0} --> {1}", buffer, this); //$NON-NLS-1$
      }

      receiveSerializer.addWork(new ReceiverWork(buffer));
    }
    else
    {
      // Shutting down
      buffer.release();
    }
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Channel[{0}]", channelIndex); //$NON-NLS-1$
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (channelIndex == Buffer.NO_CHANNEL)
    {
      throw new IllegalStateException("channelIndex == INVALID_CHANNEL_ID"); //$NON-NLS-1$
    }

    if (connector == null)
    {
      throw new IllegalStateException("connector == null"); //$NON-NLS-1$
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    sendQueue = new ConcurrentLinkedQueue();
    if (receiveExecutor == null)
    {
      receiveSerializer = new SynchronousWorkSerializer();
    }
    else
    {
      receiveSerializer = new AsynchronousWorkSerializer(receiveExecutor);
      // receiveSerializer = new CompletionWorkSerializer();
      // receiveSerializer = new QueueWorkerWorkSerializer();
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(receiveHandler);
    receiveHandler = null;

    connector.removeChannel(this);
    receiveSerializer = null;
    if (sendQueue != null)
    {
      sendQueue.clear();
      sendQueue = null;
    }

    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  private final class ChannelIDImpl implements IChannelID, Cloneable, Serializable
  {
    private static final long serialVersionUID = 1L;

    public ChannelIDImpl()
    {
    }

    public IConnector getConnector()
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
      if (obj instanceof IChannelID)
      {
        IChannelID that = (IChannelID)obj;
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
    private final IBuffer buffer;

    private ReceiverWork(IBuffer buffer)
    {
      this.buffer = buffer;
    }

    public void run()
    {
      if (receiveHandler != null)
      {
        receiveHandler.handleBuffer(buffer);
      }
      else
      {
        // Shutting down
        buffer.release();
      }
    }
  }
}
