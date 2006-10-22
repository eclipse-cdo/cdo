package org.eclipse.net4j.util.lifecycle;

/**
 * @author Eike Stepper
 */
public interface LifecycleListener
{
  public void notifyLifecycleActivated(LifecycleNotifier notifier);

  public void notifyLifecycleDeactivating(LifecycleNotifier notifier);
}