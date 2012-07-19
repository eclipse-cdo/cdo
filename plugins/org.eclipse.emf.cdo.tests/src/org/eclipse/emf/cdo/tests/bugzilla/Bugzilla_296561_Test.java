/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOSavepoint;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;

import org.eclipse.net4j.util.ReflectUtil;

import org.eclipse.emf.spi.cdo.InternalCDOObject;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentMap;

/**
 * Problems with savepoints and detached elements on complete rollback
 * <p>
 * See https://bugs.eclipse.org/296561
 * 
 * @author Victor Roldan Betancort
 */
public class Bugzilla_296561_Test extends AbstractCDOTest
{
  public void testBugzilla_296561__checkTransactionObjects() throws Exception
  {
    {
      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      resource.getContents().add(customer);

      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();

      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.getResource(getResourcePath("/test1"));
      Customer customer = (Customer)res.getContents().get(0);
      customer.setName("modified");

      Customer customer2 = getModel1Factory().createCustomer();
      customer2.setName("customer2");

      res.getContents().add(customer2);
      CDOID id = CDOUtil.getCDOObject(customer2).cdoID();

      CDOSavepoint savePoint = transaction.setSavepoint();
      msg(savePoint);

      res.getContents().remove(customer2);

      try
      {
        transaction.getObject(id);
        fail("ObjectNotFoundException expected");
      }
      catch (ObjectNotFoundException expected)
      {
      }

      Field field = ReflectUtil.getField(transaction.getClass(), "objects");
      Object value = ReflectUtil.getValue(field, transaction);

      @SuppressWarnings("unchecked")
      ConcurrentMap<CDOID, InternalCDOObject> objects = (ConcurrentMap<CDOID, InternalCDOObject>)value;
      assertEquals(false, objects.containsKey(id));
    }
  }

  public void testBugzilla_296561() throws Exception
  {
    {
      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");

      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      resource.getContents().add(customer);
      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.getResource(getResourcePath("/test1"));

      Customer customer = (Customer)res.getContents().get(0);
      customer.setName("modified");

      Customer customer2 = getModel1Factory().createCustomer();
      customer2.setName("customer2");

      res.getContents().add(customer2);
      CDOSavepoint savePoint = transaction.setSavepoint();

      res.getContents().remove(customer2);
      savePoint.rollback();
      assertEquals(true, res.getContents().contains(customer2));
    }

    {
      CDOSession session = openSession();

      CDOTransaction transaction = session.openTransaction();
      CDOResource res = transaction.getResource(getResourcePath("/test1"));
      Customer customer = (Customer)res.getContents().get(0);
      customer.setName("modified");

      Customer customer2 = getModel1Factory().createCustomer();
      customer2.setName("customer2");

      res.getContents().add(customer2);
      transaction.setSavepoint();
      res.getContents().remove(customer2);
      transaction.rollback();
      assertEquals(false, res.getContents().contains(customer2));
    }
  }
}
