/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.om.log;

import org.eclipse.net4j.util.lifecycle.Singleton;
import org.eclipse.net4j.util.om.OMLogHandler;
import org.eclipse.net4j.util.om.OMLogger;
import org.eclipse.net4j.util.om.OMLogger.Level;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.internal.net4j.bundle.Net4j;
import org.eclipse.internal.net4j.bundle.OSGiBundle;

/**
 * @author Eike Stepper
 */
public class EclipseLoggingBridge implements OMLogHandler
{
  @Singleton
  public static final EclipseLoggingBridge INSTANCE = new EclipseLoggingBridge();

  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_OM, EclipseLoggingBridge.class);

  protected EclipseLoggingBridge()
  {
  }

  public void logged(OMLogger logger, Level level, String msg, Throwable t)
  {
    try
    {
      OSGiBundle bundle = ((OSGiBundle)logger.getBundle());
      ILog log = Platform.getLog(bundle.getBundleContext().getBundle());
      log.log(new Status(toEclipse(level), bundle.getBundleID(), IStatus.OK, msg, t));
    }
    catch (Exception ex)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace(ex);
      }
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
