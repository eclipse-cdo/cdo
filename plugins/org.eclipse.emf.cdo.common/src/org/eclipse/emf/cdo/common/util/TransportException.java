/*
 * Copyright (c) 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.util;

/**
 * An unchecked exception that indicates transport-level problems.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 */
public class TransportException extends CDOException
{
  private static final long serialVersionUID = 1L;

  public TransportException()
  {
  }

  public TransportException(String message)
  {
    super(message);
  }

  public TransportException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public TransportException(Throwable cause)
  {
    super(cause);
  }
}
