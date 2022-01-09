/*
 * Copyright (c) 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Skips;

/**
 * Bug 414270: Do not access sub branches if repository does not support branching.
 *
 * @author Eike Stepper
 */
@Skips(IRepositoryConfig.CAPABILITY_BRANCHING)
public class Bugzilla_414270_Test extends AbstractCDOTest
{
  public void testGetBranches() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOBranch[] branches = mainBranch.getBranches();
    assertEquals(0, branches.length);
  }

  public void testGetBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOBranch branch = mainBranch.getBranch(getBranchName("blabla"));
    assertEquals(null, branch);
  }

  public void testCreateBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();

    try
    {
      mainBranch.createBranch(getBranchName("blabla"));
      fail("IllegalStateException expected");
    }
    catch (IllegalStateException expected)
    {
      // SUCCESS
    }
  }

  public void testManagerGetBranchByID() throws Exception
  {
    CDOSession session = openSession();
    CDOBranchManager branchManager = session.getBranchManager();
    CDOBranch mainBranch = branchManager.getBranch(CDOBranch.MAIN_BRANCH_ID);
    assertEquals(branchManager.getMainBranch(), mainBranch);

    CDOBranch branch = branchManager.getBranch(4711);
    assertEquals(null, branch);
  }

  public void testManagerGetBranchByPath() throws Exception
  {
    CDOSession session = openSession();
    CDOBranchManager branchManager = session.getBranchManager();
    CDOBranch mainBranch = branchManager.getBranch(CDOBranch.MAIN_BRANCH_NAME);
    assertEquals(branchManager.getMainBranch(), mainBranch);

    CDOBranch branch = branchManager.getBranch("/MAIN/" + getBranchName("blabla"));
    assertEquals(null, branch);
  }

  public void testManagerQuery() throws Exception
  {
    CDOSession session = openSession();
    CDOBranchManager branchManager = session.getBranchManager();

    final CDOBranch[] result = { null };
    int count = branchManager.getBranches(CDOBranch.MAIN_BRANCH_ID, CDOBranch.MAIN_BRANCH_ID + 1, new CDOBranchHandler()
    {
      @Override
      public void handleBranch(CDOBranch branch)
      {
        if (result[0] != null)
        {
          fail("Only one result branch expected: the main branch");
        }

        result[0] = branch;
      }
    });

    assertEquals(1, count);
    assertEquals(branchManager.getMainBranch(), result[0]);
  }

  public void testManagerQueryNoResult() throws Exception
  {
    CDOSession session = openSession();
    CDOBranchManager branchManager = session.getBranchManager();

    int count = branchManager.getBranches(CDOBranch.MAIN_BRANCH_ID + 1, CDOBranch.MAIN_BRANCH_ID + 2, new CDOBranchHandler()
    {
      @Override
      public void handleBranch(CDOBranch branch)
      {
        fail("No result branch expected");
      }
    });

    assertEquals(0, count);
  }
}
