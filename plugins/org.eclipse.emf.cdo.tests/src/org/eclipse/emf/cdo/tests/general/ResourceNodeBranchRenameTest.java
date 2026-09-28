/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.general;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesAfter;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * Controls for ResourceNode branch commits without a SecurityManager.
 *
 * @author Eike Stepper
 */
@CleanRepositoriesBefore(reason = "ResourceNode branch rename control")
@CleanRepositoriesAfter(reason = "ResourceNode branch rename control")
public class ResourceNodeBranchRenameTest extends AbstractCDOTest
{
  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testBranchResourceRenameCommitWithoutSecurityManager() throws Exception
  {
    String resourcePath = getResourcePath("resource-node-branch-rename");
    String renamedPath = resourcePath.substring(0, resourcePath.lastIndexOf('/')) + "/renamed";

    try (CDOSession session = openSession())
    {
      CDOTransaction mainTransaction = session.openTransaction();
      mainTransaction.createResource(resourcePath);
      CDOCommitInfo commitInfo = mainTransaction.commit();
      mainTransaction.close();

      CDOBranch branch = session.getBranchManager().getMainBranch().createBranch(getBranchName("rename"), commitInfo.getTimeStamp());

      try (CDOTransaction branchTransaction = session.openTransaction(branch))
      {
        branchTransaction.getResource(resourcePath).setName("renamed");
        branchTransaction.commit();
        assertTrue(branchTransaction.hasResource(renamedPath));
      }
    }
  }
}
