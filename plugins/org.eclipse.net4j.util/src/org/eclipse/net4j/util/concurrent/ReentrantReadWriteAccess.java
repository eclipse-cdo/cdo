/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Eike Stepper
 * @since 3.16
 */
public final class ReentrantReadWriteAccess
{
  private final ReentrantReadWriteLock lock;

  private final Access readAccess;

  private final Access writeAccess;

  public ReentrantReadWriteAccess(boolean fair)
  {
    lock = new ReentrantReadWriteLock(fair);
    readAccess = new Access(lock.readLock());
    writeAccess = new Access(lock.writeLock());
  }

  public ReentrantReadWriteAccess()
  {
    this(false);
  }

  public ReentrantReadWriteLock getLock()
  {
    return lock;
  }

  public Access readAccess()
  {
    return readAccess;
  }

  public Access writeAccess()
  {
    return writeAccess;
  }

  public Condition newCondition()
  {
    return writeAccess.newCondition();
  }
}
