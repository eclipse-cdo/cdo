/*
 * Copyright (c) 2018, 2024 Eike Stepper (Loehne, Germany) and others.
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
 * A CDO {@link CDOException exception} thrown from a <code>CDOResourceNode</code> to indicate
 * an illegal attempt to change the path of the node to a path that already exists.
 *
 * @author Eike Stepper
 * @since 4.8
 */
public final class CDODuplicateResourceException extends CDOException
{
  private static final long serialVersionUID = 1L;

  public CDODuplicateResourceException()
  {
  }

  public CDODuplicateResourceException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public CDODuplicateResourceException(String message)
  {
    super(message);
  }

  public CDODuplicateResourceException(Throwable cause)
  {
    super(cause);
  }
}
