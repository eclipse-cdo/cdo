package org.eclipse.net4j.util.lifecycle;

/**
 * @author Eike Stepper
 */
public interface LifecycleNotifier extends Lifecycle.Introspection
{
  public void addLifecycleListener(LifecycleListener listener);

  public void removeLifecycleListener(LifecycleListener listener);
}
