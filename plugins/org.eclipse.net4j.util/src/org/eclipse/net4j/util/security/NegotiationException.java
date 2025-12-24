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
package org.eclipse.net4j.util.security;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class NegotiationException extends SecurityException
{
  private static final long serialVersionUID = 1L;

  public NegotiationException()
  {
  }

  public NegotiationException(String s)
  {
    super(s);
  }

  public NegotiationException(Throwable cause)
  {
    super(cause);
  }

  public NegotiationException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
