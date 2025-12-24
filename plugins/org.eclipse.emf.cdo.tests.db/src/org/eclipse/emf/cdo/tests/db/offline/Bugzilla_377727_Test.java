/*
 * Copyright (c) 2012, 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db.offline;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractSyncingTest;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.OfflineConfig;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model3.Class1;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.io.IOUtil;

/**
 * @author Stefan Winkler
 */
public class Bugzilla_377727_Test extends AbstractSyncingTest
{
  @Requires(RepositoryConfig.CAPABILITY_OFFLINE)
  public void testAsyncPackages() throws Exception
  {
    CDOID id1;
    CDOID id2;

    IOUtil.OUT().println("=== Disconnect clone ===");

    // disconnect clone from master
    ((OfflineConfig)getRepositoryConfig()).stopMasterTransport();
    while (getRepository().getState() == CDOCommonRepository.State.ONLINE)
    {
      ConcurrencyUtil.sleep(250L);
    }

    IOUtil.OUT().println("=== Clone is disconnected ===");

    {
      // on disconnected master
      CDOSession session = openSession("master");
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("test"));

      Class1 c1 = getModel3Factory().createClass1();
      resource.getContents().add(c1);
      id1 = CDOUtil.getCDOObject(c1).cdoID();

      Category cat1 = getModel1Factory().createCategory();
      cat1.setName("Test");
      resource.getContents().add(cat1);
      id2 = CDOUtil.getCDOObject(cat1).cdoID();

      transaction.commit();
      transaction.close();
      session.close();
    }

    // reconnect clone and let sync
    IOUtil.OUT().println("=== Reconnect clone ===");

    ((OfflineConfig)getRepositoryConfig()).startMasterTransport();
    while (getRepository().getState() != CDOCommonRepository.State.ONLINE)
    {
      ConcurrencyUtil.sleep(250L);
    }

    IOUtil.OUT().println("=== Clone is reconnected ===");

    CDOSession session = openSession();
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath("test"));

    Class1 c1 = (Class1)resource.getContents().get(0);
    assertEquals(id1, CDOUtil.getCDOObject(c1).cdoID());

    Category cat = (Category)resource.getContents().get(1);
    assertEquals(id2, CDOUtil.getCDOObject(cat).cdoID());
  }
}
