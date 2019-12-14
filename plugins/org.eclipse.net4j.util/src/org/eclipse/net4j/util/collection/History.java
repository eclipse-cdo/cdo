/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.collection;

import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class History<T> extends Notifier implements IHistory<T>
{
  protected List<IHistoryElement<T>> elements = new ArrayList<>(0);

  private boolean loaded;

  public History()
  {
  }

  public List<IHistoryElement<T>> getElements()
  {
    lazyLoad();
    return elements;
  }

  public void setElements(List<IHistoryElement<T>> newElements)
  {
    if (newElements == null)
    {
      newElements = new ArrayList<>(0);
    }

    if (!elements.equals(newElements))
    {
      elements = newElements;
      changed();
    }
  }

  @Override
  public boolean isEmpty()
  {
    lazyLoad();
    return elements.isEmpty();
  }

  @Override
  public int size()
  {
    lazyLoad();
    return elements.size();
  }

  @Override
  public int indexOf(T data)
  {
    lazyLoad();
    for (int i = 0; i < elements.size(); i++)
    {
      if (elements.get(i).getData().equals(data))
      {
        return i;
      }
    }

    return -1;
  }

  @Override
  public IHistoryElement<T> get(int index)
  {
    lazyLoad();
    return elements.get(index);
  }

  @Override
  public boolean add(T data)
  {
    lazyLoad();
    int index = indexOf(data);
    IHistoryElement<T> element = index != -1 ? elements.remove(index) : createElement(data);
    elements.add(0, element);

    boolean changed = index != 0;
    if (changed)
    {
      changed();
    }

    return changed;
  }

  @Override
  public IHistoryElement<T> remove(int index)
  {
    lazyLoad();
    IHistoryElement<T> element = elements.remove(index);
    if (element != null)
    {
      changed();
    }

    return element;
  }

  @Override
  public boolean clear()
  {
    if (elements.isEmpty())
    {
      return false;
    }

    elements.clear();
    changed();
    return true;
  }

  @Override
  public T getMostRecent()
  {
    lazyLoad();
    if (isEmpty())
    {
      return null;
    }

    return elements.get(0).getData();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <D> D[] getData(D[] a)
  {
    lazyLoad();
    int size = elements.size();
    if (a.length < size)
    {
      a = (D[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
    }

    for (int i = 0; i < size; i++)
    {
      a[i] = (D)elements.get(i).getData();
    }

    if (a.length > size)
    {
      a[size] = null;
    }

    return a;
  }

  @Override
  @SuppressWarnings("unchecked")
  public IHistoryElement<T>[] toArray()
  {
    lazyLoad();
    return elements.toArray(new IHistoryElement[elements.size()]);
  }

  @Override
  public Iterator<IHistoryElement<T>> iterator()
  {
    lazyLoad();
    return elements.iterator();
  }

  @SuppressWarnings("unchecked")
  protected IHistoryElement<T> createElement(T data)
  {
    @SuppressWarnings("rawtypes")
    HistoryElement result = new HistoryElement(this, data);
    return result;
  }

  protected void load()
  {
  }

  protected void save()
  {
  }

  protected final void changed()
  {
    save();
    fireChangedEvent();
  }

  private void lazyLoad()
  {
    if (!loaded)
    {
      loaded = true;
      load();
    }
  }

  private void fireChangedEvent()
  {
    IListener[] listeners = getListeners();
    if (listeners != null)
    {
      fireEvent(new IHistoryChangeEvent()
      {
        @Override
        public IHistory<?> getSource()
        {
          return History.this;
        }
      }, listeners);
    }
  }
}
