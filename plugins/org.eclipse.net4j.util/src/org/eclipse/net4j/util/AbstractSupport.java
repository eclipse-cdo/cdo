/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

import org.eclipse.net4j.internal.util.bundle.OM;

/**
 * @author Eike Stepper
 * @since 3.22
 */
public abstract class AbstractSupport
{
  private Boolean available;

  protected AbstractSupport()
  {
  }

  public boolean isAvailable()
  {
    if (available == null)
    {
      try
      {
        available = determineAvailability();
      }
      catch (Throwable ex)
      {
        available = false;
      }
    }

    return available;
  }

  protected abstract boolean determineAvailability() throws Throwable;

  /**
   * @author Eike Stepper
   */
  public static class ClassAvailability extends AbstractSupport
  {
    private final String pluginID;

    private final String className;

    public ClassAvailability(String pluginID, String className)
    {
      this.pluginID = pluginID;
      this.className = className;
    }

    @Override
    protected boolean determineAvailability() throws Throwable
    {
      return OM.BUNDLE.loadClass(pluginID, className) != null;
    }
  }
}
