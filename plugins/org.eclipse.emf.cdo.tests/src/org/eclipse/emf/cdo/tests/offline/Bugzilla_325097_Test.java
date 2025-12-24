/*
 * Copyright (c) 2011-2013 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.spi.server.InternalSynchronizableRepository;
import org.eclipse.emf.cdo.tests.AbstractSyncingTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * @author Eike Stepper
 */
public class Bugzilla_325097_Test extends AbstractSyncingTest
{
  @Override
  protected boolean isFailover()
  {
    return true;
  }

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

  public void testNewObjectAfterSwitch() throws Exception
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

    sleep(1000);

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

    // Create a new company
    company = getModel1Factory().createCompany();
    company.setName("Test2");

    resource.getContents().add(company);
    transaction.commit();

    session.close();
    LifecycleUtil.deactivate(backup);
    LifecycleUtil.deactivate(master);
  }
}
