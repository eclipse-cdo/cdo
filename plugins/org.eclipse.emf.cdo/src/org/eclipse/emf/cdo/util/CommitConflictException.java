/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.transaction.CDOConflictResolver;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * A {@link ConcurrentAccessException concurrent access exception} that indicates that some of the local modifications are based on old revisions
 * because other transactions have intermittently committed their modifications.
 * <p>
 * It's usually possible and adequate to {@link CDOTransaction#rollback() rollback} the transaction, <i>replay</i> the model modifications and
 * commit the transaction again (optimistic strategy). Pessimistic {@link CDOObject#cdoWriteLock() locks} can help to avoid the problematic situation
 * (see also {@link CDOAutoLocker}).
 * <p>
 * Instances of this class indicate commit conflicts that are detected in the repository. They can also occur if a {@link CDOConflictResolver conflict resolver}
 * is used locally (network race condition).
 * <p>
 * For detection of local commit conflicts see {@link LocalCommitConflictException}.
 *
 * @author Eike Stepper
 * @since 4.2
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class CommitConflictException extends ConcurrentAccessException
{
  private static final long serialVersionUID = 1L;

  public CommitConflictException(String message)
  {
    super(message);
  }
}
