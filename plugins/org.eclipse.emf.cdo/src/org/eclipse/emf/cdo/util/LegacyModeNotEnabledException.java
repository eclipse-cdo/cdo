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
package org.eclipse.emf.cdo.util;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public class LegacyModeNotEnabledException extends IllegalStateException
{
  private static final long serialVersionUID = 1L;

  public LegacyModeNotEnabledException()
  {
    this("Legacy mode is not enabled");
  }

  public LegacyModeNotEnabledException(String s)
  {
    super(s);
  }

  public LegacyModeNotEnabledException(Throwable cause)
  {
    super(cause);
  }

  public LegacyModeNotEnabledException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
