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
package org.eclipse.internal.net4j.transport;

import org.eclipse.net4j.transport.Buffer;

/**
 * @author Eike Stepper
 */
public class BufferFactoryImpl extends BufferProviderImpl
{
  public BufferFactoryImpl(short bufferCapacity)
  {
    super(bufferCapacity);
  }

  @Override
  protected Buffer doProvideBuffer()
  {
    BufferImpl buffer = new BufferImpl(this, getBufferCapacity());
    System.out.println(toString() + ": Created " + buffer);
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
    return "BufferFactory[capacity=" + getBufferCapacity() + "]";
  }
}
