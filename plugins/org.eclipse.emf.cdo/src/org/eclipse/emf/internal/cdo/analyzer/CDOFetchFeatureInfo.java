/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
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

import org.eclipse.emf.cdo.common.model.CDOClass;
import org.eclipse.emf.cdo.common.model.CDOFeature;

/**
 * @author Simon McDuff
 */
public class CDOFetchFeatureInfo
{
  private long timeBeforeUsed;

  private long latencyTime;

  private boolean active;

  private CDOClass cdoClass;

  private CDOFeature cdoFeature;

  public CDOFetchFeatureInfo(CDOClass cdoClass, CDOFeature cdoFeature)
  {
    this.cdoClass = cdoClass;
    this.cdoFeature = cdoFeature;
    active = false;
    latencyTime = -1;
  }

  public CDOClass getCDOClass()
  {
    return cdoClass;
  }

  public CDOFeature getCDOFeature()
  {
    return cdoFeature;
  }

  public boolean isActive()
  {
    return active;
  }

  public void setActive(boolean active)
  {
    this.active = active;
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

  @Override
  public int hashCode()
  {
    return cdoClass.hashCode() ^ cdoFeature.hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDOFetchFeatureInfo)
    {
      CDOFetchFeatureInfo featureInfo = (CDOFetchFeatureInfo)obj;
      return featureInfo.cdoClass == cdoClass && featureInfo.cdoFeature == cdoFeature;
    }

    return false;
  }
}
