/*
 * Copyright (c) 2008, 2009, 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.net4j.util.defs;

public class DefException extends RuntimeException
{

  private static final long serialVersionUID = 1L;

  public DefException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public DefException(Throwable cause)
  {
    super(cause);
  }

}
