/*
 * Copyright (c) 2009-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

/**
 * Bug 267352: NullPointerException on reload
 *
 * @author Simon McDuff
 */
public class Bugzilla_267352_Test extends AbstractCDOTest
{
  public void testReload() throws Exception
  {
    final Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");

    final boolean[] done = { false };
    final Exception[] exception = { null };

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(customer);
    transaction.commit();

    Thread thread = new Thread("ChangeObjects")
    {
      @Override
      public void run()
      {
        CDOSession session = openSession();

        try
        {
          CDOTransaction transaction = session.openTransaction();
          CDOObject customerToLoad = CDOUtil.getCDOObject(transaction.getObject(customer));

          while (!done[0])
          {
            sleep(10);

            // Could fail if the attach is not thread safe
            transaction.reload(customerToLoad);
          }
        }
        catch (Exception ex)
        {
          exception[0] = ex;
        }
        finally
        {
          session.close();
        }
      }
    };

    thread.start();

    for (int i = 0; i < 100 && exception[0] == null; i++)
    {
      synchronized (transaction)
      {
        customer.setName("Ottawa" + i);
        transaction.commit();
      }
    }

    done[0] = true;
    thread.join(DEFAULT_TIMEOUT);

    if (exception[0] != null)
    {
      exception[0].printStackTrace();
      fail(exception[0].getMessage());
    }
  }

  public void test1() throws Exception
  {
    testReload();
  }

  public void test2() throws Exception
  {
    testReload();
  }

  public void test3() throws Exception
  {
    testReload();
  }

  public void test4() throws Exception
  {
    testReload();
  }

  public void test5() throws Exception
  {
    testReload();
  }

  public void test6() throws Exception
  {
    testReload();
  }

  public void test7() throws Exception
  {
    testReload();
  }

  public void test8() throws Exception
  {
    testReload();
  }

  public void test9() throws Exception
  {
    testReload();
  }
}
