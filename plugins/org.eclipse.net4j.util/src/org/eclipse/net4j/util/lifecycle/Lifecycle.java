/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.lifecycle;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Eike Stepper
 */
public class Lifecycle extends Notifier implements ILifecycle.Introspection
{
  public static boolean USE_LABEL = true;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_LIFECYCLE, Lifecycle.class);

  private static final ContextTracer DUMPER = new ContextTracer(OM.DEBUG_LIFECYCLE_DUMP, Lifecycle.class);

  private static final boolean TRACE_IGNORING = false;

  private ILifecycleState lifecycleState = ILifecycleState.INACTIVE;

  protected Lifecycle()
  {
  }

  public final void activate() throws LifecycleException
  {
    if (lifecycleState == ILifecycleState.INACTIVE)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Activating " + this);//$NON-NLS-1$
      }

      lifecycleState = ILifecycleState.ACTIVATING;
      fireEvent(new LifecycleEvent(this, ILifecycleEvent.Kind.ABOUT_TO_ACTIVATE));

      try
      {
        doBeforeActivate();
        doActivate();
      }
      catch (RuntimeException ex)
      {
        throw ex;
      }
      catch (Exception ex)
      {
        throw new LifecycleException(ex);
      }

      if (!isDeferredActivation())
      {
        deferredActivate();
      }
    }
    else
    {
      if (TRACE_IGNORING && TRACER.isEnabled())
      {
        TRACER.format("Ignoring activation in state {0} for {1}", lifecycleState, this);//$NON-NLS-1$
      }
    }
  }

  public final Exception deactivate()
  {
    if (lifecycleState == ILifecycleState.ACTIVE || lifecycleState == ILifecycleState.ACTIVATING)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Deactivating " + this);//$NON-NLS-1$
      }

      try
      {
        lifecycleState = ILifecycleState.DEACTIVATING;
        doBeforeDeactivate();
        fireEvent(new LifecycleEvent(this, ILifecycleEvent.Kind.ABOUT_TO_DEACTIVATE));

        if (!isDeferredDeactivation())
        {
          deferredDeactivate();
        }
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
        return ex;
      }
    }
    else
    {
      if (TRACE_IGNORING && TRACER.isEnabled())
      {
        TRACER.format("Ignoring deactivation in state {0} for {1}", lifecycleState, this);//$NON-NLS-1$
      }
    }

    return null;
  }

  public final ILifecycleState getLifecycleState()
  {
    return lifecycleState;
  }

  public final boolean isActive()
  {
    return lifecycleState == ILifecycleState.ACTIVE;
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
    if (lifecycleState != ILifecycleState.ACTIVE)
    {
      throw new IllegalStateException("Not active: " + this);
    }
  }

  protected final void checkInactive()
  {
    if (lifecycleState != ILifecycleState.INACTIVE)
    {
      throw new IllegalStateException("Not inactive: " + this);
    }
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

  protected final void deferredActivate()
  {
    lifecycleState = ILifecycleState.ACTIVE;
    fireEvent(new LifecycleEvent(this, ILifecycleEvent.Kind.ACTIVATED));
    dump();
  }

  protected final void deferredDeactivate() throws Exception
  {
    doDeactivate();
    lifecycleState = ILifecycleState.INACTIVE;
    fireEvent(new LifecycleEvent(this, ILifecycleEvent.Kind.DEACTIVATED));
  }

  protected boolean isDeferredActivation()
  {
    return false;
  }

  protected boolean isDeferredDeactivation()
  {
    return false;
  }

  protected void doBeforeActivate() throws Exception
  {
  }

  protected void doActivate() throws Exception
  {
  }

  protected void doBeforeDeactivate() throws Exception
  {
  }

  protected void doDeactivate() throws Exception
  {
  }
}
