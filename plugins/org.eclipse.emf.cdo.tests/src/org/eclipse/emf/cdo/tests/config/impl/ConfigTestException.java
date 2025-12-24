/*
 * Copyright (c) 2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.config.impl;

/**
 * @author Eike Stepper
 */
public class ConfigTestException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public ConfigTestException()
  {
  }

  public ConfigTestException(String message)
  {
    super(message);
  }

  public ConfigTestException(Throwable cause)
  {
    super(cause);
  }

  public ConfigTestException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
