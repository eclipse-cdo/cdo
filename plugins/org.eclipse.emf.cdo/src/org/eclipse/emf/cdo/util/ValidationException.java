/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
