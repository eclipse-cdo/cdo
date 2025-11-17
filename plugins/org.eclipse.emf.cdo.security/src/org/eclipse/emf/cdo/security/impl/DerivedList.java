/*
 * Copyright (c) 2013, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.security.impl;

import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.emf.ecore.util.InternalEList;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * @author Eike Stepper
 */
abstract class DerivedList<E extends EObject> implements InternalEList<E>, EStructuralFeature.Setting
{
  protected DerivedList()
  {
  }

  private InternalEList<E> getList()
  {
    InternalEObject owner = getOwner();

    return CDOUtil.sync(owner).supply(() -> {
      EStructuralFeature feature = getFeature();
      Object[] data = getData();

      return new EcoreEList.UnmodifiableEList.FastCompare<>(owner, feature, data.length, data);
    });
  }

  protected abstract InternalEObject getOwner();

  protected abstract EStructuralFeature getFeature();

  protected abstract Object[] getData();

  @Override
  public void move(int newPosition, E object)
  {
    getList().move(newPosition, object);
  }

  @Override
  public E move(int newPosition, int oldPosition)
  {
    return getList().move(newPosition, oldPosition);
  }

  @Override
  public E basicGet(int index)
  {
    return getList().basicGet(index);
  }

  @Override
  public List<E> basicList()
  {
    return getList().basicList();
  }

  @Override
  public Iterator<E> basicIterator()
  {
    return getList().basicIterator();
  }

  @Override
  public ListIterator<E> basicListIterator()
  {
    return getList().basicListIterator();
  }

  @Override
  public ListIterator<E> basicListIterator(int index)
  {
    return getList().basicListIterator(index);
  }

  @Override
  public Object[] basicToArray()
  {
    return getList().basicToArray();
  }

  @Override
  public <T> T[] basicToArray(T[] array)
  {
    return getList().basicToArray(array);
  }

  @Override
  public int basicIndexOf(Object object)
  {
    return getList().basicIndexOf(object);
  }

  @Override
  public int basicLastIndexOf(Object object)
  {
    return getList().basicLastIndexOf(object);
  }

  @Override
  public boolean basicContains(Object object)
  {
    return getList().basicContains(object);
  }

  @Override
  public boolean basicContainsAll(Collection<?> collection)
  {
    return getList().basicContainsAll(collection);
  }

  @Override
  public NotificationChain basicRemove(Object object, NotificationChain notifications)
  {
    return getList().basicRemove(object, notifications);
  }

  @Override
  public NotificationChain basicAdd(E object, NotificationChain notifications)
  {
    return getList().basicAdd(object, notifications);
  }

  @Override
  public void addUnique(E object)
  {
    getList().addUnique(object);
  }

  @Override
  public void addUnique(int index, E object)
  {
    getList().addUnique(index, object);
  }

  @Override
  public boolean addAllUnique(Collection<? extends E> collection)
  {
    return getList().addAllUnique(collection);
  }

  @Override
  public boolean addAllUnique(int index, Collection<? extends E> collection)
  {
    return getList().addAllUnique(index, collection);
  }

  @Override
  public E setUnique(int index, E object)
  {
    return getList().setUnique(index, object);
  }

  @Override
  public int size()
  {
    return getList().size();
  }

  @Override
  public boolean isEmpty()
  {
    return getList().isEmpty();
  }

  @Override
  public boolean contains(Object o)
  {
    return getList().contains(o);
  }

  @Override
  public Iterator<E> iterator()
  {
    return getList().iterator();
  }

  @Override
  public Object[] toArray()
  {
    return getList().toArray();
  }

  @Override
  public <T> T[] toArray(T[] a)
  {
    return getList().toArray(a);
  }

  @Override
  public boolean add(E e)
  {
    return getList().add(e);
  }

  @Override
  public boolean remove(Object o)
  {
    return getList().remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c)
  {
    return getList().containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends E> c)
  {
    return getList().addAll(c);
  }

  @Override
  public boolean addAll(int index, Collection<? extends E> c)
  {
    return getList().addAll(index, c);
  }

  @Override
  public boolean removeAll(Collection<?> c)
  {
    return getList().removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c)
  {
    return getList().retainAll(c);
  }

  @Override
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

  @Override
  public E get(int index)
  {
    return getList().get(index);
  }

  @Override
  public E set(int index, E element)
  {
    return getList().set(index, element);
  }

  @Override
  public void add(int index, E element)
  {
    getList().add(index, element);
  }

  @Override
  public E remove(int index)
  {
    return getList().remove(index);
  }

  @Override
  public int indexOf(Object o)
  {
    return getList().indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o)
  {
    return getList().lastIndexOf(o);
  }

  @Override
  public ListIterator<E> listIterator()
  {
    return getList().listIterator();
  }

  @Override
  public ListIterator<E> listIterator(int index)
  {
    return getList().listIterator(index);
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex)
  {
    return getList().subList(fromIndex, toIndex);
  }

  @Override
  public EObject getEObject()
  {
    return getOwner();
  }

  @Override
  public EStructuralFeature getEStructuralFeature()
  {
    return getFeature();
  }

  @Override
  public Object get(boolean resolve)
  {
    return getList();
  }

  @Override
  public void set(Object newValue)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isSet()
  {
    return !getList().isEmpty();
  }

  @Override
  public void unset()
  {
    throw new UnsupportedOperationException();
  }

  /**
   * @author Eike Stepper
   */
  static abstract class RecursionSafe<E extends EObject, O extends EObject> extends DerivedList<E>
  {
    protected RecursionSafe()
    {
    }

    @Override
    protected Object[] getData()
    {
      @SuppressWarnings("unchecked")
      O start = (O)getOwner();

      Set<Object> visited = new HashSet<>();
      Set<E> result = new HashSet<>();

      getData(start, visited, result);
      return result.toArray();
    }

    protected abstract void getData(O object, Set<Object> visited, Set<E> result);
  }
}
