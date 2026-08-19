/*
 * Copyright (c) 2008, 2011-2014, 2021, 2022, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.signal;

import java.util.List;

/**
 * An exception that wraps an exception that has been thrown during the execution of a remote {@link SignalReactor signal}.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public class RemoteException extends org.eclipse.net4j.util.io.RemoteException
{
  private static final long serialVersionUID = 1L;

  private final boolean whileResponding;

  private final transient RequestWithConfirmation<?> localRequest;

  private StackTraceElement[] localStackTrace;

  /**
   * @since 4.0
   */
  public RemoteException(Throwable remoteCause, RequestWithConfirmation<?> localRequest, boolean whileResponding)
  {
    super(remoteCause.getMessage(), remoteCause);
    this.localRequest = localRequest;
    this.whileResponding = whileResponding;
  }

  public RemoteException(String message, boolean whileResponding)
  {
    this(message, null, null, whileResponding);
  }

  /**
   * @since 4.22
   */
  public RemoteException(String message, List<String> exceptionNames, String remoteStackTrace, boolean whileResponding)
  {
    super(message, exceptionNames, remoteStackTrace);
    this.whileResponding = whileResponding;
    localRequest = null;
  }

  public boolean whileResponding()
  {
    return whileResponding;
  }

  /**
   * @since 4.0
   */
  public RequestWithConfirmation<?> getLocalRequest()
  {
    return localRequest;
  }

  /**
   * @since 4.0
   */
  public void setLocalStacktrace(StackTraceElement[] stackTrace)
  {
    localStackTrace = stackTrace;
  }

  /**
   * Returns the local stack as it stood at the time that the <i>remote</i> exception was detected <i>locally</i>. Note
   * that no local problem occurred at the point in the code identified by this stacktrace.
   *
   * @since 4.0
   */
  public StackTraceElement[] getLocalStackTrace()
  {
    return localStackTrace;
  }
}
