/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
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
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.RevisionManager;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Simon McDuff
 */
public class ChunkingWithMEMTest extends AbstractCDOTest
{
  public void testReadNative() throws Exception
  {
    CDORevision revisionToRemove = null;

    {
      msg("Opening session");
      CDOSession session = openModel1Session();

      msg("Attaching transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource("/test1");

      msg("Creating customer");
      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");
      resource.getContents().add(customer);
      revisionToRemove = CDOUtil.getCDOObject(customer).cdoRevision();
      for (int i = 0; i < 100; i++)
      {
        msg("Creating salesOrder" + i);
        SalesOrder salesOrder = getModel1Factory().createSalesOrder();
        salesOrder.setId(i);
        salesOrder.setCustomer(customer);
        resource.getContents().add(salesOrder);
      }

      msg("Committing");
      transaction.commit();
      session.close();
    }

    TestRevisionManager revisionManager = (TestRevisionManager)getRepository().getRevisionManager();
    revisionManager.removeCachedRevision(revisionToRemove);

    msg("Opening session");
    CDOSession session = openModel1Session();
    session.setReferenceChunkSize(10);

    msg("Attaching transaction");
    CDOTransaction transaction = session.openTransaction();
    msg("Loading resource");
    CDOResource resource = transaction.getResource("/test1");

    Customer customer = (Customer)resource.getContents().get(0);
    EList<SalesOrder> salesOrders = customer.getSalesOrders();
    int i = 0;
    for (Iterator<SalesOrder> it = salesOrders.iterator(); it.hasNext();)
    {
      msg(i++);
      SalesOrder salesOrder = it.next();
      msg(salesOrder);
    }

    session.close();
  }

  public void testWriteNative() throws Exception
  {
    CDORevision revisionToRemove = null;

    {
      msg("Opening session");
      CDOSession session = openModel1Session();

      msg("Attaching transaction");
      CDOTransaction transaction = session.openTransaction();

      msg("Creating resource");
      CDOResource resource = transaction.createResource("/test1");

      msg("Creating customer");
      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");
      resource.getContents().add(customer);

      for (int i = 0; i < 100; i++)
      {
        msg("Creating salesOrder" + i);
        SalesOrder salesOrder = getModel1Factory().createSalesOrder();
        salesOrder.setId(i);
        salesOrder.setCustomer(customer);
        resource.getContents().add(salesOrder);
      }

      msg("Committing");
      transaction.commit();
      revisionToRemove = CDOUtil.getCDOObject(customer).cdoRevision();
      session.close();
    }

    TestRevisionManager revisionManager = (TestRevisionManager)getRepository().getRevisionManager();
    revisionManager.removeCachedRevision(revisionToRemove);

    msg("Opening session");
    CDOSession session = openModel1Session();
    session.setReferenceChunkSize(10);

    msg("Attaching transaction");
    CDOTransaction transaction = session.openTransaction();

    msg("Loading resource");
    CDOResource resource = transaction.getResource("/test1");

    Customer customer = (Customer)resource.getContents().get(0);
    EList<SalesOrder> salesOrders = customer.getSalesOrders();
    for (int i = 50; i < 70; i++)
    {
      SalesOrder salesOrder = getModel1Factory().createSalesOrder();
      salesOrder.setId(i + 1000);
      resource.getContents().add(salesOrder);
      salesOrders.set(i, salesOrder);
    }

    transaction.commit();
    session.close();
  }

  @Override
  protected Repository createRepository()
  {
    Map<String, String> props = new HashMap<String, String>();
    // props.put(IRepository.PROP_SUPPORTING_REVISION_DELTAS, "true");

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
    return repository;
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

    public void removeCachedRevision(CDORevision revision)
    {
      super.removeCachedRevision(revision.getID(), revision.getVersion());
    }
  }
}
