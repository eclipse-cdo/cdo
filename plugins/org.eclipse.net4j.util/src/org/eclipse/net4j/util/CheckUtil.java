/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * Provides static methods that check object states and invocation arguments.
 *
 * @author Eike Stepper
 */
public final class CheckUtil
{
  /**
   * @since 3.7
   */
  public static final boolean HAS_DEBUGGER_ATTACHED = hasDebuggerAttached();

  /**
   * @since 3.7
   */
  public static final boolean SANITIZE_TIMEOUT = OMPlatform.INSTANCE.isProperty("org.eclipse.net4j.util.CheckUtil.sanitizeTimeout", HAS_DEBUGGER_ATTACHED);

  private static final long ONE_DAY = 1000 * 60 * 60 * 24;

  private static int counter;

  private CheckUtil()
  {
  }

  public static void checkNull(Object handle, String msg) throws NullPointerException
  {
    if (handle == null)
    {
      throw new NullPointerException(msg);
    }
  }

  public static void checkArg(boolean expr, String msg) throws IllegalArgumentException
  {
    if (!expr)
    {
      throw new IllegalArgumentException(msg);
    }
  }

  public static void checkArg(Object handle, String handleName) throws IllegalArgumentException
  {
    if (handle == null)
    {
      throw new IllegalArgumentException(handleName + " is null"); //$NON-NLS-1$
    }
  }

  public static void checkState(boolean expr, String msg) throws IllegalStateException
  {
    if (!expr)
    {
      throw new IllegalStateException(msg);
    }
  }

  public static void checkState(Object handle, String handleName) throws IllegalStateException
  {
    if (handle == null)
    {
      throw new IllegalStateException(handleName + " is null"); //$NON-NLS-1$
    }
  }

  /**
   * @since 3.6
   */
  public static void countUp(String message)
  {
    IOUtil.OUT().println(message + (++counter));
  }

  /**
   * @since 3.6
   */
  public static void countDown(String message)
  {
    IOUtil.OUT().println(message + --counter);
  }

  /**
   *
   * @since 3.7
   */
  public static long sanitizeTimeout(long timeout)
  {
    if (SANITIZE_TIMEOUT)
    {
      timeout = Math.max(timeout, ONE_DAY);
    }

    return timeout;
  }

  private static boolean hasDebuggerAttached()
  {
    try
    {
      String property = OMPlatform.INSTANCE.getProperty("org.eclipse.net4j.util.CheckUtil.hasDebuggerAttached");
      if (property != null)
      {
        return Boolean.parseBoolean(property);
      }

      RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
      String jvmArguments = runtimeMXBean.getInputArguments().toString();
      return jvmArguments.contains("-agentlib:jdwp");
    }
    catch (Throwable ex)
    {
      return false;
    }
  }
}
