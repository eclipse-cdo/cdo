/*
 * Copyright (c) 2008, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.container.delegate;

import java.util.Queue;

/**
 * A default implementation of a {@link IContainerQueue container queue}.
 *
 * @author Eike Stepper
 */
public class ContainerQueue<E> extends ContainerCollection<E> implements IContainerQueue<E>
{
  public ContainerQueue(Queue<E> delegate)
  {
    super(delegate);
  }

  @Override
  public Queue<E> getDelegate()
  {
    return (Queue<E>)super.getDelegate();
  }

  /**
   * @category READ
   */
  @Override
  public E element()
  {
    return getDelegate().element();
  }

  /**
   * @category WRITE
   */
  @Override
  public boolean offer(E o)
  {
    boolean modified = getDelegate().offer(o);
    if (modified)
    {
      fireAddedEvent(o);
    }

    return modified;
  }

  /**
   * @category READ
   */
  @Override
  public E peek()
  {
    return getDelegate().element();
  }

  /**
   * @category WRITE
   */
  @Override
  public E poll()
  {
    E removed = getDelegate().poll();
    if (removed != null)
    {
      fireRemovedEvent(removed);
    }

    return removed;
  }

  /**
   * @category WRITE
   */
  @Override
  public E remove()
  {
    E removed = getDelegate().remove();
    if (removed != null)
    {
      fireRemovedEvent(removed);
    }

    return removed;
  }
}
