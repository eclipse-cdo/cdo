/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.util.CDOException;

/**
 * @author Eike Stepper
 */
public class SessionCreationException extends CDOException
{
  private static final long serialVersionUID = 1L;

  public SessionCreationException()
  {
  }

  public SessionCreationException(String message)
  {
    super(message);
  }

  public SessionCreationException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public SessionCreationException(Throwable cause)
  {
    super(cause);
  }
}
