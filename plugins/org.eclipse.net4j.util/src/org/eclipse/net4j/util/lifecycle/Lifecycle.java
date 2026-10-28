/*
 * Copyright (c) 2008-2012, 2019-2022, 2024, 2025 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.lifecycle.ILifecycle.DeferrableActivation;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil.ReactivationTrigger;
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

  private volatile LifecycleState lifecycleState = LifecycleState.INACTIVE;

  private ActivationAttempt activationAttempt;

  @ExcludeFromDump
  private Semaphore lifecycleSemaphore = new Semaphore(1);

  /**
   * @since 2.0
   */
  public Lifecycle()
  {
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

  /**
   * @since 3.2
   */
  @Override
  public boolean isDeferredActivation()
  {
    return false;
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
    ActivationAttempt activation;

    synchronized (this)
    {
      activation = activationAttempt;
    }

    completeDeferredActivation(activation, successful);
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

  /**
   * Cancels an activation that is still waiting for an asynchronous completion. This hook is called by deactivation while the lifecycle
   * state is {@link LifecycleState#ACTIVATING}; implementations can stop the pending operation and call
   * {@link #deferredActivate(boolean)} with {@code false} to release the activation semaphore exactly once. If activation has already
   * completed when this hook returns, normal deactivation continues. The default implementation leaves cancellation to the activation
   * owner.
   *
   * @since 3.31
   */
  protected void doCancelActivation()
  {
  }

  protected void doBeforeDeactivate() throws Exception
  {
  }

  protected void doDeactivate() throws Exception
  {
  }

  void internalActivate() throws LifecycleException
  {
    ActivationAttempt activation = null;

    try
    {
      for (;;)
      {
        if (lifecycleState == LifecycleState.INACTIVE)
        {
          if (TRACER.isEnabled())
          {
            TRACER.trace("Activating " + this); //$NON-NLS-1$
          }

          lock();

          synchronized (this)
          {
            if (lifecycleState != LifecycleState.INACTIVE)
            {
              // Someone else must have called activate() between the first "if" above and lock().
              unlock();
              return;
            }

            activation = new ActivationAttempt();
            activationAttempt = activation;
          }

          IListener[] listeners = getListeners();
          if (listeners.length != 0)
          {
            fireEvent(new LifecycleEvent(this, ILifecycleEvent.Kind.ABOUT_TO_ACTIVATE), listeners);
          }

          doBeforeActivate();
          lifecycleState = LifecycleState.ACTIVATING;

          try
          {
            doActivate();
          }
          catch (ReactivationTrigger trigger)
          {
            // Restart activation if triggered for this lifecycle.
            if (trigger.isFor(this))
            {
              synchronized (this)
              {
                if (activationAttempt != activation)
                {
                  throw trigger;
                }

                completeDeferredActivation(activation, false);
                activation = null;
              }

              continue;
            }

            // Rethrow to catch block in outer lifecycle.
            throw trigger;
          }

          if (!isDeferredActivation())
          {
            try
            {
              deferredActivate(true);
            }
            catch (ReactivationTrigger trigger)
            {
              // Restart activation if triggered for this lifecycle.
              if (trigger.isFor(this))
              {
                // Deactivate first, but do not notify listeners.
                doBeforeDeactivate();
                lifecycleState = LifecycleState.DEACTIVATING;

                doDeactivate();
                lifecycleState = LifecycleState.INACTIVE;

                unlock();
                continue;
              }

              // Rethrow to catch block in outer lifecycle.
              throw trigger;
            }
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

        break;
      }
    }
    catch (RuntimeException | Error ex)
    {
      failActivation(activation);
      throw ex;
    }
    catch (Throwable ex)
    {
      failActivation(activation);
      throw new LifecycleException(ex);
    }
  }

  Exception internalDeactivate()
  {
    boolean locked = false;

    try
    {
      if (lifecycleState == LifecycleState.ACTIVATING && isDeferredActivation())
      {
        doCancelActivation();
      }

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

  private void completeDeferredActivation(ActivationAttempt activation, boolean successful)
  {
    synchronized (this)
    {
      if (activation == null || activationAttempt != activation || successful && lifecycleState != LifecycleState.ACTIVATING)
      {
        return;
      }

      if (successful)
      {
        lifecycleState = LifecycleState.ACTIVE;
      }
      else if (lifecycleState == LifecycleState.ACTIVATING)
      {
        lifecycleState = LifecycleState.INACTIVE;
      }

      activationAttempt = null;
    }

    if (successful)
    {
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
      unlock();
    }
  }

  private void failActivation(ActivationAttempt activation)
  {
    completeDeferredActivation(activation, false);
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

  /**
   * Identifies one invocation of {@link #internalActivate()} and owns its lifecycle semaphore acquisition.
   *
   * @author Eike Stepper
   */
  private static final class ActivationAttempt
  {
  }
}
