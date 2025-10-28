/*
 * Copyright (c) 2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import org.eclipse.net4j.util.ObjectUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A simple holder for a single element that implements {@link Consumer} and {@link Supplier}.
 * <p>
 * This class is not thread-safe.
 *
 * @author Eike Stepper
 * @since 3.16
 */
public final class Holder<T> implements Consumer<T>, Supplier<T>
{
  private final Supplier<T> creator;

  private final T unset;

  private T element;

  public Holder()
  {
    creator = null;
    unset = null;
  }

  public Holder(T element)
  {
    this();
    set(element);
  }

  /**
   * @since 3.29
   */
  public Holder(Supplier<T> creator)
  {
    this(creator, null);
  }

  /**
   * @since 3.29
   */
  public Holder(Supplier<T> creator, T unset)
  {
    this.creator = creator;
    this.unset = unset;
    element = unset;
  }

  /**
   * @since 3.29
   */
  public boolean isSet()
  {
    return element != unset;
  }

  /**
   * @since 3.29
   */
  public T handleIfSet(Consumer<T> consumer)
  {
    T elem = getIfSet();
    ObjectUtil.ifNotNull(elem, consumer);
    return elem;
  }

  /**
   * @since 3.29
   */
  public T getIfSet()
  {
    return isSet() ? element : null;
  }

  @Override
  public T get()
  {
    if (!isSet() && creator != null)
    {
      element = creator.get();
    }

    return element;
  }

  public T set(T element)
  {
    T oldElement = this.element;
    this.element = element;
    return oldElement;
  }

  @Override
  public void accept(T element)
  {
    set(element);
  }
}
