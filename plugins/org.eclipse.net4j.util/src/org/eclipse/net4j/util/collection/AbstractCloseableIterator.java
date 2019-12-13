/*
 * Copyright (c) 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.collection;

/**
 * @author Eike Stepper
 * @since 3.7
 */
public abstract class AbstractCloseableIterator<E> extends AbstractIterator<E> implements CloseableIterator<E>
{
  public AbstractCloseableIterator()
  {
  }

  @Override
  protected abstract Object computeNextElement();

  @Override
  public abstract void close();

  @Override
  public abstract boolean isClosed();

  @SuppressWarnings("unchecked")
  public static <T> CloseableIterator<T> emptyCloseable()
  {
    return (CloseableIterator<T>)EmptyIterator.INSTANCE;
  }
}
