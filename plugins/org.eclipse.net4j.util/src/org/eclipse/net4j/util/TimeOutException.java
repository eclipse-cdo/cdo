/*******************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Sympedia Methods and Tools.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *******************************************************************************/
package org.eclipse.net4j.util;


public class TimeOutException extends Net4jException
{
  /**
   * 
   */
  private static final long serialVersionUID = 3977295508371158066L;

  /**
   * 
   */
  public TimeOutException()
  {
    super();
  }

  /**
   * @param message
   */
  public TimeOutException(String message)
  {
    super(message);
  }

  /**
   * @param cause
   */
  public TimeOutException(Throwable cause)
  {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public TimeOutException(String message, Throwable cause)
  {
    super(message, cause);
  }
}