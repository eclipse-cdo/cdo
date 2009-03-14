/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ISessionManager;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 */
public class RepositoryTest extends AbstractCDOTest
{
  public void testSessionClosed() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");
    resource.getContents().add(createCustomer("Eike"));
    transaction.commit(); // Ensure that model1 is committed to the repository

    ISessionManager sessionManager = getRepository().getSessionManager();
    assertEquals(1, sessionManager.getSessions().length);

    session.close();
    sleep(100);
    assertEquals(0, sessionManager.getSessions().length);
  }

  public void testWriteAccessHandlers() throws Exception
  {
    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/res1");
    resource.getContents().add(createCustomer("Eike"));
    transaction.commit(); // Ensure that model1 is committed to the repository

    getRepository().addHandler(new IRepository.WriteAccessHandler()
    {
      EPackage model1Package = getRepository().getPackageRegistry().getEPackage(getModel1Package().getNsURI());

      EClass customerClass = (EClass)model1Package.getEClassifier("Customer");

      EStructuralFeature nameFeature = customerClass.getEStructuralFeature("name");

      public void handleTransactionBeforeCommitting(ITransaction transaction,
          IStoreAccessor.CommitContext commitContext, OMMonitor monitor) throws RuntimeException
      {
        CDORevision[] newObjects = commitContext.getNewObjects();
        for (CDORevision revision : newObjects)
        {
          if (revision.getEClass() == customerClass)
          {
            String name = (String)revision.data().get(nameFeature, 0);
            if ("Admin".equals(name))
            {
              throw new IllegalStateException("Adding a customer with name 'Admin' is not allowed");
            }
          }
        }
      }
    });

    resource.getContents().add(createCustomer("Simon"));
    transaction.commit();
    resource.getContents().add(createCustomer("Admin"));

    try
    {
      transaction.commit();
      fail("TransactionException expected");
    }
    catch (TransactionException expected)
    {
      // Success
      transaction.rollback();
    }

    resource.getContents().add(createCustomer("Martin"));
    transaction.commit();
    resource.getContents().add(createCustomer("Nick"));
    transaction.commit();
    session.close();
  }

  public void testReadAccessHandlers() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/res1");
      resource.getContents().add(createCustomer("Eike"));
      resource.getContents().add(createCustomer("Simon"));
      resource.getContents().add(createCustomer("Admin"));
      resource.getContents().add(createCustomer("Martin"));
      resource.getContents().add(createCustomer("Nick"));
      transaction.commit();
      session.close();
    }

    getRepository().addHandler(new CDOServerUtil.RepositoryReadAccessValidator()
    {
      EPackage model1Package = getRepository().getPackageRegistry().getEPackage(getModel1Package().getNsURI());

      EClass customerClass = (EClass)model1Package.getEClassifier("Customer");

      EStructuralFeature nameFeature = customerClass.getEStructuralFeature("name");

      @Override
      protected String validate(ISession session, CDORevision revision)
      {
        if (revision.getEClass() == customerClass)
        {
          String name = (String)revision.data().get(nameFeature, 0);
          if ("Admin".equals(name))
          {
            return "Confidential!";
          }
        }

        return null;
      }
    });

    CDOSession session = openModel1Session();
    CDOView view = session.openView();
    CDOResource resource = view.getResource("/res1");
    int read = 0;

    try
    {
      for (EObject object : resource.getContents())
      {
        Customer customer = (Customer)object;
        System.out.println(customer.getName());
        ++read;
      }
    }
    catch (Exception ex)
    {
    }

    assertEquals(2, read);
    session.close();
  }

  private Customer createCustomer(String name)
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setName(name);
    return customer;
  };
}
