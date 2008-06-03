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
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeoutException;

/**
 * @author Eike Stepper
 */
public abstract class Signal implements Runnable
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, Signal.class);

  private SignalProtocol protocol;

  private int correlationID;

  private BufferInputStream bufferInputStream;

  private BufferOutputStream bufferOutputStream;

  protected Signal()
  {
  }

  public SignalProtocol getProtocol()
  {
    return protocol;
  }

  protected final int getCorrelationID()
  {
    return correlationID;
  }

  protected final BufferInputStream getBufferInputStream()
  {
    return bufferInputStream;
  }

  protected final BufferOutputStream getBufferOutputStream()
  {
    return bufferOutputStream;
  }

  protected InputStream wrapInputStream(InputStream in)
  {
    try
    {
      return protocol.wrapInputStream(in);
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
      return protocol.wrapOutputStream(out);
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
      protocol.finishOutputStream(out);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
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

  protected void runSync() throws Exception
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

  protected abstract short getSignalID();

  protected abstract void execute(BufferInputStream in, BufferOutputStream out) throws Exception;

  void setProtocol(SignalProtocol protocol)
  {
    this.protocol = protocol;
  }

  void setCorrelationID(int correlationID)
  {
    this.correlationID = correlationID;
  }

  void setBufferInputStream(BufferInputStream inputStream)
  {
    this.bufferInputStream = inputStream;
  }

  void setBufferOutputStream(BufferOutputStream outputStream)
  {
    this.bufferOutputStream = outputStream;
  }
}
