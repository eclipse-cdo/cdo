/*
 * Copyright (c) 2022, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.io;

import org.eclipse.net4j.util.ReflectUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An exception that wraps an exception that has been thrown in a different JVM.
 *
 * @author Eike Stepper
 * @since 3.17
 */
public class RemoteException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  private final List<String> exceptionNames;

  private final String remoteStackTrace;

  public RemoteException()
  {
    exceptionNames = Collections.emptyList();
    remoteStackTrace = null;
  }

  public RemoteException(String message)
  {
    super(message);
    exceptionNames = Collections.emptyList();
    remoteStackTrace = null;
  }

  public RemoteException(Throwable cause)
  {
    super(cause);
    exceptionNames = collectExceptionNames(cause);
    remoteStackTrace = formatStackTrace(cause);
  }

  public RemoteException(String message, Throwable cause)
  {
    super(message, cause);
    exceptionNames = collectExceptionNames(cause);
    remoteStackTrace = formatStackTrace(cause);
  }

  /**
   * @since 3.30
   */
  public RemoteException(String message, List<String> exceptionNames, String remoteStackTrace)
  {
    super(message);
    this.exceptionNames = exceptionNames;
    this.remoteStackTrace = remoteStackTrace;
  }

  protected RemoteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
  {
    super(message, cause, enableSuppression, writableStackTrace);
    exceptionNames = collectExceptionNames(cause);
    remoteStackTrace = formatStackTrace(cause);
  }

  /**
   * @since 3.30
   */
  public List<String> getExceptionNames()
  {
    return Collections.unmodifiableList(exceptionNames);
  }

  /**
   * @since 3.30
   */
  public String getRemoteStackTrace()
  {
    return remoteStackTrace;
  }

  public RuntimeException unwrap()
  {
    Throwable cause = getCause();
    if (cause instanceof RuntimeException)
    {
      throw (RuntimeException)cause;
    }

    return this;
  }

  /**
   * @since 3.30
   */
  public static boolean is(Class<? extends Throwable> type, Throwable t)
  {
    if (type != null)
    {
      if (type.isInstance(t))
      {
        return true;
      }

      if (t instanceof RemoteException)
      {
        RemoteException remoteException = (RemoteException)t;

        List<String> exceptionNames = remoteException.getExceptionNames();
        if (exceptionNames.size() > 0 && type.getName().equals(exceptionNames.get(0)))
        {
          return true;
        }

        return is(type, t.getCause());
      }
    }

    return false;
  }

  /**
   * @since 3.30
   */
  public static List<String> collectExceptionNames(Throwable t)
  {
    List<String> exceptionNames = new ArrayList<>();

    while (t != null)
    {
      exceptionNames.add(t.getClass().getName());
      t = t.getCause();
    }

    return exceptionNames;
  }

  /**
   * @since 3.30
   */
  public static String formatStackTrace(Throwable t)
  {
    StringBuilder builder = new StringBuilder();
    ReflectUtil.appendStackTrace(builder, t.getStackTrace());
    return builder.toString();
  }
}
