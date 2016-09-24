/*
 * Copyright (c) 2004-2016 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author Eike Stepper
 * @since 3.7
 */
public class DelegatingCloseableIterator<E> implements CloseableIterator<E>
{
  private final Iterator<E> delegate;

  private boolean closed;

  public DelegatingCloseableIterator(Iterator<E> delegate)
  {
    this.delegate = delegate;
  }

  public boolean hasNext()
  {
    return !closed && delegate.hasNext();
  }

  public E next()
  {
    if (closed)
    {
      throw new NoSuchElementException();
    }

    return delegate.next();
  }

  public void close()
  {
    closed = true;
  }

  public boolean isClosed()
  {
    return closed;
  }
}
