/*
 * Copyright (c) 2012, 2015, 2016, 2019, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.container;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * An implementation of a {@link Container container} that stores its {@link #getElements() elements} in a {@link #getSet() set}.
 *
 * @since 3.2
 * @author Eike Stepper
 */
public class SetContainer<E> extends PersistableContainer<E>
{
  private final Set<E> set;

  public SetContainer(Class<E> componentType)
  {
    this(componentType, new HashSet<E>());
  }

  public SetContainer(Class<E> componentType, Set<E> set)
  {
    super(componentType);
    this.set = set;
  }

  protected Set<E> getSet()
  {
    return set;
  }

  @Override
  protected boolean backingStoreIsEmpty()
  {
    return set.isEmpty();
  }

  @Override
  protected int backingStoreSize()
  {
    return set.size();
  }

  @Override
  protected E[] backingStoreToArray(E[] a)
  {
    return set.toArray(a);
  }

  @Override
  protected void backingStoreForEach(Consumer<E> consumer)
  {
    if (consumer != null)
    {
      for (E element : set)
      {
        consumer.accept(element);
      }
    }
  }

  @Override
  protected boolean backingStoreContains(E element)
  {
    return set.contains(element);
  }

  @Override
  protected boolean backingStoreAdd(E element)
  {
    return set.add(element);
  }

  @Override
  protected boolean backingStoreRemove(E element)
  {
    return set.remove(element);
  }

  @Override
  protected void backingStoreClear()
  {
    set.clear();
  }
}
