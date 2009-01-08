/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo;

import org.eclipse.net4j.util.concurrent.RWLockManager;

import java.util.concurrent.locks.Lock;

/**
 * TODO Simon: JavaDoc
 * 
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOLock extends Lock
{
  /**
   * TODO Simon: JavaDoc
   */
  public static final int WAIT = RWLockManager.WAIT;

  /**
   * TODO Simon: JavaDoc
   */
  public static final int NO_WAIT = RWLockManager.NO_WAIT;

  /**
   * Returns <code>true</code> if this lock is currently lock, <code>false</code> otherwise.
   */
  public boolean isLocked();

  /**
   * TODO Simon: JavaDoc
   */
  public RWLockManager.LockType getType();
}
