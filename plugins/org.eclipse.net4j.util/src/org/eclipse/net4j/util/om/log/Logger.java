/*
 * Copyright (c) 2008, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.om.log;

import org.eclipse.net4j.internal.util.bundle.AbstractPlatform;
import org.eclipse.net4j.util.om.OMBundle;

/**
 * The default {@link OMLogger logger} implementation.
 *
 * @author Eike Stepper
 */
public class Logger implements OMLogger
{
  private OMBundle bundle;

  public Logger(OMBundle bundle)
  {
    this.bundle = bundle;
  }

  @Override
  public OMBundle getBundle()
  {
    return bundle;
  }

  @Override
  public void log(Level level, String msg, Throwable t)
  {
    ((AbstractPlatform)bundle.getPlatform()).log(this, level, msg, t);
  }

  @Override
  public void error(String msg, Throwable t)
  {
    log(Level.ERROR, msg, t);
  }

  @Override
  public void warn(String msg, Throwable t)
  {
    log(Level.WARN, msg, t);
  }

  @Override
  public void info(String msg, Throwable t)
  {
    log(Level.INFO, msg, t);
  }

  @Override
  public void debug(String msg, Throwable t)
  {
    log(Level.DEBUG, msg, t);
  }

  @Override
  public void log(Level level, String msg)
  {
    log(level, msg, null);
  }

  @Override
  public void error(String msg)
  {
    log(Level.ERROR, msg);
  }

  @Override
  public void warn(String msg)
  {
    log(Level.WARN, msg);
  }

  @Override
  public void info(String msg)
  {
    log(Level.INFO, msg);
  }

  @Override
  public void debug(String msg)
  {
    log(Level.DEBUG, msg);
  }

  @Override
  public void log(Level level, Throwable t)
  {
    if (t != null)
    {
      String msg = t.getMessage();
      if (msg == null)
      {
        msg = t.getClass().getSimpleName();
      }

      log(level, msg, t);
    }
  }

  @Override
  public void error(Throwable t)
  {
    log(Level.ERROR, t);
  }

  @Override
  public void warn(Throwable t)
  {
    log(Level.WARN, t);
  }

  @Override
  public void info(Throwable t)
  {
    log(Level.INFO, t);
  }

  @Override
  public void debug(Throwable t)
  {
    log(Level.DEBUG, t);
  }
}
