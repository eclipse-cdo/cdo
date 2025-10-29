/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.event;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.om.log.OMLogger.Level;

/**
 * @author Eike Stepper
 * @since 3.29
 */
public class LogListener implements IListener
{
  private final Level level;

  public LogListener()
  {
    this(Level.INFO);
  }

  public LogListener(Level level)
  {
    this.level = level;
  }

  public Level getLevel()
  {
    return level;
  }

  @Override
  public void notifyEvent(IEvent event)
  {
    if (event != null)
    {
      OM.LOG.log(level, event.toString());
    }
  }
}
