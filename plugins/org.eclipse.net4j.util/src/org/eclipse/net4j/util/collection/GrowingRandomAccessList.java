/*
 * Copyright (c) 2012, 2013, 2015, 2016, 2019, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.collection;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.RandomAccess;

/**
 * @author Eike Stepper
 * @since 3.3
 */
public class GrowingRandomAccessList<E> extends AbstractList<E> implements Queue<E>, RandomAccess
{
  private final Class<E> componentType;

  private final int pageCapacity;

  private List<E[]> pages = new ArrayList<>();

  private int firstFree;

  private int lastFree;

  public GrowingRandomAccessList(Class<E> componentType, int pageCapacity)
  {
    this.componentType = componentType;
    this.pageCapacity = pageCapacity;
  }

  @Override
  public E get(int index)
  {
    int size = size();
    if (index >= size)
    {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    index += firstFree;
    E[] page = getPage(index);
    return page[index % pageCapacity];
  }

  @Override
  public int size()
  {
    return pages.size() * pageCapacity - firstFree - lastFree;
  }

  public void addFirst(E e)
  {
    E[] page;
    if (firstFree == 0)
    {
      if (pages.isEmpty())
      {
        initFirstPage();
      }
      else
      {
        firstFree = pageCapacity;
      }

      page = createPage();
      pages.add(0, page);
    }
    else
    {
      page = pages.get(0);
    }

    page[--firstFree] = e;
  }

  public void addLast(E e)
  {
    E[] page;
    if (lastFree == 0)
    {
      if (pages.isEmpty())
      {
        initFirstPage();
      }
      else
      {
        lastFree = pageCapacity;
      }

      page = createPage();
      pages.add(page);
    }
    else
    {
      int size = pages.size();
      page = pages.get(size - 1);
    }

    page[pageCapacity - lastFree--] = e;
  }

  @Override
  public boolean add(E e)
  {
    addLast(e);
    return true;
  }

  @Override
  public E set(int index, E element)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(int index, E element)
  {
    int size = size();
    if (index > size || index < 0)
    {
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
    }

    if (index == 0)
    {
      addFirst(element);
    }
    else if (index == size)
    {
      addLast(element);
    }
    else
    {
      GrowingRandomAccessList<E> result = new GrowingRandomAccessList<>(componentType, pageCapacity);
      for (int i = 0; i < size; i++)
      {
        E e = get(i);

        if (i == index)
        {
          result.add(element);
        }

        result.add(e);
      }

      pages = result.pages;
      firstFree = result.firstFree;
      lastFree = result.lastFree;
    }
  }

  @Override
  public E remove(int index)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean addAll(int index, Collection<? extends E> c)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> c)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> c)
  {
    throw new UnsupportedOperationException();
  }

  public boolean offerFirst(E e)
  {
    addFirst(e);
    return true;
  }

  public boolean offerLast(E e)
  {
    addLast(e);
    return true;
  }

  public E removeFirst()
  {
    throw new UnsupportedOperationException();
  }

  public E removeLast()
  {
    throw new UnsupportedOperationException();
  }

  public E pollFirst()
  {
    throw new UnsupportedOperationException();
  }

  public E pollLast()
  {
    throw new UnsupportedOperationException();
  }

  public E getFirst()
  {
    return get(0);
  }

  public E getLast()
  {
    return get(size() - 1);
  }

  public E peekFirst()
  {
    if (pages.isEmpty())
    {
      return null;
    }

    return getFirst();
  }

  public E peekLast()
  {
    if (pages.isEmpty())
    {
      return null;
    }

    return getLast();
  }

  public boolean removeFirstOccurrence(Object o)
  {
    throw new UnsupportedOperationException();
  }

  public boolean removeLastOccurrence(Object o)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean offer(E e)
  {
    return offerLast(e);
  }

  @Override
  public E remove()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public E poll()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public E element()
  {
    return getFirst();
  }

  @Override
  public E peek()
  {
    return peekFirst();
  }

  public void push(E e)
  {
    addFirst(e);
  }

  public E pop()
  {
    throw new UnsupportedOperationException();
  }

  public Iterator<E> descendingIterator()
  {
    ListIterator<E> delegate = listIterator(size() - 1);
    return new BidirectionalIterator<>(delegate, true);
  }

  protected E[] createPage()
  {
    @SuppressWarnings("unchecked")
    E[] page = (E[])Array.newInstance(componentType, pageCapacity);
    return page;
  }

  protected E[] getPage(int index)
  {
    return pages.get(getPageIndex(index));
  }

  protected int getPageIndex(int index)
  {
    return index / pageCapacity;
  }

  private void initFirstPage()
  {
    int centerIndex = (pageCapacity - 1) / 2;
    firstFree = centerIndex + 1;
    lastFree = pageCapacity - centerIndex - 1;
  }
}
