/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOLock;

import org.eclipse.net4j.util.concurrent.RWLockManager;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class NOOPLockImpl implements CDOLock
{
  public static final NOOPLockImpl INSTANCE = new NOOPLockImpl();

  private NOOPLockImpl()
  {
  }

  public boolean isLocked()
  {
    return false;
  }

  public void lock()
  {
    throw new UnsupportedOperationException();
  }

  public void lockInterruptibly() throws InterruptedException
  {
    throw new UnsupportedOperationException();
  }

  public Condition newCondition()
  {
    return null;
  }

  public boolean tryLock()
  {
    return false;
  }

  public boolean tryLock(long time, TimeUnit unit) throws InterruptedException
  {
    return false;
  }

  public void unlock()
  {
    throw new UnsupportedOperationException();
  }

  public RWLockManager.LockType getType()
  {
    return null;
  }
}
