/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial api
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.hibernate.tuplizer;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDExternal;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;

import org.eclipse.net4j.util.collection.MoveableList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Wraps a moveable list so that hibernate always sees an object view while cdo always sees a cdoid view. The same for
 * EEnum: cdo wants to see an int (the ordinal), hibernate the real eenum value. This to support querying with EENum
 * parameters.
 * 
 * @author Martin Taal
 */
public class HibernateMoveableListWrapper implements MoveableList<Object>
{
  private List<Object> delegate;

  public HibernateMoveableListWrapper()
  {
  }

  public Object move(int targetIndex, int sourceIndex)
  {
    int size = size();
    if (sourceIndex >= size)
    {
      throw new IndexOutOfBoundsException("sourceIndex=" + sourceIndex + ", size=" + size); //$NON-NLS-1$  //$NON-NLS-2$
    }

    if (targetIndex >= size)
    {
      throw new IndexOutOfBoundsException("targetIndex=" + targetIndex + ", size=" + size); //$NON-NLS-1$ //$NON-NLS-2$
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

  /**
   * @return the delegate
   */
  public List<Object> getDelegate()
  {
    return delegate;
  }

  /**
   * @param delegate
   *          the delegate to set
   */
  public void setDelegate(List<Object> delegate)
  {
    this.delegate = delegate;
  }

  protected Object getObject(Object o)
  {
    if (o == null)
    {
      return null;
    }

    if (o instanceof CDOID && CDOIDUtil.isNull((CDOID)o))
    {
      return null;
    }
    else if (o instanceof CDOIDExternal)
    {
      return o;
    }

    // is already resolved
    if (!(o instanceof CDOID))
    {
      return o;
    }

    return HibernateUtil.getInstance().getCDORevision((CDOID)o);
  }

  protected List<Object> getObjects(List<?> ids)
  {
    List<Object> result = new ArrayList<Object>();
    for (Object o : ids)
    {
      result.add(getObject(o));
    }

    return result;
  }

  protected Object getValue(Object o)
  {
    if (o instanceof CDOIDExternal)
    {
      return o;
    }

    // can happen for primitive typed lists
    if (!(o instanceof CDORevision))
    {
      return o;
    }

    return HibernateUtil.getInstance().getCDOID(o);
  }

  protected List<Object> getValues(Collection<?> c)
  {
    List<Object> newC = new ArrayList<Object>();
    for (Object o : c)
    {
      newC.add(getValue(o));
    }

    return newC;
  }

  public void add(int index, Object element)
  {
    getDelegate().add(index, getValue(element));
  }

  public boolean add(Object o)
  {
    return getDelegate().add(getValue(o));
  }

  public boolean addAll(Collection<? extends Object> c)
  {
    return getDelegate().addAll(getValues(c));
  }

  public boolean addAll(int index, Collection<? extends Object> c)
  {
    return getDelegate().addAll(index, getValues(c));
  }

  public void clear()
  {
    getDelegate().clear();
  }

  public boolean contains(Object o)
  {
    return getDelegate().contains(getValue(o));
  }

  public boolean containsAll(Collection<?> c)
  {
    return getDelegate().containsAll(getValues(c));
  }

  public Object get(int index)
  {
    return getObject(getDelegate().get(index));
  }

  public int indexOf(Object o)
  {
    return getDelegate().indexOf(getValue(o));
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
    return getDelegate().lastIndexOf(getValue(o));
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
    return getDelegate().remove(getValue(o));
  }

  public boolean removeAll(Collection<?> c)
  {
    return getDelegate().removeAll(getValues(c));
  }

  public boolean retainAll(Collection<?> c)
  {
    return getDelegate().retainAll(getValues(c));
  }

  public Object set(int index, Object element)
  {
    return getDelegate().set(index, getValue(element));
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
    Object[] result = new Object[size()];
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

  private static final class CDOHibernateIterator implements Iterator<Object>
  {
    private final Iterator<?> delegate;

    public CDOHibernateIterator(Iterator<?> delegate)
    {
      this.delegate = delegate;
    }

    public boolean hasNext()
    {
      return delegate.hasNext();
    }

    public Object next()
    {
      Object o = delegate.next();
      if (o instanceof CDOIDExternal)
      {
        return o;
      }
      else if (o instanceof CDOID)
      {
        return HibernateUtil.getInstance().getCDORevision((CDOID)o);
      }

      return o;
    }

    public void remove()
    {
      delegate.remove();
    }
  }

  private static final class CDOHibernateListIterator implements ListIterator<Object>
  {
    private final ListIterator<Object> delegate;

    public CDOHibernateListIterator(ListIterator<Object> delegate)
    {
      this.delegate = delegate;
    }

    public void add(Object o)
    {
      delegate.add(HibernateUtil.getInstance().getCDOID(o));
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
      Object o = delegate.next();
      if (o instanceof CDOID)
      {
        return HibernateUtil.getInstance().getCDORevision((CDOID)delegate.next());
      }

      return o;
    }

    public int nextIndex()
    {
      return delegate.nextIndex();
    }

    public Object previous()
    {
      Object o = delegate.previous();
      if (o instanceof CDOID)
      {
        return HibernateUtil.getInstance().getCDORevision((CDOID)delegate.next());
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
      delegate.set(HibernateUtil.getInstance().getCDOID(o));
    }
  }
}
