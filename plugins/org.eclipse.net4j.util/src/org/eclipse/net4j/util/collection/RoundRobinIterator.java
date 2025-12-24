/*
 * Copyright (c) 2018, 2019 Eike Stepper (Loehne, Germany) and others.
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

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Eike Stepper
 * @since 3.9
 */
public class RoundRobinIterator<T> implements Iterator<T>
{
  private final Collection<T> collection;

  private Iterator<T> delegate;

  public RoundRobinIterator(Collection<T> collection)
  {
    this.collection = collection;
    if (!collection.isEmpty())
    {
      delegate = collection.iterator();
    }
  }

  @Override
  public boolean hasNext()
  {
    return delegate != null;
  }

  @Override
  public T next()
  {
    if (delegate == null)
    {
      throw new NoSuchElementException();
    }

    if (!delegate.hasNext())
    {
      delegate = collection.iterator();
    }

    return delegate.next();
  }

  @Override
  public void remove()
  {
    throw new UnsupportedOperationException();
  }
}
