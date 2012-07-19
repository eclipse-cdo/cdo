/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Szabolcs Bardy - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

/**
 * @author Szabolcs Bardy
 */
public class Bugzilla_337054_Test extends AbstractCDOTest
{
  private final static String RESOURCE_NAME = "/337054";

  private int testedListSize = 3;

  @Requires({ IRepositoryConfig.CAPABILITY_BRANCHING, IRepositoryConfig.CAPABILITY_RESTARTABLE })
  public void testCDOElementProxies() throws Exception
  {
    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(0, 300));

    CDOTransaction mainTransaction = session.openTransaction();
    CDOResource resource = mainTransaction.createResource(getResourcePath(RESOURCE_NAME));

    // fill up resource contents if empty
    int actualSize = resource.getContents().size();
    if (actualSize < testedListSize)
    {
      msg("Filling up list...");

      for (int i = actualSize; i < testedListSize; i++)
      {
        Company company = getModel1Factory().createCompany();
        company.setName("TestCompany_" + i);
        resource.getContents().add(company);

        resource.getContents().add(company);

        actualSize++;
      }

      msg("Committing data...");
      mainTransaction.commit();
    }

    mainTransaction.close();

    msg("Creating a branch with a new element: ");

    String branchName = String.valueOf(System.currentTimeMillis());
    CDOBranch branch = session.getBranchManager().getMainBranch().createBranch(branchName);

    Company branchCompany = getModel1Factory().createCompany();
    branchCompany.setName("TestCompany_" + actualSize);

    CDOTransaction branchTransaction = session.openTransaction(branch);
    CDOResource branchRootResource = branchTransaction.getOrCreateResource(getResourcePath(RESOURCE_NAME));
    branchRootResource.getContents().add(branchCompany);

    branchTransaction.commit();
    branchTransaction.close();

    // restart repository
    restartRepository();

    session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(0, 300));

    // merge
    mainTransaction = session.openTransaction(session.getBranchManager().getMainBranch());
    branch = session.getBranchManager().getMainBranch().getBranch(branchName);

    resource = mainTransaction.getResource(getResourcePath(RESOURCE_NAME));
    int resourceContentsBeforeMerge = resource.getContents().size();
    msg("Before: " + resourceContentsBeforeMerge);

    mainTransaction.merge(branch.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    mainTransaction.commit();

    int resourceContentsAfterMerge = resource.getContents().size();
    msg("After: " + resourceContentsAfterMerge);

    assertEquals(resourceContentsBeforeMerge + 1, resourceContentsAfterMerge);
  }
}
