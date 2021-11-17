/*
 * Copyright (c) 2011, 2012, 2020, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionConfigurationImpl;
import org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionImpl;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eike Stepper
 */
public class AbstractLockingTest extends AbstractCDOTest
{
  public static final long NO_DELAY = -1L;

  private final AtomicInteger activeLockNotifications = new AtomicInteger();

  public AbstractLockingTest()
  {
  }

  protected long getInvalidationDelay()
  {
    return NO_DELAY;
  }

  @Override
  protected CDONet4jSessionConfiguration createNet4jSessionConfiguration(String repositoryName)
  {
    return new LockingSessionConfiguration();
  }

  protected final void waitForActiveLockNotifications() throws InterruptedException
  {
    long end = System.currentTimeMillis() + DEFAULT_TIMEOUT;

    while (System.currentTimeMillis() < end)
    {
      synchronized (activeLockNotifications)
      {
        if (activeLockNotifications.get() <= 0)
        {
          return;
        }

        activeLockNotifications.wait(100L);
      }
    }
  }

  protected static CDOLock lockRead(EObject object) throws InterruptedException
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    CDOLock lock = cdoObject.cdoReadLock();
    assertEquals(true, lock.tryLock(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    return lock;
  }

  protected static CDOLock lockWrite(EObject object) throws InterruptedException
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    CDOLock lock = cdoObject.cdoWriteLock();
    assertEquals(true, lock.tryLock(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    return lock;
  }

  protected static CDOLock lockOption(EObject object) throws InterruptedException
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    CDOLock lock = cdoObject.cdoWriteOption();
    assertEquals(true, lock.tryLock(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
    return lock;
  }

  protected static CDOLock unlockRead(EObject object) throws InterruptedException
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    CDOLock lock = cdoObject.cdoReadLock();
    lock.unlock();
    return lock;
  }

  protected static CDOLock unlockWrite(EObject object) throws InterruptedException
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    CDOLock lock = cdoObject.cdoWriteLock();
    lock.unlock();
    return lock;
  }

  protected static CDOLock unlockOption(EObject object) throws InterruptedException
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    CDOLock lock = cdoObject.cdoWriteOption();
    lock.unlock();
    return lock;
  }

  protected static CDOLock assertReadLock(boolean expected, EObject object)
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    CDOLock lock = cdoObject.cdoReadLock();
    assertEquals(expected, lock.isLocked());
    return lock;
  }

  protected static CDOLock assertWriteLock(boolean expected, EObject object)
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    CDOLock lock = cdoObject.cdoWriteLock();
    assertEquals(expected, lock.isLocked());
    return lock;
  }

  protected static void assertOptionLock(boolean expected, EObject object)
  {
    CDOObject cdoObject = CDOUtil.getCDOObject(object);
    CDOLock lock = cdoObject.cdoWriteOption();
    assertEquals(expected, lock.isLocked());
  }

  /**
   * @author Eike Stepper
   */
  protected class LockingSessionConfiguration extends CDONet4jSessionConfigurationImpl
  {
    @Override
    public InternalCDOSession createSession()
    {
      return new CDONet4jSessionImpl()
      {
        @Override
        public void handleLockNotification(CDOLockChangeInfo lockChangeInfo, InternalCDOView sender, boolean async)
        {
          synchronized (activeLockNotifications)
          {
            activeLockNotifications.incrementAndGet();
            activeLockNotifications.notifyAll();
          }

          super.handleLockNotification(lockChangeInfo, sender, async);
        }

        @Override
        protected void doHandleLockNotification(CDOLockChangeInfo lockChangeInfo, InternalCDOView sender, boolean notifyViews)
        {
          super.doHandleLockNotification(lockChangeInfo, sender, notifyViews);

          synchronized (activeLockNotifications)
          {
            activeLockNotifications.decrementAndGet();
            activeLockNotifications.notifyAll();
          }
        }

        @Override
        public void invalidate(InvalidationData invalidationData)
        {
          long delay = getInvalidationDelay();
          if (delay != NO_DELAY)
          {
            sleep(delay); // Delay the invalidation handling to give lock notifications a chance to overtake.
          }

          super.invalidate(invalidationData);
        }

      };
    }
  }
}
