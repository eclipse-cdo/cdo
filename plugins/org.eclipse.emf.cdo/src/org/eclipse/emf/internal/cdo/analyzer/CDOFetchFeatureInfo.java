/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.internal.cdo.analyzer;

/**
 * @author Eike Stepper
 */
public class CDOFetchFeatureInfo
{
  private long timeBeforeUsed;

  private long latencyTime;

  private boolean active;

  public CDOFetchFeatureInfo()
  {
    active = false;
    latencyTime = -1;
  }

  public boolean isActive()
  {
    return active;
  }

  public void setActive(boolean isActive)
  {
    active = isActive;
  }

  public long getTimeBeforeUsed()
  {
    return timeBeforeUsed;
  }

  public void setTimeBeforeUsed(long timeBeforeUsed)
  {
    this.timeBeforeUsed = timeBeforeUsed;
  }

  public long getLatencyTime()
  {
    return latencyTime;
  }

  public void setLatencyTime(long latencyTime)
  {
    this.latencyTime = latencyTime;
  }

  public void updateLatencyTime(long latencyTime)
  {
    if (getLatencyTime() == -1)
    {
      setLatencyTime(latencyTime);
    }
    else
    {
      setLatencyTime((latencyTime + getLatencyTime()) / 2);
    }
  }

  public void updateTimeInfo(long elapseTimeBeforeLastRequest)
  {
    if (getTimeBeforeUsed() == 0)
    {
      setTimeBeforeUsed(elapseTimeBeforeLastRequest);
    }
    else
    {
      setTimeBeforeUsed((getTimeBeforeUsed() + elapseTimeBeforeLastRequest) / 2);
    }
  }
}
