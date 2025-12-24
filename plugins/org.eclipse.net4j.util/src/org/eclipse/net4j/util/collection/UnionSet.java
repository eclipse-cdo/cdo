/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.collection;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 3.14
 */
public class UnionSet<E> extends AbstractSet<E>
{
  private final Set<E> set1;

  private final Set<E> set2;

  public UnionSet(Collection<? extends E> c1, Collection<? extends E> c2)
  {
    set1 = getSet(c1);
    set2 = getSet(c2);
  }

  @Override
  public Iterator<E> iterator()
  {
    return new ComposedIterator<>(set1.iterator(), new AbstractFilteredIterator<E>(set2.iterator())
    {
      @Override
      protected boolean isValid(E element2)
      {
        return !set1.contains(element2);
      }
    });
  }

  @Override
  public int size()
  {
    int size = set1.size();

    for (E element2 : set2)
    {
      if (!set1.contains(element2))
      {
        ++size;
      }
    }

    return size;
  }

  @SuppressWarnings("unchecked")
  private Set<E> getSet(Collection<? extends E> c)
  {
    if (c instanceof Set)
    {
      return (Set<E>)c;
    }

    return new HashSet<>(c);
  }
}
