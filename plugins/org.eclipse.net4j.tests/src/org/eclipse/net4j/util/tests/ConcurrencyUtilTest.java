/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

/**
 * Tests for {@link ConcurrencyUtil}.
 *
 * @author Eike Stepper
 */
public class ConcurrencyUtilTest extends AbstractOMTest
{
  public void testRestoreInterrupt()
  {
    try
    {
      ConcurrencyUtil.restoreInterrupt(new InterruptedException());
      assertTrue(Thread.currentThread().isInterrupted());
    }
    finally
    {
      Thread.interrupted();
    }
  }

  public void testIgnoreNonInterrupt()
  {
    try
    {
      ConcurrencyUtil.restoreInterrupt(new RuntimeException());
      assertFalse(Thread.currentThread().isInterrupted());
    }
    finally
    {
      Thread.interrupted();
    }
  }

  public void testIgnoreNestedInterrupt()
  {
    try
    {
      ConcurrencyUtil.restoreInterrupt(new RuntimeException(new InterruptedException()));
      assertFalse(Thread.currentThread().isInterrupted());
    }
    finally
    {
      Thread.interrupted();
    }
  }
}
