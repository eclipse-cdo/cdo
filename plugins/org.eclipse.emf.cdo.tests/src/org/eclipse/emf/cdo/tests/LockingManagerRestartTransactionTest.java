/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockAreaNotFoundException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * @author Eike Stepper
 */
public class LockingManagerRestartTransactionTest extends AbstractLockingTest
{
  protected CDOSession session;

  protected CDOTransaction transaction;

  protected CDOResource resource;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.createResource(getResourcePath("/res1"));
  }

  @Override
  protected void doTearDown() throws Exception
  {
    LifecycleUtil.deactivate(session);
    super.doTearDown();
  }

  protected void restart(String durableLockingID)
  {
    transaction.close();
    transaction = session.openTransaction(durableLockingID);
    resource = transaction.getOrCreateResource(getResourcePath("/res1"));
  }

  public void testWrongDurableLockingID() throws Exception
  {
    try
    {
      restart("ABC");
      fail("LockAreaNotFoundException expected");
    }
    catch (LockAreaNotFoundException expected)
    {
      assertEquals("ABC", expected.getDurableLockingID());
    }
  }

  public void testGetDurableLockingID() throws Exception
  {
    String durableLockingID = transaction.enableDurableLocking(true);
    String actual = transaction.getDurableLockingID();
    assertEquals(durableLockingID, actual);

    restart(durableLockingID);

    actual = transaction.getDurableLockingID();
    assertEquals(durableLockingID, actual);
  }

  public void testKeepDurableLockingID() throws Exception
  {
    String durableLockingID = transaction.enableDurableLocking(true);
    String actual = transaction.enableDurableLocking(true);
    assertEquals(durableLockingID, actual);

    restart(durableLockingID);

    actual = transaction.enableDurableLocking(true);
    assertEquals(durableLockingID, actual);
  }

  public void testDeleteDurableLockingID() throws Exception
  {
    String durableLockingID = transaction.enableDurableLocking(true);
    String actual = transaction.enableDurableLocking(false);
    assertEquals(durableLockingID, actual);
    assertEquals(null, transaction.getDurableLockingID());

    try
    {
      restart(durableLockingID);
      fail("LockAreaNotFoundException expected");
    }
    catch (LockAreaNotFoundException expected)
    {
      assertEquals(durableLockingID, expected.getDurableLockingID());
    }
  }

  public void testDeleteDurableLockingIDAfterRestart() throws Exception
  {
    String durableLockingID = transaction.enableDurableLocking(true);
    restart(durableLockingID);

    String actual = transaction.enableDurableLocking(false);
    assertEquals(durableLockingID, actual);
    assertEquals(null, transaction.getDurableLockingID());

    try
    {
      restart(durableLockingID);
      fail("LockAreaNotFoundException expected");
    }
    catch (LockAreaNotFoundException expected)
    {
    }
  }

  public void testWrongReadOnly() throws Exception
  {
    String durableLockingID = transaction.enableDurableLocking(true);
    transaction.close();

    try
    {
      session.openView(durableLockingID);
      fail("IllegalStateException expected");
    }
    catch (IllegalStateException expected)
    {
    }
  }

  public void testWrongReadOnlyAfterRestart() throws Exception
  {
    String durableLockingID = transaction.enableDurableLocking(true);
    restart(durableLockingID);
    transaction.close();

    try
    {
      session.openView(durableLockingID);
      fail("IllegalStateException expected");
    }
    catch (IllegalStateException expected)
    {
    }
  }

  public void testDuplicateOpenView() throws Exception
  {
    String durableLockingID = transaction.enableDurableLocking(true);

    try
    {
      session.openTransaction(durableLockingID);
      fail("IllegalStateException expected");
    }
    catch (IllegalStateException expected)
    {
    }
  }

  public void testDuplicateOpenViewAfterRestart() throws Exception
  {
    String durableLockingID = transaction.enableDurableLocking(true);
    restart(durableLockingID);

    try
    {
      session.openTransaction(durableLockingID);
      fail("IllegalStateException expected");
    }
    catch (IllegalStateException expected)
    {
    }
  }

  public void testReadLockAfterEnable() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    String durableLockingID = transaction.enableDurableLocking(true);
    readLock(company);

    restart(durableLockingID);

    company = (Company)resource.getContents().get(0);
    assertReadLock(true, company);
  }

  public void testReadLockBeforeEnable() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    readLock(company);
    String durableLockingID = transaction.enableDurableLocking(true);

    restart(durableLockingID);

    company = (Company)resource.getContents().get(0);
    assertReadLock(true, company);
  }

  public void testWriteLockAfterEnable() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    String durableLockingID = transaction.enableDurableLocking(true);
    writeLock(company);

    restart(durableLockingID);

    company = (Company)resource.getContents().get(0);
    assertWriteLock(true, company);
  }

  public void testWriteLockBeforeEnable() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    writeLock(company);
    String durableLockingID = transaction.enableDurableLocking(true);

    restart(durableLockingID);

    company = (Company)resource.getContents().get(0);
    assertWriteLock(true, company);
  }

  public void testWriteOptionAfterEnable() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    String durableLockingID = transaction.enableDurableLocking(true);
    writeOption(company);

    restart(durableLockingID);

    company = (Company)resource.getContents().get(0);
    assertWriteOption(true, company);
  }

  public void testWriteOptionBeforeEnable() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    writeOption(company);
    String durableLockingID = transaction.enableDurableLocking(true);

    restart(durableLockingID);

    company = (Company)resource.getContents().get(0);
    assertWriteOption(true, company);
  }

  public void testLockUpgradeAfterEnable() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    String durableLockingID = transaction.enableDurableLocking(true);
    readLock(company);
    writeLock(company);

    restart(durableLockingID);

    company = (Company)resource.getContents().get(0);
    assertWriteLock(true, company);
  }

  public void testLockUpgradeBeforeEnable() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    readLock(company);
    writeLock(company);
    String durableLockingID = transaction.enableDurableLocking(true);

    restart(durableLockingID);

    company = (Company)resource.getContents().get(0);
    assertWriteLock(true, company);
  }

  public void testLockDowngradeAfterEnable() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    String durableLockingID = transaction.enableDurableLocking(true);

    readLock(company);
    assertReadLock(true, company);
    assertWriteLock(false, company);

    writeLock(company);
    assertReadLock(true, company);
    assertWriteLock(true, company);

    writeUnlock(company);
    assertReadLock(true, company);
    assertWriteLock(false, company);

    restart(durableLockingID);

    company = (Company)resource.getContents().get(0);
    assertReadLock(true, company);
    assertWriteLock(false, company);
  }

  public void testLockDowngradeBeforeEnable() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    readLock(company);
    assertReadLock(true, company);
    assertWriteLock(false, company);

    writeLock(company);
    assertReadLock(true, company);
    assertWriteLock(true, company);

    writeUnlock(company);
    assertReadLock(true, company);
    assertWriteLock(false, company);
    String durableLockingID = transaction.enableDurableLocking(true);

    restart(durableLockingID);

    company = (Company)resource.getContents().get(0);
    assertReadLock(true, company);
    assertWriteLock(false, company);
  }
}
