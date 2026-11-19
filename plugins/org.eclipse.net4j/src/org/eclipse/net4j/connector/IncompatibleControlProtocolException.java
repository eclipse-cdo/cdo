/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.connector;

/**
 * Reports that a peer cannot participate in the negotiated Net4j control protocol.
 * <p>
 * The exception identifies incompatibility failures detected during the mandatory HELLO exchange or while interpreting control
 * messages under the established protocol version. Its cause, when present, retains the decoding or transport failure that exposed the
 * incompatibility.
 *
 * @author Eike Stepper
 * @since 4.23
 */
public class IncompatibleControlProtocolException extends ConnectorException
{
  private static final long serialVersionUID = 1L;

  /**
   * Creates a control-protocol incompatibility with a diagnostic message.
   *
   * @param message the incompatibility diagnostic
   */
  public IncompatibleControlProtocolException(String message)
  {
    super(message);
  }

  /**
   * Creates a control-protocol incompatibility with a diagnostic message and its originating failure.
   *
   * @param message the incompatibility diagnostic
   * @param cause the decoding or transport failure that exposed the incompatibility
   */
  public IncompatibleControlProtocolException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
