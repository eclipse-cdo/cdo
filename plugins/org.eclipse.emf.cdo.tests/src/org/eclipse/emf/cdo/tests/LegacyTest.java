/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Fluegge - recreation of the test case
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.LegacyModeNotEnabledException;

/**
 * @author Eike Stepper
 */
public class LegacyTest extends AbstractCDOTest
{
  public void testLegacyModeEnabled() throws Exception
  {
    Customer customer = getModel1Factory().createCustomer();
    customer.setName("Martin Fluegge");
    customer.setStreet("ABC Street 7");
    customer.setCity("Berlin");

    CDOSession session = openSession();
    CDOUtil.setLegacyModeDefault(false);
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    try
    {
      resource.getContents().add(customer);
      transaction.commit();

      if (isConfig(LEGACY))
      {
        fail("LegacyModeNotEnabledException expected");
      }
    }
    catch (LegacyModeNotEnabledException ex)
    {
      if (!isConfig(LEGACY))
      {
        fail("Native mode should not throw an exception here (" + ex.getMessage() + ")");
      }
    }
  }
}
