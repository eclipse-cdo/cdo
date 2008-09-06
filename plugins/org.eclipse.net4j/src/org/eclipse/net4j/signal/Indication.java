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
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Eike Stepper
 */
public abstract class Indication extends SignalReactor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, Indication.class);

  protected Indication()
  {
  }

  @Override
  protected void execute(BufferInputStream in, BufferOutputStream out) throws Exception
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("================ Indicating " + ReflectUtil.getSimpleClassName(this)); //$NON-NLS-1$
    }

    InputStream wrappedInputStream = wrapInputStream(in);

    try
    {
      indicating(ExtendedDataInputStream.wrap(wrappedInputStream));
    }
    catch (IOException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      sendExceptionMessage(ex);
      throw ex;
    }
    finally
    {
      finishInputStream(wrappedInputStream);
    }
  }

  protected abstract void indicating(ExtendedDataInputStream in) throws IOException;

  /**
   * @since 2.0
   */
  protected String getMessage(Exception ex)
  {
    String msg = ex.getLocalizedMessage();
    if (StringUtil.isEmpty(msg))
    {
      msg = ex.getClass().getName();
    }

    return msg;
  }

  void sendExceptionMessage(Exception ex) throws Exception
  {
    SignalProtocol protocol = getProtocol();
    int correlationID = -getCorrelationID();
    String message = getMessage(ex);
    new ExceptionMessageRequest(protocol, correlationID, message).send();
  }
}
