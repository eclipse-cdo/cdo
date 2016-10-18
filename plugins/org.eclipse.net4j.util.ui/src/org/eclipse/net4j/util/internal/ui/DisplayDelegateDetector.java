/*
 * Copyright (c) 2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

  public boolean isDelegate(Thread thread, Thread owner)
  {
    if (owner.getState() == State.WAITING)
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
  public static abstract class Factory extends org.eclipse.net4j.util.concurrent.DelegableReentrantLock.DelegateDetector.Factory
  {
    public static final String TYPE = "display";

    public Factory()
    {
      super(TYPE);
    }

    @Override
    public DelegateDetector create(String description) throws ProductCreationException
    {
      return new DisplayDelegateDetector();
    }
  }
}
