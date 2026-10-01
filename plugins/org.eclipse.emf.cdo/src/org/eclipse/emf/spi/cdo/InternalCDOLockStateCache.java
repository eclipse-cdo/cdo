/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.lock.CDOLockState;

import java.util.Collection;

/**
 * @author Eike Stepper
 * @since 4.31
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOLockStateCache extends CDOLockStateCache
{
  /**
   * Adds lock states without the historical best-effort exception handling.
   * <p>
   * This method is for cache-only query actions whose sequence baseline has been accepted. A failure must escape so
   * the lock change sequencer can suspend and request an authoritative snapshot.
   *
   * @param branch the branch whose cache entries are updated
   * @param lockStates the complete states captured by the query
   */
  public void addLockStatesStrict(CDOBranch branch, Collection<? extends CDOLockState> lockStates);

  public void replaceSnapshot(Collection<? extends CDOLockState> lockStates);
}
