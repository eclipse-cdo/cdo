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
import org.eclipse.net4j.buffer.BufferOutputStream;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.concurrent.TimeoutException;

/**
 * @author Eike Stepper
 */
public abstract class Signal implements Runnable
{
  /**
   * @since 2.0
   */
  public static final long NO_TIMEOUT = BufferInputStream.NO_TIMEOUT;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, Signal.class);

  private SignalProtocol<?> protocol;

  private short id;

  private String name;

  private int correlationID;

  private BufferInputStream bufferInputStream;

  private BufferOutputStream bufferOutputStream;

  private Object currentStream;

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

  @Override
  public String toString()
  {
    return MessageFormat.format("Signal[protocol={0}, id={1}, name={2}, correlation={3}]", getProtocol().getType(),
        getID(), getName(), getCorrelationID());
  }

  public final void run()
  {
    String threadName = null;
    try
    {
      if (OM.SET_SIGNAL_THREAD_NAME)
      {
        threadName = getClass().getSimpleName();
        Thread.currentThread().setName(threadName);
      }

      runSync();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
    finally
    {
      if (threadName != null)
      {
        Thread.currentThread().setName(threadName + "(FINISHED)");
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

  protected InputStream wrapInputStream(InputStream in)
  {
    try
    {
      currentStream = protocol.wrapInputStream(in);
      return (InputStream)currentStream;
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  protected OutputStream wrapOutputStream(OutputStream out)
  {
    try
    {
      currentStream = protocol.wrapOutputStream(out);
      return (OutputStream)currentStream;
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  protected void finishInputStream(InputStream in)
  {
    try
    {
      currentStream = null;
      protocol.finishInputStream(in);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  protected void finishOutputStream(OutputStream out)
  {
    try
    {
      currentStream = null;
      protocol.finishOutputStream(out);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
  }

  protected abstract void execute(BufferInputStream in, BufferOutputStream out) throws Exception;

  void runSync() throws Exception
  {
    try
    {
      execute(bufferInputStream, bufferOutputStream);
    }
    catch (EOFException ex)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace(ex);
      }

      throw new TimeoutException("Timeout");
    }
    catch (Exception ex)
    {
      throw ex;
    }
    finally
    {
      getProtocol().stopSignal(this);
    }
  }

  void setCorrelationID(int correlationID)
  {
    this.correlationID = correlationID;
  }

  void setBufferInputStream(BufferInputStream inputStream)
  {
    bufferInputStream = inputStream;
  }

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

    OutputStream wrappedOutputStream = wrapOutputStream(out);
    ExtendedDataOutputStream extended = ExtendedDataOutputStream.wrap(wrappedOutputStream);

    try
    {
      doExtendedOutput(extended);
    }
    catch (Error ex)
    {
      OM.LOG.error(ex);
      throw ex;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      throw ex;
    }
    finally
    {
      finishOutputStream(wrappedOutputStream);
    }

    out.flushWithEOS();
  }

  void doInput(BufferInputStream in) throws Exception
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("================ {0}: {1}", getInputMeaning(), this); //$NON-NLS-1$
    }

    InputStream wrappedInputStream = wrapInputStream(in);
    ExtendedDataInputStream extended = ExtendedDataInputStream.wrap(wrappedInputStream);

    try
    {
      doExtendedInput(extended);
    }
    catch (Error ex)
    {
      OM.LOG.error(ex);
      throw ex;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
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
