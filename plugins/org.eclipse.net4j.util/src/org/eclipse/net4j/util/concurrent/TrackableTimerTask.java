/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.om.OMPlatform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TimerTask;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 * @since 3.3
 */
public abstract class TrackableTimerTask extends TimerTask
{
  /**
   * The boolean value of the system property <code>org.eclipse.net4j.util.concurrent.TrackTimerTasks</code>.
   */
  public static final boolean TRACK_TIMER_TASKS = OMPlatform.INSTANCE.isProperty("org.eclipse.net4j.util.concurrent.TrackTimerTasks");

  private static final Map<TrackableTimerTask, ConstructionInfo> CONSTRUCTION_INFOS = TRACK_TIMER_TASKS
      ? new WeakHashMap<>()
      : null;

  protected TrackableTimerTask()
  {
    if (TRACK_TIMER_TASKS)
    {
      synchronized (CONSTRUCTION_INFOS)
      {
        CONSTRUCTION_INFOS.put(this, new ConstructionInfo());
      }
    }
  }

  @Override
  public boolean cancel()
  {
    if (TRACK_TIMER_TASKS)
    {
      synchronized (CONSTRUCTION_INFOS)
      {
        CONSTRUCTION_INFOS.remove(this);
      }
    }

    return super.cancel();
  }

  public static Collection<Exception> getConstructionStackTraces(long minLifeTimeMillis)
  {
    if (!TRACK_TIMER_TASKS)
    {
      return Collections.emptyList();
    }

    long maxTimeStamp = System.currentTimeMillis() - minLifeTimeMillis;
    Collection<Exception> result = new ArrayList<>();

    synchronized (CONSTRUCTION_INFOS)
    {
      for (ConstructionInfo constructionInfo : CONSTRUCTION_INFOS.values())
      {
        if (constructionInfo.timeStamp < maxTimeStamp)
        {
          result.add(constructionInfo.stackTrace);
        }
      }
    }

    return result;
  }

  public static void logConstructionStackTraces(long minLifeTimeMillis)
  {
    if (TRACK_TIMER_TASKS)
    {
      Collection<Exception> constructionStackTraces = getConstructionStackTraces(minLifeTimeMillis);
      if (!constructionStackTraces.isEmpty())
      {
        for (Exception exception : constructionStackTraces)
        {
          OM.LOG.info(exception);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ConstructionInfo
  {
    public final long timeStamp = System.currentTimeMillis();

    public final Exception stackTrace = getStackTrace();

    private Exception getStackTrace()
    {
      try
      {
        throw new Exception("The timer task " + TrackableTimerTask.this + " has been constructed here:");
      }
      catch (Exception ex)
      {
        return ex;
      }
    }
  }
}
