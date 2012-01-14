/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.internal.hibernate.HibernateUtil;
import org.eclipse.emf.cdo.spi.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.hibernate.proxy.HibernateProxy;

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
public class WrappedHibernateList implements InternalCDOList
{
  private List<Object> delegate;

  private boolean frozen;

  public WrappedHibernateList()
  {
  }

  public void move(int newPosition, Object object)
  {
    checkFrozen();
    move(newPosition, indexOf(object));
  }

  public Object move(int targetIndex, int sourceIndex)
  {
    checkFrozen();
    int size = size();
    if (sourceIndex >= size)
    {
      throw new IndexOutOfBoundsException("sourceIndex=" + sourceIndex + ", size=" + size); //$NON-NLS-1$ //$NON-NLS-2$
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
   * There's a duplicate of this method in CDOListImpl!!!
   */
  public boolean adjustReferences(CDOReferenceAdjuster adjuster, EStructuralFeature feature)
  {
    boolean changed = false;

    CDOType type = CDOModelUtil.getType(feature);
    int size = size();
    for (int i = 0; i < size; i++)
    {
      Object element = get(i);
      Object newID = type.adjustReferences(adjuster, element, feature, i);
      if (newID != element) // Just an optimization for NOOP adjusters
      {
        set(i, newID);
        changed = true;
      }
    }

    return changed;
  }

  public InternalCDOList clone(EClassifier classifier)
  {
    CDOType type = CDOModelUtil.getType(classifier);
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

  protected List<Object> getObjects(List<?> ids)
  {
    List<Object> result = new ArrayList<Object>();
    for (Object o : ids)
    {
      result.add(getObject(o));
    }

    return result;
  }

  protected CDOID getCDOID(Object o)
  {
    if (o instanceof CDOID)
    {
      return (CDOID)o;
    }

    return HibernateUtil.getInstance().getCDOID(o);
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
    checkFrozen();
    getDelegate().add(index, getCDOID(element));
  }

  public boolean add(Object o)
  {
    checkFrozen();
    return getDelegate().add(getCDOID(o));
  }

  public boolean addAll(Collection<? extends Object> c)
  {
    checkFrozen();
    return getDelegate().addAll(getCDOIDs(c));
  }

  public boolean addAll(int index, Collection<? extends Object> c)
  {
    checkFrozen();
    return getDelegate().addAll(index, getCDOIDs(c));
  }

  public void clear()
  {
    checkFrozen();
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
    final Object delegateValue = getDelegate().get(index);
    if (delegateValue instanceof CDOID)
    {
      return delegateValue;
    }

    final Object value = getObject(delegateValue);
    if (value instanceof CDORevision || value instanceof HibernateProxy)
    {
      return HibernateUtil.getInstance().getCDOID(value);
    }

    if (value instanceof EEnumLiteral)
    {
      return ((EEnumLiteral)value).getValue();
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
    return new CDOHibernateListIterator(this, getDelegate().listIterator());
  }

  public ListIterator<Object> listIterator(int index)
  {
    return new CDOHibernateListIterator(this, getDelegate().listIterator(index));
  }

  public Object remove(int index)
  {
    checkFrozen();
    return getDelegate().remove(index);
  }

  public boolean remove(Object o)
  {
    checkFrozen();
    return getDelegate().remove(getCDOID(o));
  }

  public boolean removeAll(Collection<?> c)
  {
    checkFrozen();
    return getDelegate().removeAll(getCDOIDs(c));
  }

  public boolean retainAll(Collection<?> c)
  {
    return getDelegate().retainAll(getCDOIDs(c));
  }

  public Object set(int index, Object element)
  {
    checkFrozen();
    if (element instanceof CDOID)
    {
      return getDelegate().set(index, element);
    }

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

      // CDO always wants to have the integer for an EENUM
      if (o instanceof EEnumLiteral)
      {
        return ((EEnumLiteral)o).getValue();
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

    private final WrappedHibernateList owner;

    public CDOHibernateListIterator(WrappedHibernateList owner, ListIterator<Object> delegate)
    {
      this.delegate = delegate;
      this.owner = owner;
    }

    public void add(Object o)
    {
      owner.checkFrozen();

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
      owner.checkFrozen();
      delegate.remove();
    }

    public void set(Object o)
    {
      owner.checkFrozen();
      delegate.set(HibernateUtil.getInstance().getCDOID(o));
    }
  }

  public void freeze()
  {
    frozen = true;
  }

  private void checkFrozen()
  {
    if (frozen)
    {
      throw new IllegalStateException("Cannot modify a frozen list");
    }
  }

  public void setWithoutFrozenCheck(int i, Object value)
  {
    getDelegate().set(i, value);
  }
}
