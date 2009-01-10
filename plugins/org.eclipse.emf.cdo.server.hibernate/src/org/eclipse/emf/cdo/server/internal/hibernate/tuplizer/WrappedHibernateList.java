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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;

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
public class WrappedHibernateList implements InternalCDOList
{
  private List<Object> delegate;

  public WrappedHibernateList()
  {
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

  public void adjustReferences(CDOReferenceAdjuster adjuster, CDOType type)
  {
    int size = size();
    for (int i = 0; i < size; i++)
    {
      Object element = get(i);
      Object newID = type.adjustReferences(adjuster, element);
      if (newID != element)
      {
        set(i, newID);
      }
    }
  }

  public InternalCDOList clone(CDOType type)
  {
    int size = size();
    InternalCDOList list = (InternalCDOList)CDOListFactory.DEFAULT.createList(size, 0, 0);
    for (int i = 0; i < size; i++)
    {
      list.add(type.copyValue(get(i)));
    }
    return list;
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

    // is already resolved
    if (!(o instanceof CDOID))
    {
      return o;
    }

    return HibernateUtil.getInstance().getCDORevision((CDOID)o);
  }

  protected List<Object> getObjects(List<?> cdoIDs)
  {
    List<Object> result = new ArrayList<Object>();
    for (Object o : cdoIDs)
    {
      result.add(getObject(o));
    }

    return result;
  }

  protected CDOID getCDOID(Object o)
  {
    CDORevision cdoRevision = (CDORevision)o;
    return cdoRevision.getID();
  }

  protected List<CDOID> getCDOIDs(Collection<?> c)
  {
    List<CDOID> newC = new ArrayList<CDOID>();
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
    final Object value = getObject(getDelegate().get(index));
    if (value instanceof CDORevision)
    {
      return ((CDORevision)value).getID();
    }
    return value;
  }

  public Object get(int index, boolean resolve)
  {
    // Since delegate is a hibernate list, it is never a CDOElementProxy
    // so the parameter resolve can be ignored
    return get(index);
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
    if (element instanceof CDOID)
    {
      return getDelegate().set(index, element);
    }
    else
    {
      return getDelegate().set(index, getCDOID(element));
    }
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
      if (o instanceof CDOID)
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
      delegate.set(((CDORevision)o).getID());
    }
  }

}
