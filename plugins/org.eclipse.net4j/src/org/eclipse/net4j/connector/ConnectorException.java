/*
 * Copyright (c) 2007, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.connector;

/**
 * Thrown by an {@link IConnector} to indicate connection problems.
 *
 * @author Eike Stepper
 */
public class ConnectorException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public ConnectorException()
  {
  }

  public ConnectorException(String message)
  {
    super(message);
  }

  public ConnectorException(Throwable cause)
  {
    super(cause);
  }

  public ConnectorException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
