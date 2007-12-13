/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.MEMStore;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.RepositoryFactory;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.protocol.revision.CDORevision;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.net4j.util.io.IOUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Simon McDuff
 */
public class ChunkingWithMEMTest extends AbstractCDOTest
{
  @Override
  protected IStore createStore()
  {
    return new MEMStore();
  }

  @Override
  protected Repository createRepository()
  {
    Map<String, String> props = new HashMap<String, String>();
    IStore store = createStore();
    Repository repository = new Repository()
    {
      @Override
      protected RevisionManager createRevisionManager()
      {
        return new TestRevisionManager(this);
      }
    };

    repository.setName(REPOSITORY_NAME);
    repository.setProperties(props);
    repository.setStore(store);
    store.setRepository(repository);
    return repository;
  }

  public void testReadNative() throws Exception
  {
    CDORevision revisionToRemove = null;

    {
      msg("Opening session");
      CDOSession session = openModel1Session();

      msg("Attaching transaction");
      CDOTransaction transaction = session.openTransaction(new ResourceSetImpl());

      msg("Creating resource");
      CDOResource resource = transaction.createResource("/test1");

      msg("Creating customer");
      Customer customer = Model1Factory.eINSTANCE.createCustomer();
      customer.setName("customer");
      resource.getContents().add(customer);
      revisionToRemove = customer.cdoRevision();
      for (int i = 0; i < 100; i++)
      {
        msg("Creating salesOrder" + i);
        SalesOrder salesOrder = Model1Factory.eINSTANCE.createSalesOrder();
        salesOrder.setId(i);
        salesOrder.setCustomer(customer);
        resource.getContents().add(salesOrder);
      }

      msg("Committing");
      transaction.commit();
    }

    // ************************************************************* //
    Repository repos = (Repository)container.getElement(RepositoryFactory.PRODUCT_GROUP, RepositoryFactory.TYPE,
        REPOSITORY_NAME);
    TestRevisionManager revManagerTest = (TestRevisionManager)repos.getRevisionManager();
    // Remove a specific version
    revManagerTest.removeRevision(revisionToRemove);

    msg("Opening session");
    CDOSession session = openModel1Session();
    session.setReferenceChunkSize(10);

    msg("Attaching transaction");
    CDOTransaction transaction = session.openTransaction(new ResourceSetImpl());
    msg("Loading resource");
    CDOResource resource = transaction.getResource("/test1");

    Customer customer = (Customer)resource.getContents().get(0);
    EList<SalesOrder> salesOrders = customer.getSalesOrders();
    int i = 0;
    for (Iterator<SalesOrder> it = salesOrders.iterator(); it.hasNext();)
    {
      IOUtil.OUT().println(i++);
      SalesOrder salesOrder = it.next();
      IOUtil.OUT().println(salesOrder);
    }
  }

  public void testWriteNative() throws Exception
  {
    CDORevision revisionToRemove = null;

    {
      msg("Opening session");
      CDOSession session = openModel1Session();

      msg("Attaching transaction");
      CDOTransaction transaction = session.openTransaction(new ResourceSetImpl());

      msg("Creating resource");
      CDOResource resource = transaction.createResource("/test1");

      msg("Creating customer");
      Customer customer = Model1Factory.eINSTANCE.createCustomer();
      customer.setName("customer");
      resource.getContents().add(customer);

      for (int i = 0; i < 100; i++)
      {
        msg("Creating salesOrder" + i);
        SalesOrder salesOrder = Model1Factory.eINSTANCE.createSalesOrder();
        salesOrder.setId(i);
        salesOrder.setCustomer(customer);
        resource.getContents().add(salesOrder);
      }

      msg("Committing");
      transaction.commit();
      revisionToRemove = customer.cdoRevision();
    }

    // ************************************************************* //
    Repository repos = (Repository)container.getElement(RepositoryFactory.PRODUCT_GROUP, RepositoryFactory.TYPE,
        REPOSITORY_NAME);
    TestRevisionManager revManagerTest = (TestRevisionManager)repos.getRevisionManager();

    revManagerTest.removeRevision(revisionToRemove);

    msg("Opening session");
    CDOSession session = openModel1Session();
    session.setReferenceChunkSize(10);

    msg("Attaching transaction");
    CDOTransaction transaction = session.openTransaction(new ResourceSetImpl());

    msg("Loading resource");
    CDOResource resource = transaction.getResource("/test1");

    Customer customer = (Customer)resource.getContents().get(0);
    EList<SalesOrder> salesOrders = customer.getSalesOrders();
    for (int i = 50; i < 70; i++)
    {
      SalesOrder salesOrder = Model1Factory.eINSTANCE.createSalesOrder();
      salesOrder.setId(i + 1000);
      resource.getContents().add(salesOrder);
      salesOrders.set(i, salesOrder);
    }

    transaction.commit();
  }

  /**
   * @author Simon McDuff
   */
  private class TestRevisionManager extends RevisionManager
  {
    public TestRevisionManager(Repository repository)
    {
      super(repository);
    }

    public void removeRevision(CDORevision revision)
    {
      super.removeRevision(revision.getID(), revision.getVersion());
    }
  }
}
