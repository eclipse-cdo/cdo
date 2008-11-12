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
package org.eclipse.spi.net4j;

import org.eclipse.net4j.buffer.BufferState;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferHandler;
import org.eclipse.net4j.channel.IChannelMultiplexer;
import org.eclipse.net4j.util.concurrent.IWorkSerializer;
import org.eclipse.net4j.util.concurrent.QueueWorkerWorkSerializer;
import org.eclipse.net4j.util.concurrent.SynchronousWorkSerializer;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import java.text.MessageFormat;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class Channel extends Lifecycle implements InternalChannel
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_CHANNEL, Channel.class);

  private String userID;

  private InternalChannelMultiplexer channelMultiplexer;

  private short id = IBuffer.NO_CHANNEL;

  private ExecutorService receiveExecutor;

  /**
   * The external handler for buffers passed from the {@link #connector}.
   */
  private IBufferHandler receiveHandler;

  private IWorkSerializer receiveSerializer;

  private transient Queue<IBuffer> sendQueue;

  public Channel()
  {
  }

  public String getUserID()
  {
    return userID;
  }

  public void setUserID(String userID)
  {
    this.userID = userID;
  }

  public Location getLocation()
  {
    return channelMultiplexer.getLocation();
  }

  public boolean isClient()
  {
    return channelMultiplexer.isClient();
  }

  public boolean isServer()
  {
    return channelMultiplexer.isServer();
  }

  public IChannelMultiplexer getMultiplexer()
  {
    return channelMultiplexer;
  }

  public void setMultiplexer(IChannelMultiplexer channelMultiplexer)
  {
    this.channelMultiplexer = (InternalChannelMultiplexer)channelMultiplexer;
  }

  public short getID()
  {
    return id;
  }

  public void setID(short id)
  {
    checkArg(id != IBuffer.NO_CHANNEL, "id == IBuffer.NO_CHANNEL"); //$NON-NLS-1$
    this.id = id;
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
      TRACER.format("Handling buffer: {0} --> {1}", buffer, this); //$NON-NLS-1$
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

  public void handleBufferFromMultiplexer(IBuffer buffer)
  {
    if (receiveHandler != null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Handling buffer from multiplexer: {0} --> {1}", buffer, this); //$NON-NLS-1$
      }

      receiveSerializer.addWork(craeteReceiverWork(buffer));
    }
    else
    {
      // Shutting down
      buffer.release();
    }
  }

  protected ReceiverWork craeteReceiverWork(IBuffer buffer)
  {
    return new ReceiverWork(this, buffer);
  }

  public short getBufferCapacity()
  {
    return channelMultiplexer.getBufferCapacity();
  }

  public IBuffer provideBuffer()
  {
    return channelMultiplexer.provideBuffer();
  }

  public void retainBuffer(IBuffer buffer)
  {
    channelMultiplexer.retainBuffer(buffer);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Channel[{0}, {1}]", id, getLocation()); //$NON-NLS-1$
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(id != IBuffer.NO_CHANNEL, "channelID == NO_CHANNEL"); //$NON-NLS-1$
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
          return "ReceiveSerializer-" + Channel.this;
        }
      }

      receiveSerializer = new ChannelReceiveSerializer();
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    channelMultiplexer.closeChannel(this);
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

  public void close()
  {
    deactivate();
  }

  /**
   * @author Eike Stepper
   */
  protected static class ReceiverWork implements Runnable
  {
    private final InternalChannel channel;

    private final IBuffer buffer;

    public ReceiverWork(InternalChannel channel, IBuffer buffer)
    {
      this.channel = channel;
      this.buffer = buffer;
    }

    public void run()
    {
      IBufferHandler receiveHandler = channel.getReceiveHandler();
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
