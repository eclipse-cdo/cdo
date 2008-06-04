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
package org.eclipse.net4j.signal;

import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.ChannelOutputStream;
import org.eclipse.net4j.protocol.Protocol;
import org.eclipse.net4j.util.io.IStreamWrapper;
import org.eclipse.net4j.util.io.StreamWrapperChain;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Eike Stepper
 */
public abstract class SignalProtocol extends Protocol
{
  public static final long NO_TIMEOUT = BufferInputStream.NO_TIMEOUT;

  private static final int MIN_CORRELATION_ID = 1;

  private static final int MAX_CORRELATION_ID = Integer.MAX_VALUE;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, SignalProtocol.class);

  private static final ContextTracer STREAM_TRACER = new ContextTracer(OM.DEBUG_BUFFER_STREAM, SignalProtocol.class);

  private IStreamWrapper streamWrapper;

  private Map<Integer, Signal> signals = new ConcurrentHashMap<Integer, Signal>(0);

  private int nextCorrelationID = MIN_CORRELATION_ID;

  protected SignalProtocol()
  {
  }

  public IStreamWrapper getStreamWrapper()
  {
    return streamWrapper;
  }

  public void setStreamWrapper(IStreamWrapper streamWrapper)
  {
    this.streamWrapper = streamWrapper;
  }

  public void addStreamWrapper(IStreamWrapper streamWrapper)
  {
    if (this.streamWrapper == null)
    {
      this.streamWrapper = streamWrapper;
    }
    else
    {
      this.streamWrapper = new StreamWrapperChain(streamWrapper, this.streamWrapper);
    }
  }

  public boolean waitForSignals(long timeout)
  {
    synchronized (signals)
    {
      while (!signals.isEmpty())
      {
        try
        {
          signals.wait(timeout);
        }
        catch (InterruptedException ex)
        {
          return false;
        }
      }
    }

    return true;
  }

  public InputStream wrapInputStream(InputStream in) throws IOException
  {
    if (streamWrapper != null)
    {
      in = streamWrapper.wrapInputStream(in);
    }

    return in;
  }

  public OutputStream wrapOutputStream(OutputStream out) throws IOException
  {
    if (streamWrapper != null)
    {
      out = streamWrapper.wrapOutputStream(out);
    }

    return out;
  }

  public void finishInputStream(InputStream in) throws IOException
  {
    if (streamWrapper != null)
    {
      streamWrapper.finishInputStream(in);
    }
  }

  public void finishOutputStream(OutputStream out) throws IOException
  {
    if (streamWrapper != null)
    {
      streamWrapper.finishOutputStream(out);
    }
  }

  public void handleBuffer(IBuffer buffer)
  {
    ByteBuffer byteBuffer = buffer.getByteBuffer();
    int correlationID = byteBuffer.getInt();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Received buffer for correlation " + correlationID); //$NON-NLS-1$
    }

    Signal signal;
    synchronized (signals)
    {
      if (correlationID > 0)
      {
        // Incoming indication
        signal = signals.get(-correlationID);
        if (signal == null)
        {
          short signalID = byteBuffer.getShort();
          if (TRACER.isEnabled())
          {
            TRACER.trace("Got signal id " + signalID); //$NON-NLS-1$
          }

          signal = provideSignalReactor(signalID);
          signal.setProtocol(this);
          signal.setCorrelationID(-correlationID);
          signal.setBufferInputStream(new SignalInputStream(getInputStreamTimeout()));
          signal.setBufferOutputStream(new SignalOutputStream(-correlationID, signalID, false));
          signals.put(-correlationID, signal);
          getExecutorService().execute(signal);
        }
      }
      else
      {
        // Incoming confirmation
        signal = signals.get(-correlationID);
        if (signal == null)
        {
          if (TRACER.isEnabled())
          {
            TRACER.trace("Discarding buffer"); //$NON-NLS-1$
          }

          buffer.release();
        }
      }
    }

    if (signal != null) // Can be null after timeout
    {
      BufferInputStream inputStream = signal.getBufferInputStream();
      inputStream.handleBuffer(buffer);
    }
  }

  public long getInputStreamTimeout()
  {
    return NO_TIMEOUT;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("SignalProtocol[{0}]", getType()); //$NON-NLS-1$ 
  }

  protected final SignalReactor provideSignalReactor(short signalID)
  {
    checkActive();
    SignalReactor signal = createSignalReactor(signalID);
    if (signal == null)
    {
      throw new IllegalArgumentException("Invalid signalID " + signalID);
    }

    return signal;
  }

  /**
   * Returns a new signal instance to serve the given signal ID or <code>null</code> if the signal ID is invalid/unknown
   * for this protocol.
   */
  protected abstract SignalReactor createSignalReactor(short signalID);

  void startSignal(SignalActor<?> signalActor, long timeout) throws Exception
  {
    if (signalActor.getProtocol() != this)
    {
      throw new IllegalArgumentException("signalActor.getProtocol() != this"); //$NON-NLS-1$
    }

    short signalID = signalActor.getSignalID();
    int correlationID = signalActor.getCorrelationID();
    signalActor.setBufferInputStream(new SignalInputStream(timeout));
    signalActor.setBufferOutputStream(new SignalOutputStream(correlationID, signalID, true));
    synchronized (signals)
    {
      signals.put(correlationID, signalActor);
    }

    signalActor.runSync();
  }

  void stopSignal(Signal signal)
  {
    int correlationID = signal.getCorrelationID();
    synchronized (signals)
    {
      signals.remove(correlationID);
      signals.notifyAll();
    }
  }

  synchronized int getNextCorrelationID()
  {
    int correlationID = nextCorrelationID;
    if (nextCorrelationID == MAX_CORRELATION_ID)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Correlation wrap around"); //$NON-NLS-1$
      }

      nextCorrelationID = MIN_CORRELATION_ID;
    }
    else
    {
      ++nextCorrelationID;
    }

    return correlationID;
  }

  /**
   * @author Eike Stepper
   */
  class SignalInputStream extends BufferInputStream
  {
    private long timeout;

    public SignalInputStream(long timeout)
    {
      this.timeout = timeout;
    }

    @Override
    public long getMillisBeforeTimeout()
    {
      return timeout;
    }
  }

  /**
   * @author Eike Stepper
   */
  class SignalOutputStream extends ChannelOutputStream
  {
    public SignalOutputStream(final int correlationID, final short signalID, final boolean addSignalID)
    {
      super(getChannel(), new IBufferProvider()
      {
        private IBufferProvider delegate = getBufferProvider();

        private boolean firstBuffer = addSignalID;

        public short getBufferCapacity()
        {
          return delegate.getBufferCapacity();
        }

        public IBuffer provideBuffer()
        {
          IBuffer buffer = delegate.provideBuffer();
          ByteBuffer byteBuffer = buffer.startPutting(getChannel().getChannelIndex());
          if (STREAM_TRACER.isEnabled())
          {
            STREAM_TRACER.trace("Providing buffer for correlation " + correlationID); //$NON-NLS-1$
          }

          byteBuffer.putInt(correlationID);
          if (firstBuffer)
          {
            if (SignalProtocol.TRACER.isEnabled())
            {
              STREAM_TRACER.trace("Put signal id " + signalID); //$NON-NLS-1$
            }

            byteBuffer.putShort(signalID);
          }

          firstBuffer = false;
          return buffer;
        }

        public void retainBuffer(IBuffer buffer)
        {
          delegate.retainBuffer(buffer);
        }
      });
    }
  }
}
