/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418454: adapted from CDO Common
 */
package org.eclipse.net4j.util.security;

/**
 * An unchecked {@link SecurityException security exception} indicating that a user has canceled an attempt to authenticate himself.
 *
 * @author Eike Stepper
 * @since 3.4
 */
public class NotAuthenticatedException extends SecurityException
{
  private static final long serialVersionUID = 1L;

  public NotAuthenticatedException()
  {
  }

  public NotAuthenticatedException(String s)
  {
    super(s);
  }

  public NotAuthenticatedException(Throwable cause)
  {
    super(cause);
  }

  public NotAuthenticatedException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
