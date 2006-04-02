/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server;


import org.eclipse.emf.cdo.core.CDOException;


public class ResourceNotFoundException extends CDOException
{
  /**
   * 
   */
  private static final long serialVersionUID = 3545239124224062515L;

  public ResourceNotFoundException()
  {
    super();
  }

  public ResourceNotFoundException(String message)
  {
    super(message);
  }

  public ResourceNotFoundException(Throwable cause)
  {
    super(cause);
  }

  public ResourceNotFoundException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
