/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui;

import org.eclipse.net4j.util.concurrent.QueueRunner;

import org.eclipse.swt.widgets.Display;

/**
 * @author Eike Stepper
 * @since 3.1
 */
public class UIQueueRunner extends QueueRunner
{
  private Display display;

  private boolean async;

  public UIQueueRunner(Display display)
  {
    this(display, false);
  }

  public UIQueueRunner(Display display, boolean async)
  {
    this.display = display;
    this.async = async;
  }

  public Display getDisplay()
  {
    return display;
  }

  public boolean isAsync()
  {
    return async;
  }

  @Override
  protected void work(WorkContext context, Runnable runnable)
  {
    if (display.isDisposed())
    {
      context.terminate();
    }

    if (async)
    {
      display.asyncExec(runnable);
    }
    else
    {
      display.syncExec(runnable);
    }
  }
}
