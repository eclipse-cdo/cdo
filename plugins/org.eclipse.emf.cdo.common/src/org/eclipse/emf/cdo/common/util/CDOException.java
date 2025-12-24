/*
 * Copyright (c) 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.util;

/**
 * An unchecked exception for general CDO purposes.
 *
 * @author Eike Stepper
 */
public class CDOException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public CDOException()
  {
  }

  public CDOException(String message)
  {
    super(message);
  }

  public CDOException(Throwable cause)
  {
    super(cause);
  }

  public CDOException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
