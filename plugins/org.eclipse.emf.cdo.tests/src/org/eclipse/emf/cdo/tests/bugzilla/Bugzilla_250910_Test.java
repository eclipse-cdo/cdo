/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.util.TestAdapter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

/**
 * 250910: IllegalArgumentException: created > revised
 * <p>
 * See bug 250910
 * 
 * @author Simon McDuff
 */
public class Bugzilla_250910_Test extends AbstractCDOTest
{
  public void testBugzilla_250910() throws Exception
  {
    CDOSession session = openSession();

    CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(getResourcePath(resourcePath));
    Company company = getModel1Factory().createCompany();

    res.getContents().add(company);
    transaction1.commit();

    CDOID id = CDOUtil.getCDOObject(company).cdoID();

    for (int i = 0; i < 20; i++)
    {
      TestAdapter testAdapter = new TestAdapter();
      CDOSession session2 = openSession();
      CDOTransaction transaction2 = session2.openTransaction();
      company.setName(String.valueOf(i));

      Company company2;
      synchronized (transaction2)
      {
        transaction1.commit();

        transaction2.options().setInvalidationNotificationEnabled(true);
        company2 = (Company)CDOUtil.getEObject(transaction2.getObject(id, true));
        company2.eAdapters().add(testAdapter);
      }

      assertEquals(String.valueOf(i), company2.getName());
      // Need a way to test if an error occured in the invalidation process.
    }
  }
}
