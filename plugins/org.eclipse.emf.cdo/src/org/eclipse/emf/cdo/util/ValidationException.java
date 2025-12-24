/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

/**
 * A {@link DataIntegrityException data integrity exception} indicating that the attempt
 * to commit a transaction was rejected because of violation of server-side validation checks.
 *
 * @author Christian W. Damus (CEA LIST)
 * @since 4.3
 */
public class ValidationException extends DataIntegrityException
{
  private static final long serialVersionUID = 1L;

  public ValidationException()
  {
  }

  public ValidationException(String message)
  {
    super(message);
  }

  public ValidationException(Throwable cause)
  {
    super(cause);
  }

  public ValidationException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
