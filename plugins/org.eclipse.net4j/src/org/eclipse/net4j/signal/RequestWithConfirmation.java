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
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Eike Stepper
 */
public abstract class RequestWithConfirmation<RESULT> extends Request
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, RequestWithConfirmation.class);

  /**
   * @since 2.0
   */
  protected RequestWithConfirmation(SignalProtocol protocol)
  {
    super(protocol);
  }

  @Override
  protected short getSignalID()
  {
    return 0;
  }

  @Override
  @SuppressWarnings("unchecked")
  public RESULT send() throws Exception
  {
    return (RESULT)super.send();
  }

  @Override
  @SuppressWarnings("unchecked")
  public RESULT send(long timeout) throws Exception
  {
    return (RESULT)super.send(timeout);
  }

  @Override
  protected final void execute(BufferInputStream in, BufferOutputStream out) throws Exception
  {
    super.execute(in, out);
    if (TRACER.isEnabled())
    {
      TRACER.trace("================ Confirming " + ReflectUtil.getSimpleClassName(this)); //$NON-NLS-1$
    }

    InputStream wrappedInputStream = wrapInputStream(in);
    RESULT result = confirming(ExtendedDataInputStream.wrap(wrappedInputStream));
    finishInputStream(wrappedInputStream);
    setResult(result);
  }

  /**
   * <b>Important Note:</b> The confirmation must not be empty, i.e. the stream must be used at least to read a
   * <code>boolean</code>. Otherwise synchronization problems will result!
   */
  protected abstract RESULT confirming(ExtendedDataInputStream in) throws IOException;

  void setExceptionMessage(String message)
  {
    getBufferInputStream().setException(new SignalRemoteException(message));
  }
}
