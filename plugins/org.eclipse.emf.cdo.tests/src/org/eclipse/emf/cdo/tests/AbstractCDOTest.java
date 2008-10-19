/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.config.ConfigTest;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.CDOLegacyWrapper;
import org.eclipse.emf.internal.cdo.util.FSMUtil;

import org.eclipse.net4j.tests.AbstractTransportTest;

import org.eclipse.emf.ecore.EObject;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author Eike Stepper
 */
public abstract class AbstractCDOTest extends ConfigTest
{
  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    startTransport();
  }

  public static void assertEquals(Object expected, Object actual)
  {
    // IMPORTANT: Give possible CDOLegacyWrapper a chance for actual, too
    if (actual != null && actual.equals(expected))
    {
      return;
    }

    AbstractTransportTest.assertEquals(expected, actual);
  }

  public static void assertEquals(String message, Object expected, Object actual)
  {
    if (expected == null && actual == null)
    {
      return;
    }

    if (expected != null && expected.equals(actual))
    {
      return;
    }

    // IMPORTANT: Give possible CDOLegacyWrapper a chance for actual, too
    if (actual != null && actual.equals(expected))
    {
      return;
    }

    failNotEquals(message, expected, actual);
  }

  protected static void assertTransient(EObject eObject)
  {
    CDOObject object = CDOUtil.getCDOObject(eObject);
    if (object != null)
    {
      assertEquals(true, FSMUtil.isTransient(object));
      assertNull(object.cdoID());
      assertNull(object.cdoRevision());
      assertNull(object.cdoView());
    }
  }

  protected static void assertInvalid(EObject eObject)
  {
    CDOObject object = CDOUtil.getCDOObject(eObject);
    if (object != null)
    {
      assertEquals(true, FSMUtil.isInvalid(object));
    }
  }

  protected static void assertNotTransient(EObject eObject, CDOView view)
  {
    CDOObject object = FSMUtil.adapt(eObject, view);
    assertEquals(false, FSMUtil.isTransient(object));
    assertNotNull(object.cdoID());
    assertNotNull(object.cdoRevision());
    assertNotNull(object.cdoView());
    if (eObject instanceof CDOResource && ((CDOResource)eObject).isRoot())
    {
      assertNull(object.eResource());
    }
    else
    {
      assertNotNull(object.eResource());
    }
    assertEquals(view, object.cdoView());
    assertEquals(object, view.getObject(object.cdoID(), false));
  }

  protected static void assertNew(EObject eObject, CDOView view)
  {
    CDOObject object = FSMUtil.adapt(eObject, view);
    assertNotTransient(object, view);
    assertEquals(CDOState.NEW, object.cdoState());
  }

  protected static void assertDirty(EObject eObject, CDOView view)
  {
    CDOObject object = FSMUtil.adapt(eObject, view);
    assertNotTransient(object, view);
    assertEquals(CDOState.DIRTY, object.cdoState());
  }

  protected static void assertClean(EObject eObject, CDOView view)
  {
    CDOObject object = FSMUtil.adapt(eObject, view);
    assertNotTransient(object, view);
    assertEquals(CDOState.CLEAN, object.cdoState());
  }

  protected static void assertProxy(EObject eObject)
  {
    CDOObject object = CDOUtil.getCDOObject(eObject);
    if (object != null)
    {
      assertEquals(false, FSMUtil.isTransient(object));
      assertNotNull(object.cdoID());
      assertNotNull(object.cdoView());
      assertEquals(CDOState.PROXY, object.cdoState());
    }
  }

  protected static void assertContent(EObject eContainer, EObject eContained)
  {
    CDOObject container = CDOUtil.getCDOObject(eContainer);
    CDOObject contained = CDOUtil.getCDOObject(eContained);
    if (container != null && contained != null)
    {
      assertEquals(true, container.eContents().contains(contained));
      if (container instanceof CDOResource)
      {
        assertEquals(container, contained.eResource());
        assertEquals(null, contained.eContainer());
        assertEquals(true, ((CDOResource)container).getContents().contains(contained));
      }
      else
      {
        assertEquals(container.eResource(), contained.eResource());
        assertEquals(container, contained.eContainer());
      }
    }
  }

  protected static void assertNotProxy(Object object)
  {
    assertEquals(false, CDOLegacyWrapper.isLegacyProxy(object));
  }

  protected static void assertCreatedTime(EObject eObject)
  {
    CDOObject object = CDOUtil.getCDOObject(eObject);
    if (object != null)
    {
      CDOView view = object.cdoView();
      if (view instanceof CDOTransaction)
      {
        CDOTransaction transaction = (CDOTransaction)view;
        assertEquals(transaction.getLastCommitTime(), object.cdoRevision().getCreated());
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static interface ITimeOuter
  {
    public boolean timedOut() throws InterruptedException;
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class PollingTimeOuter implements ITimeOuter
  {
    private int retries;

    private long interval;

    public PollingTimeOuter(int retries, long interval)
    {
      this.retries = retries;
      this.interval = interval;
    }

    public int getRetries()
    {
      return retries;
    }

    public long getInterval()
    {
      return interval;
    }

    public boolean timedOut() throws InterruptedException
    {
      for (int i = 0; i < retries; i++)
      {
        if (successful())
        {
          return false;
        }

        sleep(interval);
      }

      return true;
    }

    protected abstract boolean successful();
  }

  /**
   * @author Eike Stepper
   */
  public static class LockTimeOuter implements ITimeOuter
  {
    private Lock lock;

    private long millis;

    public LockTimeOuter(Lock lock, long millis)
    {
      this.lock = lock;
      this.millis = millis;
    }

    public Lock getLock()
    {
      return lock;
    }

    public long getMillis()
    {
      return millis;
    }

    public boolean timedOut() throws InterruptedException
    {
      Condition condition = lock.newCondition();
      return !condition.await(millis, TimeUnit.MILLISECONDS);
    }
  }
}
