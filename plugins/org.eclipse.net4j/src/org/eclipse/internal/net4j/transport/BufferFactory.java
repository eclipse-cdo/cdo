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
package org.eclipse.internal.net4j.transport;

import org.eclipse.net4j.transport.IBuffer;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.Net4j;

/**
 * @author Eike Stepper
 */
public class BufferFactory extends BufferProvider
{
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_BUFFER, BufferFactory.class);

  public BufferFactory(short bufferCapacity)
  {
    super(bufferCapacity);
  }

  @Override
  protected IBuffer doProvideBuffer()
  {
    Buffer buffer = new Buffer(this, getBufferCapacity());
    if (TRACER.isEnabled())
    {
      TRACER.trace("Created " + buffer); //$NON-NLS-1$
    }

    return buffer;
  }

  @Override
  protected void doRetainBuffer(IBuffer buffer)
  {
    if (buffer instanceof Buffer)
    {
      ((Buffer)buffer).dispose();
    }

    buffer = null;
  }

  @Override
  public String toString()
  {
    return "BufferFactory[capacity=" + getBufferCapacity() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
  }
}
