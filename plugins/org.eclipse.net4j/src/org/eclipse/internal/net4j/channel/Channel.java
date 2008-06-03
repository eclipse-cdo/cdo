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
package org.eclipse.internal.net4j.channel;

import org.eclipse.net4j.buffer.BufferState;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferHandler;
import org.eclipse.net4j.channel.IChannelMultiplexer;
import org.eclipse.net4j.util.concurrent.IWorkSerializer;
import org.eclipse.net4j.util.concurrent.QueueWorkerWorkSerializer;
import org.eclipse.net4j.util.concurrent.SynchronousWorkSerializer;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import org.eclipse.spi.net4j.InternalChannel;

import java.text.MessageFormat;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class Channel extends Lifecycle implements InternalChannel
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_CHANNEL, Channel.class);

  private int channelID;

  private IChannelMultiplexer channelMultiplexer;

  private short channelIndex = IBuffer.NO_CHANNEL;

  private ExecutorService receiveExecutor;

  /**
   * The external handler for buffers passed from the {@link #connector}.
   */
  private IBufferHandler receiveHandler;

  private IWorkSerializer receiveSerializer;

  private Queue<IBuffer> sendQueue;

  public Channel()
  {
  }

  public int getChannelID()
  {
    return channelID;
  }

  public void setChannelID(int channelID)
  {
    this.channelID = channelID;
  }

  public IChannelMultiplexer getChannelMultiplexer()
  {
    return channelMultiplexer;
  }

  public void setChannelMultiplexer(IChannelMultiplexer channelMultiplexer)
  {
    this.channelMultiplexer = channelMultiplexer;
  }

  public short getChannelIndex()
  {
    return channelIndex;
  }

  public void setChannelIndex(short channelIndex)
  {
    if (channelIndex == IBuffer.NO_CHANNEL)
    {
      throw new IllegalArgumentException("channelIndex == INVALID_CHANNEL_ID"); //$NON-NLS-1$
    }

    this.channelIndex = channelIndex;
  }

  public ExecutorService getReceiveExecutor()
  {
    return receiveExecutor;
  }

  public void setReceiveExecutor(ExecutorService receiveExecutor)
  {
    this.receiveExecutor = receiveExecutor;
  }

  public IBufferHandler getReceiveHandler()
  {
    return receiveHandler;
  }

  public void setReceiveHandler(IBufferHandler receiveHandler)
  {
    this.receiveHandler = receiveHandler;
  }

  public Queue<IBuffer> getSendQueue()
  {
    return sendQueue;
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
    BufferState state = buffer.getState();
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
      channelMultiplexer.multiplexChannel(this);
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
    checkState(channelIndex != IBuffer.NO_CHANNEL, "channelIndex == NO_CHANNEL"); //$NON-NLS-1$
    checkState(channelMultiplexer, "channelMultiplexer"); //$NON-NLS-1$
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    sendQueue = new ConcurrentLinkedQueue<IBuffer>();
    if (receiveExecutor == null)
    {
      receiveSerializer = new SynchronousWorkSerializer();
    }
    else
    {
      // CompletionWorkSerializer throws "One command already pending"
      // receiveSerializer = new CompletionWorkSerializer();
      // receiveSerializer = new AsynchronousWorkSerializer(receiveExecutor);
      // receiveSerializer = new SynchronousWorkSerializer();

      class ChannelReceiveSerializer extends QueueWorkerWorkSerializer
      {
        @Override
        protected String getThreadName()
        {
          return "ReceiveSerializer" + channelIndex;
        }
      }

      receiveSerializer = new ChannelReceiveSerializer();
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(receiveHandler);
    receiveHandler = null;

    channelMultiplexer.removeChannel(this);
    if (receiveSerializer != null)
    {
      receiveSerializer.dispose();
      receiveSerializer = null;
    }

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
