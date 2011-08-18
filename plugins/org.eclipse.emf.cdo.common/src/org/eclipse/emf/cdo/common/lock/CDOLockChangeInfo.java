/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.common.lock;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;

/**
 * Represents a change in the lock state of a set of objects. Instances are meant to be sent from the server to the
 * client for the purpose of notifying the latter.
 * 
 * @author Caspar De Groot
 * @since 4.1
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOLockChangeInfo extends CDOBranchPoint
{
  /**
   * @return The branch at which the lock changes took place, same as <code>getView().getBranch()</code>.
   */
  public CDOBranch getBranch();

  /**
   * @return The repository time at which the lock changes took place. This is only an informal indication; no formal
   *         relation (e.g. an ordering) with commit timestamps is guaranteed.
   */
  public long getTimeStamp();

  /**
   * @return The view, represented as a {@link CDOLockOwner}, that authored the lock changes.
   */
  public CDOLockOwner getLockOwner();

  /**
   * @return The new lock states of the objects that were affected by the change
   */
  public CDOLockState[] getLockStates();

  /**
   * @return the type of lock operation that caused the lock changes
   */
  public Operation getOperation();

  /**
   * Enumerates the possible locking operations.
   * 
   * @author Caspar De Groot
   */
  public enum Operation
  {
    LOCK, UNLOCK
  }
}
