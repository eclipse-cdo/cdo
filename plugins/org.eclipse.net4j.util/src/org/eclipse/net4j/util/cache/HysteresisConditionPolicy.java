/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.cache;

import org.eclipse.net4j.util.cache.ICacheMonitor.Condition;

/**
 * @author Eike Stepper
 */
public class HysteresisConditionPolicy extends ThresholdConditionPolicy
{
  private long thresholdYellowRed;

  private long thresholdGreenYellow;

  public HysteresisConditionPolicy(long thresholdYellowRed, long thresholdRedYellow, long thresholdGreenYellow, long thresholdYellowGreen)
  {
    super(thresholdRedYellow, thresholdYellowGreen);
    if (thresholdYellowRed > thresholdRedYellow)
    {
      throw new IllegalArgumentException("thresholdYellowRed > thresholdRedYellow"); //$NON-NLS-1$
    }

    if (thresholdRedYellow > thresholdGreenYellow)
    {
      throw new IllegalArgumentException("thresholdRedYellow > thresholdGreenYellow"); //$NON-NLS-1$
    }

    if (thresholdGreenYellow > thresholdYellowGreen)
    {
      throw new IllegalArgumentException("thresholdGreenYellow > thresholdYellowGreen"); //$NON-NLS-1$
    }

    this.thresholdGreenYellow = thresholdGreenYellow;
    this.thresholdYellowRed = thresholdYellowRed;
  }

  public long getThresholdYellowRed()
  {
    return thresholdYellowRed;
  }

  public long getThresholdGreenYellow()
  {
    return thresholdGreenYellow;
  }

  @Override
  protected Condition getNewCondition(Condition oldCondition, long freeMemory)
  {
    switch (oldCondition)
    {
    case GREEN:
      if (freeMemory < thresholdYellowRed)
      {
        return Condition.RED;
      }

      if (freeMemory < thresholdGreenYellow)
      {
        return Condition.YELLOW;
      }

      return Condition.GREEN;

    case YELLOW:
      if (freeMemory < thresholdYellowRed)
      {
        return Condition.RED;
      }

      if (freeMemory > getThresholdYellowGreen())
      {
        return Condition.GREEN;
      }

      return Condition.YELLOW;

    case RED:
      if (freeMemory > getThresholdYellowGreen())
      {
        return Condition.GREEN;
      }

      if (freeMemory > getThresholdRedYellow())
      {
        return Condition.YELLOW;
      }

      return Condition.RED;

    default:
      throw new IllegalArgumentException("oldCondition == " + oldCondition); //$NON-NLS-1$
    }
  }
}
