/*
 * Copyright (c) 2012, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.util.test;

/**
 * @author Eike Stepper
 * @since 3.3
 */
public final class TestExecuter
{
  private static Object currentTest;

  private TestExecuter()
  {
  }

  public static Object getCurrrentTest()
  {
    return currentTest;
  }

  public static void execute(Object test, Executable executable) throws Throwable
  {
    if (currentTest != null)
    {
      throw new IllegalStateException("Recursive calls are not supported");
    }

    try
    {
      currentTest = test;
      executable.execute();
    }
    finally
    {
      currentTest = null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface Executable
  {
    public void execute() throws Throwable;
  }
}
