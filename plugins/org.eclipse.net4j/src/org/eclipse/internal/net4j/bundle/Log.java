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

import org.eclipse.net4j.util.operation.ILogger;
import org.eclipse.net4j.util.operation.ILogger.Level;

import org.eclipse.internal.net4j.util.operation.ConsoleLogger;

/**
 * @author Eike Stepper
 */
public final class Log
{
  private static ILogger logger;

  private Log()
  {
  };

  public static void log(Level level, Object plastic)
  {
    getLogger().log(level, plastic);
  }

  public static void error(Object plastic)
  {
    getLogger().error(plastic);
  }

  public static void warn(Object plastic)
  {
    getLogger().warn(plastic);
  }

  public static void info(Object plastic)
  {
    getLogger().info(plastic);
  }

  public static void debug(Object plastic)
  {
    getLogger().debug(plastic);
  }

  public static synchronized ILogger getLogger()
  {
    if (logger == null)
    {
      logger = new ConsoleLogger(Activator.BUNDLE_ID);
    }

    return logger;
  }

  public static void setLogger(ILogger logger)
  {
    Log.logger = logger;
  }

  public static void unsetLogger(ILogger logger)
  {
    if (Log.logger == logger)
    {
      Log.logger = null;
    }
  }
}