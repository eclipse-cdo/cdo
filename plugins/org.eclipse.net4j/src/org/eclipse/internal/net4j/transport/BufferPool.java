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
import org.eclipse.net4j.transport.IBufferPool;
import org.eclipse.net4j.transport.IBufferProvider;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.Net4j;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Eike Stepper
 */
public class BufferPool extends BufferProvider implements IBufferPool, IBufferPool.Introspection
{
  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_BUFFER, BufferPool.class);

  private final IBufferProvider factory;

  private final Queue<IBuffer> queue = new ConcurrentLinkedQueue<IBuffer>();

  private int pooledBuffers;

  public BufferPool(IBufferProvider factory)
  {
    super(factory.getBufferCapacity());
    this.factory = factory;
  }

  public int getPooledBuffers()
  {
    return pooledBuffers;
  }

  public boolean evictOne()
  {
    IBuffer buffer = queue.poll();
    if (buffer == null)
    {
      return false;
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Evicting " + buffer); //$NON-NLS-1$
    }

    factory.retainBuffer(buffer);
    --pooledBuffers;
    return true;
  }

  public int evict(int survivors)
  {
    int evictedBuffers = 0;
    while (pooledBuffers > survivors)
    {
      if (evictOne())
      {
        ++evictedBuffers;
      }
      else
      {
        break;
      }
    }

    return evictedBuffers;
  }

  @Override
  protected IBuffer doProvideBuffer()
  {
    IBuffer buffer = queue.poll();
    if (buffer == null)
    {
      buffer = factory.provideBuffer();
      ((Buffer)buffer).setBufferProvider(this);
    }

    buffer.clear();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Obtained " + buffer); //$NON-NLS-1$
    }

    return buffer;
  }

  @Override
  protected void doRetainBuffer(IBuffer buffer)
  {
    if (buffer.getCapacity() != getBufferCapacity())
    {
      throw new IllegalArgumentException("buffer.getCapacity() != getBufferCapacity()"); //$NON-NLS-1$
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("Retaining " + buffer); //$NON-NLS-1$
    }

    queue.add(buffer);
  }

  @Override
  public String toString()
  {
    return "BufferPool[size=" + pooledBuffers + ", " + factory + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }
}
