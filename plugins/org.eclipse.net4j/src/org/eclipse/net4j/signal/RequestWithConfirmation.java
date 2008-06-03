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
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Eike Stepper
 */
public abstract class RequestWithConfirmation<RESULT> extends SignalActor<RESULT>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, RequestWithConfirmation.class);

  protected RequestWithConfirmation(IChannel channel)
  {
    super(channel);
  }

  @Override
  protected final void execute(BufferInputStream in, BufferOutputStream out) throws Exception
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("================ Requesting " + ReflectUtil.getSimpleClassName(this)); //$NON-NLS-1$
    }

    OutputStream wrappedOutputStream = wrapOutputStream(out);
    requesting(ExtendedDataOutputStream.wrap(wrappedOutputStream));
    finishOutputStream(wrappedOutputStream);
    out.flushWithEOS();
    if (TRACER.isEnabled())
    {
      TRACER.trace("================ Confirming " + ReflectUtil.getSimpleClassName(this)); //$NON-NLS-1$
    }

    InputStream wrappedInputStream = wrapInputStream(in);
    RESULT result = confirming(ExtendedDataInputStream.wrap(wrappedInputStream));
    finishInputStream(wrappedInputStream);
    setResult(result);
  }

  protected abstract void requesting(ExtendedDataOutputStream out) throws IOException;

  /**
   * <b>Important Note:</b> The confirmation must not be empty, i.e. the stream must be used at least to read a
   * <code>boolean</code>. Otherwise synchronization problems will result!
   */
  protected abstract RESULT confirming(ExtendedDataInputStream in) throws IOException;
}
