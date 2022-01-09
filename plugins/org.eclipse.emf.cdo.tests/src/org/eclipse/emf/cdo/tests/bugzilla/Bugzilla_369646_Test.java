/*
 * Copyright (c) 2012, 2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.io.IOUtil;

/**
 * Make timeouts in read-access requests configurable
 * <p>
 * See bug 369646.
 *
 * @author Eike Stepper
 */
@Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
@CleanRepositoriesBefore(reason = "Branch creation")
public class Bugzilla_369646_Test extends AbstractCDOTest
{
  public void testSetBranchWithSubBranches() throws Exception
  {
    // Note: as of bug 369646, this fails with DBStore and range-based branching mapping strategy.
    // The reason is that this strategy uses partially loaded resources internally.

    // Set up a resource with 1 object as content
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource res = transaction.createResource(getResourcePath("/test"));

    Category c = getModel1Factory().createCategory();
    c.setName("Test");

    res.getContents().add(c);
    transaction.commit();

    // Create two cascading branches
    CDOBranch sub1 = transaction.getBranch().createBranch(getBranchName("sub1"));
    CDOBranch sub2 = sub1.createBranch("sub2");

    // Now delete the contents in the sub2 branch
    transaction.setBranch(sub2);
    res.getContents().remove(0);
    transaction.commit();

    // And now try to switch the transaction to the parent branch
    transaction.setBranch(sub1);
  }

  @Requires(IRepositoryConfig.CAPABILITY_CHUNKING)
  public void testSetBranchWithPCL() throws Exception
  {
    {
      // Set up a resource with 10 objects as content
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.createResource(getResourcePath("/test"));

      Category cat = getModel1Factory().createCategory();
      cat.setName("Container");
      res.getContents().add(cat);

      for (int i = 0; i < 10; i++)
      {
        Category c = getModel1Factory().createCategory();
        c.setName("Test " + i);
        cat.getCategories().add(c);
      }

      transaction.commit();

      // Create a branch
      CDOBranch sub1 = transaction.getBranch().createBranch(getBranchName("sub1"));
      transaction.setBranch(sub1);

      // Modify list in branch
      cat.getCategories().remove(3);
      cat.getCategories().remove(7);

      // Commit
      transaction.commit();
      session.close();
    }

    // Now clear the cache on server
    clearCache(getRepository().getRevisionManager());

    // Open a new session with PCL enabled
    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 1));

    // Load the category into the resource (is now partially loaded)
    CDOView view = session.openView();
    CDOResource res = view.getResource(getResourcePath("/test"));
    Category cat = (Category)res.getContents().get(0);

    // And switch view target
    view.setBranch(view.getBranch().getBranches()[0]);
    IOUtil.OUT().println("Result: " + cat.getCategories());
  }
}
