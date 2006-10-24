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
package org.eclipse.internal.net4j.util.operation;

import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Eike Stepper
 */
public class BundleLogger extends AbstractLogger
{
  private String bundleName;

  private ServiceTracker logTracker;

  public BundleLogger(BundleContext context)
  {
    bundleName = context.getBundle().getSymbolicName();
    logTracker = new ServiceTracker(context, LogService.class.getName(), null);
    logTracker.open();
  }

  @Override
  public void dispose()
  {
    logTracker.close();
    logTracker = null;
  }

  public String getLoggerName()
  {
    return bundleName;
  }

  public void log(Level level, Object plastic)
  {
    LogService logService = (LogService)logTracker.getService();
    if (logService != null)
    {
      if (plastic instanceof Throwable)
      {
        Throwable t = (Throwable)plastic;
        logService.log(toOSGi(level), t.getLocalizedMessage(), t);
      }
      else
      {
        logService.log(toOSGi(level), plastic.toString());
      }
    }
  }

  private static int toOSGi(Level level)
  {
    switch (level)
    {
    case ERROR:
      return LogService.LOG_ERROR;
    case WARN:
      return LogService.LOG_WARNING;
    case INFO:
      return LogService.LOG_INFO;
    case DEBUG:
      return LogService.LOG_DEBUG;
    default:
      throw new IllegalArgumentException("Illegal log level: " + level);
    }
  }
}
