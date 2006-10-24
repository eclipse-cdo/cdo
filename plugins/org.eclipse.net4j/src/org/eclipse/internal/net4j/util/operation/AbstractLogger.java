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

import org.eclipse.net4j.util.operation.ILogger;

/**
 * @author Eike Stepper
 */
public abstract class AbstractLogger implements ILogger
{
  public AbstractLogger()
  {
  }

  public void error(Object plastic)
  {
    log(Level.ERROR, plastic);
  }

  public void warn(Object plastic)
  {
    log(Level.WARN, plastic);
  }

  public void info(Object plastic)
  {
    log(Level.INFO, plastic);
  }

  public void debug(Object plastic)
  {
    log(Level.DEBUG, plastic);
  }

  public void dispose()
  {
  }
}
