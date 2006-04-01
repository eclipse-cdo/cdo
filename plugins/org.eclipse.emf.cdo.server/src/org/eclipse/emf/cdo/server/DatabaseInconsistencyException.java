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
package org.eclipse.emf.cdo.server;


public class DatabaseInconsistencyException extends CdoServerException
{
  /**
   * 
   */
  private static final long serialVersionUID = 3618700786088031544L;

  /**
   * 
   */
  public DatabaseInconsistencyException()
  {
    super();
  }

  /**
   * @param message
   */
  public DatabaseInconsistencyException(String message)
  {
    super(message);
  }

  /**
   * @param cause
   */
  public DatabaseInconsistencyException(Throwable cause)
  {
    super(cause);
  }

  /**
   * @param message
   * @param cause
   */
  public DatabaseInconsistencyException(String message, Throwable cause)
  {
    super(message, cause);
  }
}