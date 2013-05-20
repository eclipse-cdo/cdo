/*
 * Copyright (c) 2009-2012 Eike Stepper (Berlin, Germany) and others.
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
 * NullPointerException on reload
 * <p>
 * See bug 267352
 * 
 * @author Simon McDuff
 */
public class Bugzilla_267352_Test extends AbstractCDOTest
{
  public void testBugzilla_267352() throws Exception
  {
    final Customer customer = getModel1Factory().createCustomer();
    final boolean done[] = new boolean[1];
    final Exception exception[] = new Exception[1];
    done[0] = false;
    customer.setName("customer");

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));
    resource.getContents().add(customer);
    transaction.commit();

    Runnable changeObjects = new Runnable()
    {
      public void run()
      {
        try
        {
          CDOSession session = openSession();
          CDOTransaction transaction = session.openTransaction();
          CDOObject customerToLoad = transaction.getObject(CDOUtil.getCDOObject(customer).cdoID());
          while (!done[0])
          {
            sleep(10);

            // Could fail if the attach is not thread safe
            transaction.reload(customerToLoad);
          }

          transaction.close();
          session.close();
        }
        catch (Exception ex)
        {
          exception[0] = ex;
        }
      }
    };

    new Thread(changeObjects).start();
    for (int i = 0; i < 100 && exception[0] == null; i++)
    {
      customer.setName("Ottawa" + i);
      transaction.commit();
    }

    done[0] = true;
    if (exception[0] != null)
    {
      exception[0].printStackTrace();
      fail(exception[0].getMessage());
    }

    session.close();
  }
}
