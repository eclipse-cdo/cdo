/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j.util.lifecycle;

import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.internal.net4j.bundle.Net4j;
import org.eclipse.internal.net4j.util.event.Notifier;

/**
 * @author Eike Stepper
 */
public class Lifecycle extends Notifier implements ILifecycle
{
  public static boolean USE_LABEL = true;

  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_LIFECYCLE, Lifecycle.class);

  private static final ContextTracer DUMPER = new ContextTracer(Net4j.DEBUG_LIFECYCLE_DUMP, Lifecycle.class);

  private boolean active;

  protected Lifecycle()
  {
  }

  public final synchronized void activate() throws Exception
  {
    if (!active)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Activating " + this);//$NON-NLS-1$
      }

      doBeforeActivate();
      fireEvent(new LifecycleEvent(this, ILifecycleEvent.Kind.ABOUT_TO_ACTIVATE));
      dump();

      doActivate();
      active = true;
      fireEvent(new LifecycleEvent(this, ILifecycleEvent.Kind.ACTIVATED));
    }
  }

  public final synchronized Exception deactivate()
  {
    if (active)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Deactivating " + this);//$NON-NLS-1$
      }

      try
      {
        doBeforeDeactivate();
        fireEvent(new LifecycleEvent(this, ILifecycleEvent.Kind.ABOUT_TO_DEACTIVATE));
        doDeactivate();
        fireEvent(new LifecycleEvent(this, ILifecycleEvent.Kind.DEACTIVATED));
      }
      catch (Exception ex)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace(ex);
        }

        return ex;
      }
      finally
      {
        active = false;
      }
    }

    return null;
  }

  public final boolean isActive()
  {
    return active;
  }

  public void dump()
  {
    if (DUMPER.isEnabled())
    {
      DUMPER.trace("DUMP" + ReflectUtil.toString(this)); //$NON-NLS-1$
    }
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

  protected void checkActive()
  {
    if (!active)
    {
      throw new IllegalStateException("Not active: " + this);
    }
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
