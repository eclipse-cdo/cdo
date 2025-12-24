/*
 * Copyright (c) 2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.transaction.CDOAutoLocker;

/**
 * A {@link ConcurrentAccessException concurrent access exception} that indicates that the repository can not acquire optimistic locks for some of the locally modified objects.
 * <p>
 * It's usually possible and adequate to attempt to commit the transaction again (optimistic strategy).
 * Pessimistic {@link CDOObject#cdoWriteLock() locks} can help to avoid the problematic situation (see also {@link CDOAutoLocker}) at commit time.
 * <p>
 * The optimistic locking timeout can be configured on the server side:
 *
 * <pre>
    &lt;property name="optimisticLockingTimeout" value="10000"/&gt;
 * </pre>
 *
 * @author Eike Stepper
 * @since 4.2
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class OptimisticLockingException extends ConcurrentAccessException
{
  private static final long serialVersionUID = 1L;

  public OptimisticLockingException(String message)
  {
    super(message);
  }
}
