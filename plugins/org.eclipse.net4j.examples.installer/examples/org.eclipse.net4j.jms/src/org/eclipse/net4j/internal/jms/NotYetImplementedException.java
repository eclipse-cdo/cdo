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
package org.eclipse.net4j.internal.jms;

public class NotYetImplementedException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public NotYetImplementedException()
  {
  }

  public NotYetImplementedException(String message)
  {
    super(message);
  }

  public NotYetImplementedException(Throwable cause)
  {
    super(cause);
  }

  public NotYetImplementedException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
