/*
 * Copyright (c) 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;

import org.eclipse.emf.spi.cdo.InternalCDOObject;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class CDOObjectWrapper extends CDOObjectWrapperBase implements InternalCDOObject
{
  public CDOObjectWrapper()
  {
  }

  /**
   * @since 2.0
   */
  @Override
  public CDOLock cdoReadLock()
  {
    return CDOObjectImpl.createLock(this, LockType.READ);
  }

  /**
   * @since 2.0
   */
  @Override
  public CDOLock cdoWriteLock()
  {
    return CDOObjectImpl.createLock(this, LockType.WRITE);
  }

  /**
   * @since 4.1
   */
  @Override
  public CDOLock cdoWriteOption()
  {
    return CDOObjectImpl.createLock(this, LockType.OPTION);
  }

  @Override
  public synchronized CDOLockState cdoLockState()
  {
    return CDOObjectImpl.getLockState(this);
  }

  public abstract void cdoInternalRollback(InternalCDORevision revision);
}
