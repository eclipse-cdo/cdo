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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;

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
    CDOSession session = openSession(getRepository().getName() + "_master");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource");

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
    CDOSession masterSession = openSession(getRepository().getName() + "_master");
    CDOTransaction transaction = masterSession.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource");

    TestListener listener = new TestListener();
    CDOSession backupSession = openSession();
    backupSession.addListener(listener);

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    resource.getContents().add(company);
    long timeStamp = transaction.commit().getTimeStamp();
    assertEquals(true, backupSession.waitForUpdate(timeStamp, DEFAULT_TIMEOUT));
    checkEvent(listener, 1, 3, 1, 0);

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
    InternalRepository backup = getRepository();
    InternalRepository master = getRepository(backup.getName() + "_master");

    TestListener listener = new TestListener();
    CDOSession masterSession = openSession(master.getName());
    masterSession.addListener(listener);

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    CDOSession backupSession = openSession();
    waitForOnline(backupSession.getRepositoryInfo());

    CDOTransaction transaction = backupSession.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource");

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
}
