/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.signal;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class RemoteException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  private boolean whileResponding;

  public RemoteException(Throwable cause, boolean whileResponding)
  {
    super(cause);
    this.whileResponding = whileResponding;
  }

  public RemoteException(String message, boolean whileResponding)
  {
    super(message);
    this.whileResponding = whileResponding;
  }

  public boolean whileResponding()
  {
    return whileResponding;
  }
}
