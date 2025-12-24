/*
 * Copyright (c) 2007, 2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Eike Stepper
 */
public final class NonBlockingLongCounter
{
  private AtomicLong value;

  public NonBlockingLongCounter()
  {
    this(0L);
  }

  public NonBlockingLongCounter(long initialValue)
  {
    value = new AtomicLong(initialValue);
  }

  public long getValue()
  {
    return value.get();
  }

  public long increment()
  {
    long v;
    do
    {
      v = value.get();
    } while (!value.compareAndSet(v, v + 1));

    return v + 1;
  }

  /**
   * @since 3.0
   */
  public long decrement()
  {
    long v;
    do
    {
      v = value.get();
    } while (!value.compareAndSet(v, v - 1));

    return v - 1;
  }

  @Override
  public String toString()
  {
    return Long.toString(getValue());
  }
}
