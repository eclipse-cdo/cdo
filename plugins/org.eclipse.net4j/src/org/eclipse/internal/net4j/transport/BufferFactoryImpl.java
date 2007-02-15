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

import org.eclipse.net4j.transport.Buffer;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.Net4j;

/**
 * @author Eike Stepper
 */
public class BufferFactoryImpl extends BufferProviderImpl
{
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_BUFFER, BufferFactoryImpl.class);

  public BufferFactoryImpl(short bufferCapacity)
  {
    super(bufferCapacity);
  }

  @Override
  protected Buffer doProvideBuffer()
  {
    BufferImpl buffer = new BufferImpl(this, getBufferCapacity());
    if (TRACER.isEnabled())
    {
      TRACER.trace("Created " + buffer); //$NON-NLS-1$
    }

    return buffer;
  }

  @Override
  protected void doRetainBuffer(Buffer buffer)
  {
    if (buffer instanceof BufferImpl)
    {
      ((BufferImpl)buffer).dispose();
    }

    buffer = null;
  }

  @Override
  public String toString()
  {
    return "BufferFactory[capacity=" + getBufferCapacity() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
  }
}
