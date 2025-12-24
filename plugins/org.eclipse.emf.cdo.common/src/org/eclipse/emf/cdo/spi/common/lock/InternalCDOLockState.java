/*
 * Copyright (c) 2011-2013, 2015, 2016, 2020, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.lock;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockDelta;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Caspar De Groot
 * @since 4.1
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOLockState extends CDOLockState
{
  /**
   * @since 4.6
   * @deprecated As of 4.12 no longer supported.
   */
  @Deprecated
  public static final CDOLockState UNLOCKED = null;

  /**
   * @since 4.15
   */
  public CDOLockDelta addOwner(CDOLockOwner owner, LockType type);

  /**
   * @since 4.15
   */
  public CDOLockDelta removeOwner(CDOLockOwner owner, LockType type);

  /**
   * @since 4.15
   */
  public CDOLockDelta[] clearOwner(CDOLockOwner owner);

  /**
   * @since 4.15
   */
  public CDOLockDelta[] remapOwner(CDOLockOwner oldOwner, CDOLockOwner newOwner);

  /**
   * @since 4.15
   */
  public void remapID(CDOID newID);

  /**
   * @deprecated As of 4.15 use {@link #addOwner(CDOLockOwner, LockType) addOwner(owner, LockType.READ)}.
   */
  @Deprecated
  public void addReadLockOwner(CDOLockOwner owner);

  /**
   * @deprecated As of 4.15 use {@link #removeOwner(CDOLockOwner, LockType) removeOwner(owner, LockType.READ)}.
   */
  @Deprecated
  public boolean removeReadLockOwner(CDOLockOwner owner);

  /**
   * @deprecated As of 4.15 use {@link #addOwner(CDOLockOwner, LockType) addOwner(owner, LockType.WRITE)}
   * or {@link #removeOwner(CDOLockOwner, LockType) removeOwner(owner, LockType.WRITE)}.
   */
  @Deprecated
  public void setWriteLockOwner(CDOLockOwner owner);

  /**
   * @deprecated As of 4.15 use {@link #addOwner(CDOLockOwner, LockType) addOwner(owner, LockType.OPTION)}.
   * or {@link #removeOwner(CDOLockOwner, LockType) removeOwner(owner, LockType.OPTION)}.
   */
  @Deprecated
  public void setWriteOptionOwner(CDOLockOwner owner);

  /**
   * @since 4.4
   * @deprecated As of 4.16 use {@link #clearOwner(CDOLockOwner)}.
   */
  @Deprecated
  public boolean removeOwner(CDOLockOwner owner);

  /**
   * Update the {@link CDOLockOwner lockOwners} of this lock state from the one passed in.
   *
   * @since 4.5
   * @deprecated As of 4.15 not supported anymore.
   */
  @Deprecated
  public void updateFrom(CDOLockState source);

  /**
   * @since 4.2
   * @deprecated As of 4.5 not supported anymore.
   */
  @Deprecated
  public void updateFrom(Object object, CDOLockState source);

  /**
   * @since 4.2
   * @deprecated As of 4.15 not supported anymore.
   */
  @Deprecated
  public void dispose();
}
