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
package org.eclipse.net4j.signal;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.stream.BufferInputStream;
import org.eclipse.net4j.stream.BufferOutputStream;

import org.eclipse.internal.net4j.bundle.OM;

import java.io.EOFException;
import java.util.concurrent.TimeoutException;

/**
 * @author Eike Stepper
 */
public abstract class Signal implements Runnable
{
  @SuppressWarnings("unused")
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, Signal.class);

  private SignalProtocol protocol;

  private int correlationID;

  private BufferInputStream inputStream;

  private BufferOutputStream outputStream;

  protected Signal()
  {
  }

  protected SignalProtocol getProtocol()
  {
    return protocol;
  }

  protected final int getCorrelationID()
  {
    return correlationID;
  }

  protected final BufferInputStream getInputStream()
  {
    return inputStream;
  }

  protected final BufferOutputStream getOutputStream()
  {
    return outputStream;
  }

  public final void run()
  {
    try
    {
      runSync();
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  protected void runSync() throws Exception
  {
    try
    {
      execute(inputStream, outputStream);
    }
    catch (EOFException ex)
    {
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

  void setInputStream(BufferInputStream inputStream)
  {
    this.inputStream = inputStream;
  }

  void setOutputStream(BufferOutputStream outputStream)
  {
    this.outputStream = outputStream;
  }
}
