/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public interface IRecoverableProperty
{
  public String getName();

  public String getValue();

  public void setValue(String value);

  public void recover();

  /**
   * @author Eike Stepper
   */
  public static abstract class NamedRecoverableProperty implements IRecoverableProperty
  {
    private String name;

    public NamedRecoverableProperty(String name)
    {
      this.name = name;
    }

    public final String getName()
    {
      return name;
    }

    @Override
    public String toString()
    {
      return name + "=" + getValue();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class IntCounter extends NamedRecoverableProperty
  {
    private int start;

    private int end;

    private int value;

    private boolean needNext;

    public IntCounter(String name, int start, int end)
    {
      super(name);
      this.start = start;
      this.end = end;

      value = this.start;
    }

    public final int getStart()
    {
      return start;
    }

    public final int getEnd()
    {
      return end;
    }

    public final boolean isAscending()
    {
      return start < end;
    }

    public final synchronized int getNextInt()
    {
      if (needNext)
      {
        if (value == end)
        {
          overflow();
        }
    
        if (isAscending())
        {
          ++value;
        }
        else
        {
          --value;
        }
      }
    
      needNext = true;
      return value;
    }

    public final synchronized int getInt()
    {
      return value;
    }

    public final synchronized void setInt(int value)
    {
      checkValue(value);
      this.value = value;
      needNext = false;
    }

    public final synchronized String getValue()
    {
      return Integer.toString(value);
    }

    public final synchronized void setValue(String value)
    {
      int intValue = Integer.parseInt(value);
      checkValue(intValue);
      this.value = intValue;
      needNext = false;
    }

    private void checkValue(int value)
    {
      if (isAscending())
      {
        if (value < start || value > end)
        {
          overflow();
        }
      }
      else
      {
        if (value > start || value < end)
        {
          overflow();
        }
      }
    }

    private static void overflow()
    {
      throw new IllegalStateException("Overflow");
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class LongCounter extends NamedRecoverableProperty
  {
    private long start;

    private long end;

    private long value;

    private boolean needNext;

    public LongCounter(String name, long start, long end)
    {
      super(name);
      this.start = start;
      this.end = end;

      value = this.start;
    }

    public final long getStart()
    {
      return start;
    }

    public final long getEnd()
    {
      return end;
    }

    public final boolean isAscending()
    {
      return start < end;
    }

    public final synchronized long getNextLong()
    {
      if (needNext)
      {
        if (value == end)
        {
          overflow();
        }
    
        if (isAscending())
        {
          ++value;
        }
        else
        {
          --value;
        }
      }
    
      needNext = true;
      return value;
    }

    public final synchronized long getLong()
    {
      return value;
    }

    public final synchronized void setLong(long value)
    {
      checkValue(value);
      this.value = value;
      needNext = false;
    }

    public final synchronized String getValue()
    {
      return Long.toString(value);
    }

    public final synchronized void setValue(String value)
    {
      long longValue = Long.parseLong(value);
      checkValue(longValue);
      this.value = longValue;
      needNext = false;
    }

    private void checkValue(long value)
    {
      if (isAscending())
      {
        if (value < start || value > end)
        {
          overflow();
        }
      }
      else
      {
        if (value > start || value < end)
        {
          overflow();
        }
      }
    }

    private static void overflow()
    {
      throw new IllegalStateException("Overflow");
    }
  }
}
