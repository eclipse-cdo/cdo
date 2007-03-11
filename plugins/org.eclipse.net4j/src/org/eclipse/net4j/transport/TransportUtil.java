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
package org.eclipse.net4j.transport;

import org.eclipse.internal.net4j.transport.BufferFactory;
import org.eclipse.internal.net4j.transport.BufferPool;

/**
 * @author Eike Stepper
 */
public final class TransportUtil
{
  public static final short DEFAULT_BUFFER_CAPACITY = 4096;

  private TransportUtil()
  {
  }

  public static IBufferProvider.Introspection createBufferFactory(short bufferCapacity)
  {
    return new BufferFactory(bufferCapacity);
  }

  public static IBufferProvider.Introspection createBufferFactory()
  {
    return new BufferFactory(DEFAULT_BUFFER_CAPACITY);
  }

  public static IBufferPool.Introspection createBufferPool(IBufferProvider factory)
  {
    return new BufferPool(factory);
  }

  public static IBufferPool.Introspection createBufferPool(short bufferCapacity)
  {
    return createBufferPool(createBufferFactory(bufferCapacity));
  }

  public static IBufferPool.Introspection createBufferPool()
  {
    return createBufferPool(createBufferFactory());
  }
}
