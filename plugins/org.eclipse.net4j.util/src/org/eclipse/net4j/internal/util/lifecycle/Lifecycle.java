/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.lifecycle;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.internal.util.event.Notifier;
import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.ILifecycleState;

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

  public final void activate() throws Exception
  {
    if (lifecycleState == ILifecycleState.INACTIVE)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Activating " + this);//$NON-NLS-1$
      }

      lifecycleState = ILifecycleState.ACTIVATING;
      fireEvent(new LifecycleEvent(this, ILifecycleEvent.Kind.ABOUT_TO_ACTIVATE));
      doBeforeActivate();
      doActivate();

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
      finally
      {
        lifecycleState = ILifecycleState.INACTIVE;
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
    else
    {
      return super.toString();
    }
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

  protected final void deferredActivate()
  {
    lifecycleState = ILifecycleState.ACTIVE;
    fireEvent(new LifecycleEvent(this, ILifecycleEvent.Kind.ACTIVATED));
    dump();
  }

  protected final void deferredDeactivate() throws Exception
  {
    doDeactivate();
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
