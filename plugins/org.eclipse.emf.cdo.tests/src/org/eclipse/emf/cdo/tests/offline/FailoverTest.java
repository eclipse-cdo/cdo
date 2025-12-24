/*
 * Copyright (c) 2011, 2012, 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.offline;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractSyncingTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.tests.TestListener;

/**
 * @author Eike Stepper
 */
public class FailoverTest extends AbstractSyncingTest
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

  public void testMasterCommits_ArrivalInBackup() throws Exception
  {
    CDOSession session = openSession("master");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("test"));

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    // 2 * Root resource + folder + resource + company
    int expectedRevisions = 2 + 1 + 1 + 1;

    resource.getContents().add(company);
    long timeStamp = transaction.commit().getTimeStamp();
    checkRevisions(getRepository(), timeStamp, expectedRevisions);

    for (int i = 0; i < 10; i++)
    {
      company.setName("Test" + i);
      timeStamp = transaction.commit().getTimeStamp();
      expectedRevisions += 1; // Changed company
      checkRevisions(getRepository(), timeStamp, expectedRevisions);
    }

    for (int i = 0; i < 10; i++)
    {
      company.getCategories().add(getModel1Factory().createCategory());
      timeStamp = transaction.commit().getTimeStamp();
      expectedRevisions += 2; // Changed company + new category
      checkRevisions(getRepository(), timeStamp, expectedRevisions);
    }

    for (int i = 0; i < 10; i++)
    {
      company.getCategories().remove(0);
      timeStamp = transaction.commit().getTimeStamp();
      expectedRevisions += 2; // Changed company + detached category
      checkRevisions(getRepository(), timeStamp, expectedRevisions);
    }

    session.close();
  }

  public void testMasterCommits_NotificationsFromBackup() throws Exception
  {
    CDOSession masterSession = openSession("master");
    CDOTransaction transaction = masterSession.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    TestListener listener = new TestListener();
    CDOSession backupSession = openSession();
    backupSession.addListener(listener);

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    resource.getContents().add(company);
    long timeStamp = transaction.commit().getTimeStamp();
    assertEquals(true, backupSession.waitForUpdate(timeStamp, DEFAULT_TIMEOUT));
    checkEvent(listener, 0, 4, 1, 0);

    for (int i = 0; i < 10; i++)
    {
      company.setName("Test" + i);
      timeStamp = transaction.commit().getTimeStamp();
      assertEquals(true, backupSession.waitForUpdate(timeStamp, DEFAULT_TIMEOUT));
      checkEvent(listener, 0, 0, 1, 0);
    }

    for (int i = 0; i < 10; i++)
    {
      company.getCategories().add(getModel1Factory().createCategory());
      timeStamp = transaction.commit().getTimeStamp();
      assertEquals(true, backupSession.waitForUpdate(timeStamp, DEFAULT_TIMEOUT));
      checkEvent(listener, 0, 1, 1, 0);
    }

    for (int i = 0; i < 10; i++)
    {
      company.getCategories().remove(0);
      timeStamp = transaction.commit().getTimeStamp();
      assertEquals(true, backupSession.waitForUpdate(timeStamp, DEFAULT_TIMEOUT));
      checkEvent(listener, 0, 0, 1, 1);
    }

    backupSession.close();
    masterSession.close();
  }

  public void testClientCommitsToBackupForbidden() throws Exception
  {
    InternalRepository master = getRepository("master");

    CDOSession masterSession = openSession(master.getName());
    waitForOnline(masterSession.getRepositoryInfo());

    TestListener listener = new TestListener();
    masterSession.addListener(listener);
    dumpEvents(masterSession);

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    InternalRepository backup = getRepository();
    CDOSession backupSession = openSession(backup.getName());
    waitForOnline(backupSession.getRepositoryInfo());

    CDOTransaction transaction = backupSession.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    resource.getContents().add(company);

    try
    {
      transaction.commit();
      fail("Exception expected");
    }
    catch (Exception expected)
    {
      // SUCCESS
    }
  }

  public void testPauseMasterTransport() throws Exception
  {
    CDOSession session = openSession("master");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    resource.getContents().add(company);
    long timeStamp = transaction.commit().getTimeStamp();

    getOfflineConfig().stopMasterTransport();
    waitForOffline(getRepository());

    for (int i = 0; i < 10; i++)
    {
      company.setName("Test" + i);
      timeStamp = transaction.commit().getTimeStamp();
    }

    for (int i = 0; i < 10; i++)
    {
      company.getCategories().add(getModel1Factory().createCategory());
      timeStamp = transaction.commit().getTimeStamp();
    }

    for (int i = 0; i < 10; i++)
    {
      company.getCategories().remove(0);
      timeStamp = transaction.commit().getTimeStamp();
    }

    getOfflineConfig().startMasterTransport();
    waitForOnline(getRepository());

    assertEquals(timeStamp, getRepository().getLastReplicatedCommitTime());
    session.close();
  }

  public void testSwitchMaster() throws Exception
  {
    CDOSession session = openSession("master");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    resource.getContents().add(company);
    transaction.commit();

    for (int i = 0; i < 10; i++)
    {
      company.setName("Test" + i);
      transaction.commit();
    }

    for (int i = 0; i < 10; i++)
    {
      company.getCategories().add(getModel1Factory().createCategory());
      transaction.commit();
    }

    for (int i = 0; i < 5; i++)
    {
      company.getCategories().remove(0);
      transaction.commit();
    }

    startBackupTransport();

    try
    {
      getRepository().setType(CDOCommonRepository.Type.MASTER);
      getRepository("master").setType(CDOCommonRepository.Type.BACKUP);
      company.setName("Commit should fail");

      try
      {
        transaction.commit();
        fail("Exception expected");
      }
      catch (Exception expected)
      {
        // SUCCESS
      }

      session.close();
      session = openSession();
      transaction = session.openTransaction();
      resource = transaction.getResource(getResourcePath("/my/resource"));
      company = (Company)resource.getContents().get(0);
      company.setName("Commit should NOT fail");
      transaction.commit();
    }
    finally
    {
      stopBackupTransport();
      session.close();
    }
  }

  public void testSwitchMasterAndCommit() throws Exception
  {
    CDOSession session = openSession("master");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    resource.getContents().add(company);
    transaction.setCommitComment("Company created");
    transaction.commit();

    for (int i = 0; i < 10; i++)
    {
      company.getCategories().add(getModel1Factory().createCategory());
      transaction.setCommitComment("Category added");
      transaction.commit();
    }

    startBackupTransport();

    try
    {
      getRepository().setType(CDOCommonRepository.Type.MASTER);
      getRepository("master").setType(CDOCommonRepository.Type.BACKUP);

      session.close();
      session = openSession();
      transaction = session.openTransaction();

      resource = transaction.getResource(getResourcePath("/my/resource"));
      company = (Company)resource.getContents().get(0);

      for (int i = 0; i < 10; i++)
      {
        company.setName("AfterFailover-" + i);
        transaction.setCommitComment("Name changed after failover");
        transaction.commit();
      }
    }
    finally
    {
      stopBackupTransport();
      session.close();
    }
  }
}
