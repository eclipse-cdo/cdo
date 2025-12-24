/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * A {@link CommitException commit exception} that indicates problems that are caused by concurrent access to the repository.
 * <p>
 * Subtypes of this exception allow to determine a more specific reason for the problem. They all have in common that it's usually
 * possible and adequate to {@link CDOTransaction#rollback() rollback} the transaction, <i>replay</i> the model modifications and
 * commit the transaction again.
 *
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
}
