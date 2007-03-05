/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.signal;

import org.eclipse.net4j.stream.BufferInputStream;
import org.eclipse.net4j.stream.BufferOutputStream;
import org.eclipse.net4j.transport.IChannel;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.stream.ExtendedDataInputStream;
import org.eclipse.net4j.util.stream.ExtendedDataOutputStream;

import org.eclipse.internal.net4j.bundle.Net4j;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class RequestWithConfirmation<RESULT> extends SignalActor<RESULT>
{
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_SIGNAL, RequestWithConfirmation.class);

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

    requesting(new ExtendedDataOutputStream(out));
    out.flush();
    if (TRACER.isEnabled())
    {
      TRACER.trace("================ Confirming " + ReflectUtil.getSimpleClassName(this)); //$NON-NLS-1$
    }

    setResult(confirming(new ExtendedDataInputStream(in)));
  }

  protected abstract void requesting(ExtendedDataOutputStream out) throws IOException;

  protected abstract RESULT confirming(ExtendedDataInputStream in) throws IOException;
}
