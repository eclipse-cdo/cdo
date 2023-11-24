/*
 * Copyright (c) 2009-2012, 2019, 2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleState;

import java.util.Timer;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class TimerLifecycle extends Timer implements ILifecycle
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.timers"; //$NON-NLS-1$

  private Lifecycle delegate = new Lifecycle()
  {
    @Override
    protected void doDeactivate() throws Exception
    {
      superCancel();
    }
  };

  public TimerLifecycle()
  {
    activate();
  }

  public TimerLifecycle(boolean isDaemon)
  {
    super(isDaemon);
    activate();
  }

  public TimerLifecycle(String name)
  {
    super(name);
    activate();
  }

  public TimerLifecycle(String name, boolean isDaemon)
  {
    super(name, isDaemon);
    activate();
  }

  /**
   * @since 3.0
   */
  @Override
  public final LifecycleState getLifecycleState()
  {
    return delegate.getLifecycleState();
  }

  @Override
  public final boolean isActive()
  {
    return delegate.isActive();
  }

  @Override
  public void addListener(IListener listener)
  {
    delegate.addListener(listener);
  }

  @Override
  public void removeListener(IListener listener)
  {
    delegate.removeListener(listener);
  }

  @Override
  public IListener[] getListeners()
  {
    return delegate.getListeners();
  }

  @Override
  public boolean hasListeners()
  {
    return delegate.hasListeners();
  }

  @Override
  public final void activate() throws LifecycleException
  {
    delegate.activate();
  }

  @Override
  public final Exception deactivate()
  {
    return delegate.deactivate();
  }

  @Override
  public String toString()
  {
    return "Timer";
  }

  @Override
  public void cancel()
  {
    // Do nothing. Call deactivate() instead.
  }

  private void superCancel()
  {
    super.cancel();
  }

  /**
   * @author Eike Stepper
   */
  public static class DaemonFactory extends Factory
  {
    public static final String TYPE = "daemon";

    public DaemonFactory()
    {
      super(PRODUCT_GROUP, TYPE);
    }

    @Override
    public Object create(String name) throws ProductCreationException
    {
      if (name == null)
      {
        return new TimerLifecycle(true);
      }

      return new TimerLifecycle(name, true);
    }

    public static TimerLifecycle getTimer(IManagedContainer container, String name)
    {
      return (TimerLifecycle)container.getElement(PRODUCT_GROUP, TYPE, name);
    }
  }
}
