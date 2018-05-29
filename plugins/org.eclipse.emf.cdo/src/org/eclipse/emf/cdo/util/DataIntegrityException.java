/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * A {@link CommitException commit exception} that indicates data integrity problems.
 * <p>
 * Subtypes of this exception allow to determine a more specific reason for the problem. They all have in common that it's usually
 * <b>not</b> adequate to {@link CDOTransaction#rollback() rollback} the transaction, <i>replay</i> the model modifications and commit the transaction again.
 *
 * @author Eike Stepper
 * @since 4.2
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class DataIntegrityException extends CommitException
{
  private static final long serialVersionUID = 1L;

  public DataIntegrityException()
  {
  }

  public DataIntegrityException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public DataIntegrityException(String message)
  {
    super(message);
  }

  public DataIntegrityException(Throwable cause)
  {
    super(cause);
  }
}
