/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractLockingTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * @author Eike Stepper
 */
public class Bugzilla_562246_Test extends AbstractLockingTest
{
  public void _testLockAutoReleasePropagation() throws Exception
  {
    for (int i = 0; i < 10000; i++)
    {
      System.out.println("Different session: " + i);
      testLockAutoReleasePropagation_DifferentSession();
      closeAllSessions();

      System.out.println("Same session: " + i);
      testLockAutoReleasePropagation_SameSession();
      closeAllSessions();
    }
  }

  public void testLockAutoReleasePropagation_DifferentSession() throws Exception
  {
    run(false);
  }

  public void testLockAutoReleasePropagation_SameSession() throws Exception
  {
    run(true);
  }

  private void run(boolean sameSession) throws ConcurrentAccessException, CommitException, Exception
  {
    CDOSession session = openSession();

    CDOTransaction trans = session.openTransaction();
    trans.getOrCreateResource(getResourcePath(sameSession));
    trans.commit();
    trans.close();

    CDOView view = session.openView();
    view.options().setLockNotificationEnabled(true);
    CDOResource resource = view.getResource(getResourcePath(sameSession));
    CDOLock writeLock = resource.cdoWriteLock();

    lockAndUnlock(view, sameSession);
    assertFalse("Lock auto-release was not successful", writeLock.isLockedByOthers());

    // Important for this bug: test it a second time
    lockAndUnlock(view, sameSession);
    assertFalse("Lock auto-release was not successful", writeLock.isLockedByOthers());
  }

  private void lockAndUnlock(CDOView view, boolean sameSession) throws Exception
  {
    CDOSession session = sameSession ? view.getSession() : openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOCommitInfo commitInfo;

    try
    {
      transaction.options().setAutoReleaseLocksEnabled(true);
      CDOResource resource = transaction.getResource(getResourcePath(sameSession));
      resource.cdoWriteLock().lock(500);

      commitInfo = transaction.commit();
      sleep(200);
    }
    finally
    {
      transaction.close();

      if (!sameSession)
      {
        session.close();
      }
    }

    view.waitForUpdate(commitInfo.getTimeStamp());
  }

  private String getResourcePath(boolean sameSession)
  {
    return getResourcePath("/autoReleaseLockTest" + (sameSession ? "SameSesssion" : "DifferentSession"));
  }
}
