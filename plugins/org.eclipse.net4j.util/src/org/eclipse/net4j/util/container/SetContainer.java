/*
 * Copyright (c) 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.container;

import org.eclipse.net4j.util.container.delegate.IContainerSet;
import org.eclipse.net4j.util.container.delegate.IContainerSortedSet;
import org.eclipse.net4j.util.io.IORuntimeException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An implementation of a {@link Container container} that stores its {@link #getElements() elements} in a {@link #getSet() set}.
 *
 * @since 3.2
 * @author Eike Stepper
 * @see IContainerSet
 * @see IContainerSortedSet
 */
public class SetContainer<E> extends Container<E> implements IContainer.Modifiable<E>, IContainer.Persistable<E>
{
  private final Class<E> componentType;

  private final Set<E> set;

  private Persistence<E> persistence;

  public SetContainer(Class<E> componentType)
  {
    this(componentType, new HashSet<E>());
  }

  public SetContainer(Class<E> componentType, Set<E> set)
  {
    this.componentType = componentType;
    this.set = set;
  }

  public final Class<E> getComponentType()
  {
    return componentType;
  }

  /**
   * @since 3.5
   */
  @Override
  public final Persistence<E> getPersistence()
  {
    return persistence;
  }

  /**
   * @since 3.5
   */
  @Override
  public final void setPersistence(Persistence<E> persistence)
  {
    this.persistence = persistence;
  }

  /**
   * @since 3.5
   */
  public boolean isSavedWhenModified()
  {
    return true;
  }

  @Override
  public synchronized boolean isEmpty()
  {
    return set.isEmpty();
  }

  @Override
  public E[] getElements()
  {
    E[] array;
    synchronized (this)
    {
      @SuppressWarnings("unchecked")
      E[] a = (E[])Array.newInstance(componentType, set.size());

      array = set.toArray(a);
    }

    array = sortElements(array);
    return array;
  }

  @Override
  public boolean addElement(E element)
  {
    if (!validateElement(element))
    {
      return false;
    }

    IContainerEvent<E> event = null;
    synchronized (this)
    {
      if (set.add(element))
      {
        elementAdded(element);
        event = newContainerEvent(element, IContainerDelta.Kind.ADDED);
        notifyAll();
      }
    }

    if (event != null)
    {
      fireEvent(event);
      containerModified();
      return true;
    }

    return false;
  }

  /**
   * @since 3.5
   */
  @Override
  public boolean addAllElements(Collection<E> elements)
  {
    List<E> validElements = new ArrayList<E>();
    for (E element : elements)
    {
      if (validateElement(element))
      {
        validElements.add(element);
      }
    }

    ContainerEvent<E> event = null;
    synchronized (this)
    {
      for (E element : validElements)
      {
        if (set.add(element))
        {
          elementAdded(element);

          if (event == null)
          {
            event = newContainerEvent();
          }

          event.addDelta(element, IContainerDelta.Kind.ADDED);
        }
      }

      if (event != null)
      {
        notifyAll();
      }
    }

    if (event != null)
    {
      fireEvent(event);
      containerModified();
      return true;
    }

    return false;
  }

  @Override
  public boolean removeElement(E element)
  {
    IContainerEvent<E> event = null;
    synchronized (this)
    {
      if (set.remove(element))
      {
        elementRemoved(element);
        event = newContainerEvent(element, IContainerDelta.Kind.REMOVED);
        notifyAll();
      }
    }

    if (event != null)
    {
      fireEvent(event);
      containerModified();
      return true;
    }

    return false;
  }

  /**
   * @since 3.5
   */
  @Override
  public boolean removeAllElements(Collection<E> elements)
  {
    ContainerEvent<E> event = null;
    synchronized (this)
    {
      for (E element : elements)
      {
        if (set.remove(element))
        {
          elementRemoved(element);

          if (event == null)
          {
            event = newContainerEvent();
          }

          event.addDelta(element, IContainerDelta.Kind.REMOVED);
        }
      }

      if (event != null)
      {
        notifyAll();
      }
    }

    if (event != null)
    {
      fireEvent(event);
      containerModified();
      return true;
    }

    return false;
  }

  public void clear()
  {
    ContainerEvent<E> event = null;
    synchronized (this)
    {
      for (E element : set)
      {
        if (set.contains(element))
        {
          elementRemoved(element);

          if (event == null)
          {
            event = newContainerEvent();
          }

          event.addDelta(element, IContainerDelta.Kind.REMOVED);
        }
      }

      if (event != null)
      {
        notifyAll();
      }
    }

    if (event != null)
    {
      fireEvent(event);
      containerModified();
    }
  }

  /**
   * @since 3.5
   */
  @Override
  public synchronized void load() throws IORuntimeException
  {
    if (persistence != null)
    {
      Collection<E> elements = persistence.loadElements();

      set.clear();
      set.addAll(elements);
    }
  }

  /**
   * @since 3.5
   */
  @Override
  public synchronized void save() throws IORuntimeException
  {
    if (persistence != null)
    {
      persistence.saveElements(set);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    load();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    if (!isSavedWhenModified())
    {
      save();
    }

    super.doDeactivate();
  }

  protected Set<E> getSet()
  {
    return set;
  }

  /**
   * Called outside synchronized(this).
   */
  protected E[] sortElements(E[] array)
  {
    return array;
  }

  /**
   * Called outside synchronized(this).
   */
  protected boolean validateElement(E element)
  {
    return true;
  }

  /**
   * Called outside synchronized(this).
   *
   * @since 3.5
   */
  protected void containerModified()
  {
    if (isSavedWhenModified())
    {
      save();
    }
  }

  /**
   * Called inside synchronized(this).
   *
   * @since 3.5
   */
  protected void elementAdded(E element)
  {
  }

  /**
   * Called inside synchronized(this).
   *
   * @since 3.5
   */
  protected void elementRemoved(E element)
  {
  }
}
