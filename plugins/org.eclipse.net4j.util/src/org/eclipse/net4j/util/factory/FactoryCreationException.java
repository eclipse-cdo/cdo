/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.util.factory;

/**
 * @author Eike Stepper
 */
public class FactoryCreationException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public FactoryCreationException()
  {
  }

  public FactoryCreationException(String message)
  {
    super(message);
  }

  public FactoryCreationException(Throwable cause)
  {
    super(cause);
  }

  public FactoryCreationException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
