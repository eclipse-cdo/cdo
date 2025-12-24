/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.container.delegate;

import org.eclipse.net4j.util.container.ContainerEvent;
import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.event.IListener;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/**
 * A default implementation of a {@link IContainerList container list}.
 *
 * @author Eike Stepper
 */
public class ContainerList<E> extends ContainerCollection<E> implements IContainerList<E>
{
  public ContainerList(List<E> delegate)
  {
    super(delegate);
  }

  @Override
  public List<E> getDelegate()
  {
    return (List<E>)super.getDelegate();
  }

  /**
   * @category WRITE
   */
  @Override
  public void add(int index, E element)
  {
    getDelegate().add(index, element);
    fireAddedEvent(element);
  }

  /**
   * @category WRITE
   */
  @Override
  public boolean addAll(int index, Collection<? extends E> c)
  {
    ContainerEvent<E> event = createEvent(getDelegate(), IContainerDelta.Kind.ADDED);
    getDelegate().addAll(index, c);
    return dispatchEvent(event);
  }

  /**
   * @category READ
   */
  @Override
  public E get(int index)
  {
    return getDelegate().get(index);
  }

  /**
   * @category READ
   */
  @Override
  public int indexOf(Object o)
  {
    return getDelegate().indexOf(o);
  }

  /**
   * @category READ
   */
  @Override
  public int lastIndexOf(Object o)
  {
    return getDelegate().lastIndexOf(o);
  }

  /**
   * @category READ
   */
  @Override
  public ListIterator<E> listIterator()
  {
    return new DelegatingListIterator(getDelegate().listIterator());
  }

  /**
   * @category READ
   */
  @Override
  public ListIterator<E> listIterator(int index)
  {
    return new DelegatingListIterator(getDelegate().listIterator(index));
  }

  /**
   * @category WRITE
   */
  @Override
  public E remove(int index)
  {
    E removed = getDelegate().remove(index);
    if (removed != null)
    {
      fireRemovedEvent(removed);
    }

    return removed;
  }

  /**
   * @category WRITE
   */
  @Override
  public E set(int index, E element)
  {
    E removed = getDelegate().set(index, element);

    IListener[] listeners = getListeners();
    if (listeners.length != 0)
    {
      ContainerEvent<E> event = new ContainerEvent<>(ContainerList.this);
      event.addDelta(removed, IContainerDelta.Kind.REMOVED);
      event.addDelta(element, IContainerDelta.Kind.ADDED);

      fireEvent(event, listeners);
    }

    return removed;
  }

  /**
   * @category READ
   */
  @Override
  public List<E> subList(int fromIndex, int toIndex)
  {
    return getDelegate().subList(fromIndex, toIndex);
  }

  /**
   * A delegating {@link ListIterator list iterator}.
   *
   * @author Eike Stepper
   */
  public class DelegatingListIterator extends DelegatingIterator implements ListIterator<E>
  {
    public DelegatingListIterator(ListIterator<E> delegate)
    {
      super(delegate);
    }

    @Override
    public ListIterator<E> getDelegate()
    {
      return (ListIterator<E>)super.getDelegate();
    }

    /**
     * @category WRITE
     */
    @Override
    public void add(E o)
    {
      getDelegate().add(o);
      fireAddedEvent(o);
      last = o;
    }

    /**
     * @category WRITE
     */
    @Override
    public void set(E o)
    {
      getDelegate().set(o);

      IListener[] listeners = getListeners();
      if (listeners.length != 0)
      {
        ContainerEvent<E> event = new ContainerEvent<>(ContainerList.this);
        event.addDelta(last, IContainerDelta.Kind.REMOVED);
        event.addDelta(o, IContainerDelta.Kind.ADDED);

        fireEvent(event, listeners);
      }

      last = o;
    }

    /**
     * @category READ
     */
    @Override
    public boolean hasPrevious()
    {
      return getDelegate().hasPrevious();
    }

    /**
     * @category READ
     */
    @Override
    public int nextIndex()
    {
      return getDelegate().nextIndex();
    }

    /**
     * @category READ
     */
    @Override
    public E previous()
    {
      return getDelegate().previous();
    }

    /**
     * @category READ
     */
    @Override
    public int previousIndex()
    {
      return getDelegate().previousIndex();
    }
  }
}
