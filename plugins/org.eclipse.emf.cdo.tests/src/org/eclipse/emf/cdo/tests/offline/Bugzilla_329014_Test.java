/*
 * Copyright (c) 2011-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Pascal Lehmann - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.offline;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractSyncingTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

/**
 * LastUpdateTimestamp of ReplicatorSession not set on local commits.
 * <p>
 * See bug 329014
 *
 * @author Pascal Lehmann
 * @since 4.0
 */
public class Bugzilla_329014_Test extends AbstractSyncingTest
{
  private static final String RESOURCE_NAME = "/my/resource";

  private CDOID id;

  private InternalRepository master;

  private InternalRepository clone;

  private CDOSession masterSession;

  private CDOSession cloneSession;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    clone = getRepository();
    cloneSession = openSession();
    cloneSession.options().setPassiveUpdateMode(PassiveUpdateMode.CHANGES);
    waitForOnline(clone);

    master = getRepository("master");
    masterSession = openSession(master.getName());
    masterSession.options().setPassiveUpdateMode(PassiveUpdateMode.CHANGES);

    // initial model.
    CDOTransaction transaction = masterSession.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_NAME));
    Company company = getModel1Factory().createCompany();
    company.setName("Company1");
    resource.getContents().add(company);
    transaction.commit();
    id = CDOUtil.getCDOObject(company).cdoID();

    transaction.close();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    if (cloneSession != null)
    {
      cloneSession.close();
      cloneSession = null;
    }

    if (masterSession != null)
    {
      masterSession.close();
      masterSession = null;
    }

    super.doTearDown();
  }

  @Requires(IRepositoryConfig.CAPABILITY_OFFLINE)
  public void testSynchronizationMasterCloneWithoutReplication() throws Exception
  {
    // open transactions.
    CDOTransaction masterTransaction = masterSession.openTransaction();
    CDOTransaction cloneTransaction = cloneSession.openTransaction();
    sleep(1000);

    // grab and touch the company on master.
    Company masterCompany = (Company)masterTransaction.getObject(id);
    assertNotNull(masterCompany);
    masterCompany.getName();

    // grab and touch the company on clone.
    Company cloneCompany = (Company)cloneTransaction.getObject(id);
    assertNotNull(cloneCompany);
    cloneCompany.getName();

    // do changes: master / clone / master

    masterCompany.setName("Company2");
    masterTransaction.commit();
    masterCompany.setName("Company3");
    masterTransaction.commit();

    cloneTransaction.waitForUpdate(masterTransaction.getLastCommitTime(), DEFAULT_TIMEOUT);

    cloneCompany.setName("Company4");
    cloneTransaction.commit();
    cloneCompany.setName("Company5");
    cloneTransaction.commit();

    masterTransaction.waitForUpdate(cloneTransaction.getLastCommitTime(), DEFAULT_TIMEOUT);

    masterCompany.setName("Company6");
    masterTransaction.commit();
    masterCompany.setName("Company7");
    masterTransaction.commit();

    cloneTransaction.waitForUpdate(masterTransaction.getLastCommitTime(), DEFAULT_TIMEOUT);

    // check.
    assertEquals(masterCompany.getName(), cloneCompany.getName());
  }

  @Requires(IRepositoryConfig.CAPABILITY_OFFLINE)
  public void testSynchronizationCloneMasterWithoutReplication() throws Exception
  {
    // open transactions.
    CDOTransaction masterTransaction = masterSession.openTransaction();
    CDOTransaction cloneTransaction = cloneSession.openTransaction();
    sleep(1000);

    // grab and touch the company on master.
    Company masterCompany = (Company)masterTransaction.getObject(id);
    assertNotNull(masterCompany);
    masterCompany.getName();

    // grab and touch the company on clone.
    Company cloneCompany = (Company)cloneTransaction.getObject(id);
    assertNotNull(cloneCompany);
    cloneCompany.getName();

    // do changes: clone / master / clone

    cloneCompany.setName("Company2");
    cloneTransaction.commit();
    cloneCompany.setName("Company3");
    cloneTransaction.commit();

    masterTransaction.waitForUpdate(cloneTransaction.getLastCommitTime(), DEFAULT_TIMEOUT);

    masterCompany.setName("Company4");
    masterTransaction.commit();
    masterCompany.setName("Company5");
    masterTransaction.commit();

    cloneTransaction.waitForUpdate(masterTransaction.getLastCommitTime(), DEFAULT_TIMEOUT);

    cloneCompany.setName("Company6");
    cloneTransaction.commit();
    cloneCompany.setName("Company7");
    cloneTransaction.commit();

    masterTransaction.waitForUpdate(cloneTransaction.getLastCommitTime(), DEFAULT_TIMEOUT);

    // check.
    assertEquals(cloneCompany.getName(), masterCompany.getName());
  }

  @Requires(IRepositoryConfig.CAPABILITY_OFFLINE)
  public void testSynchronizationMasterCloneWithReplication() throws Exception
  {
    // open transactions.
    CDOTransaction masterTransaction = masterSession.openTransaction();
    CDOTransaction cloneTransaction = cloneSession.openTransaction();
    sleep(1000);

    // grab and touch the company on master.
    Company masterCompany = (Company)masterTransaction.getObject(id);
    assertNotNull(masterCompany);
    masterCompany.getName();

    // grab and touch the company on clone.
    Company cloneCompany = (Company)cloneTransaction.getObject(id);
    assertNotNull(cloneCompany);
    cloneCompany.getName();

    // do changes: master / clone / master

    // go offline.
    getOfflineConfig().stopMasterTransport();
    waitForOffline(clone);

    masterCompany.setName("Company2");
    masterTransaction.commit();
    masterCompany.setName("Company3");
    masterTransaction.commit();
    msg(CDOUtil.getCDOObject(masterCompany).cdoRevision().getVersion());

    // go online.
    getOfflineConfig().startMasterTransport();
    waitForOnline(clone);

    cloneTransaction.waitForUpdate(masterTransaction.getLastCommitTime(), DEFAULT_TIMEOUT);

    cloneCompany.getName();
    msg(CDOUtil.getCDOObject(cloneCompany).cdoRevision().getVersion());

    cloneCompany.setName("Company4");
    cloneTransaction.commit();
    cloneCompany.setName("Company5");
    cloneTransaction.commit();

    masterTransaction.waitForUpdate(cloneTransaction.getLastCommitTime(), DEFAULT_TIMEOUT);

    masterCompany.setName("Company6");
    masterTransaction.commit();
    masterCompany.setName("Company7");
    masterTransaction.commit();

    cloneTransaction.waitForUpdate(masterTransaction.getLastCommitTime(), DEFAULT_TIMEOUT);

    // check.
    assertEquals(masterCompany.getName(), cloneCompany.getName());
  }

  @Requires(IRepositoryConfig.CAPABILITY_OFFLINE)
  public void testSynchronizationCloneMasterWithReplication() throws Exception
  {
    // open transactions.
    CDOTransaction masterTransaction = masterSession.openTransaction();
    CDOTransaction cloneTransaction = cloneSession.openTransaction();
    sleep(1000);

    // grab and touch the company on master.
    Company masterCompany = (Company)masterTransaction.getObject(id);
    assertNotNull(masterCompany);
    masterCompany.getName();

    // grab and touch the company on clone.
    Company cloneCompany = (Company)cloneTransaction.getObject(id);
    assertNotNull(cloneCompany);
    cloneCompany.getName();

    // do changes: clone / master / clone

    cloneCompany.setName("Company2");
    cloneTransaction.commit();
    cloneCompany.setName("Company3");
    cloneTransaction.commit();

    masterTransaction.waitForUpdate(cloneTransaction.getLastCommitTime(), DEFAULT_TIMEOUT);

    // go offline.
    getOfflineConfig().stopMasterTransport();
    waitForOffline(clone);

    masterCompany.setName("Company4");
    masterTransaction.commit();
    masterCompany.setName("Company5");
    masterTransaction.commit();

    // go online.
    getOfflineConfig().startMasterTransport();
    waitForOnline(clone);

    cloneTransaction.waitForUpdate(masterTransaction.getLastCommitTime(), DEFAULT_TIMEOUT);

    cloneCompany.setName("Company6");
    cloneTransaction.commit();
    cloneCompany.setName("Company7");
    cloneTransaction.commit();

    masterTransaction.waitForUpdate(cloneTransaction.getLastCommitTime(), DEFAULT_TIMEOUT);

    // check.
    assertEquals(cloneCompany.getName(), masterCompany.getName());
  }
}
