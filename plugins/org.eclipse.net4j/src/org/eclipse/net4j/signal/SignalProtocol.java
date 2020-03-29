/*
 * Copyright (c) 2006-2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Andre Dietisheim - maintenance
 */
package org.eclipse.net4j.signal;

import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.ChannelOutputStream;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IStreamWrapper;
import org.eclipse.net4j.util.io.StreamWrapperChain;
import org.eclipse.net4j.util.io.StringCompressor;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import org.eclipse.spi.net4j.Protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The default implementation of a {@link ISignalProtocol signal protocol}.
 * <p>
 * On the {@link org.eclipse.net4j.ILocationAware.Location#SERVER receiver side(s)} of protocol the
 * {@link #createSignalReactor(short) createSignalReactor()} method has to be overridden to
 * create appropriate peer instances for incoming {@link Signal signals}.
 *
 * @author Eike Stepper
 */
public class SignalProtocol<INFRA_STRUCTURE> extends Protocol<INFRA_STRUCTURE> implements ISignalProtocol<INFRA_STRUCTURE>
{
  /**
   * @since 4.7
   */
  public static final long COMPRESSED_STRINGS_ACKNOWLEDGE_TIMEOUT = OMPlatform.INSTANCE
      .getProperty("org.eclipse.net4j.signal.COMPRESSED_STRINGS_ACKNOWLEDGE_TIMEOUT", 5000L);

  /**
   * @since 2.0
   */
  public static final short SIGNAL_REMOTE_EXCEPTION = -1;

  /**
   * @since 2.0
   */
  public static final short SIGNAL_MONITOR_CANCELED = -2;

  /**
   * @since 2.0
   */
  public static final short SIGNAL_MONITOR_PROGRESS = -3;

  /**
   * @since 4.1
   */
  public static final short SIGNAL_SET_TIMEOUT = -4;

  /**
   * @since 4.7
   */
  public static final short SIGNAL_ACKNOWLEDGE_COMPRESSED_STRINGS = -5;

  /**
   * Begin Of Signal.
   */
  private static final int BOS_BIT = 1;

  private static final int BOS_MASK = ~BOS_BIT;

  private static final int INC_CORRELATION_ID = 1 << BOS_BIT; // 2

  private static final int MIN_CORRELATION_ID = INC_CORRELATION_ID;

  private static final int MAX_CORRELATION_ID = Integer.MAX_VALUE & BOS_MASK; // 2147483646

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, SignalProtocol.class);

  private long timeout = DEFAULT_TIMEOUT;

  private IStreamWrapper streamWrapper;

  private Map<Integer, Signal> signals = new HashMap<>();

  private int nextCorrelationID = MIN_CORRELATION_ID;

  private boolean failingOver;

  /**
   * @since 2.0
   */
  public SignalProtocol(String type)
  {
    super(type);
  }

  /**
   * @since 2.0
   */
  @Override
  public long getTimeout()
  {
    return timeout;
  }

  /**
   * Equivalent to calling SignalProtocol.setTimeout(timeout, false).
   *
   * @since 2.0
   */
  @Override
  public void setTimeout(long timeout)
  {
    setTimeout(timeout, false);
  }

  /**
   * Update the timeout used for signal end of stream waiting time.
   *
   * @param timeout the new timeout
   * @param useOldTimeoutToSendNewOne <code>true</code> to use the old timeout, <code>false</code> to use the new specified one to sent to server side the new specified timeout
   * @return <code>true</code> if the new specified timeout has correctly been sent
   *
   * NOTE: this second parameter is useful mostly for test to be able to set a to small timeout
   * @since 4.4
   */
  public boolean setTimeout(long timeout, boolean useOldTimeoutToSendNewOne)
  {
    boolean timeoutSent = false;
    long oldTimeout = this.timeout;
    if (!useOldTimeoutToSendNewOne)
    {
      handleSetTimeOut(timeout);
    }

    if (oldTimeout != timeout && isActive())
    {
      timeoutSent = sendSetTimeout();
    }

    if (timeoutSent && useOldTimeoutToSendNewOne)
    {
      handleSetTimeOut(timeout);
    }

    return timeoutSent;
  }

  @Override
  public IStreamWrapper getStreamWrapper()
  {
    return streamWrapper;
  }

  @Override
  public void setStreamWrapper(IStreamWrapper streamWrapper)
  {
    this.streamWrapper = streamWrapper;
  }

  @Override
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

  /**
   * @since 2.0
   */
  @Override
  public IChannel open(IConnector connector)
  {
    return connector.openChannel(this);
  }

  /**
   * @since 2.0
   */
  @Override
  public void close()
  {
    LifecycleUtil.deactivate(this, OMLogger.Level.DEBUG);
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

  /**
   * Handles a given (incoming) buffer. Creates a signal to act upon the given buffer or uses a previously created
   * signal.
   */
  @Override
  public void handleBuffer(IBuffer buffer)
  {
    ByteBuffer byteBuffer = buffer.getByteBuffer();
    int correlationID = byteBuffer.getInt();

    boolean beginOfSignal = false;
    if ((correlationID & BOS_BIT) != 0)
    {
      correlationID &= BOS_MASK;
      beginOfSignal = true;

      if (TRACER.isEnabled())
      {
        TRACER.trace("Received first buffer for correlation " + correlationID); //$NON-NLS-1$
      }
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Received buffer for correlation " + correlationID); //$NON-NLS-1$
      }
    }

    Signal signal = null;
    boolean newSignalScheduled = false;

    synchronized (signals)
    {
      if (correlationID > 0)
      {
        // Incoming indication
        if (beginOfSignal)
        {
          short signalID = byteBuffer.getShort();
          if (TRACER.isEnabled())
          {
            TRACER.trace("Got signalID: " + signalID); //$NON-NLS-1$
          }

          signal = provideSignalReactor(signalID);
          if (signal != null)
          {
            signal.setCorrelationID(-correlationID);
            signal.setBufferInputStream(new SignalInputStream(getTimeout()));
            if (signal instanceof IndicationWithResponse)
            {
              signal.setBufferOutputStream(new SignalOutputStream(-correlationID, signalID, false));
            }

            signals.put(-correlationID, signal);
            getExecutorService().execute(signal);
            newSignalScheduled = true;
          }
        }
        else
        {
          signal = signals.get(-correlationID);
        }
      }
      else
      {
        // Incoming confirmation
        signal = signals.get(-correlationID);
      }
    }

    if (signal != null) // Can be null after timeout
    {
      if (newSignalScheduled)
      {
        fireSignalScheduledEvent(signal);
      }

      BufferInputStream inputStream = signal.getBufferInputStream();
      inputStream.handleBuffer(buffer);
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Discarding buffer"); //$NON-NLS-1$
      }

      buffer.release();
    }
  }

  @Override
  public String toString()
  {
    IChannel channel = getChannel();
    if (channel != null)
    {
      return MessageFormat.format("SignalProtocol[{0}, {1}, {2}]", channel.getID(), channel.getLocation(), getType()); //$NON-NLS-1$
    }

    return MessageFormat.format("SignalProtocol[{0}]", getType()); //$NON-NLS-1$
  }

  @Override
  protected void doBeforeDeactivate() throws Exception
  {
    synchronized (signals)
    {
      // Wait at most 10 seconds for running signals to finish
      int waitMillis = 10 * 1000;
      long stop = System.currentTimeMillis() + waitMillis;
      while (!signals.isEmpty() && System.currentTimeMillis() < stop)
      {
        signals.wait(1000L);
      }
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    try
    {
      synchronized (signals)
      {
        signals.clear();
      }

      IChannel channel = getChannel();
      if (channel != null)
      {
        channel.close();
        setChannel(null);
      }
    }
    finally
    {
      super.doDeactivate();
    }
  }

  @Override
  protected void handleChannelDeactivation()
  {
    if (!failingOver)
    {
      super.handleChannelDeactivation();
    }
  }

  protected final SignalReactor provideSignalReactor(short signalID)
  {
    if (!isActive())
    {
      return null;
    }

    switch (signalID)
    {
    case SIGNAL_REMOTE_EXCEPTION:
      return new RemoteExceptionIndication(this);

    case SIGNAL_MONITOR_CANCELED:
      return new MonitorCanceledIndication(this);

    case SIGNAL_MONITOR_PROGRESS:
      return new MonitorProgressIndication(this);

    case SIGNAL_SET_TIMEOUT:
      return new SetTimeoutIndication(this);

    case SIGNAL_ACKNOWLEDGE_COMPRESSED_STRINGS:
      return new AcknowledgeCompressedStringsIndication(this);

    default:
      checkStringCompressorAcknowledgements();

      SignalReactor signal = createSignalReactor(signalID);
      if (signal == null)
      {
        throw new InvalidSignalIDException(signalID);
      }

      return signal;
    }
  }

  /**
   * Returns a new signal instance to serve the given signal ID or <code>null</code> if the signal ID is invalid/unknown
   * for this protocol.
   */
  protected SignalReactor createSignalReactor(short signalID)
  {
    return null;
  }

  /**
   * Returns <code>true</code> by default, override to change this behavior.
   *
   * @since 4.1
   */
  protected boolean isSendingTimeoutChanges()
  {
    return true;
  }

  /**
   * @since 4.7
   */
  protected StringCompressor getStringCompressor()
  {
    return null;
  }

  synchronized int getNextCorrelationID()
  {
    int correlationID = nextCorrelationID;
    if (nextCorrelationID == MAX_CORRELATION_ID)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Correlation ID wrap-around"); //$NON-NLS-1$
      }

      nextCorrelationID = MIN_CORRELATION_ID;
    }
    else
    {
      nextCorrelationID += INC_CORRELATION_ID;
    }

    return correlationID;
  }

  InputStream wrapInputStream(InputStream in) throws IOException
  {
    if (streamWrapper != null)
    {
      in = streamWrapper.wrapInputStream(in);
    }

    return in;
  }

  OutputStream wrapOutputStream(OutputStream out) throws IOException
  {
    if (streamWrapper != null)
    {
      out = streamWrapper.wrapOutputStream(out);
    }

    return out;
  }

  void finishInputStream(InputStream in) throws IOException
  {
    if (streamWrapper != null)
    {
      streamWrapper.finishInputStream(in);
    }
  }

  void finishOutputStream(OutputStream out) throws IOException
  {
    if (streamWrapper != null)
    {
      streamWrapper.finishOutputStream(out);
    }
  }

  void startSignal(SignalActor signalActor, long timeout) throws Exception
  {
    checkArg(signalActor.getProtocol() == this, "Wrong protocol"); //$NON-NLS-1$

    short signalID = signalActor.getID();
    int correlationID = signalActor.getCorrelationID();

    signalActor.setBufferOutputStream(new SignalOutputStream(correlationID, signalID, true));
    if (signalActor instanceof RequestWithConfirmation<?>)
    {
      signalActor.setBufferInputStream(new SignalInputStream(timeout));
    }

    synchronized (signals)
    {
      signals.put(correlationID, signalActor);
    }

    fireSignalScheduledEvent(signalActor);
    signalActor.runSync();

    checkStringCompressorAcknowledgements();
  }

  void stopSignal(Signal signal, Exception exception)
  {
    int correlationID = signal.getCorrelationID();
    synchronized (signals)
    {
      signals.remove(correlationID);
      signals.notifyAll();
    }

    fireSignalFinishedEvent(signal, exception);
  }

  void handleRemoteException(int correlationID, Throwable t, boolean responding)
  {
    synchronized (signals)
    {
      Signal signal = signals.remove(correlationID);
      if (signal instanceof RequestWithConfirmation<?>)
      {
        RequestWithConfirmation<?> request = (RequestWithConfirmation<?>)signal;
        request.setRemoteException(t, responding);
      }

      signals.notifyAll();
    }
  }

  void handleMonitorProgress(int correlationID, double totalWork, double work)
  {
    synchronized (signals)
    {
      Signal signal = signals.get(correlationID);
      if (signal instanceof RequestWithMonitoring<?>)
      {
        RequestWithMonitoring<?> request = (RequestWithMonitoring<?>)signal;
        request.setMonitorProgress(totalWork, work);
      }
    }
  }

  void handleMonitorCanceled(int correlationID)
  {
    synchronized (signals)
    {
      Signal signal = signals.get(-correlationID);
      if (signal instanceof IndicationWithMonitoring)
      {
        IndicationWithMonitoring indication = (IndicationWithMonitoring)signal;
        indication.setMonitorCanceled();
      }
    }
  }

  void handleSetTimeOut(long timeout)
  {
    long oldTimeout = this.timeout;
    if (oldTimeout != timeout)
    {
      this.timeout = timeout;
      fireEvent(new TimeoutChangedEvent(this, oldTimeout, timeout));
    }
  }

  boolean sendSetTimeout()
  {
    boolean timeoutSent = false;
    if (isSendingTimeoutChanges())
    {
      try
      {
        timeoutSent = new SetTimeoutRequest(this, this.timeout).send();
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }
    return timeoutSent;
  }

  private void checkStringCompressorAcknowledgements()
  {
    StringCompressor compressor = getStringCompressor();
    if (compressor != null)
    {
      Collection<Integer> acknowledgements = compressor.getPendingAcknowledgements(COMPRESSED_STRINGS_ACKNOWLEDGE_TIMEOUT);

      if (acknowledgements != null)
      {
        try
        {
          new AcknowledgeCompressedStringsRequest(this, acknowledgements).sendAsync();
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
  }

  private void fireSignalScheduledEvent(Signal signal)
  {
    IListener[] listeners = getListeners();
    if (listeners != null)
    {
      IEvent event = new SignalScheduledEvent<>(this, signal);
      fireEvent(event, listeners);
    }
  }

  private void fireSignalFinishedEvent(Signal signal, Exception exception)
  {
    IListener[] listeners = getListeners();
    if (listeners != null)
    {
      IEvent event = new SignalFinishedEvent<>(this, signal, exception);
      fireEvent(event, listeners);
    }
  }

  /**
   * @author Eike Stepper
   * @since 4.10
   */
  public static final class InvalidSignalIDException extends IllegalArgumentException
  {
    private static final long serialVersionUID = 1L;

    private final short signalID;

    public InvalidSignalIDException(short signalID)
    {
      super("Invalid signal ID " + signalID);
      this.signalID = signalID;
    }

    public short getSignalID()
    {
      return signalID;
    }
  }

  /**
   * An {@link IEvent event} fired from a {@link ISignalProtocol signal protocol} when the protocol {@link ISignalProtocol#setTimeout(long) timeout}
   * has been changed.
   *
   * @author Eike Stepper
   * @since 4.1
   */
  public static final class TimeoutChangedEvent extends Event
  {
    private static final long serialVersionUID = 1L;

    private long oldTimeout;

    private long newTimeout;

    private TimeoutChangedEvent(ISignalProtocol<?> source, long oldTimeout, long newTimeout)
    {
      super(source);
      this.oldTimeout = oldTimeout;
      this.newTimeout = newTimeout;
    }

    @Override
    public SignalProtocol<?> getSource()
    {
      return (SignalProtocol<?>)super.getSource();
    }

    public long getOldTimeout()
    {
      return oldTimeout;
    }

    public long getNewTimeout()
    {
      return newTimeout;
    }

    @Override
    public String toString()
    {
      return "TimeoutChangedEvent [oldTimeout=" + oldTimeout + ", newTimeout=" + newTimeout + ", source=" + source + "]";
    }

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

    @Override
    protected void closeChannel()
    {
      IChannel channel = getChannel();
      LifecycleUtil.deactivate(channel);
    }
  }

  /**
   * @author Eike Stepper
   */
  class SignalOutputStream extends ChannelOutputStream
  {
    public SignalOutputStream(int correlationID, short signalID, boolean request)
    {
      super(getChannel(), new IBufferProvider()
      {
        private IBufferProvider delegate = getBufferProvider();

        private boolean beginOfSignal = true;

        private boolean addSignalID = request;

        @Override
        public short getBufferCapacity()
        {
          return delegate.getBufferCapacity();
        }

        @Override
        public IBuffer provideBuffer()
        {
          IChannel channel = getChannel();
          if (channel == null)
          {
            throw new IORuntimeException("No channel for protocol " + SignalProtocol.this); //$NON-NLS-1$
          }

          IBuffer buffer = delegate.provideBuffer();
          ByteBuffer byteBuffer = buffer.startPutting(channel.getID());

          if (beginOfSignal)
          {
            byteBuffer.putInt(correlationID | BOS_BIT);
            beginOfSignal = false;

            if (addSignalID)
            {
              byteBuffer.putShort(signalID);
              addSignalID = false;
            }
          }
          else
          {
            byteBuffer.putInt(correlationID);
          }

          return buffer;
        }

        @Override
        public void retainBuffer(IBuffer buffer)
        {
          delegate.retainBuffer(buffer);
        }
      });
    }
  }
}
