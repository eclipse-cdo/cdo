/*
 * Copyright (c) 2014-2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent;
import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent.ChangeKind;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Test that when another client creates a branch, and we call {@link CDOBranch#getBranches()} for the first time, it calls all existing branches.
 *
 * @author Esteban Dugueperoux
 */
@Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
@CleanRepositoriesBefore(reason = "Branching")
public class Bugzilla_447912_Test extends AbstractCDOTest
{
  public void testCDOBranch_getBranches() throws Exception
  {
    CDOSession session1 = openSession();
    CDOView view1 = session1.openView();
    CDOBranch mainBranchFromView1 = view1.getBranch();
    String branchB1Name = "b1";
    String branchB2Name = "b2";
    mainBranchFromView1.createBranch(branchB1Name);

    CDOSession session2 = openSession();
    CDOView view2 = session2.openView();
    CDOBranch mainBranchFromView2 = view2.getBranch();

    NewBranchNotificationListener listener = new NewBranchNotificationListener(session2);
    mainBranchFromView1.createBranch(branchB2Name);

    assertEquals("Timeout - No CDOBranchChangedEvent received.", true, listener.getLatch().await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));

    assertEquals(mainBranchFromView1.getBranches().length, mainBranchFromView2.getBranches().length);
    assertEquals(session1.getBranchManager().getMainBranch().getBranches().length, session2.getBranchManager().getMainBranch().getBranches().length);
    assertEquals(2, mainBranchFromView1.getBranches().length);
    assertEquals(mainBranchFromView1.getBranches()[0].getID(), mainBranchFromView2.getBranches()[0].getID());
    assertEquals(mainBranchFromView1.getBranches()[1].getID(), mainBranchFromView2.getBranches()[1].getID());
    assertEquals(mainBranchFromView1.getBranches()[0].getName(), mainBranchFromView2.getBranches()[0].getName());
    assertEquals(mainBranchFromView1.getBranches()[1].getName(), mainBranchFromView2.getBranches()[1].getName());
    assertEquals(branchB1Name, mainBranchFromView2.getBranches()[0].getName());
    assertEquals(branchB2Name, mainBranchFromView2.getBranches()[1].getName());
  }

  /**
   * @author Esteban Dugueperoux
   */
  private static final class NewBranchNotificationListener implements IListener
  {
    private final CountDownLatch latch = new CountDownLatch(1);

    public NewBranchNotificationListener(CDOSession session)
    {
      session.getBranchManager().addListener(this);
    }

    public final CountDownLatch getLatch()
    {
      return latch;
    }

    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof CDOBranchChangedEvent)
      {
        CDOBranchChangedEvent branchChangedEvent = (CDOBranchChangedEvent)event;
        if (branchChangedEvent.getChangeKind() == ChangeKind.CREATED)
        {
          latch.countDown();
        }
      }
    }
  }
}
