/*
 * Copyright (c) 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
