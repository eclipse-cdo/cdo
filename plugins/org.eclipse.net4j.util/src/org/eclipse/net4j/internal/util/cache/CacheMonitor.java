/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.cache;

import org.eclipse.net4j.internal.util.event.Event;
import org.eclipse.net4j.internal.util.lifecycle.Worker;
import org.eclipse.net4j.util.ImplementationError;
import org.eclipse.net4j.util.cache.ICache;
import org.eclipse.net4j.util.cache.ICacheMonitor;
import org.eclipse.net4j.util.cache.ICacheMonitorEvent;
import org.eclipse.net4j.util.cache.ICacheRegistration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class CacheMonitor extends Worker implements ICacheMonitor
{
  private static final long DEFAULT_PAUSE_GREEN = 60L * 1000L; // 1 minute

  private static final long DEFAULT_PAUSE_YELLOW = 5L * 1000L; // 5 seconds

  private static final long DEFAULT_PAUSE_RED = 100L; // 100 milliseconds

  private long pauseGREEN = DEFAULT_PAUSE_GREEN;

  private long pauseYELLOW = DEFAULT_PAUSE_YELLOW;

  private long pauseRED = DEFAULT_PAUSE_RED;

  private ConditionPolicy conditionPolicy;

  private Condition condition;

  private Map<ICache, ICacheRegistration> caches = new HashMap<ICache, ICacheRegistration>();

  public CacheMonitor()
  {
  }

  public long getPauseGREEN()
  {
    return pauseGREEN;
  }

  public void setPauseGREEN(long pauseGREEN)
  {
    this.pauseGREEN = pauseGREEN;
  }

  public long getPauseYELLOW()
  {
    return pauseYELLOW;
  }

  public void setPauseYELLOW(long pauseYELLOW)
  {
    this.pauseYELLOW = pauseYELLOW;
  }

  public long getPauseRED()
  {
    return pauseRED;
  }

  public void setPauseRED(long pauseRED)
  {
    this.pauseRED = pauseRED;
  }

  public ConditionPolicy getConditionPolicy()
  {
    return conditionPolicy;
  }

  public void setConditionPolicy(ConditionPolicy conditionPolicy)
  {
    this.conditionPolicy = conditionPolicy;
  }

  public Condition getCondition()
  {
    return condition;
  }

  public void setCondition(Condition newCondition)
  {
    if (newCondition == null)
    {
      throw new ImplementationError("newCondition == null");
    }

    Condition oldCondition = condition;
    if (newCondition != oldCondition)
    {
      condition = newCondition;
      fireEvent(new CacheMonitorEvent(oldCondition, newCondition));
    }
  }

  public ICacheRegistration registerCache(ICache cache)
  {
    ICacheRegistration registration = new CacheRegistration(this, cache);
    caches.put(cache, registration);
    return registration;
  }

  public void deregisterCache(ICache cache)
  {
    ICacheRegistration registration = caches.remove(cache);
    if (registration != null)
    {
      registration.dispose();
    }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (conditionPolicy == null)
    {
      throw new IllegalStateException("conditionPolicy == null");
    }
  }

  @Override
  protected void work(WorkContext context) throws Exception
  {
    Condition newCondition = conditionPolicy.getNewCondition(condition);
    setCondition(newCondition);

    switch (newCondition)
    {
    case GREEN:
      context.nextWork(pauseGREEN);
      break;

    case YELLOW:
      context.nextWork(pauseYELLOW);
      break;

    case RED:
      handleConditionRED();
      context.nextWork(pauseRED);
      break;
    }
  }

  protected void handleConditionRED()
  {
    // TODO Implement method CacheMonitor.handleConditionRED()
    throw new UnsupportedOperationException("Not yet implemented");
  }

  /**
   * @author Eike Stepper
   */
  private final class CacheMonitorEvent extends Event implements ICacheMonitorEvent
  {
    private static final long serialVersionUID = 1L;

    private Condition oldCondition;

    private Condition newCondition;

    public CacheMonitorEvent(Condition oldCondition, Condition newCondition)
    {
      super(CacheMonitor.this);
      this.oldCondition = oldCondition;
      this.newCondition = newCondition;
    }

    public ICacheMonitor getCacheMonitor()
    {
      return CacheMonitor.this;
    }

    public Condition getOldCondition()
    {
      return oldCondition;
    }

    public Condition getNewCondition()
    {
      return newCondition;
    }
  }
}
