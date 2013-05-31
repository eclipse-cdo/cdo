/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

/**
 * @author Eike Stepper
 * @since 4.2
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class ConcurrentAccessException extends CommitException
{
  private static final long serialVersionUID = 1L;

  public ConcurrentAccessException()
  {
  }

  public ConcurrentAccessException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public ConcurrentAccessException(String message)
  {
    super(message);
  }

  public ConcurrentAccessException(Throwable cause)
  {
    super(cause);
  }

  @Override
  public boolean isFatal()
  {
    return false;
  }
}
