/*
 * Copyright (c) 2006, 2007, 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util;

/**
 * Should not be used anymore in favor of {@link AssertionError}. Likely to be deprecated soon.
 *
 * @author Eike Stepper
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ImplementationError extends Error
{
  private static final long serialVersionUID = 1L;

  public ImplementationError()
  {
  }

  public ImplementationError(String message)
  {
    super(message);
  }

  public ImplementationError(String message, Throwable cause)
  {
    super(message, cause);
  }

  public ImplementationError(Throwable cause)
  {
    super(cause);
  }
}
