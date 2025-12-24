/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

/**
 * An interface with a {@link #run()} method that can propagate checked exceptions.
 *
 * @author Eike Stepper
 * @since 3.12
 */
@FunctionalInterface
public interface RunnableWithException
{
  public void run() throws Exception;

  public static void forkAndWait(RunnableWithException runnable) throws Exception
  {
    Exception[] exception = { null };
    Thread thread = new Thread(runnable.getClass().getName())
    {
      @Override
      public void run()
      {
        try
        {
          runnable.run();
        }
        catch (Exception ex)
        {
          exception[0] = ex;
        }
      }
    };

    thread.start();
    thread.join();

    if (exception[0] != null)
    {
      throw exception[0];
    }
  }
}
