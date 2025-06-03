/*
 * Copyright (c) 2018, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
