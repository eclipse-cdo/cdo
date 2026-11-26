/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
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

/**
 * Indicates that a signal was aborted or rejected locally because its protocol was deactivated.
 *
 * @author Eike Stepper
 * @since 4.23
 */
public class SignalProtocolDeactivatedException extends IllegalStateException
{
  private static final long serialVersionUID = 1L;

  /**
   * Creates an exception with the standard protocol deactivation message.
   */
  public SignalProtocolDeactivatedException()
  {
    super("Signal protocol was deactivated"); //$NON-NLS-1$
  }

  /**
   * Creates an exception with a message describing the rejected signal operation.
   *
   * @param message
   *          a description of the aborted or rejected operation
   */
  public SignalProtocolDeactivatedException(String message)
  {
    super(message);
  }

  /**
   * Creates an exception with a message and cause.
   *
   * @param message
   *          a description of the aborted or rejected operation
   * @param cause
   *          the underlying cause
   */
  public SignalProtocolDeactivatedException(String message, Throwable cause)
  {
    super(message, cause);
  }

  /**
   * Creates an exception with an underlying cause.
   *
   * @param cause
   *          the underlying cause
   */
  public SignalProtocolDeactivatedException(Throwable cause)
  {
    super(cause);
  }
}
