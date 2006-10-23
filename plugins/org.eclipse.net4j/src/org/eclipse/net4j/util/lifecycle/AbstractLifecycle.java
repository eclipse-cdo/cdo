package org.eclipse.net4j.util.lifecycle;

import org.eclipse.net4j.util.ReflectUtil;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Eike Stepper
 */
public abstract class AbstractLifecycle implements Lifecycle, LifecycleNotifier
{
  public static boolean DUMP_ON_ACTIVATE = false;

  public static boolean USE_LABEL = true;

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
      System.out.println(toString() + ": Activating");
      onAccessBeforeActivate();
      onActivate();
      active = true;

      if (DUMP_ON_ACTIVATE)
      {
        ReflectUtil.dump(this, toString() + ": DUMP ");
      }

      fireLifecycleActivated();
    }
  }

  public final synchronized Exception deactivate()
  {
    if (active)
    {
      System.out.println(toString() + ": Deactivating");
      fireLifecycleDeactivating();

      try
      {
        onDeactivate();
      }
      catch (Exception ex)
      {
        ex.printStackTrace();
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
        ex.printStackTrace();
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
        ex.printStackTrace();
      }
    }
  }

  protected void onAccessBeforeActivate() throws Exception
  {
  }

  protected void onActivate() throws Exception
  {
  }

  protected void onDeactivate() throws Exception
  {
  }
}
