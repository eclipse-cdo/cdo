/*
 * Copyright (c) 2011, 2012, 2019-2021, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import static org.junit.Assume.assumeNotNull;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockAreaNotFoundException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.ILockingManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.Set;

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
    skipStoreWithoutDurableLocking();

    session = openSession();
    transaction = session.openTransaction();
    resource = transaction.createResource(getResourcePath("/res1"));
  }

  @Override
  protected void doTearDown() throws Exception
  {
    LifecycleUtil.deactivate(session);
    session = null;
    transaction = null;
    resource = null;
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
    String durableLockingID = transaction.enableDurableLocking();
    String actual = transaction.getDurableLockingID();
    assertEquals(durableLockingID, actual);

    restart(durableLockingID);

    actual = transaction.getDurableLockingID();
    assertEquals(durableLockingID, actual);
  }

  public void testKeepDurableLockingID() throws Exception
  {
    String durableLockingID = transaction.enableDurableLocking();
    String actual = transaction.enableDurableLocking();
    assertEquals(durableLockingID, actual);

    restart(durableLockingID);

    actual = transaction.enableDurableLocking();
    assertEquals(durableLockingID, actual);
  }

  public void testEnableDurableLockingWithNewObject() throws Exception
  {
    CDOObject company = CDOUtil.getCDOObject(getModel1Factory().createCompany());
    resource.getContents().add(company);
    assertNew(company, transaction);

    lockWrite(company);

    CDOLockState lockState = company.cdoLockState();
    assertEquals(transaction.getLockOwner(), lockState.getWriteLockOwner());

    transaction.enableDurableLocking();
    assertEquals(transaction.getLockOwner(), lockState.getWriteLockOwner());

    transaction.disableDurableLocking(false);
    assertEquals(transaction.getLockOwner(), lockState.getWriteLockOwner());
  }

  public void testDisableDurableLocking() throws Exception
  {
    String durableLockingID = transaction.enableDurableLocking();
    transaction.disableDurableLocking(false);
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

  public void testDisableDurableLockingAfterRestart() throws Exception
  {
    String durableLockingID = transaction.enableDurableLocking();
    restart(durableLockingID);

    transaction.disableDurableLocking(false);
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

  public void testDisableDurableLockingAndReleaseLocks() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();
    lockRead(company);

    transaction.enableDurableLocking();
    assertReadLock(true, company);

    transaction.disableDurableLocking(true);
    assertReadLock(false, company);
  }

  public void testWrongReadOnly() throws Exception
  {
    String durableLockingID = transaction.enableDurableLocking();
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
    String durableLockingID = transaction.enableDurableLocking();
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
    String durableLockingID = transaction.enableDurableLocking();

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
    String durableLockingID = transaction.enableDurableLocking();
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

    String durableLockingID = transaction.enableDurableLocking();
    lockRead(company);

    restart(durableLockingID);

    company = (Company)resource.getContents().get(0);
    assertReadLock(true, company);
  }

  public void testReadLockBeforeEnable() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    lockRead(company);
    String durableLockingID = transaction.enableDurableLocking();

    restart(durableLockingID);

    company = (Company)resource.getContents().get(0);
    assertReadLock(true, company);
  }

  public void testWriteLockAfterEnable() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    String durableLockingID = transaction.enableDurableLocking();
    lockWrite(company);

    restart(durableLockingID);

    company = (Company)resource.getContents().get(0);
    assertWriteLock(true, company);
  }

  public void testWriteLockBeforeEnable() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    lockWrite(company);
    String durableLockingID = transaction.enableDurableLocking();

    restart(durableLockingID);

    company = (Company)resource.getContents().get(0);
    assertWriteLock(true, company);
  }

  public void testWriteOptionAfterEnable() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    String durableLockingID = transaction.enableDurableLocking();
    lockOption(company);

    restart(durableLockingID);

    company = (Company)resource.getContents().get(0);
    assertOptionLock(true, company);
  }

  public void testWriteOptionBeforeEnable() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    lockOption(company);
    String durableLockingID = transaction.enableDurableLocking();

    restart(durableLockingID);

    company = (Company)resource.getContents().get(0);
    assertOptionLock(true, company);
  }

  public void testLockUpgradeAfterEnable() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    String durableLockingID = transaction.enableDurableLocking();
    lockRead(company);
    lockWrite(company);

    restart(durableLockingID);

    company = (Company)resource.getContents().get(0);
    assertWriteLock(true, company);
  }

  public void testLockUpgradeBeforeEnable() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    lockRead(company);
    lockWrite(company);
    String durableLockingID = transaction.enableDurableLocking();

    restart(durableLockingID);

    company = (Company)resource.getContents().get(0);
    assertWriteLock(true, company);
  }

  public void testLockDowngradeAfterEnable() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    String durableLockingID = transaction.enableDurableLocking();

    lockRead(company);
    assertReadLock(true, company);
    assertWriteLock(false, company);

    lockWrite(company);
    assertReadLock(true, company);
    assertWriteLock(true, company);

    unlockWrite(company);
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

    lockRead(company);
    assertReadLock(true, company);
    assertWriteLock(false, company);

    lockWrite(company);
    assertReadLock(true, company);
    assertWriteLock(true, company);

    unlockWrite(company);
    assertReadLock(true, company);
    assertWriteLock(false, company);
    String durableLockingID = transaction.enableDurableLocking();

    restart(durableLockingID);

    company = (Company)resource.getContents().get(0);
    assertReadLock(true, company);
    assertWriteLock(false, company);
  }

  public void testDurableViewHandler() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    lockWrite(company);

    String durableLockingID = transaction.enableDurableLocking();

    final boolean[] gotCalled = { false };
    getRepository().getLockingManager().addDurableViewHandler(new ILockingManager.DurableViewHandler()
    {
      @Override
      public void openingView(CDOCommonSession session, int viewID, boolean readOnly, LockArea area)
      {
        gotCalled[0] = true;
      }
    });

    restart(durableLockingID);

    assertEquals(true, gotCalled[0]);
  }

  public void testGetLockAreaForLockedObject() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    String durableLockingID = transaction.enableDurableLocking();
    lockWrite(company);

    InternalSession serverSession = serverSession(session);
    InternalLockManager lm = serverSession.getRepository().getLockingManager();

    CDOServerUtil.execute(serverSession, () -> {
      LockArea lockArea = lm.getLockArea(durableLockingID);
      assertEquals(durableLockingID, lockArea.getDurableLockingID());

      CDOID id = CDOUtil.getCDOObject(company).cdoID();
      Object lockKey = lm.getLockKey(id);

      Set<IView> lockOwners = lm.getLockOwners(lockKey);
      assertEquals(durableLockingID, lockOwners.iterator().next().getDurableLockingID());
    });
  }

  public void _testClearLocksOnServer() throws Exception
  {
    InternalSession serverSession = serverSession(session);
    InternalLockManager lm = serverSession.getRepository().getLockingManager();

    transaction.commit();
    transaction.options().setLockNotificationEnabled(true);
    // EventUtil.addListener(transaction, CDOViewLocksChangedEvent.class, System.out::println);

    CDOView localView = session.openView();
    localView.options().setLockNotificationEnabled(true);
    // EventUtil.addListener(localView, CDOViewLocksChangedEvent.class, System.out::println);
    CDOResource localResource = localView.getResource(resource.getPath());

    CDOSession remoteSession = openSession();
    CDOView remoteView = remoteSession.openView();
    remoteView.options().setLockNotificationEnabled(true);
    // EventUtil.addListener(remoteView, CDOViewLocksChangedEvent.class, System.out::println);
    CDOResource remoteResource = remoteView.getResource(resource.getPath());

    lockWrite(resource);
    assertWriteLock(true, resource);
    assertServerLockState(resource);
    assertServerLockState(localResource);
    assertServerLockState(remoteResource);

    System.out.println("Normal:  " + ((InternalCDOView)transaction).getLockOwner());
    String durableLockingID = transaction.enableDurableLocking();
    System.out.println("Durable: " + ((InternalCDOView)transaction).getLockOwner());

    assertWriteLock(true, resource);
    assertServerLockState(resource);
    assertServerLockState(localResource);
    assertServerLockState(remoteResource);

    sleep(100);
    assertEquals(true, localResource.cdoWriteLock().isLockedByOthers());
    // System.out.println(localResource.cdoLockState());

    transaction.close();
    sleep(100);
    // System.out.println(localResource.cdoLockState());

    CDOServerUtil.execute(serverSession, () -> {
      // LockArea lockArea = lm.getLockArea(durableLockingID);
      // System.out.println(lockArea);
      lm.deleteLockArea(durableLockingID);
    });

    sleep(1000);
    // assertWriteLock(false, resource);
    // assertServerLockState(resource);
    assertServerLockState(localResource);
    assertServerLockState(remoteResource);
  }

  public void _testClearLocksOnServer2() throws Exception
  {
    transaction.commit();

    InternalSession serverSession = serverSession(session);
    InternalLockManager lm = serverSession.getRepository().getLockingManager();

    CDOView localView = session.openView();
    localView.options().setLockNotificationEnabled(true);
    CDOResource localResource = localView.getResource(resource.getPath());

    CDOSession remoteSession = openSession();
    CDOView remoteView = remoteSession.openView();
    remoteView.options().setLockNotificationEnabled(true);
    CDOResource remoteResource = remoteView.getResource(resource.getPath());
    String durableLockingID = remoteView.enableDurableLocking();

    lockWrite(remoteResource);
    assertWriteLock(true, remoteResource);
    assertServerLockState(resource);
    assertServerLockState(localResource);
    assertServerLockState(remoteResource);

    sleep(100);
    assertEquals(true, localResource.cdoWriteLock().isLockedByOthers());

    CDOServerUtil.execute(serverSession, () -> {
      LockArea lockArea = lm.getLockArea(durableLockingID);
      lockArea.getLocks().clear();
      lm.updateLockArea(lockArea);
      // lm.deleteLockArea(durableLockingID); //we can also delete the lock area
    });

    // The following locking/unlocking is currently necessary to generate lock state updates
    // Maybe, it could be removed in the future.
    lockWrite(resource);
    unlockWrite(resource);

    // Now everything should be unlocked
    assertWriteLock(false, resource);
    assertWriteLock(false, remoteResource);
    assertWriteLock(false, localResource);

    // Assert that locking still works
    lockWrite(remoteResource);
    assertWriteLock(true, remoteResource);
    assertServerLockState(resource);
    assertServerLockState(localResource);
    assertServerLockState(remoteResource);
    unlockWrite(remoteResource);
  }

  private static void assertServerLockState(CDOObject object)
  {
    CDOView view = object.cdoView();
    assumeNotNull(view);

    CDOLockState clientLockState = object.cdoLockState();
    assumeNotNull(clientLockState);

    IRepository repository = CDOServerUtil.getRepository(view.getSession());
    InternalLockManager lm = (InternalLockManager)repository.getLockingManager();

    CDOBranch serverBranch = CDOBranchUtil.adjustBranch(view.getBranch(), repository.getBranchManager());
    Object lockKey = lm.getLockKey(object.cdoID(), serverBranch);
    LockState<Object, IView> serverLockState = lm.getLockState(lockKey);
    assumeNotNull(serverLockState);

    assertLockOwners(clientLockState.getReadLockOwners(), //
        serverLockState.getReadLockOwners());

    assertLockOwner(clientLockState.getWriteLockOwner(), //
        serverLockState.getWriteLockOwner());

    assertLockOwner(clientLockState.getWriteOptionOwner(), //
        serverLockState.getWriteOptionOwner());
  }

  private static void assertLockOwners(Set<CDOLockOwner> clientLockOwners, Set<IView> serverViews)
  {
    assertEquals(clientLockOwners.size(), serverViews.size());

    for (IView serverView : serverViews)
    {
      CDOLockOwner serverLockOwner = CDOLockUtil.createLockOwner(serverView);
      assertTrue("Missing on client: " + serverLockOwner, clientLockOwners.contains(serverLockOwner));
    }
  }

  private static void assertLockOwner(CDOLockOwner clientLockOwner, IView serverView)
  {
    if (clientLockOwner == null)
    {
      assertEquals(null, serverView);
    }
    else
    {
      CDOLockOwner serverLockOwner = CDOLockUtil.createLockOwner(serverView);
      assertEquals(clientLockOwner, serverLockOwner);

      // assumeNotNull(serverLockOwner);
      //
      // assertEquals(clientLockOwner.getSessionID(), //
      // serverLockOwner.getSessionID());
      //
      // assertEquals(clientLockOwner.getViewID(), //
      // serverLockOwner.getViewID());
    }
  }
}
