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

import org.eclipse.net4j.IChannel;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.stream.BufferInputStream;
import org.eclipse.net4j.stream.BufferOutputStream;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import org.eclipse.internal.net4j.bundle.OM;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class Request extends SignalActor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, Request.class);

  protected Request(IChannel channel)
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
  }

  protected abstract void requesting(ExtendedDataOutputStream out) throws IOException;
}
