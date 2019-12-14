/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Ed Merks - initial API and implementation
 */
package org.eclipse.net4j.util.ref;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * @author Ed Merks
 * @since 3.3
 */
public class Interner<E>
{
  private static final int[] PRIME_CAPACITIES = new int[] { 17, 37, 67, 131, 257, 521, 1031, 2053, 4099, 8209, 16411, 32771, 65537, 131101, 262147, 524309,
      1048583, 2097169, 4194319, 8388617, 16777259, 33554467, 67108879, 134217757, 268435459, 536870923, 1073741827, 2147483629 };

  private int size;

  private int capacityIndex;

  private Entry<E>[] entries;

  private ReferenceQueue<E> queue = new ReferenceQueue<>();

  public Interner()
  {
  }

  public Interner(int minimumCapacity)
  {
    grow(minimumCapacity);
  }

  /**
   * Ensures that the set has at least the specifies capacity.
   * Higher capacity ensures fewer collisions hence faster lookup.
   * Does nothing if the specified capacity is smaller than the current capacity.
   */
  public void grow(int minimumCapacity)
  {
    int currentCapacity = PRIME_CAPACITIES[capacityIndex];
    if (currentCapacity < minimumCapacity)
    {
      for (int i = 0, length = PRIME_CAPACITIES.length; i < length; ++i)
      {
        int capacity = PRIME_CAPACITIES[i];
        if (capacity > minimumCapacity)
        {
          capacityIndex = i;
          rehash(newEntries(capacity));
          break;
        }
      }
    }
  }

  public E intern(E object)
  {
    cleanup();

    int hashCode = hashCode(object);
    if (entries != null)
    {
      int index = index(hashCode);
      for (Entry<E> entry = entries[index]; entry != null; entry = entry.next)
      {
        if (hashCode == entry.hashCode)
        {
          E otherObject = entry.get();
          if (equals(object, otherObject))
          {
            return otherObject;
          }
        }
      }
    }

    Entry<E> entry = createEntry(object, hashCode);
    addEntry(entry);
    return object;
  }

  /**
   * Gets the first entry in the table with exactly the given hash code.
   * It's very useful to call {@link Entry#getNextEntry()} to yield the next entry with exactly this same hash code.
   */
  protected Entry<E> getEntry(int hashCode)
  {
    cleanup();

    if (entries != null)
    {
      int index = index(hashCode);
      for (Entry<E> entry = entries[index]; entry != null; entry = entry.next)
      {
        if (hashCode == entry.hashCode)
        {
          return entry;
        }
      }
    }

    return null;
  }

  protected int hashCode(E object)
  {
    return object.hashCode();
  }

  /**
   * Returns true if the two objects are to be considered equal.
   * The first object will always be the one passed in as an argument to {@link #intern(Object)}.
   */
  protected boolean equals(E object, E otherObject)
  {
    return object == otherObject || object.equals(otherObject);
  }

  protected Entry<E> createEntry(E object, int hashCode)
  {
    return new Entry<>(object, hashCode, queue);
  }

  /**
   * Adds a new entry, {@link #ensureCapacity() ensures} the capacity is sufficient and increases the {@link #size}.
   */
  protected void addEntry(Entry<E> entry)
  {
    ensureCapacity();
    ++size;
    putEntry(entry);
  }

  private Entry<E>[] newEntries(int capacity)
  {
    @SuppressWarnings("unchecked")
    Entry<E>[] newEntries = new Entry[capacity];
    return newEntries;
  }

  private void ensureCapacity()
  {
    int capacity = PRIME_CAPACITIES[capacityIndex];
    if (entries == null)
    {
      entries = newEntries(capacity);
    }
    else if (size > (capacity >> 2) * 3)
    {
      // The current size is more the 3/4 of the current capacity
      ++capacityIndex;
      rehash(newEntries(PRIME_CAPACITIES[capacityIndex]));
    }
  }

  private void rehash(Entry<E>[] newEntries)
  {
    Entry<E>[] oldEntries = entries;
    entries = newEntries;
    if (oldEntries != null)
    {
      for (int i = 0, length = oldEntries.length; i < length; ++i)
      {
        Entry<E> entry = oldEntries[i];
        while (entry != null)
        {
          Entry<E> nextEntry = entry.next;
          putEntry(entry);
          entry = nextEntry;
        }
      }
    }
  }

  private int index(int hashCode)
  {
    return (hashCode & 0x7FFFFFFF) % entries.length;
  }

  private void putEntry(Entry<E> entry)
  {
    int index = index(entry.hashCode);
    Entry<E> otherEntry = entries[index];
    entries[index] = entry;
    entry.next = otherEntry;
  }

  private void cleanup()
  {
    for (;;)
    {
      @SuppressWarnings("unchecked")
      Entry<E> entry = (Entry<E>)queue.poll();
      if (entry == null)
      {
        return;
      }

      removeEntry(entry);
    }
  }

  private void removeEntry(Entry<E> entry)
  {
    int index = index(entry.hashCode);
    Entry<E> otherEntry = entries[index];
    --size;
    if (entry == otherEntry)
    {
      entries[index] = entry.next;
    }
    else
    {
      for (Entry<E> nextOtherEntry = otherEntry.next; nextOtherEntry != null; otherEntry = nextOtherEntry, nextOtherEntry = nextOtherEntry.next)
      {
        if (nextOtherEntry == entry)
        {
          otherEntry.next = entry.next;
          break;
        }
      }
    }

    // Make life easier for the garbage collector.
    entry.next = null;
    entry.clear();
  }

  /**
   * A weak reference holder that caches the hash code of the referent and is chained in the {@link Interner#entries} to handle collisions.
   *
   * @author Ed Merks
   */
  protected static class Entry<E> extends WeakReference<E>
  {
    public final int hashCode;

    public Entry<E> next;

    public Entry(E object, int hashCode, ReferenceQueue<? super E> queue)
    {
      super(object, queue);
      this.hashCode = hashCode;
    }

    public Entry<E> getNextEntry()
    {
      for (Entry<E> entry = next; entry != null; entry = entry.next)
      {
        if (entry.hashCode == hashCode)
        {
          return entry;
        }
      }

      return null;
    }

    @Override
    public String toString()
    {
      E object = get();
      return object == null ? "null" : object.toString();
    }
  }
}
