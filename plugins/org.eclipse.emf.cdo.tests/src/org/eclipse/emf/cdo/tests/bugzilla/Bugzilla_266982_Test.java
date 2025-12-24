/*
 * Copyright (c) 2009-2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

/**
 * IllegalStateException in CDOStore.getRevision
 * <p>
 * See bug 266982
 *
 * @author Simon McDuff
 */
public class Bugzilla_266982_Test extends AbstractCDOTest
{
  public void testBugzilla_266982() throws Exception
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
      @Override
      public void run()
      {
        try
        {
          CDOSession session = openSession();
          CDOTransaction transaction = session.openTransaction();
          Customer customerToLoad = (Customer)CDOUtil.getEObject(transaction.getObject(CDOUtil.getCDOObject(customer).cdoID()));
          while (!done[0])
          {
            // Could fail if the attach is not thread safe
            customerToLoad.getName();
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

    for (int i = 0; i < 500 && exception[0] == null; i++)
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
