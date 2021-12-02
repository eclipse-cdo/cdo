/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author Eike Stepper
 * @since 3.16
 */
public final class Access implements AutoCloseable
{
  private final Lock lock;

  public Access(Lock lock)
  {
    this.lock = lock;
  }

  public Lock getLock()
  {
    return lock;
  }

  public Condition newCondition()
  {
    return lock.newCondition();
  }

  public Access access()
  {
    lock.lock();
    return this;
  }

  @Override
  public void close()
  {
    lock.unlock();
  }
}
