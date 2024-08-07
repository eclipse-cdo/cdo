/*
 * Copyright (c) 2008-2012, 2019-2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.lifecycle;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.lifecycle.ILifecycle.DeferrableActivation;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.concurrent.Semaphore;

/**
 * A default implementation of an entity with a {@link ILifecycle lifecycle}.
 *
 * @author Eike Stepper
 */
public class Lifecycle extends Notifier implements ILifecycle, DeferrableActivation
{
  public static boolean USE_LABEL = true;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_LIFECYCLE, Lifecycle.class);

  private static final ContextTracer DUMPER = new ContextTracer(OM.DEBUG_LIFECYCLE_DUMP, Lifecycle.class);

  private static final boolean TRACE_IGNORING = false;

  private static final boolean LOCKING = OMPlatform.INSTANCE.isProperty("org.eclipse.net4j.util.lifecycle.Lifecycle.LOCKING", true);

  private LifecycleState lifecycleState = LifecycleState.INACTIVE;

  @ExcludeFromDump
  private Semaphore lifecycleSemaphore = new Semaphore(1);

  /**
   * @since 2.0
   */
  public Lifecycle()
  {
  }

  void internalActivate() throws LifecycleException
  {
    try
    {
      if (lifecycleState == LifecycleState.INACTIVE)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Activating " + this); //$NON-NLS-1$
        }

        lock();

        if (lifecycleState != LifecycleState.INACTIVE)
        {
          // Someone else must have called activate() between the first "if" above and lock().
          unlock();
          return;
        }

        IListener[] listeners = getListeners();
        if (listeners.length != 0)
        {
          fireEvent(new LifecycleEvent(this, ILifecycleEvent.Kind.ABOUT_TO_ACTIVATE), listeners);
        }

        doBeforeActivate();

        lifecycleState = LifecycleState.ACTIVATING;
        doActivate();

        if (!isDeferredActivation())
        {
          deferredActivate(true);
        }

        dump();
      }
      else
      {
        if (TRACE_IGNORING)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Ignoring activation in state {0} for {1}", lifecycleState, this); //$NON-NLS-1$
          }
        }
      }
    }
    catch (RuntimeException | Error ex)
    {
      deferredActivate(false);
      throw ex;
    }
    catch (Throwable ex)
    {
      deferredActivate(false);
      throw new LifecycleException(ex);
    }
  }

  Exception internalDeactivate()
  {
    boolean locked = false;

    try
    {
      if (lifecycleState == LifecycleState.ACTIVE)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Deactivating " + this); //$NON-NLS-1$
        }

        locked = lock();

        if (lifecycleState != LifecycleState.ACTIVE)
        {
          // Someone else must have called deactivate() between the first "if" above and lock().
          if (locked)
          {
            unlock();
            locked = false;
          }

          return null;
        }

        doBeforeDeactivate();

        IListener[] listeners = getListeners();
        if (listeners.length != 0)
        {
          fireEvent(new LifecycleEvent(this, ILifecycleEvent.Kind.ABOUT_TO_DEACTIVATE), listeners);
        }

        lifecycleState = LifecycleState.DEACTIVATING;
        doDeactivate();

        lifecycleState = LifecycleState.INACTIVE;

        if (locked)
        {
          unlock();
          locked = false;
        }

        // Get listeners again because they could have changed since the first call to getListeners().
        listeners = getListeners();
        if (listeners.length != 0)
        {
          fireEvent(new LifecycleEvent(this, ILifecycleEvent.Kind.DEACTIVATED), listeners);
        }

        return null;
      }

      if (TRACE_IGNORING)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Ignoring deactivation in state {0} for {1}", lifecycleState, this); //$NON-NLS-1$
        }
      }

      return null;
    }
    catch (Exception | Error ex)
    {
      lifecycleState = LifecycleState.INACTIVE;

      if (locked)
      {
        unlock();
      }

      if (ex instanceof Exception)
      {
        return (Exception)ex;
      }

      throw (Error)ex;
    }
  }

  @Override
  public final void activate() throws LifecycleException
  {
    internalActivate();
  }

  @Override
  public final Exception deactivate()
  {
    return internalDeactivate();
  }

  /**
   * @since 3.0
   */
  @Override
  public final LifecycleState getLifecycleState()
  {
    return lifecycleState;
  }

  @Override
  public final boolean isActive()
  {
    return lifecycleState == LifecycleState.ACTIVE;
  }

  @Override
  public String toString()
  {
    if (USE_LABEL)
    {
      return ReflectUtil.getLabel(this);
    }

    return super.toString();
  }

  protected final void dump()
  {
    if (DUMPER.isEnabled())
    {
      DUMPER.trace("DUMP" + ReflectUtil.toString(this)); //$NON-NLS-1$
    }
  }

  protected final void checkActive()
  {
    LifecycleUtil.checkActive(this);
  }

  protected final void checkInactive()
  {
    LifecycleUtil.checkInactive(this);
  }

  protected final void checkNull(Object handle, String msg) throws NullPointerException
  {
    CheckUtil.checkNull(handle, msg);
  }

  protected final void checkArg(boolean expr, String msg) throws IllegalArgumentException
  {
    CheckUtil.checkArg(expr, msg);
  }

  protected final void checkArg(Object handle, String handleName) throws IllegalArgumentException
  {
    CheckUtil.checkState(handle, handleName);
  }

  protected final void checkState(boolean expr, String msg) throws IllegalStateException
  {
    CheckUtil.checkState(expr, msg);
  }

  protected final void checkState(Object handle, String handleName) throws IllegalStateException
  {
    CheckUtil.checkState(handle, handleName);
  }

  /**
   * @since 2.0
   */
  protected final void deferredActivate(boolean successful)
  {
    if (successful)
    {
      lifecycleState = LifecycleState.ACTIVE;

      try
      {
        doAfterActivate();
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
        deactivate();
        return;
      }

      unlock();

      IListener[] listeners = getListeners();
      if (listeners.length != 0)
      {
        fireEvent(new LifecycleEvent(this, ILifecycleEvent.Kind.ACTIVATED), listeners);
      }
    }
    else
    {
      lifecycleState = LifecycleState.INACTIVE;
      unlock();
    }
  }

  /**
   * @since 3.2
   */
  @Override
  public boolean isDeferredActivation()
  {
    return false;
  }

  protected void doBeforeActivate() throws Exception
  {
  }

  protected void doActivate() throws Exception
  {
  }

  /**
   * @since 3.0
   */
  protected void doAfterActivate() throws Exception
  {
  }

  protected void doBeforeDeactivate() throws Exception
  {
  }

  protected void doDeactivate() throws Exception
  {
  }

  private boolean lock()
  {
    if (LOCKING)
    {
      try
      {
        lifecycleSemaphore.acquire();
        return true;
      }
      catch (Throwable ex)
      {
        //$FALL-THROUGH$
      }
    }

    return false;
  }

  private void unlock()
  {
    if (LOCKING)
    {
      try
      {
        lifecycleSemaphore.release();
      }
      catch (Throwable ex)
      {
        //$FALL-THROUGH$
      }
    }
  }
}
