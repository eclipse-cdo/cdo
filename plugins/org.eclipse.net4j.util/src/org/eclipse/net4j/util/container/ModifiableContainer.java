/*
 * Copyright (c) 2022, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.container;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * An abstract implementation of a {@link IContainer.Modifiable modifiable container}.
 *
 * @since 3.18
 * @author Eike Stepper
 */
public abstract class ModifiableContainer<E> extends Container<E> implements IContainer.Modifiable<E>
{
  private final Class<E> componentType;

  public ModifiableContainer(Class<E> componentType)
  {
    this.componentType = componentType;
  }

  public final Class<E> getComponentType()
  {
    return componentType;
  }

  @Override
  public synchronized boolean isEmpty()
  {
    return backingStoreIsEmpty();
  }

  @Override
  public E[] getElements()
  {
    E[] array;
    synchronized (this)
    {
      @SuppressWarnings("unchecked")
      E[] a = (E[])Array.newInstance(componentType, backingStoreSize());

      array = backingStoreToArray(a);
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
      boolean first = backingStoreIsEmpty();

      if (backingStoreAdd(element))
      {
        if (first)
        {
          firstElementAdded(element);
        }
        else
        {
          elementAdded(element);
        }

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

  @Override
  public boolean addAllElements(Collection<E> elements)
  {
    List<E> validElements = new ArrayList<>();
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
      boolean first = backingStoreIsEmpty();

      for (E element : validElements)
      {
        if (backingStoreAdd(element))
        {
          if (first)
          {
            firstElementAdded(element);
            first = false;
          }
          else
          {
            elementAdded(element);
          }

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
      if (backingStoreRemove(element))
      {
        if (backingStoreIsEmpty())
        {
          lastElementRemoved(element);
        }
        else
        {
          elementAdded(element);
        }

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

  @Override
  public boolean removeAllElements(Collection<E> elements)
  {
    ContainerEvent<E> event = null;
    synchronized (this)
    {
      for (E element : elements)
      {
        if (backingStoreRemove(element))
        {
          if (backingStoreIsEmpty())
          {
            lastElementRemoved(element);
          }
          else
          {
            elementAdded(element);
          }

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
    if (isEmpty())
    {
      return;
    }

    ContainerEvent<E> event = newContainerEvent();
    synchronized (this)
    {
      backingStoreForEach(element -> {
        if (backingStoreContains(element))
        {
          elementRemoved(element);
          event.addDelta(element, IContainerDelta.Kind.REMOVED);
        }
      });

      backingStoreClear();

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
   */
  protected void containerModified()
  {
  }

  /**
   * Called inside synchronized(this).
   * @since 3.24
   */
  protected void firstElementAdded(E element)
  {
    elementAdded(element);
  }

  /**
   * Called inside synchronized(this).
   */
  protected void elementAdded(E element)
  {
  }

  /**
   * Called inside synchronized(this).
   */
  protected void elementRemoved(E element)
  {
  }

  /**
   * Called inside synchronized(this).
   * @since 3.24
   */
  protected void lastElementRemoved(E element)
  {
    elementRemoved(element);
  }

  protected abstract boolean backingStoreIsEmpty();

  protected abstract int backingStoreSize();

  protected abstract E[] backingStoreToArray(E[] a);

  protected abstract void backingStoreForEach(Consumer<E> consumer);

  protected abstract boolean backingStoreContains(E element);

  protected abstract boolean backingStoreAdd(E element);

  protected abstract boolean backingStoreRemove(E element);

  protected abstract void backingStoreClear();
}
