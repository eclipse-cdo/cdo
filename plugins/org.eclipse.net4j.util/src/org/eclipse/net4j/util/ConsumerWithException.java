/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

import java.util.function.Consumer;

/**
 * An interface with an {@link #accept(Object)} method that can propagate checked exceptions.
 *
 * @author Eike Stepper
 * @since 3.12
 */
@FunctionalInterface
public interface ConsumerWithException<T, E extends Exception>
{
  public void accept(T t) throws E;

  public static <T, E extends Exception> Consumer<T> consumer(ConsumerWithException<T, E> consumerWithException, Class<E> exceptionClass,
      Consumer<E> exceptionHandler)
  {
    return t -> {
      try
      {
        consumerWithException.accept(t);
      }
      catch (Exception ex)
      {
        try
        {
          if (exceptionHandler != null)
          {
            E exCast = exceptionClass.cast(ex);
            exceptionHandler.accept(exCast);
          }
        }
        catch (ClassCastException ccEx)
        {
          //$FALL-THROUGH$
        }

        throw new RuntimeException(ex);
      }
    };
  }

  public static <T, E extends Exception> Consumer<T> consumer(ConsumerWithException<T, E> consumerWithException)
  {
    return consumer(consumerWithException, null, null);
  }
}
