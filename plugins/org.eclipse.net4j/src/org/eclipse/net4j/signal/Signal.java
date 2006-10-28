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
package org.eclipse.net4j.signal;

import org.eclipse.net4j.transport.util.BufferInputStream;
import org.eclipse.net4j.transport.util.BufferOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.Net4j;

/**
 * @author Eike Stepper
 */
public abstract class Signal implements Runnable
{
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_SIGNAL, Signal.class);

  private SignalProtocol protocol;

  private int correlationID;

  private BufferInputStream inputStream;

  private BufferOutputStream outputStream;

  protected Signal()
  {
  }

  protected final SignalProtocol getProtocol()
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
      execute(inputStream, outputStream);
    }
    catch (Exception ex)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace(this, ex);
      }
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
