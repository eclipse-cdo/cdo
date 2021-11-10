/*
 * Copyright (c) 2006-2013, 2015, 2016, 2018-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.signal;

import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.buffer.BufferOutputStream;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IOTimeoutException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

/**
 * Represents a single communications use-case in the scope of a {@link ISignalProtocol signal protocol}.
 *
 * @author Eike Stepper
 */
public abstract class Signal implements Runnable
{
  /**
   * @since 2.0
   */
  public static final long NO_TIMEOUT = BufferInputStream.NO_TIMEOUT;

  private static final boolean DISABLE_LOG_EXCEPTIONS = OMPlatform.INSTANCE.isProperty("org.eclipse.net4j.signal.Signal.disableLogExceptions");

  private static final boolean SHORT_TO_STRING = OMPlatform.INSTANCE.isProperty("org.eclipse.net4j.signal.Signal.shortToString");

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, Signal.class);

  private SignalProtocol<?> protocol;

  private short id;

  private String name;

  private int correlationID;

  private BufferInputStream bufferInputStream;

  private BufferOutputStream bufferOutputStream;

  private InputStream wrappedInputStream;

  private OutputStream wrappedOutputStream;

  private Object currentStream;

  long scheduled = -1L;

  /**
   * Both implementation classes of a logical signal must have the same signalID. The signalID of a user signals must be
   * equal to or greater than zero.
   *
   * @since 2.0
   */
  public Signal(SignalProtocol<?> protocol, short id, String name)
  {
    this.protocol = protocol;
    this.id = id;
    this.name = name;
  }

  /**
   * @since 2.0
   * @see #Signal(SignalProtocol, short, String)
   */
  public Signal(SignalProtocol<?> protocol, short id)
  {
    this(protocol, id, null);
  }

  /**
   * @since 2.0
   * @see #Signal(SignalProtocol, short, String)
   */
  public Signal(SignalProtocol<?> protocol, Enum<?> literal)
  {
    this(protocol, (short)literal.ordinal(), literal.name());
  }

  public SignalProtocol<?> getProtocol()
  {
    LifecycleUtil.checkActive(protocol);
    return protocol;
  }

  /**
   * Returns the short integer ID of this signal that is unique among all signals of the associated
   * {@link #getProtocol() protocol}.
   *
   * @since 2.0
   */
  public final short getID()
  {
    return id;
  }

  /**
   * @since 2.0
   */
  public String getName()
  {
    if (name == null)
    {
      // Needs no synchronization because any thread would set the same value.
      name = ReflectUtil.getSimpleClassName(this);
    }

    return name;
  }

  /**
   * @since 2.0
   */
  public final int getCorrelationID()
  {
    return correlationID;
  }

  /**
   * @since 2.0
   */
  @Override
  public String toString()
  {
    return toString(SHORT_TO_STRING);
  }

  /**
   * @since 4.13
   */
  public String toString(boolean shortForm)
  {
    String name = getName();

    String additionalInfo = getAdditionalInfo();
    if (StringUtil.isEmpty(additionalInfo))
    {
      if (shortForm)
      {
        return name;
      }

      return MessageFormat.format("{0}[protocol={1}, correlation={2}]", name, getProtocol().getType(), //$NON-NLS-1$
          getCorrelationID());
    }

    if (shortForm)
    {
      return MessageFormat.format("{0}[{1}]", name, additionalInfo); //$NON-NLS-1$
    }

    return MessageFormat.format("{0}[protocol={1}, correlation={2}, {3}]", name, getProtocol().getType(), //$NON-NLS-1$
        getCorrelationID(), additionalInfo);
  }

  /**
   * @since 4.5
   */
  protected String getAdditionalInfo()
  {
    return "";
  }

  @Override
  public final void run()
  {
    String threadName = null;
    Thread currentThread = null;

    try
    {
      if (OM.SET_SIGNAL_THREAD_NAME)
      {
        threadName = getClass().getSimpleName();
        currentThread = Thread.currentThread();
        ConcurrencyUtil.setThreadName(currentThread, threadName);
      }

      runSync();
    }
    catch (Exception ex)
    {
      if (DISABLE_LOG_EXCEPTIONS)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Exception in signal", ex); //$NON-NLS-1$
        }
      }
      else
      {
        OM.LOG.error(ex);
      }
    }
    finally
    {
      if (threadName != null)
      {
        ConcurrencyUtil.setThreadName(currentThread, threadName + "(FINISHED)"); //$NON-NLS-1$
      }
    }
  }

  protected final BufferInputStream getBufferInputStream()
  {
    return bufferInputStream;
  }

  protected final BufferOutputStream getBufferOutputStream()
  {
    return bufferOutputStream;
  }

  /**
   * @since 2.0
   */
  protected final void flush() throws IOException
  {
    if (currentStream instanceof OutputStream)
    {
      ((OutputStream)currentStream).flush();
    }
  }

  /**
   * @since 4.4
   */
  protected boolean closeChannelAfterMe()
  {
    return false;
  }

  /**
   * @since 4.5
   */
  protected boolean closeInputStreamAfterMe()
  {
    return true;
  }

  /**
   * @since 4.5
   */
  protected boolean closeOutputStreamAfterMe()
  {
    return true;
  }

  /**
   * @since 2.0
   */
  protected InputStream getCurrentInputStream()
  {
    if (currentStream instanceof InputStream)
    {
      return (InputStream)currentStream;
    }

    return null;
  }

  /**
   * @since 2.0
   */
  protected OutputStream getCurrentOutputStream()
  {
    if (currentStream instanceof OutputStream)
    {
      return (OutputStream)currentStream;
    }

    return null;
  }

  protected InputStream wrapInputStream(InputStream in) throws IOException
  {
    currentStream = getProtocol().wrapInputStream(in);
    return (InputStream)currentStream;
  }

  protected OutputStream wrapOutputStream(OutputStream out) throws IOException
  {
    currentStream = getProtocol().wrapOutputStream(out);
    return (OutputStream)currentStream;
  }

  protected void finishInputStream(InputStream in) throws IOException
  {
    currentStream = null;
    protocol.finishInputStream(in);
  }

  protected void finishOutputStream(OutputStream out) throws IOException
  {
    currentStream = null;
    protocol.finishOutputStream(out);
  }

  protected abstract void execute(BufferInputStream in, BufferOutputStream out) throws Exception;

  void runSync() throws Exception
  {
    Exception exception = null;

    try
    {
      execute(bufferInputStream, bufferOutputStream);
    }
    catch (IOTimeoutException ex) // Thrown from BufferInputStream
    {
      exception = ex.createTimeoutException();
      throw exception;
    }
    catch (Exception ex)
    {
      exception = ex;
      throw exception;
    }
    finally
    {
      if (closeInputStreamAfterMe())
      {
        IOUtil.closeSilent(wrappedInputStream != null ? wrappedInputStream : bufferInputStream);
      }

      if (closeOutputStreamAfterMe())
      {
        IOUtil.closeSilent(wrappedOutputStream != null ? wrappedOutputStream : bufferOutputStream);
      }

      protocol.stopSignal(this, exception);
    }
  }

  void setCorrelationID(int correlationID)
  {
    this.correlationID = correlationID;
  }

  /**
   * Transfers ownership of the passed stream to this signal.
   * The signal closes the stream when done reading.
   */
  void setBufferInputStream(BufferInputStream inputStream)
  {
    bufferInputStream = inputStream;
  }

  /**
   * Transfers ownership of the passed stream to this signal.
   * The signal closes the stream when done writing.
   */
  void setBufferOutputStream(BufferOutputStream outputStream)
  {
    bufferOutputStream = outputStream;
  }

  void doOutput(BufferOutputStream out) throws Exception
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("================ {0}: {1}", getOutputMeaning(), this); //$NON-NLS-1$
    }

    wrappedOutputStream = wrapOutputStream(out);
    ExtendedDataOutputStream extended = ExtendedDataOutputStream.wrap(wrappedOutputStream);

    try
    {
      doExtendedOutput(extended);
    }
    catch (Error ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw ex;
    }
    finally
    {
      finishOutputStream(wrappedOutputStream);
    }

    boolean ccam = closeChannelAfterMe();
    out.flushWithEOS(ccam);
  }

  void doInput(BufferInputStream in) throws Exception
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("================ {0}: {1}", getInputMeaning(), this); //$NON-NLS-1$
    }

    wrappedInputStream = wrapInputStream(in);
    ExtendedDataInputStream extended = ExtendedDataInputStream.wrap(wrappedInputStream);

    try
    {
      doExtendedInput(extended);
    }
    catch (Error ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw ex;
    }
    finally
    {
      finishInputStream(wrappedInputStream);
    }
  }

  void doExtendedOutput(ExtendedDataOutputStream out) throws Exception
  {
  }

  void doExtendedInput(ExtendedDataInputStream in) throws Exception
  {
  }

  abstract String getOutputMeaning();

  abstract String getInputMeaning();
}
