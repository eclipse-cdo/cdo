/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.internal.net4j.buffer;

import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.OM;

import org.eclipse.spi.net4j.InternalBuffer;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class BufferFactory extends BufferProvider
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_BUFFER, BufferFactory.class);

  public BufferFactory(short bufferCapacity)
  {
    super(bufferCapacity);
  }

  @Override
  protected IBuffer doProvideBuffer()
  {
    IBuffer buffer = new Buffer(this, getBufferCapacity());
    if (TRACER.isEnabled())
    {
      TRACER.trace("Created " + buffer); //$NON-NLS-1$
    }

    return buffer;
  }

  @Override
  protected void doRetainBuffer(IBuffer buffer)
  {
    if (buffer instanceof InternalBuffer)
    {
      ((InternalBuffer)buffer).dispose();
    }

    buffer = null;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("BufferFactory[{0}]", getBufferCapacity()); //$NON-NLS-1$
  }
}
