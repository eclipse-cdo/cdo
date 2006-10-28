/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.bundle;

import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMLogHandler;
import org.eclipse.net4j.util.om.OMLogger;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.OMTraceHandler;
import org.eclipse.net4j.util.om.OMTracer;
import org.eclipse.net4j.util.om.OMLogger.Level;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Eike Stepper
 */
public abstract class AbstractOMPlatform implements OMPlatform
{
  static Object systemContext;

  private static ContextTracer __TRACER__;

  private Map<String, AbstractOMBundle> bundles = new ConcurrentHashMap(0);

  private Queue<OMLogHandler> logHandlers = new ConcurrentLinkedQueue();

  private Queue<OMTraceHandler> traceHandlers = new ConcurrentLinkedQueue();

  private boolean debugging;

  protected AbstractOMPlatform()
  {
    debugging = Boolean.parseBoolean(System.getProperty("debug", "false")); //$NON-NLS-1$ //$NON-NLS-2$
  }

  public synchronized OMBundle bundle(String bundleID, Class accessor)
  {
    OMBundle bundle = bundles.get(bundleID);
    if (bundle == null)
    {
      bundle = createBundle(bundleID, accessor);
    }

    return bundle;
  }

  public void addLogHandler(OMLogHandler logHandler)
  {
    if (!logHandlers.contains(logHandler))
    {
      logHandlers.add(logHandler);
    }
  }

  public void removeLogHandler(OMLogHandler logHandler)
  {
    logHandlers.remove(logHandler);
  }

  public void addTraceHandler(OMTraceHandler traceHandler)
  {
    if (!traceHandlers.contains(traceHandler))
    {
      traceHandlers.add(traceHandler);
    }
  }

  public void removeTraceHandler(OMTraceHandler traceHandler)
  {
    traceHandlers.remove(traceHandler);
  }

  public boolean isDebugging()
  {
    return debugging;
  }

  public void setDebugging(boolean debugging)
  {
    this.debugging = debugging;
  }

  protected void log(OMLogger logger, Level level, String msg, Throwable t)
  {
    for (OMLogHandler logHandler : logHandlers)
    {
      try
      {
        logHandler.logged(logger, level, msg, t);
      }
      catch (Exception ex)
      {
        if (TRACER().isEnabled())
        {
          TRACER().trace(this, ex);
        }
      }
    }
  }

  protected void trace(OMTracer tracer, Class context, Object instance, String msg, Throwable t)
  {
    for (OMTraceHandler traceHandler : traceHandlers)
    {
      try
      {
        traceHandler.traced(tracer, context, instance, msg, t);
      }
      catch (Exception ex)
      {
        if (TRACER().isEnabled())
        {
          TRACER().trace(this, ex);
        }
      }
    }
  }

  protected abstract OMBundle createBundle(String bundleID, Class accessor);

  protected abstract String getDebugOption(String bundleID, String option);

  protected abstract void setDebugOption(String bundleID, String option, String value);

  /**
   * TODO Make configurable via system property
   */
  public static synchronized OMPlatform createPlatform()
  {
    try
    {
      if (systemContext != null)
      {
        return new OSGiPlatform(systemContext);
      }

      return new LegacyPlatform();
    }
    catch (Exception ex)
    {
      if (TRACER().isEnabled())
      {
        TRACER().trace(ex);
      }
    }

    return null;
  }

  private static ContextTracer TRACER()
  {
    if (__TRACER__ == null)
    {
      __TRACER__ = new ContextTracer(Net4j.DEBUG_OM, AbstractOMPlatform.class);
    }

    return __TRACER__;
  }
}