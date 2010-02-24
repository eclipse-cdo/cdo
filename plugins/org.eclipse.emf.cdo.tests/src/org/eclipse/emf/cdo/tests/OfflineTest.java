/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.mem.MEMStore;
import org.eclipse.emf.cdo.server.InternalStore;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.MEMOffline;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class OfflineTest extends AbstractCDOTest
{
  @Override
  protected void doSetUp() throws Exception
  {
    skipUnlessConfig(MEM_OFFLINE);
    super.doSetUp();
  }

  @Override
  public MEMOffline getRepositoryConfig()
  {
    return (MEMOffline)super.getRepositoryConfig();
  }

  public void testSynchronizer() throws Exception
  {
    CDOSession session = openSession(getRepository().getName() + "_master");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource");

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    // Root resource + folder + resource + company
    int expectedRevisions = 1 + 1 + 1 + 1;

    resource.getContents().add(company);
    long timeStamp = transaction.commit().getTimeStamp();
    checkClone(timeStamp, expectedRevisions);

    for (int i = 0; i < 10; i++)
    {
      company.setName("Test" + i);
      timeStamp = transaction.commit().getTimeStamp();
      expectedRevisions += 1; // Changed company
      checkClone(timeStamp, expectedRevisions);
    }

    for (int i = 0; i < 10; i++)
    {
      company.getCategories().add(getModel1Factory().createCategory());
      timeStamp = transaction.commit().getTimeStamp();
      expectedRevisions += 2; // Changed company + new category
      checkClone(timeStamp, expectedRevisions);
    }

    for (int i = 0; i < 10; i++)
    {
      company.getCategories().remove(0);
      timeStamp = transaction.commit().getTimeStamp();
      expectedRevisions += 2; // Changed company + detached category
      checkClone(timeStamp, expectedRevisions);
    }

    session.close();
  }

  private void checkClone(final long timeStamp, int expectedRevisions) throws InterruptedException
  {
    final InternalRepository repository = getRepository();
    long lastCommitTimeStamp = repository.getLastCommitTimeStamp();
    while (lastCommitTimeStamp < timeStamp)
    {
      lastCommitTimeStamp = repository.waitForCommit(DEFAULT_TIMEOUT);
    }

    InternalStore store = repository.getStore();
    if (store instanceof MEMStore)
    {
      Map<CDOBranch, List<CDORevision>> allRevisions = ((MEMStore)store).getAllRevisions();
      System.out.println("\n\n\n\n\n\n\n\n\n\n" + CDORevisionUtil.dumpAllRevisions(allRevisions));

      List<CDORevision> revisions = allRevisions.get(repository.getBranchManager().getMainBranch());
      assertEquals(expectedRevisions, revisions.size());
    }
  }
}
