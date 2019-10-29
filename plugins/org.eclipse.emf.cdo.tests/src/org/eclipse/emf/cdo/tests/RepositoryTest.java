/*
 * Copyright (c) 2008-2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.spi.server.ObjectWriteAccessHandler;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.EPackageImpl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class RepositoryTest extends AbstractCDOTest
{
  public void testInsertProperties() throws Exception
  {
    Map<String, String> expected = new HashMap<String, String>();
    expected.put("BOOLEAN", "true");
    expected.put("INTEGER", "1234567");
    expected.put("LONG", "12345671234567");
    expected.put("DOUBLE", "1234567.1234567");
    expected.put("STRING", "Arbitrary text");

    InternalStore store = getRepository().getStore();
    store.setPersistentProperties(expected);

    Map<String, String> actual = store.getPersistentProperties(expected.keySet());
    assertEquals(expected, actual);
  }

  public void testUpdateProperties() throws Exception
  {
    Map<String, String> expected = new HashMap<String, String>();
    expected.put("BOOLEAN", "true");
    expected.put("INTEGER", "1234567");
    expected.put("LONG", "12345671234567");
    expected.put("DOUBLE", "1234567.1234567");
    expected.put("STRING", "Arbitrary text");

    InternalStore store = getRepository().getStore();
    store.setPersistentProperties(expected);

    expected.put("BOOLEAN", "false");
    expected.put("INTEGER", "555555");
    expected.put("LONG", "5555555555555555");
    expected.put("DOUBLE", "555555.555555");
    expected.put("STRING", "Different text");
    store.setPersistentProperties(expected);

    Map<String, String> actual = store.getPersistentProperties(expected.keySet());
    assertEquals(expected, actual);
  }

  public void testRemoveProperties() throws Exception
  {
    Map<String, String> expected = new HashMap<String, String>();
    expected.put("BOOLEAN", "true");
    expected.put("INTEGER", "1234567");
    expected.put("LONG", "12345671234567");
    expected.put("DOUBLE", "1234567.1234567");
    expected.put("STRING", "Arbitrary text");

    InternalStore store = getRepository().getStore();
    store.setPersistentProperties(expected);

    Map<String, String> actual = store.getPersistentProperties(expected.keySet());
    assertEquals(expected, actual);

    Set<String> names = new HashSet<String>(Arrays.asList(new String[] { "INTEGER", "DOUBLE" }));
    store.removePersistentProperties(names);

    expected.remove("INTEGER");
    expected.remove("DOUBLE");
    actual = store.getPersistentProperties(expected.keySet());
    assertEquals(expected, actual);
  }

  public void testSessionClosed() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    resource.getContents().add(createCustomer("Eike"));
    transaction.commit(); // Ensure that model1 is committed to the repository

    ISessionManager sessionManager = getRepository().getSessionManager();
    assertEquals(1, sessionManager.getSessions().length);

    session.close();
    sleep(100);
    assertEquals(0, sessionManager.getSessions().length);
  }

  /**
   * See bug 329254
   */
  @Requires(IRepositoryConfig.CAPABILITY_RESTARTABLE)
  public void testLastCommitTime() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    resource.getContents().add(createCustomer("Eike"));
    long timeStamp = transaction.commit().getTimeStamp();
    session.close();

    InternalRepository repository = restartRepository();
    assertEquals(timeStamp, repository.getLastCommitTimeStamp());
  }

  public void testWriteAccessHandlers() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    resource.getContents().add(createCustomer("Eike"));
    transaction.commit(); // Ensure that model1 is committed to the repository

    getRepository().addHandler(new IRepository.WriteAccessHandler()
    {
      public void handleTransactionBeforeCommitting(ITransaction transaction, CommitContext commitContext, OMMonitor monitor) throws RuntimeException
      {
        // Use the package registry of the commit context to catch new packages!
        EPackage model1Package = commitContext.getPackageRegistry().getEPackage(getModel1Package().getNsURI());
        EClass customerClass = (EClass)model1Package.getEClassifier("Customer");
        EStructuralFeature nameFeature = customerClass.getEStructuralFeature("name");

        for (CDORevision revision : commitContext.getNewObjects())
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

      public void handleTransactionAfterCommitted(ITransaction transaction, CommitContext commitContext, OMMonitor monitor)
      {
      }
    });

    resource.getContents().add(createCustomer("Simon"));
    transaction.commit();
    resource.getContents().add(createCustomer("Admin"));

    try
    {
      transaction.commit();
      fail("CommitException expected");
    }
    catch (CommitException expected)
    {
      // Success
      transaction.rollback();
    }

    resource.getContents().add(createCustomer("Martin"));
    transaction.commit();

    resource.getContents().add(createCustomer("Nick"));
    transaction.commit();
  }

  public void testWriteAccessHandlers_WithServerCDOView() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    resource.getContents().add(createCustomer("Eike"));
    transaction.commit(); // Ensure that model1 is committed to the repository

    getRepository().addHandler(new IRepository.WriteAccessHandler()
    {
      public void handleTransactionBeforeCommitting(ITransaction transaction, CommitContext commitContext, OMMonitor monitor) throws RuntimeException
      {
        CDOView view = CDOServerUtil.openView(commitContext);
        for (CDORevision revision : commitContext.getNewObjects())
        {
          EObject object = view.getObject(revision.getID());
          object = CDOUtil.getEObject(object); // Make legacy mode happy
          if (object instanceof Customer)
          {
            Customer customer = (Customer)object;
            String name = customer.getName();
            if ("Admin".equals(name))
            {
              throw new IllegalStateException("Adding a customer with name 'Admin' is not allowed");
            }
          }
        }

        view.close();
      }

      public void handleTransactionAfterCommitted(ITransaction transaction, CommitContext commitContext, OMMonitor monitor)
      {
      }
    });

    resource.getContents().add(createCustomer("Simon"));
    transaction.commit();
    resource.getContents().add(createCustomer("Admin"));

    try
    {
      transaction.commit();
      fail("CommitException expected");
    }
    catch (CommitException expected)
    {
      // Success
      transaction.rollback();
    }

    resource.getContents().add(createCustomer("Martin"));
    transaction.commit();

    resource.getContents().add(createCustomer("Nick"));
    transaction.commit();
  }

  public void testObjectWriteAccessHandler() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/res1"));
    resource.getContents().add(createCustomer("Eike"));
    transaction.commit(); // Ensure that model1 is committed to the repository

    getRepository().addHandler(new ObjectWriteAccessHandler()
    {
      @Override
      protected void handleTransactionBeforeCommitting(OMMonitor monitor) throws RuntimeException
      {
        for (EObject object : getNewObjects())
        {
          if (object instanceof Customer)
          {
            Customer customer = (Customer)object;
            String name = customer.getName();
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
      fail("CommitException expected");
    }
    catch (CommitException expected)
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
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/res1"));
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
        EClass eClass = revision.getEClass();
        EPackage ePackage = eClass.getEPackage();
        assertNotSame("Revision has dynamic package: " + ePackage.getName(), EPackageImpl.class.getName(), ePackage.getClass().getName());

        if (eClass == customerClass)
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

    CDOSession session = openSession();
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath("/res1"));
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
  }
}
