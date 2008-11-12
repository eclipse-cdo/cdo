/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Customer;

import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * Make timeouts in read-access requests configurable
 * <p>
 * See https://bugs.eclipse.org/241464
 * 
 * @author Eike Stepper
 */
public class Bugzilla_241464_Test extends AbstractCDOTest
{
  public void testBugzilla_241464() throws Exception
  {
    {
      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");

      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/test1");
      resource.getContents().add(customer);

      transaction.commit();
      session.close();
    }

    CDOSession session = openModel1Session();
    session.getProtocol().setTimeout(2000L);

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource("/test1");

    LifecycleUtil.deactivate(getRepository());

    try
    {
      Customer customer = (Customer)resource.getContents().get(0);
      System.out.println(customer.getName());
      fail("SignalRemoteException expected");
    }
    catch (RemoteException success)
    {
    }
    finally
    {
      session.close();
    }
  }
}
