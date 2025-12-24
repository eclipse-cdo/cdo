/*
 * Copyright (c) 2016, 2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.internal.ui;

import org.eclipse.net4j.util.concurrent.DelegableReentrantLock.DelegateDetector;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.swt.widgets.Display;

import java.lang.Thread.State;

/**
 * @author Eike Stepper
 */
public class DisplayDelegateDetector implements DelegateDetector
{
  public DisplayDelegateDetector()
  {
  }

  @Override
  public boolean isDelegate(Thread thread, Thread owner)
  {
    if (owner != null && owner.getState() == State.WAITING)
    {
      Display display = UIUtil.getDisplay();
      Thread displayThread = display.getThread();

      if (thread == displayThread && display.getSyncThread() == owner)
      {
        return true;
      }
    }

    return false;
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends org.eclipse.net4j.util.concurrent.DelegableReentrantLock.DelegateDetector.Factory
  {
    public static final String TYPE = "display";

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public DisplayDelegateDetector create(String description) throws ProductCreationException
    {
      return new DisplayDelegateDetector();
    }
  }
}
