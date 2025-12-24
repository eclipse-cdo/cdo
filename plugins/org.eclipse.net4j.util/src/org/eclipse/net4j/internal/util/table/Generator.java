/*
 * Copyright (c) 2014, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.table;

import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public interface Generator
{
  public Object nextValue();

  /**
   * @author Eike Stepper
   */
  public static final class Incrementer implements Generator
  {
    private final double start;

    private final double increment;

    private double value;

    public Incrementer(double start, double increment)
    {
      this.start = start;
      this.increment = increment;
      value = start;
    }

    public Incrementer(double start)
    {
      this(start, 1);
    }

    public Incrementer()
    {
      this(0);
    }

    public double getStart()
    {
      return start;
    }

    public double getIncrement()
    {
      return increment;
    }

    public double getValue()
    {
      return value;
    }

    @Override
    public Object nextValue()
    {
      try
      {
        return value;
      }
      finally
      {
        value += increment;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Sequencer<T> implements Generator
  {
    private final Iterable<T> iterable;

    private final boolean repeat;

    private Iterator<T> iterator;

    public Sequencer(Iterable<T> iterable, boolean repeat)
    {
      this.iterable = iterable;
      this.repeat = repeat;
      reset();
    }

    public Sequencer(Iterable<T> iterable)
    {
      this(iterable, false);
    }

    public Iterable<T> getIterable()
    {
      return iterable;
    }

    public boolean isRepeat()
    {
      return repeat;
    }

    @Override
    public T nextValue()
    {
      if (iterator.hasNext())
      {
        return iterator.next();
      }

      if (repeat)
      {
        reset();
        if (iterator.hasNext())
        {
          return iterator.next();
        }
      }

      return null;
    }

    private void reset()
    {
      iterator = iterable.iterator();
    }
  }
}
