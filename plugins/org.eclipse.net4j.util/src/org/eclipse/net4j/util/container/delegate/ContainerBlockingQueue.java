/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.container.delegate;

import org.eclipse.net4j.util.container.IContainerDelta;
import org.eclipse.net4j.util.event.IListener;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * A default implementation of a {@link IContainerBlockingQueue container blocking queue}.
 *
 * @author Eike Stepper
 */
public class ContainerBlockingQueue<E> extends ContainerQueue<E> implements IContainerBlockingQueue<E>
{
  public ContainerBlockingQueue(BlockingQueue<E> delegate)
  {
    super(delegate);
  }

  @Override
  public BlockingQueue<E> getDelegate()
  {
    return (BlockingQueue<E>)super.getDelegate();
  }

  /**
   * @category WRITE
   */
  @Override
  public int drainTo(Collection<? super E> c)
  {
    int drainTo = getDelegate().drainTo(c);

    IListener[] listeners = getListeners();
    if (listeners.length != 0)
    {
      fireEvent(createEvent(c, IContainerDelta.Kind.REMOVED), listeners);
    }

    return drainTo;
  }

  /**
   * @category WRITE
   */
  @Override
  public int drainTo(Collection<? super E> c, int maxElements)
  {
    int drainTo = getDelegate().drainTo(c, maxElements);
    fireEvent(createEvent(c, IContainerDelta.Kind.REMOVED));
    return drainTo;
  }

  /**
   * @category WRITE
   */
  @Override
  public boolean offer(E o, long timeout, TimeUnit unit) throws InterruptedException
  {
    boolean modified = getDelegate().offer(o, timeout, unit);
    if (modified)
    {
      fireAddedEvent(o);
    }

    return modified;
  }

  /**
   * @category WRITE
   */
  @Override
  public E poll(long timeout, TimeUnit unit) throws InterruptedException
  {
    E removed = getDelegate().poll(timeout, unit);
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
  public void put(E o) throws InterruptedException
  {
    getDelegate().put(o);
    fireAddedEvent(o);
  }

  /**
   * @category READ
   */
  @Override
  public int remainingCapacity()
  {
    return getDelegate().remainingCapacity();
  }

  /**
   * @category WRITE
   */
  @Override
  public E take() throws InterruptedException
  {
    E element = getDelegate().take();
    fireRemovedEvent(element);
    return element;
  }
}
