/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
  private static final String RESOURCE_NAME = "/337054";

  private static final int LIST_SIZE = 3;

  @Requires({ IRepositoryConfig.CAPABILITY_BRANCHING, IRepositoryConfig.CAPABILITY_CHUNKING, IRepositoryConfig.CAPABILITY_RESTARTABLE })
  public void testCDOElementProxies() throws Exception
  {
    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(0, 300));

    CDOTransaction mainTransaction = session.openTransaction();
    CDOResource resource = mainTransaction.createResource(getResourcePath(RESOURCE_NAME));

    msg("Filling up list...");
    for (int i = 0; i < LIST_SIZE; i++)
    {
      Company company = getModel1Factory().createCompany();
      company.setName("TestCompany_" + i);
      resource.getContents().add(company);
    }

    msg("Committing data...");
    mainTransaction.commit();
    mainTransaction.close();

    msg("Creating a branch with a new element...");
    String branchName = getBranchName(String.valueOf(System.currentTimeMillis()));
    CDOBranch branch = session.getBranchManager().getMainBranch().createBranch(branchName);

    Company branchCompany = getModel1Factory().createCompany();
    branchCompany.setName("TestCompany_" + LIST_SIZE);

    CDOTransaction branchTransaction = session.openTransaction(branch);
    CDOResource branchResource = branchTransaction.getResource(getResourcePath(RESOURCE_NAME));
    branchResource.getContents().add(branchCompany);

    branchTransaction.commit();
    branchTransaction.close();

    // Restart repository.
    restartRepository();

    session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(0, 300));

    // Merge.
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
