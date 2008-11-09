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
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDOLock extends Lock
{
  public static final int WAIT = RWLockManager.WAIT;

  public static final int NO_WAIT = RWLockManager.NO_WAIT;

  /**
   * Return true if it is currently lock.
   */
  public boolean isLocked();

  public RWLockManager.LockType getType();
}
