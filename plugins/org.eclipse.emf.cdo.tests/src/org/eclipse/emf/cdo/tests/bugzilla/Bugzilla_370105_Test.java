/*
 * Copyright (c) 2012-2014, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.common.util.EList;

/**
 * @author Stefan Winkler
 */
public class Bugzilla_370105_Test extends AbstractCDOTest
{
  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  @CleanRepositoriesBefore(reason = "to not be disturb by branches created by other tests")
  public void testInsertInListOf5thSubBranch() throws Throwable
  {
    Company company = initModel();

    CDOTransaction transaction = (CDOTransaction)CDOUtil.getCDOObject(company).cdoView();

    // Create 5 cascading branches
    CDOBranch parent = transaction.getBranch();
    for (int i = 0; i < 3; i++)
    {
      String branchName = "Branch-" + i;
      if (parent.isMainBranch())
      {
        branchName = getBranchName(branchName);
      }

      CDOBranch child = parent.createBranch(branchName);
      parent = child;
    }

    int branchId = parent.getID();

    // touch company in every branch
    CDOBranch branch = transaction.getBranch();
    for (int i = 0; i < 3; i++)
    {
      CDOTransaction tx2 = transaction.getSession().openTransaction(branch);
      CDOResource resource = tx2.getResource(getResourcePath("res"));
      Company c = (Company)resource.getContents().get(0);
      c.setName("Test-" + i);
      tx2.commit();
      tx2.close();
      branch = branch.getBranches()[0];
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession readSession = openSession();
    CDOBranch readBranch = readSession.getBranchManager().getBranch(branchId);

    // add category to each company
    for (int i = 0; i < 3; i++)
    {
      CDOTransaction transaction2 = readSession.openTransaction(readBranch);
      Category c = getModel1Factory().createCategory();
      c.setName("New");

      Company companyTx = transaction2.getObject(company);
      EList<Category> categories = companyTx.getCategories();
      categories.add(0, c);
      transaction2.commit();
      readBranch = readBranch.getBase().getBranch();
      transaction2.close();
    }
  }

  private Company initModel() throws CommitException
  {
    msg("Initializing model ...");
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("res"));

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);

    for (int i = 0; i < 5; i++)
    {
      Category cat = getModel1Factory().createCategory();
      cat.setName(Integer.toString(i));
      company.getCategories().add(cat);
    }

    transaction.commit();

    return company;
  }
}
