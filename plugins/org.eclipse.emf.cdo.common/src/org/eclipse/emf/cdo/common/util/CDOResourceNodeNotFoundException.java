/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
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
 * @author Eike Stepper
 * @since 4.6
 */
public final class CDOResourceNodeNotFoundException extends CDOException
{
  private static final long serialVersionUID = 1L;

  public CDOResourceNodeNotFoundException()
  {
  }

  public CDOResourceNodeNotFoundException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public CDOResourceNodeNotFoundException(String message)
  {
    super(message);
  }

  public CDOResourceNodeNotFoundException(Throwable cause)
  {
    super(cause);
  }
}
