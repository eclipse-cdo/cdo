/*
 * Copyright (c) 2011, 2012, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import static org.junit.Assume.assumeNotNull;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockAreaNotFoundException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.ILockingManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
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
    readLock(company);

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
    optionLock(company);

    restart(durableLockingID);

    company = (Company)resource.getContents().get(0);
    assertOptionLock(true, company);
  }

  public void testWriteOptionBeforeEnable() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    optionLock(company);
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

    writeLock(company);

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

  public void _testClearLocksOnServer() throws Exception
  {
    ISession serverSession = CDOServerUtil.getServerSession(session);
    InternalLockManager lm = (InternalLockManager)serverSession.getRepository().getLockingManager();

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

    writeLock(resource);
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
