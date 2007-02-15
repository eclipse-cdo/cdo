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
import org.eclipse.net4j.transport.BufferProvider;

/**
 * @author Eike Stepper
 */
public abstract class BufferProviderImpl implements BufferProvider, BufferProvider.Introspection
{
  private short bufferCapacity;

  private long providedBuffers;

  private long retainedBuffers;

  public BufferProviderImpl(short bufferCapacity)
  {
    this.bufferCapacity = bufferCapacity;
  }

  public final long getProvidedBuffers()
  {
    return providedBuffers;
  }

  public final long getRetainedBuffers()
  {
    return retainedBuffers;
  }

  public final short getBufferCapacity()
  {
    return bufferCapacity;
  }

  public final Buffer provideBuffer()
  {
    ++providedBuffers;
    return doProvideBuffer();
  }

  public final void retainBuffer(Buffer buffer)
  {
    ++retainedBuffers;
    doRetainBuffer(buffer);
  }

  @Override
  public String toString()
  {
    return "BufferProvider[capacity=" + bufferCapacity + "]"; //$NON-NLS-1$ //$NON-NLS-2$
  }

  protected abstract Buffer doProvideBuffer();

  protected abstract void doRetainBuffer(Buffer buffer);
}
