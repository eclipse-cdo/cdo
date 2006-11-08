package org.eclipse.net4j.util.registry;


/**
 * @author Eike Stepper
 */
public interface IRegistryListener<ID, E extends IRegistryElement<ID>>
{
  public void notifyRegistryEvent(IRegistry<ID, E> registry, IRegistryListener.EventType eventType, E element);

  /**
   * @author Eike Stepper
   */
  public enum EventType
  {
    REGISTERED, DEREGISTERING, RESOLVED
  }
}