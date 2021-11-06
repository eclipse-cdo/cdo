/*
 * Copyright (c) 2008, 2011-2014 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.signal;

/**
 * An exception that wraps an exception that has been thrown during the execution of a remote {@link SignalReactor signal}.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public class RemoteException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  private final boolean whileResponding;

  private final transient RequestWithConfirmation<?> localRequest;

  private StackTraceElement[] localStackTrace;

  private final String originalMessage;

  /**
   * @since 4.0
   */
  public RemoteException(Throwable remoteCause, RequestWithConfirmation<?> localRequest, boolean whileResponding)
  {
    super(remoteCause);
    this.localRequest = localRequest;
    this.whileResponding = whileResponding;
    originalMessage = remoteCause.getMessage();
  }

  /**
   * @since 4.13
   */
  public RemoteException(String message, String originalMessage, boolean whileResponding)
  {
    super(message);
    this.originalMessage = originalMessage;
    this.whileResponding = whileResponding;
    localRequest = null;
  }

  public RemoteException(String message, boolean whileResponding)
  {
    this(message, getFirstLine(message), whileResponding);
  }

  public boolean whileResponding()
  {
    return whileResponding;
  }

  /**
   * @since 4.13
   */
  public final String getOriginalMessage()
  {
    return originalMessage;
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

  static String getFirstLine(String message)
  {
    if (message == null)
    {
      return null;
    }

    int nl = message.indexOf('\n');
    if (nl == -1)
    {
      nl = message.length();
    }

    if (nl > 100)
    {
      nl = 100;
    }

    return message.substring(0, nl);
  }
}
