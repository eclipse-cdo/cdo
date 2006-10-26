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
package org.eclipse.net4j.util.om;

import org.eclipse.net4j.util.om.OMLogger.Level;

import org.eclipse.internal.net4j.bundle.OSGiBundle;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

/**
 * @author Eike Stepper
 */
public class OSGiLoggingBridge implements OMLogHandler
{
  public static final OSGiLoggingBridge INSTANCE = new OSGiLoggingBridge();

  private OSGiLoggingBridge()
  {
  }

  public void logged(OMLogger logger, Level level, String msg, Throwable t)
  {
    try
    {
      BundleContext bundleContext = ((OSGiBundle)logger.getBundle()).getBundleContext();
      logged(bundleContext, level, msg, t);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
    }
  }

  public void logged(BundleContext bundleContext, Level level, String msg, Throwable t)
      throws Exception
  {
    LogService logService = getLogService(bundleContext);
    logService.log(toOSGi(level), msg, t);
  }

  protected LogService getLogService(BundleContext bundleContext)
  {
    try
    {
      ServiceReference ref = bundleContext.getServiceReference(LogService.class.getName());
      LogService logService = (LogService)bundleContext.getService(ref);
      return logService;
    }
    catch (RuntimeException ex)
    {
      throw new IllegalStateException("Log service not found", ex);
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
