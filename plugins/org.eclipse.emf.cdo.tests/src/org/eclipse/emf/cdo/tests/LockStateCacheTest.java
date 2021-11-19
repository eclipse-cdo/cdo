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
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.lock.InternalCDOLockState;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.session.CDOLockStateCacheImpl;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.spi.cdo.CDOLockStateCache;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class LockStateCacheTest extends AbstractCDOTest
{
  public void testLockStateCache() throws Exception
  {
    CDOSession session = openSession();
    CDOLockStateCache cache = new CDOLockStateCacheImpl(session);
    CDOBranchPoint head = getTestBranch(session).getHead();

    CDOView view1 = session.openView(head);
    CDOView view2 = session.openView(head);
    CDOView view3 = session.openView(head);
    CDOView view4 = session.openView(head);

    CDOLockOwner owner1 = view1.getLockOwner();
    CDOLockOwner owner2 = view2.getLockOwner();
    CDOLockOwner owner3 = view3.getLockOwner();
    CDOLockOwner owner4 = view4.getLockOwner();

    InternalCDOLockState lockState1 = getLockState(cache, view1);
    InternalCDOLockState lockState2 = getLockState(cache, view2);
    InternalCDOLockState lockState3 = getLockState(cache, view3);
    InternalCDOLockState lockState4 = getLockState(cache, view4);

    // Write lock.
    lockState1.setWriteLockOwner(owner1);
    assertEquals(owner1, lockState1.getWriteLockOwner());
    assertEquals(owner1, lockState2.getWriteLockOwner());
    assertEquals(owner1, lockState3.getWriteLockOwner());
    assertEquals(owner1, lockState4.getWriteLockOwner());

    // Another write lock is not possible.
    assertException(IllegalStateException.class, () -> lockState2.setWriteLockOwner(owner2));

    // A read lock is not possible.
    assertException(IllegalStateException.class, () -> lockState3.addReadLockOwner(owner3));

    // A write option is not possible.
    assertException(IllegalStateException.class, () -> lockState4.setWriteOptionOwner(owner4));

    // Release write lock.
    lockState1.setWriteLockOwner(null);
    assertEquals(null, lockState1.getWriteLockOwner());

    // Read lock first time.
    lockState1.addReadLockOwner(owner1);
    assertEquals(1, lockState1.getReadLockOwners().size());
    assertTrue(lockState1.getReadLockOwners().contains(owner1));

    // A write lock is not possible.
    assertException(IllegalStateException.class, () -> lockState4.setWriteLockOwner(owner4));

    // Read lock second time.
    lockState1.addReadLockOwner(owner2);
    assertEquals(2, lockState1.getReadLockOwners().size());
    assertTrue(lockState1.getReadLockOwners().contains(owner1));
    assertTrue(lockState1.getReadLockOwners().contains(owner2));

    // A write lock is not possible.
    assertException(IllegalStateException.class, () -> lockState4.setWriteLockOwner(owner4));

    // Read lock third time.
    lockState1.addReadLockOwner(owner3);
    assertEquals(3, lockState1.getReadLockOwners().size());
    assertTrue(lockState1.getReadLockOwners().contains(owner1));
    assertTrue(lockState1.getReadLockOwners().contains(owner2));
    assertTrue(lockState1.getReadLockOwners().contains(owner3));

    // A write lock is not possible.
    assertException(IllegalStateException.class, () -> lockState4.setWriteLockOwner(owner4));

    // Write option.
    lockState1.setWriteOptionOwner(owner1);
    assertEquals(3, lockState1.getReadLockOwners().size());
    assertTrue(lockState1.getReadLockOwners().contains(owner1));
    assertTrue(lockState1.getReadLockOwners().contains(owner2));
    assertTrue(lockState1.getReadLockOwners().contains(owner3));
    assertEquals(owner1, lockState1.getWriteOptionOwner());
  }

  public void testSetWriteLockOwner() throws Exception
  {
    // Unlocked.
    initial() //
        .modify(1, 0) //
        .verify(1, 0);

    // Write locked by same owner.
    initial(1, 0) //
        .modify(1, 0) //
        .verify(1, 0);

    // Write locked by different owner.
    initial(2, 0) //
        .modify(1, 0) //
        .verify(IllegalStateException.class);

    // Write optioned by same owner.
    initial(0, 1) //
        .modify(1, 0) //
        .verify(1, 0);

    // Write optioned by different owner.
    initial(0, 2) //
        .modify(1, 0) //
        .verify(IllegalStateException.class);

    // Read locked by same owner.
    initial(0, 0, 1) //
        .modify(1, 0) // Lock upgrade.
        .verify(1, 0, 1);

    // Read locked by different owner.
    initial(0, 0, 2) //
        .modify(1, 0) //
        .verify(IllegalStateException.class);

    // Read locked by same and different owner.
    initial(0, 0, 1, 2) //
        .modify(1, 0) //
        .verify(IllegalStateException.class);

    // Read locked by same and two different owners.
    initial(0, 0, 1, 2, 3) //
        .modify(1, 0) //
        .verify(IllegalStateException.class);

    // Read locked by two different owners.
    initial(0, 0, 2, 3) //
        .modify(1, 0) //
        .verify(IllegalStateException.class);

    // Read locked by three different owners.
    initial(0, 0, 2, 3, 4) //
        .modify(1, 0) //
        .verify(IllegalStateException.class);

    // Read locked by same owner + write locked by same owner.
    initial(1, 0, 1) //
        .modify(1, 0) //
        .verifyUnmodified();

    // Read locked by different owner + write different by same owner.
    initial(2, 0, 2) //
        .modify(1, 0) //
        .verify(IllegalStateException.class);

    // Read locked by same owner + write optioned by different owner.
    initial(0, 2, 1) //
        .modify(1, 0) //
        .verify(IllegalStateException.class);

    // Read locked by different owner + write optioned by different owner.
    initial(0, 2, 2) //
        .modify(1, 0) //
        .verify(IllegalStateException.class);

    // Read locked by same and different owner + write optioned by different owner.
    initial(0, 2, 1, 2) //
        .modify(1, 0) //
        .verify(IllegalStateException.class);

    // Read locked by same and two different owners + write optioned by different owner.
    initial(0, 2, 1, 2, 3) //
        .modify(1, 0) //
        .verify(IllegalStateException.class);

    // Read locked by two different owners + write optioned by different owner.
    initial(0, 2, 2, 3) //
        .modify(1, 0) //
        .verify(IllegalStateException.class);

    // Read locked by three different owners + write optioned by different owner.
    initial(0, 2, 2, 3, 4) //
        .modify(1, 0) //
        .verify(IllegalStateException.class);

    // Read locked by same owner + write optioned by same owner.
    initial(0, 1, 1) //
        .modify(1, 0) //
        .verify(1, 0);

    // Read locked by different owner + write optioned by different owner.
    initial(0, 1, 2) //
        .modify(1, 0) //
        .verify(IllegalStateException.class);

    // Read locked by same and different owner + write optioned by same owner.
    initial(0, 1, 1, 2) //
        .modify(1, 0) //
        .verify(IllegalStateException.class);

    // Read locked by same and two different owners + write optioned by same owner.
    initial(0, 1, 1, 2, 3) //
        .modify(1, 0) //
        .verify(IllegalStateException.class);

    // Read locked by two different owners + write optioned by same owner.
    initial(0, 1, 2, 3) //
        .modify(1, 0) //
        .verify(IllegalStateException.class);

    // Read locked by three different owners + write optioned by same owner.
    initial(0, 1, 2, 3, 4) //
        .modify(1, 0) //
        .verify(IllegalStateException.class);
  }

  public void testUnsetWriteLockOwner() throws Exception
  {
    // Unlocked.
    initial() //
        .modify(-1, 0) //
        .verifyUnmodified();

    // Write locked.
    initial(1, 0) //
        .modify(-1, 0) //
        .verifyUnlocked();

    // Write optioned.
    initial(0, 1) //
        .modify(-1, 0) //
        .verifyUnmodified();

    // Read locked.
    initial(0, 0, 1) //
        .modify(-1, 0) //
        .verifyUnmodified();

    // Read locked by two owners.
    initial(0, 0, 1, 2) //
        .modify(-1, 0) //
        .verifyUnmodified();

    // Read locked by three owners.
    initial(0, 0, 2, 3, 4) //
        .modify(-1, 0) //
        .verifyUnmodified();

    // Read locked and write locked.
    initial(1, 0, 1) //
        .modify(-1, 0) // Lock downgrade.
        .verify(0, 0, 1);

    // Read locked and write optioned.
    initial(0, 1, 1) //
        .modify(-1, 0) //
        .verifyUnmodified();

    initial(0, 1, 1, 2) //
        .modify(-1, 0) //
        .verifyUnmodified();

    initial(0, 1, 1, 2, 3) //
        .modify(-1, 0) //
        .verifyUnmodified();

    initial(0, 4, 1) //
        .modify(-1, 0) //
        .verifyUnmodified();

    initial(0, 4, 1, 2) //
        .modify(-1, 0) //
        .verifyUnmodified();

    initial(0, 4, 1, 2, 3) //
        .modify(-1, 0) //
        .verifyUnmodified();
  }

  public void testSetWriteOptionOwner() throws Exception
  {
    // Unlocked.
    initial() //
        .modify(0, 1) //
        .verify(0, 1);

    // Write optioned by same owner.
    initial(0, 1) //
        .modify(0, 1) //
        .verify(0, 1);

    // Write optioned by different owner.
    initial(0, 2) //
        .modify(0, 1) //
        .verify(IllegalStateException.class);

    // Write locked by same owner.
    initial(1, 0) //
        .modify(0, 1) //
        .verify(IllegalStateException.class);

    // Write locked by different owner.
    initial(2, 0) //
        .modify(0, 1) //
        .verify(IllegalStateException.class);

    // Read locked by same owner.
    initial(0, 0, 1) //
        .modify(0, 1) //
        .verify(0, 1, 1);

    // Read locked by different owner.
    initial(0, 0, 2) //
        .modify(0, 1) //
        .verify(0, 1, 2);

    // Write locked by same owner and read locked by same owner.
    initial(1, 0, 1) //
        .modify(0, 1) //
        .verify(IllegalStateException.class);

    // Write optioned by same owner and read locked by same owner.
    initial(0, 1, 1) //
        .modify(0, 1) //
        .verifyUnmodified();

    // Write optioned by same owner and read locked by different owner.
    initial(0, 1, 2) //
        .modify(0, 1) //
        .verifyUnmodified();

    // Write optioned by different owner and read locked by same owner.
    initial(0, 2, 1) //
        .modify(0, 1) //
        .verify(IllegalStateException.class);

    // Write optioned by different owner and read locked by different owners.
    initial(0, 2, 2) //
        .modify(0, 1) //
        .verify(IllegalStateException.class);

    initial(0, 2, 3) //
        .modify(0, 1) //
        .verify(IllegalStateException.class);

    initial(0, 2, 2, 3) //
        .modify(0, 1) //
        .verify(IllegalStateException.class);

    initial(0, 2, 2, 3, 4) //
        .modify(0, 1) //
        .verify(IllegalStateException.class);
  }

  public void testUnsetWriteOptionOwner() throws Exception
  {
    // Unlocked.
    initial() //
        .modify(0, -1) //
        .verifyUnmodified();

    // Write locked.
    initial(1, 0) //
        .modify(0, -1) //
        .verifyUnmodified();

    // Write optioned.
    initial(0, 1) //
        .modify(0, -1) //
        .verifyUnlocked();

    // Read locked.
    initial(0, 0, 1) //
        .modify(0, -1) //
        .verifyUnmodified();

    // Read locked by two owners.
    initial(0, 0, 1, 2) //
        .modify(0, -1) //
        .verifyUnmodified();

    // Read locked by three owners.
    initial(0, 0, 2, 3, 4) //
        .modify(0, -1) //
        .verifyUnmodified();

    // Read locked and write locked.
    initial(1, 0, 1) //
        .modify(0, -1) //
        .verifyUnmodified();

    // Read locked and write optioned.
    initial(0, 1, 1) //
        .modify(0, -1) //
        .verify(0, 0, 1);

    initial(0, 1, 1, 2) //
        .modify(0, -1) //
        .verify(0, 0, 1, 2);

    initial(0, 1, 1, 2, 3) //
        .modify(0, -1) //
        .verify(0, 0, 1, 2, 3);
  }

  public void testAddReadLockOwner()
  {
    // Unlocked.
    initial() //
        .modify(0, 0, 1) //
        .verify(0, 0, 1);

    // Read locked by same owner.
    initial(0, 0, 1) //
        .modify(0, 0, 1) //
        .verify(0, 0, 1);

    // Read locked by different owner.
    initial(0, 0, 2) //
        .modify(0, 0, 1) //
        .verify(0, 0, 2, 1);

    // Read locked by two different owners.
    initial(0, 0, 2, 3) //
        .modify(0, 0, 1) //
        .verify(0, 0, 2, 3, 1);

    // Read locked by three different owners.
    initial(0, 0, 2, 3, 4) //
        .modify(0, 0, 1) //
        .verify(0, 0, 2, 3, 4, 1);

    // Write locked by same owner.
    initial(1, 0) //
        .modify(0, 0, 1) //
        .verify(IllegalStateException.class);

    // Write locked by same owner and read locked by same owner.
    initial(1, 0, 1) //
        .modify(0, 0, 1) //
        .verifyUnmodified();

    // Write locked by different owner.
    initial(2, 0) //
        .modify(0, 0, 1) //
        .verify(IllegalStateException.class);

    // Write locked by different owner and read locked by different owner.
    initial(2, 0, 2) //
        .modify(0, 0, 1) //
        .verify(IllegalStateException.class);

    // Write optioned by same owner.
    initial(0, 1) //
        .modify(0, 0, 1) //
        .verify(0, 1, 1);

    // Write optioned by same owner and read locked by same owner.
    initial(0, 1, 1) //
        .modify(0, 0, 1) //
        .verifyUnmodified();

    // Write optioned by same owner and read locked by different owner.
    initial(0, 1, 2) //
        .modify(0, 0, 1) //
        .verify(0, 1, 2, 1);

    // Write optioned by same owner and read locked by two different owners.
    initial(0, 1, 2, 3) //
        .modify(0, 0, 1) //
        .verify(0, 1, 2, 3, 1);

    // Write optioned by same owner and read locked by three different owners.
    initial(0, 1, 2, 3, 4) //
        .modify(0, 0, 1) //
        .verify(0, 1, 2, 3, 4, 1);

    // Write optioned by different owner.
    initial(0, 2) //
        .modify(0, 0, 1) //
        .verify(0, 2, 1);

    // Write optioned by different owner and read locked by same owner.
    initial(0, 2, 1) //
        .modify(0, 0, 1) //
        .verifyUnmodified();

    // Write optioned by different owner and read locked by different owner.
    initial(0, 2, 2) //
        .modify(0, 0, 1) //
        .verify(0, 2, 2, 1);

    // Write optioned by different owner and read locked by two different owners.
    initial(0, 2, 2, 3) //
        .modify(0, 0, 1) //
        .verify(0, 2, 2, 3, 1);

    // Write optioned by different owner and read locked by three different owners.
    initial(0, 2, 2, 3, 4) //
        .modify(0, 0, 1) //
        .verify(0, 2, 2, 3, 4, 1);
  }

  public void testRemoveReadLockOwner()
  {
    // Unlocked.
    initial() //
        .modify(0, 0, -1) //
        .verifyUnmodified();

    // Read locked by same owner.
    initial(0, 0, 1) //
        .modify(0, 0, -1) //
        .verifyUnlocked();

    // Read locked by same owner and different owner.
    initial(0, 0, 1, 2) //
        .modify(0, 0, -1) //
        .verify(0, 0, 2);

    // Read locked by same owner and two different owners.
    initial(0, 0, 1, 2, 3) //
        .modify(0, 0, -1) //
        .verify(0, 0, 2, 3);

    // Read locked by same owner and two different owners.
    initial(0, 0, 1, 2, 3, 4) //
        .modify(0, 0, -1) //
        .verify(0, 0, 2, 3, 4);

    // Read locked by different owner.
    initial(0, 0, 2) //
        .modify(0, 0, -1) //
        .verifyUnmodified();

    // Read locked by two different owners.
    initial(0, 0, 2, 3) //
        .modify(0, 0, -1) //
        .verifyUnmodified();

    // Read locked by three different owners.
    initial(0, 0, 2, 3, 4) //
        .modify(0, 0, -1) //
        .verifyUnmodified();

    // Write locked by same owner.
    initial(1, 0) //
        .modify(0, 0, -1) //
        .verifyUnmodified();

    // Write locked by same owner and read locked by same owner.
    initial(1, 0, 1) //
        .modify(0, 0, -1) //
        .verify(1, 0);

    // Write locked by different owner and read locked by different owner.
    initial(2, 0, 2) //
        .modify(0, 0, -1) //
        .verifyUnmodified();

    // Write locked by different owner.
    initial(2, 0) //
        .modify(0, 0, -1) //
        .verifyUnmodified();

    // Write optioned by same owner.
    initial(0, 1) //
        .modify(0, 0, -1) //
        .verifyUnmodified();

    // Write optioned by same owner and read locked by same owner.
    initial(0, 1, 1) //
        .modify(0, 0, -1) //
        .verify(0, 1);

    // Write optioned by same owner and read locked by same owner and different owner.
    initial(0, 1, 1, 2) //
        .modify(0, 0, -1) //
        .verify(0, 1, 2);

    initial(0, 1, 1, 2) //
        .modify(0, 0, -2) //
        .verify(0, 1, 1);

    initial(0, 1, 1, 2, 3, 4) //
        .modify(0, 0, -2) //
        .verify(0, 1, 1, 3, 4);

    // Write optioned by same owner and read locked by different owner.
    initial(0, 1, 2) //
        .modify(0, 0, -1) //
        .verifyUnmodified();

    // Write optioned by same owner and read locked by two different owners.
    initial(0, 1, 2, 3) //
        .modify(0, 0, -1) //
        .verifyUnmodified();

    // Write optioned by same owner and read locked by three different owners.
    initial(0, 1, 2, 3, 4) //
        .modify(0, 0, -1) //
        .verifyUnmodified();

    // Write optioned by different owner.
    initial(0, 2) //
        .modify(0, 0, -1) //
        .verifyUnmodified();

    // Write optioned by different owner and read locked by same owner.
    initial(0, 2, 1) //
        .modify(0, 0, -1) //
        .verify(0, 2);

    // Write optioned by different owner and read locked by different owner.
    initial(0, 2, 2) //
        .modify(0, 0, -1) //
        .verifyUnmodified();

    // Write optioned by different owner and read locked by two different owners.
    initial(0, 2, 2, 3) //
        .modify(0, 0, -1) //
        .verifyUnmodified();

    // Write optioned by different owner and read locked by three different owners.
    initial(0, 2, 2, 3, 4) //
        .modify(0, 0, -1) //
        .verifyUnmodified();
  }

  public void testRemoveOwner()
  {
    // Unlocked.
    initial() //
        .removeOwner(1) //
        .verifyUnmodified();

    // Read locked by same owner.
    initial(0, 0, 1) //
        .removeOwner(1) //
        .verifyUnlocked();

    // Read locked by same owner and different owner.
    initial(0, 0, 1, 2) //
        .removeOwner(1) //
        .verify(0, 0, 2);

    // Read locked by same owner and two different owners.
    initial(0, 0, 1, 2, 3) //
        .removeOwner(1) //
        .verify(0, 0, 2, 3);

    // Read locked by same owner and two different owners.
    initial(0, 0, 1, 2, 3, 4) //
        .removeOwner(1) //
        .verify(0, 0, 2, 3, 4);

    // Read locked by different owner.
    initial(0, 0, 2) //
        .removeOwner(1) //
        .verifyUnmodified();

    // Read locked by two different owners.
    initial(0, 0, 2, 3) //
        .removeOwner(1) //
        .verifyUnmodified();

    // Read locked by three different owners.
    initial(0, 0, 2, 3, 4) //
        .removeOwner(1) //
        .verifyUnmodified();

    // Write locked by same owner.
    initial(1, 0) //
        .removeOwner(1) //
        .verifyUnlocked();

    // Write locked by different owner.
    initial(2, 0) //
        .removeOwner(1) //
        .verifyUnmodified();

    // Write optioned by same owner.
    initial(0, 1) //
        .removeOwner(1) //
        .verifyUnlocked();

    // Write optioned by same owner and read locked by same owner.
    initial(0, 1, 1) //
        .removeOwner(1) //
        .verifyUnlocked();

    // Write optioned by same owner and read locked by same owner and different owner.
    initial(0, 1, 1, 2) //
        .removeOwner(1) //
        .verify(0, 0, 2);

    initial(0, 1, 1, 2) //
        .modify(0, 0, -2) //
        .verify(0, 1, 1);

    initial(0, 1, 1, 2, 3, 4) //
        .modify(0, 0, -2) //
        .verify(0, 1, 1, 3, 4);

    // Write optioned by same owner and read locked by different owner.
    initial(0, 1, 2) //
        .removeOwner(1) //
        .verify(0, 0, 2);

    // Write optioned by same owner and read locked by two different owners.
    initial(0, 1, 2, 3) //
        .removeOwner(1) //
        .verify(0, 0, 2, 3);

    // Write optioned by same owner and read locked by three different owners.
    initial(0, 1, 2, 3, 4) //
        .removeOwner(1) //
        .verify(0, 0, 2, 3, 4);

    // Write optioned by different owner.
    initial(0, 2) //
        .removeOwner(1) //
        .verifyUnmodified();

    // Write optioned by different owner and read locked by same owner.
    initial(0, 2, 1) //
        .removeOwner(1) //
        .verify(0, 2);

    // Write optioned by different owner and read locked by different owner.
    initial(0, 2, 2) //
        .removeOwner(1) //
        .verifyUnmodified();

    // Write optioned by different owner and read locked by two different owners.
    initial(0, 2, 2, 3) //
        .removeOwner(1) //
        .verifyUnmodified();

    // Write optioned by different owner and read locked by three different owners.
    initial(0, 2, 2, 3, 4) //
        .removeOwner(1) //
        .verifyUnmodified();
  }

  protected CDOBranch getTestBranch(CDOSession session)
  {
    return session.getBranchManager().getMainBranch();
  }

  private TestLockState initial()
  {
    return initial(0, 0);
  }

  private TestLockState initial(int writer, int optioner, int... readers)
  {
    return new TestLockState(writer, optioner, readers);
  }

  private static InternalCDOLockState getLockState(CDOLockStateCache cache, CDOView view)
  {
    CDOBranch branch = view.getBranch();
    CDOID id = view.getRootResource().cdoID();
    return (InternalCDOLockState)cache.getLockState(branch, id);
  }

  /**
   * @author Eike Stepper
   */
  public final class TestLockState
  {
    private int initialWriter;

    private int initialOptioner;

    private int[] initialReaders;

    private CDOLockStateCache cache;

    private CDOLockOwner[] lockOwners = new CDOLockOwner[5];

    private InternalCDOLockState lockState;

    public TestLockState(int writer, int optioner, int... readers)
    {
      initialWriter = writer;
      initialOptioner = optioner;
      initialReaders = readers;

      CDOSession session = openSession();
      cache = new CDOLockStateCacheImpl(session);
      CDOBranchPoint head = getTestBranch(session).getHead();

      for (int i = 0; i < lockOwners.length; i++)
      {
        CDOView view = session.openView(head);

        if (i == 0)
        {
          lockState = getLockState(cache, view);
        }
        else
        {
          lockOwners[i] = view.getLockOwner();
        }
      }

      update(writer, optioner, readers);
    }

    private void update(int writer, int optioner, int... readers)
    {
      for (int i = 0; i < readers.length; i++)
      {
        int reader = readers[i];
        if (reader > 0)
        {
          lockState.addReadLockOwner(lockOwners[reader]);
        }
        else
        {
          lockState.removeReadLockOwner(lockOwners[-reader]);
        }
      }

      if (writer == -1)
      {
        lockState.setWriteLockOwner(null);
      }
      else if (writer > 0)
      {
        lockState.setWriteLockOwner(lockOwners[writer]);
      }

      if (optioner == -1)
      {
        lockState.setWriteOptionOwner(null);
      }
      else if (optioner > 0)
      {
        lockState.setWriteOptionOwner(lockOwners[optioner]);
      }
    }

    private void update(int ownerToRemove)
    {
      lockState.removeOwner(lockOwners[ownerToRemove]);
    }

    public TestExecution modify(int writer, int optioner, int... readers)
    {
      return new TestExecution(writer, optioner, readers);
    }

    public TestExecution removeOwner(int owner)
    {
      return new TestExecution(owner);
    }

    /**
     * @author Eike Stepper
     */
    public final class TestExecution
    {
      private int writer;

      private int optioner;

      private int[] readers;

      private int ownerToRemove;

      public TestExecution(int writer, int optioner, int[] readers)
      {
        this.writer = writer;
        this.optioner = optioner;
        this.readers = readers;
      }

      public TestExecution(int ownerToRemove)
      {
        this.ownerToRemove = ownerToRemove;
      }

      public void verify(int writer, int optioner, int... readers)
      {
        try
        {
          update();

          CDOLockOwner writeLockOwner = lockState.getWriteLockOwner();
          assertEquals(lockOwners[writer], writeLockOwner);
          assertEquals(writeLockOwner != null, lockState.isLocked(LockType.WRITE, writeLockOwner, false));
          assertEquals(writeLockOwner != null, lockState.isLocked(null, writeLockOwner, false));

          CDOLockOwner writeOptionOwner = lockState.getWriteOptionOwner();
          assertEquals(lockOwners[optioner], writeOptionOwner);
          assertEquals(writeOptionOwner != null, lockState.isLocked(LockType.OPTION, writeOptionOwner, false));
          assertEquals(writeOptionOwner != null, lockState.isLocked(null, writeOptionOwner, false));

          Set<CDOLockOwner> readLockOwners = lockState.getReadLockOwners();
          assertEquals(readers.length, readLockOwners.size());

          for (int i = 0; i < readers.length; i++)
          {
            CDOLockOwner readLockOwner = lockOwners[readers[i]];
            assertTrue(readLockOwners.contains(readLockOwner));
          }

          for (int i = 1; i < lockOwners.length; i++)
          {
            CDOLockOwner readLockOwner = lockOwners[i];

            boolean readLocked = readLockOwners.contains(readLockOwner);
            assertEquals(readLocked, lockState.isLocked(LockType.READ, readLockOwner, false));

            boolean locked = readLockOwner == writeLockOwner || readLockOwner == writeOptionOwner || readLocked;
            assertEquals(locked, lockState.isLocked(null, readLockOwner, false));
          }
        }
        catch (Error ex)
        {
          dumpActualLockState();
          throw ex;
        }
        finally
        {
          cache.getSession().close();
        }
      }

      public void verifyUnlocked()
      {
        verify(0, 0);
      }

      public void verifyUnmodified()
      {
        verify(initialWriter, initialOptioner, initialReaders);
      }

      public void verify(Class<? extends Throwable> type)
      {
        try
        {
          assertException(type, () -> update());
        }
        catch (Error ex)
        {
          dumpActualLockState();
          throw ex;
        }
        finally
        {
          cache.getSession().close();
        }
      }

      private void update()
      {
        if (ownerToRemove != 0)
        {
          TestLockState.this.update(ownerToRemove);
        }
        else
        {
          TestLockState.this.update(writer, optioner, readers);
        }
      }

      private void dumpActualLockState()
      {
        IOUtil.ERR().println();
        IOUtil.ERR().println("################################################");
        IOUtil.ERR().println("Actual lock state: " //
            + index(lockState.getWriteLockOwner()) + ", " //
            + index(lockState.getWriteOptionOwner()) //
            + index(lockState.getReadLockOwners()));
        IOUtil.ERR().println("################################################");
        IOUtil.ERR().println();
      }

      private String index(Set<CDOLockOwner> lockOwners)
      {
        StringBuilder builder = new StringBuilder();
        for (CDOLockOwner lockOwner : lockOwners)
        {
          builder.append(", ");
          builder.append(index(lockOwner));
        }

        return builder.toString();
      }

      private String index(CDOLockOwner lockOwner)
      {
        for (int i = 0; i < lockOwners.length; i++)
        {
          if (lockOwners[i] == lockOwner)
          {
            return Integer.toString(i);
          }
        }

        return null;
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public static class WithSubBranch extends LockStateCacheTest
  {
    private CDOBranch testBranch;

    @Override
    protected CDOBranch getTestBranch(CDOSession session)
    {
      if (testBranch == null)
      {
        testBranch = createUniqueBranch(session.getBranchManager().getMainBranch(), "lock-state-tests");
      }

      return testBranch;
    }

    @Override
    protected void doTearDown() throws Exception
    {
      testBranch = null;
      super.doTearDown();
    }
  }
}
