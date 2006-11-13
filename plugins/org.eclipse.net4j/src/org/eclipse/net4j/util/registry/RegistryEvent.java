package org.eclipse.net4j.util.registry;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class RegistryEvent<K, V> implements IRegistryEvent<K, V>
{
  private IRegistry<K, V> registry;

  private List<IRegistryDelta<K, V>> deltas;

  public RegistryEvent(IRegistry<K, V> registry, List<IRegistryDelta<K, V>> deltas)
  {
    this.registry = registry;
    this.deltas = deltas;
  }

  public IRegistry<K, V> getRegistry()
  {
    return registry;
  }

  public IRegistryDelta<K, V>[] getDeltas()
  {
    return deltas.toArray(new IRegistryDelta[deltas.size()]);
  }
}