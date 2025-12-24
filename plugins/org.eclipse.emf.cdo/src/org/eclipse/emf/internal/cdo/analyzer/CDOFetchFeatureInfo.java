/*
 * Copyright (c) 2007-2009, 2011, 2012, 2014, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.analyzer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Objects;

/**
 * @author Simon McDuff
 */
public final class CDOFetchFeatureInfo
{
  private static final long INITIAL_TIME_BEFORE_USED = 0;

  private static final long INITIAL_LATENCY_TIME = -1;

  private final EClass eClass;

  private final EStructuralFeature feature;

  private long timeBeforeUsed = INITIAL_TIME_BEFORE_USED;

  private long latencyTime = INITIAL_LATENCY_TIME;

  private boolean active;

  public CDOFetchFeatureInfo(EClass eClass, EStructuralFeature feature)
  {
    this.eClass = eClass;
    this.feature = feature;
  }

  public EClass getEClass()
  {
    return eClass;
  }

  public EStructuralFeature getEStructuralFeature()
  {
    return feature;
  }

  public long getTimeBeforeUsed()
  {
    return timeBeforeUsed;
  }

  public long getLatencyTime()
  {
    return latencyTime;
  }

  public boolean isActive()
  {
    return active;
  }

  public void setActive(boolean active)
  {
    this.active = active;
  }

  public void updateTimeInfo(long elapseTimeBeforeLastRequest)
  {
    long oldTimeBeforeUsed = timeBeforeUsed;
    timeBeforeUsed = oldTimeBeforeUsed == INITIAL_TIME_BEFORE_USED //
        ? elapseTimeBeforeLastRequest //
        : (oldTimeBeforeUsed + elapseTimeBeforeLastRequest) / 2;
  }

  public void updateLatencyTime(long latencyTime)
  {
    long oldLatencyTime = this.latencyTime;
    this.latencyTime = oldLatencyTime == INITIAL_LATENCY_TIME //
        ? latencyTime //
        : (latencyTime + oldLatencyTime) / 2;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(eClass, feature);
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
      return featureInfo.eClass == eClass && featureInfo.feature == feature;
    }

    return false;
  }
}
