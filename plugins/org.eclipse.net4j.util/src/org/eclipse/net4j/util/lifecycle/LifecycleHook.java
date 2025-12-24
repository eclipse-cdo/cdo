/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.lifecycle;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

/**
 * @author Eike Stepper
 * @since 3.8
 */
public class LifecycleHook<T extends ILifecycle> extends Lifecycle
{
  private final IListener delegateListener = new LifecycleEventAdapter()
  {
    @Override
    protected void notifyOtherEvent(IEvent event)
    {
      delegateEvent(delegate, event);
    }

    @Override
    protected void onAboutToActivate(ILifecycle lifecycle)
    {
      delegateAboutToActivate(delegate);
    }

    @Override
    protected void onAboutToDeactivate(ILifecycle lifecycle)
    {
      delegateAboutToDeactivate(delegate);
    }

    @Override
    protected void onActivated(ILifecycle lifecycle)
    {
      delegateActivated(delegate);
      hookDelegateIfPossible();
    }

    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      unhookDelegateIfPossible();
      delegateDeactivated(delegate);
    }
  };

  private T delegate;

  private boolean listening;

  private boolean delegateHooked;

  public LifecycleHook()
  {
  }

  protected final T getDelegate()
  {
    return delegate;
  }

  protected final void setDelegate(T delegate)
  {
    T oldDelegate = this.delegate;

    if (oldDelegate != delegate)
    {
      unhookDelegateIfPossible();
      this.delegate = delegate;
      hookDelegateIfPossible();

      delegateChanged(oldDelegate, delegate);
    }
  }

  protected void delegateChanged(T oldDelegate, T newDelegate)
  {
  }

  protected void delegateEvent(T delegate, IEvent event)
  {
  }

  protected void delegateAboutToActivate(T delegate)
  {
  }

  protected void delegateActivated(T delegate)
  {
  }

  protected void delegateAboutToDeactivate(T delegate)
  {
  }

  protected void delegateDeactivated(T delegate)
  {
  }

  @Override
  protected void doActivate() throws Exception
  {
    hookDelegateIfPossible();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    unhookDelegateIfPossible();
  }

  protected void hookDelegate(T delegate)
  {
  }

  protected void unhookDelegate(T delegate)
  {
  }

  protected boolean hookInactiveDelegates()
  {
    return false;
  }

  private void hookDelegateIfPossible()
  {
    if (!listening && delegate != null)
    {
      delegate.addListener(delegateListener);
      listening = true;
    }

    if (listening && !delegateHooked && (hookInactiveDelegates() || delegate.isActive()))
    {
      hookDelegate(delegate);
      delegateHooked = true;
    }
  }

  private void unhookDelegateIfPossible()
  {
    if (delegate != null)
    {
      if (delegateHooked)
      {
        unhookDelegate(delegate);
        delegateHooked = false;
      }

      if (listening)
      {
        delegate.removeListener(delegateListener);
        listening = false;
      }
    }
  }
}
