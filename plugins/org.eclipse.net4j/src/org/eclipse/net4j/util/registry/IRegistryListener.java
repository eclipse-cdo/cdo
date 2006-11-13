package org.eclipse.net4j.util.registry;

/**
 * @author Eike Stepper
 */
public interface IRegistryListener<ID, E>
{
  public void notifyRegistryEvent(IRegistryEvent<ID, E> event);
}