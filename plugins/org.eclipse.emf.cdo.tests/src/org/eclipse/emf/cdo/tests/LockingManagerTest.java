/*
 * Copyright (c) 2008-2013, 2015, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Caspar De Groot - write options
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionLocksChangedEvent;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.LockTimeoutException;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.util.StaleRevisionLockException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.RWOLockManager;
import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;
import org.eclipse.net4j.util.concurrent.TimeoutRuntimeException;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.io.IOUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import junit.framework.AssertionFailedError;

/**
 * @author Simon McDuff
 */
public class LockingManagerTest extends AbstractLockingTest
{
  public void testAutoReleaseLocks() throws Exception
  {
    CDOSession session = openSession();

    AtomicInteger counter = new AtomicInteger();
    try (AutoCloseable listener = EventUtil.addListener(session, CDOSessionLocksChangedEvent.class, event -> counter.incrementAndGet()))
    {
      CDOTransaction transaction = session.openTransaction();
      transaction.options().setAutoReleaseLocksEnabled(false);

      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      assertWriteLock(false, resource);

      transaction.commit();
      assertWriteLock(false, resource);

      resource.cdoWriteLock().lock();
      assertWriteLock(true, resource);

      resource.getContents().add(getModel1Factory().createSupplier());
      transaction.commit();
      assertWriteLock(true, resource);

      resource.cdoWriteLock().unlock();
      assertWriteLock(false, resource);

      sleep(200);
      assertEquals(2, counter.get());
    }
  }

  public void testUnlockAll() throws Exception
  {
    RWOLockManager<Integer, Integer> lockingManager = new RWOLockManager<>();

    Set<Integer> keys = new HashSet<>();
    keys.add(1);
    keys.add(2);
    keys.add(3);

    lockingManager.lock(LockType.READ, 1, keys, 100);
    lockingManager.unlock(1);
  }

  public void testWriteOptions() throws Exception
  {
    RWOLockManager<Integer, Integer> lockingManager = new RWOLockManager<>();

    Set<Integer> keys = new HashSet<>();
    keys.add(1);
    lockingManager.lock(LockType.OPTION, 1, keys, 100);

    // (R=Read, W=Write, WO=WriteOption)
    // Scenario 1: 1 has WO, 2 requests W -> fail
    keys.clear();
    keys.add(1);

    try
    {
      lockingManager.lock(LockType.WRITE, 2, keys, 100); // Must fail
      fail("Should have thrown an exception");
    }
    catch (TimeoutRuntimeException e)
    {
    }

    // Scenario 2: 1 has WO, 2 requests R -> succeed
    try
    {
      lockingManager.lock(LockType.READ, 2, keys, 100); // Must succeed
    }
    catch (TimeoutRuntimeException e)
    {
      fail("Should not have thrown an exception");
    }

    // Scenario 3: 1 has WO, 2 has R, 1 requests W -> fail
    try
    {
      lockingManager.lock(LockType.WRITE, 1, keys, 100); // Must fail
      fail("Should have thrown an exception");
    }
    catch (TimeoutRuntimeException e)
    {
    }

    // Scenario 4: 1 has WO, 2 has R, 2 requests WO -> fail
    try
    {
      lockingManager.lock(LockType.OPTION, 2, keys, 100); // Must fail
      fail("Should have thrown an exception");
    }
    catch (TimeoutRuntimeException e)
    {
    }

    // Scenario 5: 1 has WO, 2 has nothing, 2 requests WO -> fail
    lockingManager.unlock(LockType.READ, 2, keys);
    try
    {
      lockingManager.lock(LockType.OPTION, 2, keys, 100); // Must fail
      fail("Should have thrown an exception");
    }
    catch (TimeoutRuntimeException e)
    {
    }

    // Scenario 6: 1 has W, 2 has nothing, 2 requests WO -> fail
    lockingManager.unlock(LockType.OPTION, 1, keys);
    lockingManager.lock(LockType.WRITE, 1, keys, 100);
    try
    {
      lockingManager.lock(LockType.OPTION, 2, keys, 100); // Must fail
      fail("Should have thrown an exception");
    }
    catch (TimeoutRuntimeException e)
    {
    }

    // Scenario 7: 1 has W, 1 request WO -> succeed
    try
    {
      lockingManager.lock(LockType.OPTION, 1, keys, 100); // Must succeed
    }
    catch (TimeoutRuntimeException e)
    {
      fail("Should not have thrown an exception");
    }

    // Scenario 8: 1 has W, 2 has R, 1 request WO -> succeed
    lockingManager.unlock(LockType.OPTION, 1, keys);
    lockingManager.lock(LockType.READ, 1, keys, 100);
    try
    {
      lockingManager.lock(LockType.OPTION, 1, keys, 100); // Must succeed
    }
    catch (TimeoutRuntimeException e)
    {
      fail("Should not have thrown an exception");
    }
  }

  public void testBasicUpgradeFromReadToWriteLock() throws Exception
  {
    RWOLockManager<Integer, Integer> lockingManager = new RWOLockManager<>();

    Runnable step1 = new Runnable()
    {
      @Override
      public void run()
      {
        Set<Integer> keys = new HashSet<>();
        keys.add(1);
        try
        {
          lockingManager.lock(LockType.WRITE, 1, keys, 50000);
        }
        catch (InterruptedException ex)
        {
          fail("Should not have exception");
        }
      }
    };

    ExecutorService executors = getExecutorService();
    Set<Integer> keys = new HashSet<>();
    keys.add(1);
    keys.add(2);
    keys.add(3);
    keys.add(4);

    msg("Context 1 have readlock 1,2,3,4");
    lockingManager.lock(LockType.READ, 1, keys, 1000);
    assertEquals(true, lockingManager.hasLock(LockType.READ, 1, 1));
    assertEquals(true, lockingManager.hasLock(LockType.READ, 1, 2));
    assertEquals(true, lockingManager.hasLock(LockType.READ, 1, 3));
    assertEquals(true, lockingManager.hasLock(LockType.READ, 1, 4));

    keys.clear();
    keys.add(1);
    keys.add(2);
    keys.add(3);

    msg("Context 2 have readlock 1,2,3");
    lockingManager.lock(LockType.READ, 2, keys, 1000);
    assertEquals(true, lockingManager.hasLock(LockType.READ, 2, 1));
    assertEquals(true, lockingManager.hasLock(LockType.READ, 2, 2));
    assertEquals(true, lockingManager.hasLock(LockType.READ, 2, 3));
    assertEquals(true, lockingManager.hasLockByOthers(LockType.READ, 2, 1));
    assertEquals(true, lockingManager.hasLockByOthers(LockType.READ, 1, 1));

    keys.clear();
    keys.add(4);

    msg("Context 1 have readlock 1,2,3,4 and writeLock 4");
    lockingManager.lock(LockType.WRITE, 1, keys, 1000);
    assertEquals(true, lockingManager.hasLock(LockType.READ, 1, 4));
    assertEquals(true, lockingManager.hasLock(LockType.WRITE, 1, 4));

    keys.clear();
    keys.add(1);

    try
    {
      lockingManager.lock(LockType.WRITE, 1, keys, 1000);
      fail("Should not have exception");
    }
    catch (RuntimeException expected)
    {
    }

    executors.execute(step1);
    executors.execute(step1);

    sleep(1000);

    keys.clear();
    keys.add(1);
    keys.add(2);
    keys.add(3);
    lockingManager.unlock(LockType.READ, 2, keys);

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        return lockingManager.hasLock(LockType.WRITE, 1, 1);
      }
    }.assertNoTimeOut();
  }

  public void testBasicWrongUnlock() throws Exception
  {
    RWOLockManager<Integer, Integer> lockingManager = new RWOLockManager<>();
    Set<Integer> keys = new HashSet<>();
    keys.add(1);
    lockingManager.lock(LockType.READ, 1, keys, 10000);
    lockingManager.unlock(LockType.READ, 1, keys);

    // As of 4.4 should not fail anymore.
    lockingManager.unlock(LockType.READ, 1, keys);
  }

  public void testReadTimeout() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    writeLock(company);

    CDOTransaction transaction2 = session.openTransaction();
    Company company2 = transaction2.getObject(company);

    long start = System.currentTimeMillis();
    assertWriteLock(false, company2);
    assertEquals(true, System.currentTimeMillis() - start < 300);

    start = System.currentTimeMillis();
    assertEquals(false, CDOUtil.getCDOObject(company2).cdoWriteLock().tryLock(1000, TimeUnit.MILLISECONDS));
    assertEquals(true, System.currentTimeMillis() - start >= 1000);
  }

  public void testReadLockByOthers() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    readLock(company);

    CDOTransaction transaction2 = session.openTransaction();
    Company company2 = (Company)transaction2.getResource(getResourcePath("/res1")).getContents().get(0);

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        try
        {
          CDOObject cdoCompany2 = CDOUtil.getCDOObject(company2);
          assertEquals(false, cdoCompany2.cdoWriteLock().isLockedByOthers());
          assertEquals(true, cdoCompany2.cdoReadLock().isLockedByOthers());
          return true;
        }
        catch (AssertionFailedError ex)
        {
          return false;
        }
      }
    }.assertNoTimeOut();
  }

  public void testDetachedObjects() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    Company company2 = (Company)transaction2.getResource(getResourcePath("/res1")).getContents().get(0);
    res.getContents().remove(0);

    transaction.commit();

    try
    {
      readLock(company2);
      fail("IllegalArgumentException expected");
    }
    catch (IllegalArgumentException expected)
    {
    }

    assertReadLock(false, company2);

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        try
        {
          assertEquals(false, CDOUtil.getCDOObject(company2).cdoReadLock().isLockedByOthers());
          return true;
        }
        catch (AssertionFailedError ex)
        {
          return false;
        }
      }
    }.assertNoTimeOut();
  }

  public void testWriteLockByOthers() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    writeLock(company);

    CDOTransaction transaction2 = session.openTransaction();
    Company company2 = (Company)transaction2.getResource(getResourcePath("/res1")).getContents().get(0);

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        try
        {
          CDOObject cdoCompany2 = CDOUtil.getCDOObject(company2);
          assertEquals(true, cdoCompany2.cdoWriteLock().isLockedByOthers());
          assertEquals(false, cdoCompany2.cdoReadLock().isLockedByOthers());
          return true;
        }
        catch (AssertionFailedError ex)
        {
          return false;
        }
      }
    }.assertNoTimeOut();
  }

  public void testWriteLock() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    Company company2 = (Company)transaction2.getResource(getResourcePath("/res1")).getContents().get(0);

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    CDOObject cdoCompany2 = CDOUtil.getCDOObject(company2);

    transaction.lockObjects(Collections.singletonList(cdoCompany), LockType.WRITE, CDOLock.WAIT);

    try
    {
      transaction2.lockObjects(Collections.singletonList(cdoCompany2), LockType.WRITE, 1000);
      fail("LockTimeoutException expected");
    }
    catch (LockTimeoutException expected)
    {
    }

    company2.setCity("Ottawa");
    commitAndTimeout(transaction2);
  }

  public void _testWriteLock_LongTimeout() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    Company company2 = (Company)transaction2.getResource(getResourcePath("/res1")).getContents().get(0);

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    CDOObject cdoCompany2 = CDOUtil.getCDOObject(company2);

    cdoCompany.cdoWriteLock().lock();
    IOUtil.OUT().println("Lock acquired by transaction 1");

    Thread thread = new Thread()
    {
      @Override
      public void run()
      {
        try
        {
          cdoCompany2.cdoWriteLock().lock(3 * ISignalProtocol.DEFAULT_TIMEOUT);
          IOUtil.OUT().println("Lock acquired by transaction 2");

          company2.setCity("Ottawa");
          commitAndSync(transaction2, transaction);
          IOUtil.OUT().println("Lock released by transaction 2");
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
        }
      }
    };

    thread.start();

    sleep(2 * ISignalProtocol.DEFAULT_TIMEOUT);
    cdoCompany.cdoWriteLock().unlock();
    IOUtil.OUT().println("Lock released by transaction 1");

    thread.join(DEFAULT_TIMEOUT);
    assertEquals("Ottawa", company.getCity());
  }

  public void testWriteLockViaObject() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    Company company2 = (Company)transaction2.getResource(getResourcePath("/res1")).getContents().get(0);

    writeLock(company);
    assertWriteLock(false, company2);

    company2.setCity("Ottawa");
    commitAndTimeout(transaction2);
  }

  public void testWriteLockFromDifferenceTransaction() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    Company company2 = (Company)transaction2.getResource(getResourcePath("/res1")).getContents().get(0);

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    CDOObject cdoCompany2 = CDOUtil.getCDOObject(company2);

    transaction.lockObjects(Collections.singletonList(cdoCompany), LockType.WRITE, DEFAULT_TIMEOUT);

    try
    {
      transaction2.lockObjects(Collections.singletonList(cdoCompany2), LockType.WRITE, 1000);
      fail("LockTimeoutException expected");
    }
    catch (LockTimeoutException expected)
    {
    }
  }

  public void testWriteLockMultiple() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    List<CDOObject> objects = new ArrayList<>();

    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    transaction.lockObjects(objects, LockType.WRITE, DEFAULT_TIMEOUT);

    assertWriteLock(false, company);
    assertReadLock(false, company);
    for (CDOObject object : objects)
    {
      assertWriteLock(true, object);
      assertReadLock(false, object);
    }
  }

  public void testWriteUnlockMultiple() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    List<CDOObject> objects = new ArrayList<>();

    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    transaction.lockObjects(objects, LockType.WRITE, DEFAULT_TIMEOUT);

    assertWriteLock(false, company);
    assertReadLock(false, company);
    for (CDOObject object : objects)
    {
      assertWriteLock(true, object);
      assertReadLock(false, object);
    }

    transaction.unlockObjects(objects, LockType.WRITE);

    assertWriteLock(false, company);
    assertReadLock(false, company);
    for (CDOObject object : objects)
    {
      assertWriteLock(false, object);
      assertReadLock(false, object);
    }
  }

  public void testReadLockMultiple() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    List<CDOObject> objects = new ArrayList<>();

    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    transaction.lockObjects(objects, LockType.READ, DEFAULT_TIMEOUT);

    assertReadLock(false, company);
    assertWriteLock(false, company);
    for (CDOObject object : objects)
    {
      assertReadLock(true, object);
      assertWriteLock(false, object);
    }
  }

  public void testReadUnlockMultiple() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    List<CDOObject> objects = new ArrayList<>();

    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);
    addCategory(company, objects);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    transaction.lockObjects(objects, LockType.READ, DEFAULT_TIMEOUT);

    assertReadLock(false, company);
    assertWriteLock(false, company);
    for (CDOObject object : objects)
    {
      assertReadLock(true, object);
      assertWriteLock(false, object);
    }

    transaction.unlockObjects(objects, LockType.READ);

    assertWriteLock(false, company);
    assertReadLock(false, company);
    for (CDOObject object : objects)
    {
      assertWriteLock(false, object);
      assertReadLock(false, object);
    }
  }

  public void testReadLockAndCommitFromDifferentTransaction() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    Company company2 = (Company)transaction2.getResource(getResourcePath("/res1")).getContents().get(0);
    CDOObject cdoCompany = CDOUtil.getCDOObject(company);

    transaction.lockObjects(Collections.singletonList(cdoCompany), LockType.READ, DEFAULT_TIMEOUT);
    company2.setCity("Ottawa");
    commitAndTimeout(transaction2);
  }

  public void testWriteLockAndCommitFromDifferentTransaction() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    Company company2 = (Company)transaction2.getResource(getResourcePath("/res1")).getContents().get(0);
    CDOObject cdoCompany = CDOUtil.getCDOObject(company);

    transaction.lockObjects(Collections.singletonList(cdoCompany), LockType.WRITE, DEFAULT_TIMEOUT);
    company2.setCity("Ottawa");
    commitAndTimeout(transaction2);
  }

  public void testReadLockAndCommitSameTransaction() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    readLock(company);

    company.setCity("Ottawa");
    transaction.commit();
  }

  public void testWriteLockAndCommitSameTransaction() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    writeLock(company);

    company.setCity("Ottawa");
    assertWriteLock(true, company);
    assertReadLock(false, company);

    transaction.commit();

    assertWriteLock(false, company);
    assertReadLock(false, company);
  }

  public void testWriteLockAndRollback() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    writeLock(company);
    company.setCity("Ottawa");

    transaction.rollback();
    assertWriteLock(false, company);
  }

  public void testLockUnlock() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();
    CDOObject cdoCompany = CDOUtil.getCDOObject(company);

    readLock(company);
    assertReadLock(true, company);
    assertWriteLock(false, company);

    writeLock(company);
    assertReadLock(true, company);
    assertWriteLock(true, company);

    readLock(company);
    assertReadLock(true, company);
    assertWriteLock(true, company);

    writeLock(company);
    assertReadLock(true, company);
    assertWriteLock(true, company);

    readUnlock(cdoCompany);
    assertReadLock(true, company);
    assertWriteLock(true, company);

    readUnlock(cdoCompany);
    assertReadLock(false, company);
    assertWriteLock(true, company);

    writeUnlock(cdoCompany);
    assertReadLock(false, company);
    assertWriteLock(true, company);

    writeUnlock(cdoCompany);
    assertReadLock(false, company);
    assertWriteLock(false, company);

    /********************/

    readLock(company);
    assertReadLock(true, company);
    assertWriteLock(false, company);

    writeLock(company);
    assertReadLock(true, company);
    assertWriteLock(true, company);

    readLock(company);
    assertReadLock(true, company);
    assertWriteLock(true, company);

    writeLock(company);
    assertReadLock(true, company);
    assertWriteLock(true, company);

    writeUnlock(cdoCompany);
    assertReadLock(true, company);
    assertWriteLock(true, company);

    writeUnlock(cdoCompany);
    assertReadLock(true, company);
    assertWriteLock(false, company);

    readUnlock(cdoCompany);
    assertReadLock(true, company);
    assertWriteLock(false, company);

    readUnlock(cdoCompany);
    assertReadLock(false, company);
    assertWriteLock(false, company);
  }

  /**
   * Bug 352191.
   */
  public void testLockDetached() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    res.getContents().remove(0);
    assertTransient(company);

    CDOObject cdoObject = CDOUtil.getCDOObject(company);
    transaction.lockObjects(Collections.singleton(cdoObject), LockType.WRITE, DEFAULT_TIMEOUT);

    // Verify
    CDOTransaction transaction2 = session.openTransaction();
    CDOResource res2 = transaction2.getResource(getResourcePath("/res1"));
    Company company2 = (Company)res2.getContents().get(0);
    company2.setName("NewName");
    commitAndTimeout(transaction2);
  }

  public void testTransactionClose() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    InternalRepository repo = getRepository();
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();
    IView view = repo.getSessionManager().getSession(session.getSessionID()).getView(transaction.getViewID());
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    readLock(company);
    transaction.close();
    assertEquals(false, repo.getLockingManager().hasLock(LockType.READ, view, cdoCompany.cdoID()));
  }

  public void testSessionClose() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    InternalRepository repo = getRepository();
    CDOSession session = openSession();

    CDOTransaction transaction = session.openTransaction();
    IView view = repo.getSessionManager().getSession(session.getSessionID()).getView(transaction.getViewID());
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    CDOObject cdoCompany = CDOUtil.getCDOObject(company);
    readLock(company);
    session.close();

    sleep(100);
    assertEquals(false, repo.getLockingManager().hasLock(LockType.READ, view, cdoCompany.cdoID()));
  }

  public void testReadLockedByOthers() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();
    assertFalse(CDOUtil.getCDOObject(company).cdoReadLock().isLockedByOthers());

    transaction.lockObjects(CDOUtil.getCDOObjects(company), LockType.READ, DEFAULT_TIMEOUT);
    assertReadLock(true, company);
    assertWriteLock(false, company);
    assertFalse(CDOUtil.getCDOObject(company).cdoReadLock().isLockedByOthers());

    CDOView view = session.openView();
    Company viewCompany = view.getObject(company);
    assertReadLock(false, viewCompany);
    assertWriteLock(false, viewCompany);
    assertTrue(CDOUtil.getCDOObject(viewCompany).cdoReadLock().isLockedByOthers());
  }

  public void testBugzilla_270345() throws Exception
  {
    Company company1 = getModel1Factory().createCompany();
    Company company2 = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction1 = session.openTransaction();
    CDOTransaction transaction2 = session.openTransaction();
    CDOResource res = transaction1.getOrCreateResource(getResourcePath("/res1"));
    res.getContents().add(company1);
    res.getContents().add(company2);
    transaction1.commit();

    writeLock(company1);
    assertWriteLock(true, company1);

    Company companyFrom2 = (Company)CDOUtil.getEObject(transaction2.getObject(company2));
    companyFrom2.setCity("sss");
    transaction2.commit();
    assertWriteLock(true, company1);
  }

  public void testAutoReleaseLockFalse_commit() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();
    transaction.options().setAutoReleaseLocksEnabled(false);

    writeLock(company);
    readLock(company);

    msg("Test with read/write lock");
    assertWriteLock(true, company);
    assertReadLock(true, company);

    company.setCity("Ottawa");
    transaction.commit();
    assertWriteLock(true, company);
    assertReadLock(true, company);

    msg("Clean locks");
    transaction.unlockObjects(null, null);

    msg("Test with read lock");
    readLock(company);
    assertReadLock(true, company);

    company.setCity("Toronto");
    transaction.commit();
    assertReadLock(true, company);

    transaction.options().setAutoReleaseLocksEnabled(true);
    transaction.commit();
    assertReadLock(false, company);
  }

  public void testAutoReleaseLockFalse_rollback() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();
    transaction.options().setAutoReleaseLocksEnabled(false);

    writeLock(company);
    readLock(company);

    msg("Test with read/write lock");
    assertWriteLock(true, company);
    assertReadLock(true, company);

    company.setCity("Ottawa");
    transaction.rollback();
    assertWriteLock(true, company);
    assertReadLock(true, company);

    msg("Clean locks");
    transaction.unlockObjects(null, null);

    msg("Test with read lock");
    readLock(company);
    assertReadLock(true, company);

    company.setCity("Toronto");
    transaction.rollback();
    assertReadLock(true, company);

    transaction.options().setAutoReleaseLocksEnabled(true);
    transaction.rollback();
    assertReadLock(false, company);
  }

  public void testWriteLockPerformance() throws Exception
  {
    int ITERATION = 100;
    Company company = getModel1Factory().createCompany();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));
    res.getContents().add(company);
    transaction.commit();

    long start = System.currentTimeMillis();

    // 335-418 locks/sec
    for (int i = 0; i < ITERATION; i++)
    {
      writeLock(company);
    }

    msg("Lock " + ITERATION / ((double)(System.currentTimeMillis() - start) / 1000) + " objects/sec");
  }

  public void testReadLockStaleRevision() throws Exception
  {
    lockStaleRevision(LockType.READ);
  }

  public void testWriteLockStaleRevision() throws Exception
  {
    lockStaleRevision(LockType.WRITE);
  }

  private void lockStaleRevision(LockType type) throws Exception
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));

    Company company = getModel1Factory().createCompany();
    company.setName("AAA");
    res.getContents().add(company);
    transaction.commit();

    updateInOtherSession();

    try
    {
      if (type == LockType.WRITE)
      {
        writeLock(company);
      }
      else if (type == LockType.READ)
      {
        readLock(company);
      }

      fail("StaleRevisionLockException expected");
    }
    catch (StaleRevisionLockException expected)
    {
    }

    session.close();
  }

  private void updateInOtherSession() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.getResource(getResourcePath("/res1"));

    Company company = (Company)res.getContents().get(0);
    company.setName("BBB");
    transaction.commit();

    session.close();
  }

  private void addCategory(Company company, List<CDOObject> objects)
  {
    Category category = getModel1Factory().createCategory();
    company.getCategories().add(category);
    objects.add(CDOUtil.getCDOObject(category));
  }

  public void testRecursiveLock() throws Exception
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/res1"));

    Category category1 = getModel1Factory().createCategory();
    Category category2_1 = getModel1Factory().createCategory();
    Category category2_2 = getModel1Factory().createCategory();
    Category category3 = getModel1Factory().createCategory();
    res.getContents().add(category1);
    category1.getCategories().add(category2_1);
    category1.getCategories().add(category2_2);
    category2_1.getCategories().add(category3);

    transaction.commit();

    CDOObject top = CDOUtil.getCDOObject(category1);
    transaction.lockObjects(Collections.singleton(top), LockType.WRITE, 5000, true);

    assertWriteLock(true, category1);
    assertWriteLock(true, category2_1);
    assertWriteLock(true, category2_2);
    assertWriteLock(true, category3);

    transaction.unlockObjects(Collections.singleton(top), LockType.WRITE, true);

    assertWriteLock(false, category1);
    assertWriteLock(false, category2_1);
    assertWriteLock(false, category2_2);
    assertWriteLock(false, category3);

    session.close();
  }

  public void testLockOnNewObject() throws Exception
  {
    CDOSession session1 = openSession();
    CDOSession session2 = openSession();

    CDOTransaction transaction = session1.openTransaction();
    transaction.options().setAutoReleaseLocksEnabled(false);
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    transaction.commit();

    Category category1 = getModel1Factory().createCategory();
    Category category2 = getModel1Factory().createCategory();
    Category category3 = getModel1Factory().createCategory();
    resource.getContents().add(category1);
    resource.getContents().add(category2);
    resource.getContents().add(category3);

    readLock(category1);
    writeLock(category2);
    optionLock(category3);

    assertReadLock(true, category1);
    assertWriteLock(true, category2);
    assertOptionLock(true, category3);

    readUnlock(category1);
    writeUnlock(category2);
    optionUnlock(category3);

    assertReadLock(false, category1);
    assertWriteLock(false, category2);
    assertWriteLock(false, category3);

    readLock(category1);
    writeLock(category2);
    optionLock(category3);

    transaction.commit();

    CDOView controlView = session2.openView();
    controlView.options().setLockNotificationEnabled(true);
    CDOResource r = controlView.getResource(getResourcePath("/res1"));

    CDOObject category1cv = CDOUtil.getCDOObject(r.getContents().get(0));
    CDOObject category2cv = CDOUtil.getCDOObject(r.getContents().get(1));
    CDOObject category3cv = CDOUtil.getCDOObject(r.getContents().get(2));

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        try
        {
          assertEquals(true, category1cv.cdoReadLock().isLockedByOthers());
          assertEquals(true, category2cv.cdoWriteLock().isLockedByOthers());
          assertEquals(true, category3cv.cdoWriteOption().isLockedByOthers());
          return true;
        }
        catch (AssertionFailedError ex)
        {
          return false;
        }
      }
    }.assertNoTimeOut();

    readUnlock(category1);
    writeUnlock(category2);
    optionUnlock(category3);

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        try
        {
          assertEquals(false, category1cv.cdoReadLock().isLockedByOthers());
          assertEquals(false, category2cv.cdoReadLock().isLockedByOthers());
          assertEquals(false, category3cv.cdoReadLock().isLockedByOthers());
          return true;
        }
        catch (AssertionFailedError ex)
        {
          return false;
        }
      }
    }.assertNoTimeOut();

    session1.close();
    session2.close();
  }

  public void testDeleteLockedObject() throws Exception
  {
    CDOSession session1 = openSession();
    CDOSession session2 = openSession();

    CDOTransaction tx = session1.openTransaction();
    tx.options().setAutoReleaseLocksEnabled(false);
    CDOResource resource = tx.createResource(getResourcePath("/res1"));
    Category category1 = getModel1Factory().createCategory();
    resource.getContents().add(category1);
    tx.commit();

    CDOID id = CDOUtil.getCDOObject(category1).cdoID();

    writeLock(category1);

    resource.getContents().remove(category1);
    tx.commit();

    CDOView controlView = session2.openView();

    try
    {
      controlView.getObject(id);
      fail("Should have thrown " + ObjectNotFoundException.class.getSimpleName());
    }
    catch (ObjectNotFoundException ignore)
    {
      // Do nothing
    }

    InternalLockManager mgr = getRepository().getLockingManager();
    boolean branching = getRepository().isSupportingBranches();
    Object key = branching ? CDOIDUtil.createIDAndBranch(id, tx.getBranch()) : id;
    LockState<Object, IView> state = mgr.getLockState(key);
    assertNull(state);
  }

  public void testAutoReleaseLocksOnUnchangedObject() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    Category category1 = getModel1Factory().createCategory();
    company.getCategories().add(category1);

    Category category2 = getModel1Factory().createCategory();
    company.getCategories().add(category2);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    resource.getContents().add(company);
    transaction.commit();

    CDOLock writeLock = CDOUtil.getCDOObject(category1).cdoWriteLock();
    writeLock.lock(DEFAULT_TIMEOUT);

    // Check that explicit lock on unchanged object is not released
    transaction.options().setAutoReleaseLocksEnabled(false);
    category2.setName("NewName");
    transaction.commit();
    assertEquals(true, writeLock.isLocked()); // Explicit lock not released because of AutoReleaseLocks=false
    assertEquals(false, CDOUtil.getCDOObject(category2).cdoWriteLock().isLocked()); // Implicit locks always released

    // Check that explicit lock on unchanged object is released
    transaction.options().setAutoReleaseLocksEnabled(true);
    category2.setName("NewName2");
    transaction.commit();
    assertEquals(false, writeLock.isLocked()); // Explicit lock released because of AutoReleaseLocks=true
    assertEquals(false, CDOUtil.getCDOObject(category2).cdoWriteLock().isLocked()); // Implicit locks always released
  }

  public void testAutoReleaseExplicitLocks() throws Exception
  {
    Company company = getModel1Factory().createCompany();

    Category category1 = getModel1Factory().createCategory();
    company.getCategories().add(category1);

    Category category2 = getModel1Factory().createCategory();
    company.getCategories().add(category2);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    resource.getContents().add(company);
    transaction.commit();

    CDOLock writeLock = CDOUtil.getCDOObject(category1).cdoWriteLock();
    writeLock.lock(DEFAULT_TIMEOUT);

    // Check that explicit lock on changed object is not released
    transaction.options().setAutoReleaseLocksEnabled(false);
    category1.setName("NewName");
    transaction.commit();
    assertEquals(true, writeLock.isLocked());

    // Check that explicit lock on changed object is released
    transaction.options().setAutoReleaseLocksEnabled(true);
    category1.setName("NewName2");
    transaction.commit();
    assertEquals(false, writeLock.isLocked());
  }

  public void testLockRefreshForHeldLock() throws Exception
  {
    // Client 1 creates and read-locks a resource.
    CDOTransaction tx1 = openSession().openTransaction();
    CDOResource resource1 = tx1.createResource(getResourcePath("/res1"));
    tx1.commit();
    resource1.cdoReadLock().lock();

    // Client 2 gets the resource created and read-locked by Client 1.
    CDOTransaction tx2 = openSession().openTransaction();
    CDOResource resource2 = tx2.getResource(getResourcePath("/res1"));

    // Get the lock and lock it.
    CDOLock readLockBeforeClose = resource2.cdoReadLock();
    readLockBeforeClose.lock(DEFAULT_TIMEOUT);

    boolean isLockedByOthersBeforeClose = readLockBeforeClose.isLockedByOthers();
    assertTrue(isLockedByOthersBeforeClose);

    tx1.close(); // Client 1 release read lock.

    new PollingTimeOuter()
    {
      @Override
      protected boolean successful()
      {
        CDOLock readLockAfterClose = resource2.cdoReadLock();
        boolean isLockedByOthersAfterClose = readLockAfterClose.isLockedByOthers();
        return isLockedByOthersAfterClose == false;
      }
    }.assertNoTimeOut();
  }

  public void testRefreshLockStates() throws Exception
  {
    // Client 1 creates and read-locks a resource.
    CDOTransaction tx1 = openSession().openTransaction();
    CDOResource resource1 = tx1.createResource(getResourcePath("/res1"));
    tx1.commit();
    resource1.cdoReadLock().lock();

    // Client 2 gets the resource created and read-locked by Client 1.
    CDOTransaction tx2 = openSession().openTransaction();
    CDOResource resource2 = tx2.getResource(getResourcePath("/res1"));

    // Get the lock and DON'T lock it.
    CDOLock readLockBeforeClose = resource2.cdoReadLock();
    boolean isLockedByOthersBeforeClose = readLockBeforeClose.isLockedByOthers();
    assertTrue(isLockedByOthersBeforeClose);

    tx1.close(); // Client 1 release read lock.

    tx2.refreshLockStates(null);

    CDOLock readLockAfterClose = resource2.cdoReadLock();
    boolean isLockedByOthersAfterClose = readLockAfterClose.isLockedByOthers();
    assertTrue(isLockedByOthersAfterClose);
  }

  private void commitAndTimeout(CDOTransaction transaction)
  {
    InternalRepository repository = getRepository();
    long oldTimeout = repository.getOptimisticLockingTimeout();
    repository.setOptimisticLockingTimeout(1000);

    try
    {
      transaction.commit();
      fail("CommitException expected");
    }
    catch (CommitException expected)
    {
      // SUCCESS
    }
    finally
    {
      repository.setOptimisticLockingTimeout(oldTimeout);
    }
  }
}
