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
 * A {@link CommitConflictException commit conflict exception} that indicates that the transaction has local {@link CDOTransaction#hasConflict() conflicts}.
 * <p>
 * It's usually possible and adequate to {@link CDOTransaction#rollback() rollback} the transaction, <i>replay</i> the model modifications and
 * commit the transaction again (optimistic strategy). Pessimistic {@link CDOObject#cdoWriteLock() locks} can help to avoid the problematic situation
 * (see also {@link CDOAutoLocker}).
 * <p>
 * Instances of this class indicate commit conflicts that are detected locally by analyzing the {@link org.eclipse.emf.cdo.session.CDOSession.Options#setPassiveUpdateEnabled(boolean) passive updates}
 * that result from commits of other transactions. {@link CDOConflictResolver Conflict resolvers} can help to reduce the risk of local commit conflicts.
 *
 * @author Eike Stepper
 * @since 4.2
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class LocalCommitConflictException extends CommitConflictException
{
  private static final long serialVersionUID = 1L;

  public LocalCommitConflictException(String message)
  {
    super(message);
  }

  @Override
  public boolean isLocal()
  {
    return true;
  }
}
