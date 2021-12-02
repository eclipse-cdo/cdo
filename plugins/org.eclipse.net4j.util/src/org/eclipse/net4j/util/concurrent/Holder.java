/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author Eike Stepper
 * @since 3.16
 */
public final class Holder<T> implements Consumer<T>, Supplier<T>
{
  private T element;

  public Holder(T element)
  {
    set(element);
  }

  public Holder()
  {
  }

  @Override
  public T get()
  {
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
