/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;

/**
 * A {@link OMLogHandler log handler} that appends log events to the Eclipse {@link ILog error log}.
 *
 * @author Eike Stepper
 */
public class EclipseLoggingBridge implements OMLogHandler
{
  /**
   * @Singleton
   */
  public static final EclipseLoggingBridge INSTANCE = new EclipseLoggingBridge();

  protected EclipseLoggingBridge()
  {
  }

  @Override
  public void logged(OMLogger logger, Level level, String msg, Throwable t)
  {
    try
    {
      OSGiBundle bundle = (OSGiBundle)logger.getBundle();
      ILog log = Platform.getLog(bundle.getBundleContext().getBundle());
      log.log(new Status(toEclipse(level), bundle.getBundleID(), IStatus.OK, msg, t));
    }
    catch (RuntimeException ignore)
    {
    }
  }

  public static int toEclipse(Level level)
  {
    switch (level)
    {
    case ERROR:
      return IStatus.ERROR;
    case WARN:
      return IStatus.WARNING;
    case INFO:
      return IStatus.INFO;
    case DEBUG:
      return IStatus.OK;
    default:
      throw new IllegalArgumentException("Illegal log level: " + level); //$NON-NLS-1$
    }
  }
}
