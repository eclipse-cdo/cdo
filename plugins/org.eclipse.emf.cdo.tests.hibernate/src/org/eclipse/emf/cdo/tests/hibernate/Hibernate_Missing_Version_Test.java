/*
 * Copyright (c) 2012, 2013, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectHistory;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

/**
 * @author Martin Taal
 */
@Requires(IRepositoryConfig.CAPABILITY_AUDITING)
public class Hibernate_Missing_Version_Test extends AbstractCDOTest
{
  @Override
  protected void doSetUp() throws Exception
  {
    disableConsole();
    super.doSetUp();
    skipStoreWithoutRawAccess();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    disableConsole();
    super.doTearDown();
  }

  public void testVersionChange() throws Exception
  {
    CDOSession session = openSession();
    Customer lastCustomer = null;
    {
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/res1"));
      Customer customer = getModel1Factory().createCustomer();
      customer.setName("Martin");
      resource.getContents().add(customer);
      transaction.commit();
      lastCustomer = customer;
    }
    {
      CDOTransaction transaction = session.openTransaction();
      Customer customer = transaction.getObject(lastCustomer);// (Customer)resource.getContents().get(0);
      customer.setName("Eike");
      assertEquals(1, CDOUtil.getCDOObject(customer).cdoRevision().getVersion());
      transaction.commit();
      assertEquals(2, CDOUtil.getCDOObject(customer).cdoRevision().getVersion());
      lastCustomer = customer;
    }
    {
      CDOTransaction transaction = session.openTransaction();
      Customer customer = lastCustomer; // transaction.getObject(lastCustomer);//
      // (Customer)resource.getContents().get(0);
      customer.setName("Peter");
      assertEquals(2, CDOUtil.getCDOObject(customer).cdoRevision().getVersion());
      transaction.commit();
      // assertEquals(3, CDOUtil.getCDOObject(customer).cdoRevision().getVersion());
      lastCustomer = customer;
    }
    getRepository().getRevisionManager().getCache().clear();

    // now go to another repo
    InternalRepository repo2 = getRepository("repo2", true);
    repo2.getRevisionManager().getCache().clear();
    {
      CDOSession session2 = openSession("repo2");
      CDOTransaction transaction = session2.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/res1"));
      Customer customer = (Customer)resource.getContents().get(0);
      customer.setName("John");
      assertEquals(3, CDOUtil.getCDOObject(customer).cdoRevision().getVersion());
      transaction.commit();
      assertEquals(4, CDOUtil.getCDOObject(customer).cdoRevision().getVersion());
      session2.close();
    }
    {
      CDOSession session2 = openSession("repo2");
      CDOTransaction transaction = session2.openTransaction();
      CDOResource resource = transaction.getResource(getResourcePath("/res1"));
      Customer customer = (Customer)resource.getContents().get(0);
      customer.setName("Mike");
      assertEquals(4, CDOUtil.getCDOObject(customer).cdoRevision().getVersion());
      transaction.commit();
      assertEquals(5, CDOUtil.getCDOObject(customer).cdoRevision().getVersion());
      session2.close();
    }
    {
      CDOSession session2 = openSession("repo2");
      CDOView view = session2.openView();
      CDOResource resource = view.getResource(getResourcePath("/res1"));
      Customer customer = (Customer)resource.getContents().get(0);

      assertNotNull(CDOUtil.getRevisionByVersion(CDOUtil.getCDOObject(customer), 4));
      assertNotNull(CDOUtil.getRevisionByVersion(CDOUtil.getCDOObject(customer), 3));
      assertNotNull(CDOUtil.getRevisionByVersion(CDOUtil.getCDOObject(customer), 2));
      assertNotNull(CDOUtil.getRevisionByVersion(CDOUtil.getCDOObject(customer), 1));

      CDOObjectHistory history = getCDOObjectHistory(view, customer);
      assertEquals(5, history.getElements().length);
      // for (CDOCommitInfo cdoCommitInfo : history.getElements())
      // {
      //
      // }
      view.close();
      session2.close();
    }
  }

  private synchronized CDOObjectHistory getCDOObjectHistory(CDOView audit, Object object)
  {
    CDOObjectHistory cdoObjectHistory = audit.getHistory((CDOObject)object);
    cdoObjectHistory.triggerLoad();
    long startTime = System.currentTimeMillis();
    while (cdoObjectHistory.isLoading())
    {
      ConcurrencyUtil.sleep(10);

      // waited too long
      if (System.currentTimeMillis() - startTime > 5000)
      {
        throw new IllegalStateException("commit info could not be loaded");
      }
    }
    return cdoObjectHistory;
  }
}
