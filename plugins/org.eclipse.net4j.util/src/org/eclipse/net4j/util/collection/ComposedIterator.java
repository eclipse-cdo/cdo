/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
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

/**
 * An iterator that is composed of multiple delegate iterators.
 *
 * @author Eike Stepper
 * @since 3.4
 */
public class ComposedIterator<T> extends AbstractIterator<T>
{
  private final Iterator<T>[] delegates;

  private int index;

  public ComposedIterator(Iterator<T>... delegates)
  {
    this.delegates = delegates;
  }

  @Override
  protected Object computeNextElement()
  {
    Iterator<T> delegate = delegates[index];
    while (delegate.hasNext())
    {
      return delegate.next();
    }

    if (++index < delegates.length)
    {
      return computeNextElement();
    }

    return END_OF_DATA;
  }
}
