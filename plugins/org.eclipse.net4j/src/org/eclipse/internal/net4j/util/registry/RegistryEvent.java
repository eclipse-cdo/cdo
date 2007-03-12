package org.eclipse.internal.net4j.util.registry;

import org.eclipse.net4j.util.registry.IRegistry;
import org.eclipse.net4j.util.registry.IRegistryEvent;

import org.eclipse.internal.net4j.util.container.ContainerEvent;

import java.util.Map;

/**
 * @author Eike Stepper
 */
public class RegistryEvent<K, V> extends ContainerEvent<Map.Entry<K, V>> implements IRegistryEvent<K, V>
{
  private static final long serialVersionUID = 1L;

  public RegistryEvent(IRegistry registry)
  {
    super(registry);
  }

  public IRegistry getRegistry()
  {
    return (IRegistry)getContainer();
  }
}