package org.eclipse.net4j.util.lifecycle;

import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.om.ContextTracer;

import org.eclipse.internal.net4j.bundle.Net4j;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Eike Stepper
 */
public abstract class AbstractLifecycle implements Lifecycle, LifecycleNotifier
{
  public static boolean USE_LABEL = true;

  private static final ContextTracer TRACER = new ContextTracer(Net4j.DEBUG_LIFECYCLE,
      AbstractLifecycle.class);

  private static final ContextTracer DUMPER = new ContextTracer(Net4j.DEBUG_LIFECYCLE_DUMP,
      AbstractLifecycle.class);

  private boolean active;

  /**
   * Don't initialize lazily to circumvent synchronization!
   */
  private Queue<LifecycleListener> listeners = new ConcurrentLinkedQueue();

  protected AbstractLifecycle()
  {
  }

  public final void addLifecycleListener(LifecycleListener listener)
  {
    listeners.add(listener);
  }

  public final void removeLifecycleListener(LifecycleListener listener)
  {
    listeners.remove(listener);
  }

  public final synchronized void activate() throws Exception
  {
    if (!active)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace(toString() + ": Activating");//$NON-NLS-1$
      }

      onAboutToActivate();
      fireLifecycleAboutToActivate();
      if (DUMPER.isEnabled())
      {
        DUMPER.trace("DUMP " + ReflectUtil.toString(this)); //$NON-NLS-1$
      }

      onActivate();
      active = true;

      fireLifecycleActivated();
    }
  }

  public final synchronized Exception deactivate()
  {
    if (active)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace(toString() + ": Deactivating");//$NON-NLS-1$
      }

      fireLifecycleDeactivating();

      try
      {
        onDeactivate();
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

  protected void fireLifecycleAboutToActivate()
  {
    for (LifecycleListener listener : listeners)
    {
      try
      {
        listener.notifyLifecycleAboutToActivate(this);
      }
      catch (Exception ex)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace(ex);
        }
      }
    }
  }

  protected void fireLifecycleActivated()
  {
    for (LifecycleListener listener : listeners)
    {
      try
      {
        listener.notifyLifecycleActivated(this);
      }
      catch (Exception ex)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace(ex);
        }
      }
    }
  }

  protected void fireLifecycleDeactivating()
  {
    for (LifecycleListener listener : listeners)
    {
      try
      {
        listener.notifyLifecycleDeactivating(this);
      }
      catch (Exception ex)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace(ex);
        }
      }
    }
  }

  protected void onAboutToActivate() throws Exception
  {
  }

  protected void onActivate() throws Exception
  {
  }

  protected void onDeactivate() throws Exception
  {
  }
}
