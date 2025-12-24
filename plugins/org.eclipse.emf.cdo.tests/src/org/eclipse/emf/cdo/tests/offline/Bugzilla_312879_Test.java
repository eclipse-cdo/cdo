/*
 * Copyright (c) 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fl�gge - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.offline;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalSynchronizableRepository;
import org.eclipse.emf.cdo.tests.AbstractSyncingTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;

/**
 * @author Martin Fl�gge
 */
public class Bugzilla_312879_Test extends AbstractSyncingTest
{
  private IAcceptor backupAcceptor;

  @Override
  public void tearDown() throws Exception
  {
    super.tearDown();
    stopBackupTransport();
  }

  protected void startBackupTransport()
  {
    if (backupAcceptor == null)
    {
      IOUtil.OUT().println();
      IOUtil.OUT().println("startBackupTransport()");
      IOUtil.OUT().println();
      IManagedContainer container = getServerContainer();
      backupAcceptor = (IAcceptor)container.getElement("org.eclipse.net4j.acceptors", "jvm", "backup");
    }
  }

  protected void stopBackupTransport()
  {
    if (backupAcceptor != null)
    {
      IOUtil.OUT().println();
      IOUtil.OUT().println("stopBackupTransport()");
      IOUtil.OUT().println();
      backupAcceptor.close();
      backupAcceptor = null;
    }
  }

  @Override
  protected boolean isFailover()
  {
    return true;
  }

  public void testSwitchMasterAndCommit() throws Exception
  {

    InternalSynchronizableRepository repo1_master = (InternalSynchronizableRepository)getRepository("master");
    InternalSynchronizableRepository repo1 = getRepository();

    InternalSynchronizableRepository master = repo1_master;
    InternalSynchronizableRepository backup = repo1;

    CDOSession session = openSession(master.getName());
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    resource.getContents().add(company);
    transaction.setCommitComment("Company created");
    transaction.commit();

    int categoryCount = 10;
    for (int i = 0; i < categoryCount; i++)
    {
      Category category = getModel1Factory().createCategory();
      category.setName("cat" + i);
      company.getCategories().add(category);
      transaction.setCommitComment("Category added " + i);
      transaction.commit();
    }
    sleep(1000);

    checkData(company, categoryCount, "cat", "Test");

    startBackupTransport();
    repo1.setType(CDOCommonRepository.Type.MASTER);
    repo1_master.setType(CDOCommonRepository.Type.BACKUP);

    master = repo1;
    backup = repo1_master;

    assertEquals(CDOCommonRepository.Type.MASTER, master.getType());
    assertEquals(CDOCommonRepository.Type.BACKUP, backup.getType());

    // make sure the backup repository is in sync
    waitForOnline(backup);

    session.close();
    session = openSession(master.getName());

    // make sure we are running the session on the master
    assertEquals(CDOCommonRepository.Type.MASTER, session.getRepositoryInfo().getType());
    transaction = session.openTransaction();

    resource = transaction.getResource(getResourcePath("/my/resource"));
    company = (Company)resource.getContents().get(0);

    checkData(company, categoryCount, "cat", "Test");

    int i = 0;
    for (i = 0; i < 10; i++)
    {
      company.setName("AfterFailover-" + i);
      transaction.setCommitComment("Name changed after failover");
      transaction.commit();
    }

    int c = 0;
    for (Category category : company.getCategories())
    {
      category.setName("CHANGED" + c++);
      CDOCommitInfo commitInfo = transaction.commit();
      assertEquals(0, commitInfo.getBranch().getID());
    }

    checkData(company, categoryCount, "CHANGED", "AfterFailover-" + (i - 1));

    // Switch Master back to master -----------------------------------------
    assertEquals(CDOCommonRepository.Type.BACKUP, backup.getType());
    stopBackupTransport();
    repo1_master.setType(CDOCommonRepository.Type.MASTER);
    repo1.setType(CDOCommonRepository.Type.BACKUP);

    master = repo1_master;
    backup = repo1;

    assertEquals(CDOCommonRepository.Type.MASTER, master.getType());
    assertEquals(CDOCommonRepository.Type.BACKUP, backup.getType());

    waitForOnline(backup);

    session.close();

    session = openSession(master.getName());
    assertEquals(CDOCommonRepository.Type.MASTER, session.getRepositoryInfo().getType());
    transaction = session.openTransaction();

    resource = transaction.getResource(getResourcePath("/my/resource"));
    company = (Company)resource.getContents().get(0);

    checkData(company, categoryCount, "CHANGED", "AfterFailover-" + (i - 1));

    for (i = 0; i < 10; i++)
    {
      company.setName("AfterSwitchbackToMaster-" + i);
      transaction.setCommitComment("Name changed after fallback to master");
      transaction.commit();
    }

    c = 0;
    for (Category category : company.getCategories())
    {
      category.setName("LASTCHANGE" + c++);
      CDOCommitInfo commitInfo = transaction.commit();
      assertEquals(0, commitInfo.getBranch().getID());
    }

    checkData(company, categoryCount, "LASTCHANGE", "AfterSwitchbackToMaster-" + (i - 1));

    session.close();
  }

  /**
   * checks for the correct naming of Company and it's Categories
   */
  private void checkData(Company company, int categoryCount, String categoryNamePrefix, String companyName)
  {
    assertEquals(companyName, company.getName());
    assertEquals(categoryCount, company.getCategories().size());
    int c = 0;
    for (Category category : company.getCategories())
    {
      assertEquals(categoryNamePrefix + c++, category.getName());
    }
  }
}
