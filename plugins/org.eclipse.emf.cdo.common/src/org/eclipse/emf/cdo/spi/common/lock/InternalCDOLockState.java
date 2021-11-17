/*
 * Copyright (c) 2011-2013, 2015, 2016, 2020, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.common.lock;

import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;

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

  public void addReadLockOwner(CDOLockOwner lockOwner);

  public boolean removeReadLockOwner(CDOLockOwner lockOwner);

  public void setWriteLockOwner(CDOLockOwner lockOwner);

  public void setWriteOptionOwner(CDOLockOwner lockOwner);

  /**
   * @since 4.4
   */
  public boolean removeOwner(CDOLockOwner lockOwner);

  /**
   * @since 4.15
   */
  public boolean remapOwner(CDOLockOwner oldLockOwner, CDOLockOwner newLockOwner);

  /**
   * @since 4.2
   * @deprecated As of 4.5 use {@link InternalCDOLockState#updateFrom(CDOLockState)} instead.
   * The lockedObject field cannot be changed because it is used to compute the hash code.
   * Instantiate a new {@link CDOLockState} object if you want to update the lockedObject field.
   */
  @Deprecated
  public void updateFrom(Object object, CDOLockState source);

  /**
   * Update the {@link CDOLockOwner lockOwners} of this lock state from the one passed in.
   *
   * @since 4.5
   */
  public void updateFrom(CDOLockState source);

  /**
   * @since 4.2
   * @deprecated As of 4.15 no longer used.
   */
  @Deprecated
  public void dispose();
}
