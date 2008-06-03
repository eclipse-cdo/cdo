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
package org.eclipse.net4j.util.om.trace;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Eike Stepper
 */
public class PerfTracer extends ContextTracer
{
  public static final long NOT_STARTED = 0L;

  private ConcurrentMap<Object, Long> timers = new ConcurrentHashMap<Object, Long>();

  public PerfTracer(OMTracer delegate, Class<?> context)
  {
    super(delegate, context);
  }

  public void start(Object object)
  {
    if (isEnabled())
    {
      timers.put(object, System.currentTimeMillis());
    }
  }

  public long stop(Object object)
  {
    long duration = NOT_STARTED;
    if (isEnabled())
    {
      Long timer = timers.remove(object);
      if (timer != null)
      {
        duration = System.currentTimeMillis() - timer;
        format("{0} = {1} millis", object, duration);
      }
    }

    return duration;
  }

  public long getDuration(Object object)
  {
    long duration = NOT_STARTED;
    if (isEnabled())
    {
      Long timer = timers.get(object);
      if (timer != null)
      {
        duration = System.currentTimeMillis() - timer;
      }
    }

    return duration;
  }
}
