/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015, 2019, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBAdapter.Registry;
import org.eclipse.net4j.db.IDBAdapterID;
import org.eclipse.net4j.internal.db.bundle.OM;
import org.eclipse.net4j.spi.db.DelegatingDBAdapter;
import org.eclipse.net4j.util.container.Container;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Internal, thread-safe registry implementation for versioned database adapters.
 *
 * @author Eike Stepper
 */
public final class DBAdapterRegistry extends Container<Map.Entry<String, IDBAdapter>> implements Registry
{
  public static final DBAdapterRegistry INSTANCE = new DBAdapterRegistry();

  /**
   * Values are either an eagerly registered adapter or a lazy descriptor.
   */
  private final Map<IDBAdapterID, IDBAdapterID> entries;

  private volatile boolean autoCommit = true;

  public DBAdapterRegistry()
  {
    entries = new HashMap<>();
  }

  public DBAdapterRegistry(int initialCapacity)
  {
    entries = new HashMap<>(initialCapacity);
  }

  public DBAdapterRegistry(int initialCapacity, float loadFactor)
  {
    entries = new HashMap<>(initialCapacity, loadFactor);
  }

  public DBAdapterRegistry(Map<? extends String, ? extends IDBAdapter> map)
  {
    entries = new HashMap<>(map.size());
    putAll(map);
  }

  @Override
  public synchronized IDBAdapterID[] getAdapterIDs()
  {
    List<IDBAdapterID> result = new ArrayList<>(entries.size());

    for (DBAdapterID key : allKeys())
    {
      result.add(entries.get(key));
    }

    return result.toArray(new IDBAdapterID[0]);
  }

  @Override
  public synchronized IDBAdapterID[] getAdapterIDs(String adapterName)
  {
    List<IDBAdapterID> result = new ArrayList<>();

    for (DBAdapterID key : allKeys())
    {
      if (key.getName().equalsIgnoreCase(adapterName))
      {
        result.add(entries.get(key));
      }
    }

    return result.toArray(new IDBAdapterID[0]);
  }

  @Override
  public synchronized IDBAdapter[] getAdapters()
  {
    List<IDBAdapter> result = new ArrayList<>(entries.size());

    for (DBAdapterID key : allKeys())
    {
      IDBAdapter adapter = materialize(key);
      if (adapter != null)
      {
        result.add(adapter);
      }
    }

    return result.toArray(new IDBAdapter[0]);
  }

  @Override
  public synchronized IDBAdapter[] getAdapters(String adapterName)
  {
    List<IDBAdapter> result = new ArrayList<>();

    for (DBAdapterID key : allKeys())
    {
      if (key.getName().equalsIgnoreCase(adapterName))
      {
        IDBAdapter adapter = materialize(key);
        if (adapter != null)
        {
          result.add(adapter);
        }
      }
    }

    return result.toArray(new IDBAdapter[0]);
  }

  @Override
  public synchronized IDBAdapter getAdapter(IDBAdapterID id)
  {
    return id == null ? null : materialize(DBAdapterID.copy(id));
  }

  @Override
  public synchronized IDBAdapter getAdapter(String adapterName, String version)
  {
    return materialize(new DBAdapterID(adapterName, version));
  }

  @Override
  public synchronized IDBAdapter getAdapter(String adapterName)
  {
    for (DBAdapterID key : allKeysDescending())
    {
      if (key.getName().equalsIgnoreCase(adapterName))
      {
        return materialize(key);
      }
    }

    return null;
  }

  @Override
  public synchronized IDBAdapter get(Object key)
  {
    if (key instanceof IDBAdapterID)
    {
      return getAdapter((IDBAdapterID)key);
    }

    return key instanceof String ? getAdapter((String)key) : null;
  }

  @Override
  public synchronized int size()
  {
    return entries.size();
  }

  @Override
  public synchronized boolean isEmpty()
  {
    return entries.isEmpty();
  }

  @Override
  public synchronized boolean containsKey(Object key)
  {
    if (key instanceof IDBAdapterID)
    {
      return entries.containsKey(DBAdapterID.copy((IDBAdapterID)key));
    }

    if (key instanceof String)
    {
      for (IDBAdapterID id : entries.keySet())
      {
        if (id.getName().equalsIgnoreCase((String)key))
        {
          return true;
        }
      }
    }

    return false;
  }

  @Override
  public synchronized boolean containsValue(Object value)
  {
    for (DBAdapterID key : allKeys())
    {
      IDBAdapter adapter = materialize(key);
      if (adapter == value || value != null && value.equals(adapter))
      {
        return true;
      }
    }

    return false;
  }

  @Override
  public synchronized Set<String> keySet()
  {
    Set<String> result = new LinkedHashSet<>();

    for (DBAdapterID id : allKeys())
    {
      result.add(id.getName());
    }

    return Collections.unmodifiableSet(result);
  }

  @Override
  public synchronized Collection<IDBAdapter> values()
  {
    List<IDBAdapter> result = new ArrayList<>();

    for (DBAdapterID key : allKeys())
    {
      IDBAdapter adapter = materialize(key);
      if (adapter != null)
      {
        result.add(adapter);
      }
    }

    return Collections.unmodifiableList(result);
  }

  @Override
  public synchronized Set<Map.Entry<String, IDBAdapter>> entrySet()
  {
    Set<Map.Entry<String, IDBAdapter>> result = new LinkedHashSet<>();

    for (DBAdapterID key : allKeys())
    {
      IDBAdapter adapter = materialize(key);
      if (adapter != null)
      {
        result.add(entry(key, adapter));
      }
    }

    return Collections.unmodifiableSet(result);
  }

  @Override
  public synchronized IDBAdapter remove(Object key)
  {
    throw new UnsupportedOperationException("DB adapter deregistration is not supported");
  }

  @Override
  public synchronized void clear()
  {
    throw new UnsupportedOperationException("DB adapter deregistration is not supported");
  }

  @Override
  public synchronized Map.Entry<String, IDBAdapter>[] getElements()
  {
    @SuppressWarnings("unchecked")
    Map.Entry<String, IDBAdapter>[] result = (Map.Entry<String, IDBAdapter>[])entrySet().toArray();
    return result;
  }

  @Override
  public boolean isAutoCommit()
  {
    return autoCommit;
  }

  @Override
  public void setAutoCommit(boolean on)
  {
    autoCommit = on;
  }

  @Override
  public void commit(boolean notifications)
  {
    // Do nothing.
  }

  @Override
  public void commit()
  {
    // Do nothing.
  }

  public synchronized IDBAdapter register(IDBAdapter adapter)
  {
    if (adapter == null)
    {
      throw new IllegalArgumentException("Adapter must not be null");
    }

    DBAdapterID id = DBAdapterID.copy(adapter);
    rejectDuplicate(id);
    entries.put(id, adapter);
    fireElementAddedEvent(entry(id, adapter));
    return adapter;
  }

  @Override
  public synchronized IDBAdapter put(String ignoredName, IDBAdapter adapter)
  {
    return register(adapter);
  }

  @Override
  public synchronized void putAll(Map<? extends String, ? extends IDBAdapter> map)
  {
    for (IDBAdapter adapter : map.values())
    {
      register(adapter);
    }
  }

  public synchronized DBAdapterDescriptor addDescriptor(DBAdapterDescriptor descriptor)
  {
    if (descriptor == null)
    {
      throw new IllegalArgumentException("Descriptor must not be null");
    }

    DBAdapterID id = DBAdapterID.copy(descriptor);
    rejectDuplicate(id);
    entries.put(id, descriptor);
    fireElementAddedEvent(entry(id, null));
    return descriptor;
  }

  /**
   * Kept for source compatibility; adapter deregistration is intentionally unsupported.
   */
  public DBAdapterDescriptor removeDescriptor(String adapterName)
  {
    throw new UnsupportedOperationException("DB adapter deregistration is not supported");
  }

  private void rejectDuplicate(DBAdapterID id)
  {
    if (entries.containsKey(id))
    {
      throw new IllegalStateException("Duplicate DB adapter registration: " + id.getName() + " " + id.getVersion());
    }
  }

  private IDBAdapter materialize(DBAdapterID id)
  {
    IDBAdapterID value = entries.get(id);
    if (value instanceof IDBAdapter)
    {
      return (IDBAdapter)value;
    }

    if (value instanceof DBAdapterDescriptor)
    {
      DBAdapterDescriptor descriptor = (DBAdapterDescriptor)value;
      IDBAdapter adapter = descriptor.createDBAdapter();
      if (adapter == null)
      {
        return null;
      }

      if (!DBAdapterID.copy(adapter).equals(id))
      {
        String name = descriptor.getName();
        String version = descriptor.getVersion();
        OM.LOG.warn("DB adapter metadata differs from descriptor: " + name + " " + version);

        adapter = new DelegatingDBAdapter(adapter, name, version);
      }

      entries.put(id, adapter);
      return adapter;
    }

    return null;
  }

  private List<DBAdapterID> allKeys()
  {
    List<DBAdapterID> result = new ArrayList<>(entries.size());

    for (IDBAdapterID key : entries.keySet())
    {
      result.add((DBAdapterID)key);
    }

    Collections.sort(result);
    return result;
  }

  private List<DBAdapterID> allKeysDescending()
  {
    List<DBAdapterID> result = allKeys();
    Collections.reverse(result);
    return result;
  }

  private static Map.Entry<String, IDBAdapter> entry(IDBAdapterID id, IDBAdapter adapter)
  {
    return new AbstractMap.SimpleImmutableEntry<>(id.getName(), adapter);
  }
}
