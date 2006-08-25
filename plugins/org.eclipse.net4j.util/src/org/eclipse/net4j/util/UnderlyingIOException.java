/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util;


public class UnderlyingIOException extends Net4jException
{
  /**
   * 
   */
  private static final long serialVersionUID = 3833179220800386871L;

  /**
   * 
   */
  public UnderlyingIOException()
  {
    super();
  }

  /**
   * @param message
   */
  public UnderlyingIOException(String message)
  {
    super(message);
  }

  /**
   * @param cause
   */
  public UnderlyingIOException(Throwable cause)
  {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public UnderlyingIOException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
