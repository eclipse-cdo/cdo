/*
 * Copyright (c) 2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om.log;

import org.eclipse.net4j.internal.util.om.OSGiBundle;
import org.eclipse.net4j.util.om.log.OMLogger.Level;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogLevel;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

/**
 * A {@link OMLogHandler log handler} that appends log events to the OSGi {@link Logger log service}.
 *
 * @author Eike Stepper
 * @since 3.8
 */
public class OSGiLogHandler implements OMLogHandler
{
  // @Singleton
  public static final OSGiLogHandler INSTANCE = new OSGiLogHandler();

  protected OSGiLogHandler()
  {
  }

  @Override
  public void logged(OMLogger logger, Level level, String msg, Throwable t)
  {
    try
    {
      BundleContext bundleContext = ((OSGiBundle)logger.getBundle()).getBundleContext();
      logged(bundleContext, level, msg, t);
    }
    catch (RuntimeException ignore)
    {
    }
  }

  public void logged(BundleContext bundleContext, Level level, String msg, Throwable t)
  {
    Logger logger = getLogger(bundleContext);

    switch (level)
    {
    case ERROR:
      logger.error(msg, t);
      break;

    case WARN:
      logger.warn(msg, t);
      break;

    case INFO:
      logger.info(msg, t);
      break;

    case DEBUG:
      logger.debug(msg, t);
      break;

    default:
      throw new IllegalArgumentException("Illegal log level: " + level); //$NON-NLS-1$
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected Logger getLogger(BundleContext bundleContext)
  {
    try
    {
      ServiceReference ref = bundleContext.getServiceReference(LoggerFactory.class.getName());
      LoggerFactory loggerFactory = (LoggerFactory)bundleContext.getService(ref);

      Bundle bundle = bundleContext.getBundle();
      return loggerFactory.getLogger(bundle, bundle.getSymbolicName(), Logger.class);
    }
    catch (RuntimeException ex)
    {
      throw new IllegalStateException("Logger service not found", ex); //$NON-NLS-1$
    }
  }

  public static LogLevel toOSGi(Level level)
  {
    switch (level)
    {
    case ERROR:
      return LogLevel.ERROR;
    case WARN:
      return LogLevel.WARN;
    case INFO:
      return LogLevel.INFO;
    case DEBUG:
      return LogLevel.DEBUG;
    default:
      throw new IllegalArgumentException("Illegal log level: " + level); //$NON-NLS-1$
    }
  }
}
