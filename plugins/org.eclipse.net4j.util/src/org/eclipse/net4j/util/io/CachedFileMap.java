package org.eclipse.net4j.util.io;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class CachedFileMap<K extends Comparable<K>, V> extends SortedFileMap<K, V>
{
  private Map<K, V> cache = new HashMap<K, V>();

  public CachedFileMap(File file, String mode)
  {
    super(file, mode);
  }

  @Override
  public V get(K key)
  {
    V value = cache.get(key);
    if (value == null)
    {
      value = super.get(key);
      cache.put(key, value);
    }

    return value;
  }

  @Override
  public V put(K key, V value)
  {
    cache.put(key, value);
    return super.put(key, value);
  }
}
