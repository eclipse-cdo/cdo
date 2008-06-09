/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.spi.common;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class AbstractCDOIDLong extends AbstractCDOID implements Comparable<AbstractCDOIDLong>
{
  private static final long serialVersionUID = 1L;

  private long value;

  public AbstractCDOIDLong()
  {
  }

  public AbstractCDOIDLong(long value)
  {
    if (value == 0L)
    {
      throw new IllegalArgumentException("value == 0L");
    }

    this.value = value;
  }

  public long getLongValue()
  {
    return value;
  }

  public void read(ExtendedDataInput in) throws IOException
  {
    value = in.readLong();
  }

  public void write(ExtendedDataOutput out) throws IOException
  {
    out.writeLong(value);
  }

  public int compareTo(AbstractCDOIDLong that)
  {
    if (value < that.value)
    {
      return -1;
    }

    if (value > that.value)
    {
      return 1;
    }

    return 0;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj != null && obj.getClass() == getClass())
    {
      AbstractCDOIDLong that = (AbstractCDOIDLong)obj;
      return value == that.value;
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return getClass().hashCode() ^ ObjectUtil.hashCode(value);
  }

  @Override
  public String toString()
  {
    return String.valueOf(value);
  }
}
