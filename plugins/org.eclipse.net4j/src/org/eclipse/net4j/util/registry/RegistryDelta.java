package org.eclipse.net4j.util.registry;

/**
 * @author Eike Stepper
 */
public class RegistryDelta<K, V> implements IRegistryDelta<K, V>
{
  private K key;

  private V value;

  private int kind;

  public RegistryDelta(K key, V value, int kind)
  {
    this.key = key;
    this.value = value;
    this.kind = kind;
  }

  public K getKey()
  {
    return key;
  }

  public V getValue()
  {
    return value;
  }

  public V setValue(V value)
  {
    throw new UnsupportedOperationException();
  }

  public int getKind()
  {
    return kind;
  }
}