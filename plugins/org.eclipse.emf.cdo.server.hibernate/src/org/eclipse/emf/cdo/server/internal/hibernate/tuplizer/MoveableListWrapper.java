/***************************************************************************
 * Copyright (c) 2008 - 2008 Martin Taal and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Taal - initial api
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.net4j.util.collection.MoveableList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Wraps a list and adds the move operation. The delegate should normally be a persistentlist.
 * 
 * @author Martin Taal
 */
public class MoveableListWrapper implements MoveableList<Object>
{
  private List<Object> delegate;

  public MoveableListWrapper(List<Object> delegate)
  {
    this.delegate = delegate;
  }

  /**
   * @return the delegate
   */
  public List<Object> getDelegate()
  {
    return delegate;
  }

  public Object move(int targetIndex, int sourceIndex)
  {
    int size = size();
    if (sourceIndex >= size)
    {
      throw new IndexOutOfBoundsException("sourceIndex=" + sourceIndex + ", size=" + size);
    }

    if (targetIndex >= size)
    {
      throw new IndexOutOfBoundsException("targetIndex=" + targetIndex + ", size=" + size);
    }

    Object object = get(sourceIndex);
    if (targetIndex == sourceIndex)
    {
      return object;
    }

    if (targetIndex < sourceIndex)
    {
      moveUp1(targetIndex, sourceIndex - targetIndex);
    }
    else
    {
      moveDown1(targetIndex, targetIndex - sourceIndex);
    }

    set(targetIndex, object);
    return object;
  }

  private void moveUp1(int index, int count)
  {
    for (int i = count; i > 0; i--)
    {
      set(index + i, get(index + i - 1));
    }
  }

  private void moveDown1(int index, int count)
  {
    for (int i = count; i > 0; i--)
    {
      set(index - i, get(index - i + 1));
    }
  }

  public void add(int index, Object element)
  {
    delegate.add(index, element);
  }

  public boolean add(Object e)
  {
    return delegate.add(e);
  }

  public boolean addAll(Collection<? extends Object> c)
  {
    return delegate.addAll(c);
  }

  public boolean addAll(int index, Collection<? extends Object> c)
  {
    return delegate.addAll(index, c);
  }

  public void clear()
  {
    delegate.clear();
  }

  public boolean contains(Object o)
  {
    return delegate.contains(o);
  }

  public boolean containsAll(Collection<?> c)
  {
    return delegate.containsAll(c);
  }

  @Override
  public boolean equals(Object o)
  {
    return delegate.equals(o);
  }

  public Object get(int index)
  {
    return delegate.get(index);
  }

  @Override
  public int hashCode()
  {
    return delegate.hashCode();
  }

  public int indexOf(Object o)
  {
    return delegate.indexOf(o);
  }

  public boolean isEmpty()
  {
    return delegate.isEmpty();
  }

  public Iterator<Object> iterator()
  {
    return delegate.iterator();
  }

  public int lastIndexOf(Object o)
  {
    return delegate.lastIndexOf(o);
  }

  public ListIterator<Object> listIterator()
  {
    return delegate.listIterator();
  }

  public ListIterator<Object> listIterator(int index)
  {
    return delegate.listIterator(index);
  }

  public Object remove(int index)
  {
    return delegate.remove(index);
  }

  public boolean remove(Object o)
  {
    return delegate.remove(o);
  }

  public boolean removeAll(Collection<?> c)
  {
    return delegate.removeAll(c);
  }

  public boolean retainAll(Collection<?> c)
  {
    return delegate.retainAll(c);
  }

  public Object set(int index, Object element)
  {
    return delegate.set(index, element);
  }

  public int size()
  {
    return delegate.size();
  }

  public List<Object> subList(int fromIndex, int toIndex)
  {
    return delegate.subList(fromIndex, toIndex);
  }

  public Object[] toArray()
  {
    return delegate.toArray();
  }

  public <T> T[] toArray(T[] a)
  {
    return delegate.toArray(a);
  }

}
