/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * An implementation of a {@link Container container} that stores its {@link #getElements() elements} in a {@link #getList() list}.
 *
 * @since 3.18
 * @author Eike Stepper
 */
public class ListContainer<E> extends PersistableContainer<E>
{
  private final List<E> list;

  public ListContainer(Class<E> componentType)
  {
    this(componentType, new ArrayList<E>());
  }

  public ListContainer(Class<E> componentType, List<E> list)
  {
    super(componentType);
    this.list = list;
  }

  protected List<E> getList()
  {
    return list;
  }

  @Override
  protected boolean backingStoreIsEmpty()
  {
    return list.isEmpty();
  }

  @Override
  protected int backingStoreSize()
  {
    return list.size();
  }

  @Override
  protected E[] backingStoreToArray(E[] a)
  {
    return list.toArray(a);
  }

  @Override
  protected void backingStoreForEach(Consumer<E> consumer)
  {
    if (consumer != null)
    {
      for (E element : list)
      {
        consumer.accept(element);
      }
    }
  }

  @Override
  protected boolean backingStoreContains(E element)
  {
    return list.contains(element);
  }

  @Override
  protected boolean backingStoreAdd(E element)
  {
    return list.add(element);
  }

  @Override
  protected boolean backingStoreRemove(E element)
  {
    return list.remove(element);
  }

  @Override
  protected void backingStoreClear()
  {
    list.clear();
  }
}
