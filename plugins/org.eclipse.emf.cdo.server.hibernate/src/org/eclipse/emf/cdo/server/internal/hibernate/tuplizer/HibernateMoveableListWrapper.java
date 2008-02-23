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

import org.eclipse.emf.cdo.protocol.id.CDOID;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.server.internal.hibernate.CDOHibernateUtil;

import org.eclipse.net4j.internal.util.collection.MoveableArrayList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Wraps a moveable list so that hibernate always sees an object view while cdo always sees a cdoid view.
 * 
 * @author Martin Taal
 */
public class HibernateMoveableListWrapper implements List<Object>
{
  private MoveableArrayList<Object> delegate;

  /**
   * @return the delegate
   */
  public MoveableArrayList<Object> getDelegate()
  {
    return delegate;
  }

  /**
   * @param delegate
   *          the delegate to set
   */
  public void setDelegate(MoveableArrayList<Object> delegate)
  {
    this.delegate = delegate;
  }

  protected Object getObject(Object o)
  {
    if (o == null)
    {
      return null;
    }

    // is already resolved
    if (!(o instanceof CDOID))
    {
      return o;
    }

    return CDOHibernateUtil.getInstance().getCDORevision((CDOID)o);
  }

  protected List<Object> getObjects(List<?> cdoIDs)
  {
    final List<Object> result = new ArrayList<Object>();
    for (Object o : cdoIDs)
    {
      result.add(getObject(o));
    }
    return result;
  }

  protected CDOID getCDOID(Object o)
  {
    final CDORevision cdoRevision = (CDORevision)o;
    return cdoRevision.getID();
  }

  protected List<CDOID> getCDOIDs(Collection<?> c)
  {
    final List<CDOID> newC = new ArrayList<CDOID>();
    for (Object o : c)
    {
      newC.add(getCDOID(o));
    }
    return newC;
  }

  public void add(int index, Object element)
  {
    getDelegate().add(index, getCDOID(element));
  }

  public boolean add(Object o)
  {
    return getDelegate().add(getCDOID(o));
  }

  public boolean addAll(Collection<? extends Object> c)
  {
    return getDelegate().addAll(getCDOIDs(c));
  }

  public boolean addAll(int index, Collection<? extends Object> c)
  {
    return getDelegate().addAll(index, getCDOIDs(c));
  }

  public void clear()
  {
    getDelegate().clear();
  }

  public boolean contains(Object o)
  {
    return getDelegate().contains(getCDOID(o));
  }

  public boolean containsAll(Collection<?> c)
  {
    return getDelegate().containsAll(getCDOIDs(c));
  }

  public Object get(int index)
  {
    return getObject(getDelegate().get(index));
  }

  public int indexOf(Object o)
  {
    return getDelegate().indexOf(getCDOID(o));
  }

  public boolean isEmpty()
  {
    return getDelegate().isEmpty();
  }

  public Iterator<Object> iterator()
  {
    return new CDOHibernateIterator(getDelegate().iterator());
  }

  public int lastIndexOf(Object o)
  {
    return getDelegate().lastIndexOf(getCDOID(o));
  }

  public ListIterator<Object> listIterator()
  {
    return new CDOHibernateListIterator(getDelegate().listIterator());
  }

  public ListIterator<Object> listIterator(int index)
  {
    return new CDOHibernateListIterator(getDelegate().listIterator(index));
  }

  public Object remove(int index)
  {
    return getDelegate().remove(index);
  }

  public boolean remove(Object o)
  {
    return getDelegate().remove(getCDOID(o));
  }

  public boolean removeAll(Collection<?> c)
  {
    return getDelegate().removeAll(getCDOIDs(c));
  }

  public boolean retainAll(Collection<?> c)
  {
    return getDelegate().retainAll(getCDOIDs(c));
  }

  public Object set(int index, Object element)
  {
    return getDelegate().set(index, getCDOID(element));
  }

  public int size()
  {
    return getDelegate().size();
  }

  public List<Object> subList(int fromIndex, int toIndex)
  {
    return getObjects(getDelegate().subList(fromIndex, toIndex));
  }

  public Object[] toArray()
  {
    final Object[] result = new Object[size()];
    int i = 0;
    for (Object o : this)
    {
      result[i++] = o;
    }
    return result;
  }

  @SuppressWarnings("unchecked")
  public <T> T[] toArray(T[] a)
  {
    int i = 0;
    for (Object o : this)
    {
      a[i++] = (T)o;
    }
    return a;
  }

  private static class CDOHibernateIterator implements Iterator<Object>
  {
    final Iterator<?> delegate;

    CDOHibernateIterator(Iterator<?> delegate)
    {
      this.delegate = delegate;
    }

    public boolean hasNext()
    {
      return delegate.hasNext();
    }

    public Object next()
    {
      final Object o = this.delegate.next();
      if (o instanceof CDOID)
      {
        return CDOHibernateUtil.getInstance().getCDORevision((CDOID)o);
      }
      return o;
    }

    public void remove()
    {
      delegate.remove();
    }
  }

  private class CDOHibernateListIterator implements ListIterator<Object>
  {
    final ListIterator<Object> delegate;

    CDOHibernateListIterator(ListIterator<Object> delegate)
    {
      this.delegate = delegate;
    }

    public void add(Object o)
    {
      delegate.add(((CDORevision)o).getID());
    }

    public boolean hasNext()
    {
      return delegate.hasNext();
    }

    public boolean hasPrevious()
    {
      return delegate.hasPrevious();
    }

    public Object next()
    {
      final Object o = this.delegate.next();
      if (o instanceof CDOID)
      {
        return CDOHibernateUtil.getInstance().getCDORevision((CDOID)delegate.next());
      }
      return o;
    }

    public int nextIndex()
    {
      return delegate.nextIndex();
    }

    public Object previous()
    {
      final Object o = this.delegate.previous();
      if (o instanceof CDOID)
      {
        return CDOHibernateUtil.getInstance().getCDORevision((CDOID)delegate.next());
      }
      return o;
    }

    public int previousIndex()
    {
      return delegate.previousIndex();
    }

    public void remove()
    {
      delegate.remove();
    }

    public void set(Object o)
    {
      delegate.set(((CDORevision)o).getID());
    }
  }
}
