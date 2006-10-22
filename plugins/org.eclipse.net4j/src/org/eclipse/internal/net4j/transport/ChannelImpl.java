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
import org.eclipse.net4j.transport.Connector;
import org.eclipse.net4j.util.lifecycle.AbstractLifecycle;

import org.eclipse.internal.net4j.transport.BufferImpl.State;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class ChannelImpl extends AbstractLifecycle implements Channel, BufferProvider, Runnable
{
  private short channelID = BufferImpl.NO_CHANNEL;

  private AbstractConnector connector;

  /**
   * The external handler for buffers passed from the {@link #connector}.
   * <p>
   */
  private BufferHandler receiveHandler;

  /**
   * An optional executor that is used to process the {@link #receiveQueue}
   * instead of the current thread. If not <code>null</code> the sender and
   * the receiver peers become decoupled.
   * <p>
   */
  private ExecutorService receiveExecutor;

  private Occupation receiveExecutorOccupation = new Occupation();

  /**
   * TODO Optimize for embedded transport
   */
  private Queue<Buffer> receiveQueue = new ConcurrentLinkedQueue();

  private Queue<Buffer> sendQueue = new ConcurrentLinkedQueue();

  public ChannelImpl(ExecutorService receiveExecutor)
  {
    this.receiveExecutor = receiveExecutor;
  }

  public short getChannelID()
  {
    return channelID;
  }

  public void setChannelID(short channelID)
  {
    if (channelID == BufferImpl.NO_CHANNEL)
    {
      throw new IllegalArgumentException("channelID == INVALID_CHANNEL_ID");
    }

    this.channelID = channelID;
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

  public ExecutorService getReceiveExecutor()
  {
    return receiveExecutor;
  }

  public Queue<Buffer> getReceiveQueue()
  {
    return receiveQueue;
  }

  public BufferHandler getReceiveHandler()
  {
    return receiveHandler;
  }

  public void setReceiveHandler(BufferHandler receiveHandler)
  {
    this.receiveHandler = receiveHandler;
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
      System.out.println(toString() + ": Ignoring buffer in state == " + state);
      return;
    }

    sendQueue.add(buffer);
    connector.multiplexBuffer(this);
  }

  public void handleBufferFromMultiplexer(Buffer buffer)
  {
    if (receiveHandler == null)
    {
      System.out.println(toString() + ": Ignoring buffer because receiveHandler == null");
      buffer.release();
      return;
    }

    if (receiveExecutor == null)
    {
      // Bypass the receiveQueue
      receiveHandler.handleBuffer(buffer);
      return;
    }

    receiveQueue.add(buffer);

    // isOccupied can (and must) be called unsynchronized here
    if (receiveExecutorOccupation.isOccupied())
    {
      return;
    }

    synchronized (receiveExecutorOccupation)
    {
      receiveExecutorOccupation.setOccupied(true);
    }

    System.out.println(toString() + ": Spawning new receive executor");
    receiveExecutor.execute(this);
  }

  /**
   * Executed in the context of the {@link #receiveExecutor}.
   * <p>
   */
  public void run()
  {
    synchronized (receiveExecutorOccupation)
    {
      Buffer buffer;
      while ((buffer = receiveQueue.poll()) != null)
      {
        receiveHandler.handleBuffer(buffer);
      }

      receiveExecutorOccupation.setOccupied(false);
    }
  }

  @Override
  public String toString()
  {
    return "Channel[" + connector + ":" + channelID + "]";
  }

  @Override
  protected void onAccessBeforeActivate() throws Exception
  {
    super.onAccessBeforeActivate();
    if (channelID == BufferImpl.NO_CHANNEL)
    {
      throw new IllegalStateException("channelID == INVALID_CHANNEL_ID");
    }

    if (connector == null)
    {
      throw new IllegalStateException("connector == null");
    }
  }

  @Override
  protected void onDeactivate() throws Exception
  {
    sendQueue.clear();
    super.onDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  private static class Occupation
  {
    private boolean occupied = false;

    public boolean isOccupied()
    {
      return occupied;
    }

    public void setOccupied(boolean occupied)
    {
      this.occupied = occupied;
    }
  }
}
