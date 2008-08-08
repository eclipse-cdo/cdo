/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/

package org.eclipse.emf.internal.cdo.query;

import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.common.id.CDOID;

import org.eclipse.emf.common.util.EList;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * @author Simon McDuff
 */
public class CDOEList<T> implements EList<T>
{
  private List<Object> listOfObjects;

  private CDOView cdoView;

  public CDOEList(CDOView view, List<Object> list)
  {
    this.listOfObjects = list;
    this.cdoView = view;
  }

  @SuppressWarnings("unchecked")
  protected T adapt(CDOID object)
  {
    if (object.isNull()) return null;

    return (T)cdoView.getObject((CDOID)object, true);
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
    Object object = this.listOfObjects.get(index);
    if (object instanceof CDOID)
    {
      object = adapt((CDOID)object);
    }
    return (T)object;
  }

  public boolean isEmpty()
  {
    return listOfObjects.isEmpty();
  }

  public Iterator<T> iterator()
  {
    return new ECDOIDIterator(this.listOfObjects.iterator());
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
    return listOfObjects.size();
  }

  public List<T> subList(int arg0, int arg1)
  {
    throw new UnsupportedOperationException();
  }

  public Object[] toArray()
  {
    throw new UnsupportedOperationException();
  }

  @SuppressWarnings("hiding")
  public <T> T[] toArray(T[] arg0)
  {
    throw new UnsupportedOperationException();
  }

  class ECDOIDIterator implements Iterator<T>
  {
    Iterator<Object> iterator;

    ECDOIDIterator(Iterator<Object> itr)
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
