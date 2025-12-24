/*
 * Copyright (c) 2011-2013, 2018 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractSyncingTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.tests.TestListener;

/**
 * @author Eike Stepper
 */
public class OfflineDelayedTest extends AbstractSyncingTest
{
  @Override
  protected long getTestDelayedCommitHandling()
  {
    return 500L;
  }

  public void _testSyncBeforeCommittingToMaster() throws Exception
  {
    TestListener listener = new TestListener();
    InternalRepository clone = getRepository();
    waitForOnline(clone);

    {
      getOfflineConfig().stopMasterTransport();
      waitForOffline(clone);

      CDOSession masterSession = openSession("master");
      CDOTransaction masterTransaction = masterSession.openTransaction();
      CDOResource masterResource = masterTransaction.createResource(getResourcePath("/master/resource"));
      for (int i = 0; i < 10; i++)
      {
        masterResource.getContents().add(getModel1Factory().createCompany());
        masterTransaction.commit();
      }

      masterTransaction.close();
      masterSession.addListener(listener);

      getOfflineConfig().startMasterTransport();
      waitForOnline(clone);
    }

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    resource.getContents().add(company);
    transaction.commit();

    IEvent[] events = listener.getEvents();
    assertEquals(1, events.length);
  }

  public void testSyncWhileCommittingToMaster() throws Exception
  {
    disableConsole();

    InternalRepository clone = getRepository();
    waitForOnline(clone);

    {
      CDOSession masterSession = openSession("master");
      CDOTransaction masterTransaction = masterSession.openTransaction();
      CDOResource masterResource = masterTransaction.createResource(getResourcePath("/master/resource"));

      masterResource.getContents().add(getModel1Factory().createCompany());
      masterTransaction.commit();

      getOfflineConfig().stopMasterTransport();
      waitForOffline(clone);

      for (int i = 0; i < 20; i++)
      {
        masterResource.getContents().add(getModel1Factory().createCompany());
        masterTransaction.commit();
      }

      masterTransaction.close();
      enableConsole();

      getOfflineConfig().startMasterTransport();
      sleep(1000L);
    }

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    resource.getContents().add(company);
    transaction.commit();

    waitForOnline(clone);
  }

  public void _testSyncWhileCommittingToMaster_NewPackage() throws Exception
  {
    TestListener listener = new TestListener();
    InternalRepository clone = getRepository();
    waitForOnline(clone);

    {
      getOfflineConfig().stopMasterTransport();
      waitForOffline(clone);

      CDOSession masterSession = openSession("master");
      CDOTransaction masterTransaction = masterSession.openTransaction();
      CDOResource masterResource = masterTransaction.createResource(getResourcePath("/master/resource"));
      for (int i = 0; i < 20; i++)
      {
        masterResource.getContents().add(getModel1Factory().createCompany());
        masterTransaction.commit();
      }

      masterTransaction.close();
      masterSession.addListener(listener);

      getOfflineConfig().startMasterTransport();
      sleep(1000L);
    }

    Company company = getModel1Factory().createCompany();
    company.setName("Test");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    resource.getContents().add(company);
    transaction.commit();

    waitForOnline(clone);

    IEvent[] events = listener.getEvents();
    assertEquals(1, events.length);
  }
}
