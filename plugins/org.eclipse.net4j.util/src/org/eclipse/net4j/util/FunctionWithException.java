/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.util;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A function whose operation may throw a checked exception, with adapters for APIs that accept ordinary
 * {@link Function functions}.
 *
 * @param <T> the input type
 * @param <R> the result type
 * @param <E> the checked exception type
 * @author Eike Stepper
 * @since 3.31
 */
@FunctionalInterface
public interface FunctionWithException<T, R, E extends Exception>
{
  /**
   * Applies this function to an input value.
   *
   * @param value the input value
   * @return the function result
   * @throws E if the operation fails
   */
  public R apply(T value) throws E;

  /**
   * Adapts a checked-exception function to a regular {@link Function}. Any caught exception is wrapped in a
   * {@link RuntimeException}.
   *
   * @param function the checked-exception function
   * @return a function that wraps checked exceptions
   */
  public static <T, R, E extends Exception> Function<T, R> function(FunctionWithException<T, R, E> function)
  {
    return function(function, null, null);
  }

  /**
   * Adapts a checked-exception function to a regular {@link Function}. If an exception handler is supplied, exceptions
   * matching {@code exceptionClass} are passed to it. As with {@link ConsumerWithException}, the original exception is
   * then wrapped in a {@link RuntimeException}; exceptions of other types skip the handler and are wrapped directly.
   *
   * @param function the checked-exception function
   * @param exceptionClass the exception type accepted by the handler, or {@code null} when no handler is used
   * @param exceptionHandler the optional handler for the selected exception type
   * @return a function that handles matching exceptions and wraps them
   */
  public static <T, R, E extends Exception> Function<T, R> function(FunctionWithException<T, R, E> function, Class<E> exceptionClass,
      Consumer<E> exceptionHandler)
  {
    return value -> {
      try
      {
        return function.apply(value);
      }
      catch (Exception ex)
      {
        try
        {
          if (exceptionHandler != null)
          {
            exceptionHandler.accept(exceptionClass.cast(ex));
          }
        }
        catch (ClassCastException ignored)
        {
          //$FALL-THROUGH$
        }

        throw new RuntimeException(ex);
      }
    };
  }
}
