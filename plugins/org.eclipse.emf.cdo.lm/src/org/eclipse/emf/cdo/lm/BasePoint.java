/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm;

import java.util.Objects;

/**
 * @author Eike Stepper
 */
public final class BasePoint
{
  private final FloatingBaseline base;

  private final long timeStamp;

  public BasePoint(FloatingBaseline base, long timeStamp)
  {
    this.base = base;
    this.timeStamp = timeStamp;
  }

  public FloatingBaseline getBase()
  {
    return base;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(base, timeStamp);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }

    if (!(obj instanceof BasePoint))
    {
      return false;
    }

    BasePoint other = (BasePoint)obj;
    return Objects.equals(base, other.base) && timeStamp == other.timeStamp;
  }

  @Override
  public String toString()
  {
    return "BasePoint[base=" + base + ", timeStamp=" + timeStamp + "]";
  }
}
