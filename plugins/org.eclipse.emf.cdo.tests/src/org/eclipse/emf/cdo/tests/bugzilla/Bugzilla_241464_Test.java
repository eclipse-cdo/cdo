/*
 * Copyright (c) 2008-2012, 2015, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.util.TransportException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.util.TestRevisionManager;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import java.util.concurrent.TimeoutException;

/**
 * Make timeouts in read-access requests configurable
 * <p>
 * See bug 241464
 *
 * @author Eike Stepper
 */
public class Bugzilla_241464_Test extends AbstractCDOTest
{
  public void testBugzilla_241464() throws Exception
  {
    {
      CDOSession session = openSession();
      if (!(session instanceof org.eclipse.emf.cdo.net4j.CDONet4jSession))
      {
        return;
      }

      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");
      resource.getContents().add(customer);

      transaction.commit();
      session.close();
    }

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));

    ((org.eclipse.emf.cdo.net4j.CDONet4jSession)session).options().getNet4jProtocol().setTimeout(2000L);

    TestRevisionManager revisionManager = (TestRevisionManager)getRepository().getRevisionManager();
    revisionManager.setGetRevisionsDelay(10000L); // Make the protocol time out

    try
    {
      Customer customer = (Customer)resource.getContents().get(0);
      System.out.println(customer.getName());
      fail("TransportException expected");
    }
    catch (TransportException expected)
    {
      assertInstanceOf(TimeoutException.class, expected.getCause());
      // SUCCESS
    }
    finally
    {
      revisionManager.setGetRevisionsDelay(0L); // Terminate repo
    }
  }
}
