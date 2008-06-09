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

import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class AbstractCDOIDInteger extends AbstractCDOID implements Comparable<AbstractCDOIDInteger>
{
  private static final long serialVersionUID = 1L;

  private int value;

  public AbstractCDOIDInteger()
  {
  }

  public AbstractCDOIDInteger(int value)
  {
    if (value == 0)
    {
      throw new IllegalArgumentException("value == 0");
    }

    this.value = value;
  }

  public int getIntValue()
  {
    return value;
  }

  public void read(ExtendedDataInput in) throws IOException
  {
    value = in.readInt();
  }

  public void write(ExtendedDataOutput out) throws IOException
  {
    out.writeInt(value);
  }

  public int compareTo(AbstractCDOIDInteger that)
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
      AbstractCDOIDInteger that = (AbstractCDOIDInteger)obj;
      return value == that.value;
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return getClass().hashCode() ^ value;
  }

  @Override
  public String toString()
  {
    return String.valueOf(value);
  }
}
