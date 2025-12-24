/*
 * Copyright (c) 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.om.OMPlatform;

/**
 * @author Eike Stepper
 * @since 3.6
 */
public abstract class RunnableWithName implements Runnable
{
  private static final boolean ENABLE_RUNNABLE_NAMES = OMPlatform.INSTANCE.isProperty("org.eclipse.net4j.util.concurrent.ENABLE_RUNNABLE_NAMES");

  @Override
  public final void run()
  {
    if (!ENABLE_RUNNABLE_NAMES)
    {
      doRun();
      return;
    }

    Thread thread = null;
    String oldName = null;

    String name = getName();
    if (!StringUtil.isEmpty(name))
    {
      thread = Thread.currentThread();
      oldName = thread.getName();
      if (name.equals(oldName))
      {
        thread = null;
        oldName = null;
      }
      else
      {
        ConcurrencyUtil.setThreadName(thread, name);
      }
    }

    try
    {
      doRun();
    }
    finally
    {
      if (thread != null)
      {
        ConcurrencyUtil.setThreadName(thread, oldName);
      }
    }
  }

  public abstract String getName();

  protected abstract void doRun();
}
