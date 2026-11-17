/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.FunctionWithException;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * Tests adapters from checked-exception functions to ordinary functions.
 *
 * @author Eike Stepper
 */
public class FunctionWithExceptionTest extends AbstractOMTest
{
  public void testFunctionResult()
  {
    Function<String, Integer> function = FunctionWithException.function(String::length);
    assertEquals(Integer.valueOf(4), function.apply("test")); //$NON-NLS-1$
  }

  public void testCheckedExceptionIsWrapped()
  {
    Function<String, String> function = FunctionWithException.function(value -> {
      throw new IOException(value);
    });

    try
    {
      function.apply("failure"); //$NON-NLS-1$
      fail("Expected checked exception to be wrapped"); //$NON-NLS-1$
    }
    catch (RuntimeException ex)
    {
      assertTrue(ex.getCause() instanceof IOException);
      assertEquals("failure", ex.getCause().getMessage()); //$NON-NLS-1$
    }
  }

  public void testTypedExceptionHandlerRunsBeforeWrapping()
  {
    AtomicReference<IOException> handled = new AtomicReference<>();
    Function<String, String> function = FunctionWithException.function(value -> {
      throw new IOException(value);
    }, IOException.class, handled::set);

    try
    {
      function.apply("handled"); //$NON-NLS-1$
      fail("Expected checked exception to be wrapped"); //$NON-NLS-1$
    }
    catch (RuntimeException ex)
    {
      assertSame(handled.get(), ex.getCause());
      assertEquals("handled", handled.get().getMessage()); //$NON-NLS-1$
    }
  }

  public void testMismatchedExceptionSkipsHandler()
  {
    AtomicReference<IOException> handled = new AtomicReference<>();
    Function<String, String> function = FunctionWithException.function(value -> {
      throw new IllegalStateException(value);
    }, IOException.class, handled::set);

    try
    {
      function.apply("unchecked"); //$NON-NLS-1$
      fail("Expected exception to be wrapped"); //$NON-NLS-1$
    }
    catch (RuntimeException ex)
    {
      assertNull(handled.get());
      assertTrue(ex.getCause() instanceof IllegalStateException);
      assertEquals("unchecked", ex.getCause().getMessage()); //$NON-NLS-1$
    }
  }
}
