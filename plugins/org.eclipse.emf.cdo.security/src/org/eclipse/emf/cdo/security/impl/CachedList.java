/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * @author Eike Stepper
 */
abstract class CachedList<E extends EObject> implements InternalEList<E>, EStructuralFeature.Setting
{
  private SoftReference<Object[]> cache;

  protected CachedList()
  {
  }

  private InternalEList<E> getList()
  {
    Object[] data = null;
    if (cache != null)
    {
      data = cache.get();
    }

    if (data == null)
    {
      data = getData();
      cache = new SoftReference<Object[]>(data);
    }

    InternalEObject owner = getOwner();
    EStructuralFeature feature = getFeature();
    return new EcoreEList.UnmodifiableEList.FastCompare<E>(owner, feature, data.length, data);
  }

  protected abstract InternalEObject getOwner();

  protected abstract EStructuralFeature getFeature();

  protected abstract Object[] getData();

  public void move(int newPosition, E object)
  {
    getList().move(newPosition, object);
  }

  public E move(int newPosition, int oldPosition)
  {
    return getList().move(newPosition, oldPosition);
  }

  public E basicGet(int index)
  {
    return getList().basicGet(index);
  }

  public List<E> basicList()
  {
    return getList().basicList();
  }

  public Iterator<E> basicIterator()
  {
    return getList().basicIterator();
  }

  public ListIterator<E> basicListIterator()
  {
    return getList().basicListIterator();
  }

  public ListIterator<E> basicListIterator(int index)
  {
    return getList().basicListIterator(index);
  }

  public Object[] basicToArray()
  {
    return getList().basicToArray();
  }

  public <T> T[] basicToArray(T[] array)
  {
    return getList().basicToArray(array);
  }

  public int basicIndexOf(Object object)
  {
    return getList().basicIndexOf(object);
  }

  public int basicLastIndexOf(Object object)
  {
    return getList().basicLastIndexOf(object);
  }

  public boolean basicContains(Object object)
  {
    return getList().basicContains(object);
  }

  public boolean basicContainsAll(Collection<?> collection)
  {
    return getList().basicContainsAll(collection);
  }

  public NotificationChain basicRemove(Object object, NotificationChain notifications)
  {
    return getList().basicRemove(object, notifications);
  }

  public NotificationChain basicAdd(E object, NotificationChain notifications)
  {
    return getList().basicAdd(object, notifications);
  }

  public void addUnique(E object)
  {
    getList().addUnique(object);
  }

  public void addUnique(int index, E object)
  {
    getList().addUnique(index, object);
  }

  public boolean addAllUnique(Collection<? extends E> collection)
  {
    return getList().addAllUnique(collection);
  }

  public boolean addAllUnique(int index, Collection<? extends E> collection)
  {
    return getList().addAllUnique(index, collection);
  }

  public E setUnique(int index, E object)
  {
    return getList().setUnique(index, object);
  }

  public int size()
  {
    return getList().size();
  }

  public boolean isEmpty()
  {
    return getList().isEmpty();
  }

  public boolean contains(Object o)
  {
    return getList().contains(o);
  }

  public Iterator<E> iterator()
  {
    return getList().iterator();
  }

  public Object[] toArray()
  {
    return getList().toArray();
  }

  public <T> T[] toArray(T[] a)
  {
    return getList().toArray(a);
  }

  public boolean add(E e)
  {
    return getList().add(e);
  }

  public boolean remove(Object o)
  {
    return getList().remove(o);
  }

  public boolean containsAll(Collection<?> c)
  {
    return getList().containsAll(c);
  }

  public boolean addAll(Collection<? extends E> c)
  {
    return getList().addAll(c);
  }

  public boolean addAll(int index, Collection<? extends E> c)
  {
    return getList().addAll(index, c);
  }

  public boolean removeAll(Collection<?> c)
  {
    return getList().removeAll(c);
  }

  public boolean retainAll(Collection<?> c)
  {
    return getList().retainAll(c);
  }

  public void clear()
  {
    getList().clear();
  }

  @Override
  public boolean equals(Object o)
  {
    return getList().equals(o);
  }

  @Override
  public int hashCode()
  {
    return getList().hashCode();
  }

  public E get(int index)
  {
    return getList().get(index);
  }

  public E set(int index, E element)
  {
    return getList().set(index, element);
  }

  public void add(int index, E element)
  {
    getList().add(index, element);
  }

  public E remove(int index)
  {
    return getList().remove(index);
  }

  public int indexOf(Object o)
  {
    return getList().indexOf(o);
  }

  public int lastIndexOf(Object o)
  {
    return getList().lastIndexOf(o);
  }

  public ListIterator<E> listIterator()
  {
    return getList().listIterator();
  }

  public ListIterator<E> listIterator(int index)
  {
    return getList().listIterator(index);
  }

  public List<E> subList(int fromIndex, int toIndex)
  {
    return getList().subList(fromIndex, toIndex);
  }

  public EObject getEObject()
  {
    return getOwner();
  }

  public EStructuralFeature getEStructuralFeature()
  {
    return getFeature();
  }

  public Object get(boolean resolve)
  {
    return getList();
  }

  public void set(Object newValue)
  {
    throw new UnsupportedOperationException();
  }

  public boolean isSet()
  {
    return !getList().isEmpty();
  }

  public void unset()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @author Eike Stepper
   */
  static abstract class RecursionSafe<E extends EObject, O extends EObject> extends CachedList<E>
  {
    protected RecursionSafe()
    {
    }

    @Override
    protected Object[] getData()
    {
      @SuppressWarnings("unchecked")
      O start = (O)getOwner();

      Set<Object> visited = new HashSet<Object>();
      Set<E> result = new HashSet<E>();

      getData(start, visited, result);
      return result.toArray();
    }

    protected abstract void getData(O object, Set<Object> visited, Set<E> result);
  }
}
