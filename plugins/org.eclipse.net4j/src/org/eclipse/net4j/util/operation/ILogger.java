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
package org.eclipse.net4j.util.operation;

/**
 * @author Eike Stepper
 */
public interface ILogger
{
  public String getLoggerName();

  public void log(Level level, Object plastic);

  public void error(Object plastic);

  public void warn(Object plastic);

  public void info(Object plastic);

  public void debug(Object plastic);

  public void dispose();

  /**
   * @author Eike Stepper
   */
  public enum Level
  {
    ERROR, WARN, INFO, DEBUG
  }
}
