/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.query;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.EList;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * TODO Simon: Do we really need this list?
 * 
 * @author Simon McDuff
 */
public class CDOEList<T> implements EList<T>
{
  private CDOView view;

  private List<Object> objects;

  public CDOEList(CDOView view, List<Object> objects)
  {
    this.view = view;
    this.objects = objects;
  }

  @SuppressWarnings("unchecked")
  protected T adapt(CDOID id)
  {
    if (CDOIDUtil.isNull(id))
    {
      return null;
    }

    return (T)view.getObject(id, true);
  }

  public boolean add(T o)
  {
    throw new UnsupportedOperationException();
  }

  public void add(int index, T element)
  {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("unchecked")
  public T get(int index)
  {
    Object object = this.objects.get(index);
    if (object instanceof CDOID)
    {
      object = adapt((CDOID)object);
    }

    return (T)object;
  }

  public boolean isEmpty()
  {
    return objects.isEmpty();
  }

  public Iterator<T> iterator()
  {
    return new ECDOIDIterator(this.objects.iterator());
  }

  public void move(int newPosition, T object)
  {
    throw new UnsupportedOperationException();
  }

  public T move(int newPosition, int oldPosition)
  {
    throw new UnsupportedOperationException();
  }

  public boolean addAll(Collection<? extends T> arg0)
  {
    throw new UnsupportedOperationException();
  }

  public boolean addAll(int arg0, Collection<? extends T> arg1)
  {
    throw new UnsupportedOperationException();
  }

  public void clear()
  {
    throw new UnsupportedOperationException();
  }

  public boolean contains(Object arg0)
  {
    throw new UnsupportedOperationException();
  }

  public boolean containsAll(Collection<?> arg0)
  {
    throw new UnsupportedOperationException();
  }

  public int indexOf(Object arg0)
  {
    throw new UnsupportedOperationException();
  }

  public int lastIndexOf(Object arg0)
  {
    throw new UnsupportedOperationException();
  }

  public ListIterator<T> listIterator()
  {
    throw new UnsupportedOperationException();
  }

  public ListIterator<T> listIterator(int arg0)
  {
    throw new UnsupportedOperationException();
  }

  public boolean remove(Object arg0)
  {
    throw new UnsupportedOperationException();
  }

  public T remove(int arg0)
  {
    throw new UnsupportedOperationException();
  }

  public boolean removeAll(Collection<?> arg0)
  {
    throw new UnsupportedOperationException();
  }

  public boolean retainAll(Collection<?> arg0)
  {
    throw new UnsupportedOperationException();
  }

  public T set(int arg0, T arg1)
  {
    throw new UnsupportedOperationException();
  }

  public int size()
  {
    return objects.size();
  }

  public List<T> subList(int arg0, int arg1)
  {
    throw new UnsupportedOperationException();
  }

  public Object[] toArray()
  {
    Object array[] = new Object[size()];
    return toArray(array);
  }

  @SuppressWarnings("unchecked")
  public <E> E[] toArray(E[] input)
  {
    int size = size();
    if (input.length < size)
    {
      input = (E[])Array.newInstance(input.getClass(), size);
    }

    // TODO It will be more efficient to load all objects at once.
    for (int i = 0; i < size; i++)
    {
      input[i] = (E)get(i);
    }

    if (input.length > size)
    {
      input[size] = null;
    }

    return input;
  }

  /**
   * @author Simon McDuff
   */
  private class ECDOIDIterator implements Iterator<T>
  {
    private Iterator<Object> iterator;

    public ECDOIDIterator(Iterator<Object> itr)
    {
      this.iterator = itr;
    }

    public boolean hasNext()
    {
      return iterator.hasNext();
    }

    @SuppressWarnings("unchecked")
    public T next()
    {
      Object object = iterator.next();
      if (object instanceof CDOID)
      {
        object = adapt((CDOID)object);
      }

      return (T)object;
    }

    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
}
