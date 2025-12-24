/*
 * Copyright (c) 2008-2012, 2016, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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

    final CDOTransaction transaction1 = session.openTransaction();
    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(getResourcePath(resourcePath));
    Company company = getModel1Factory().createCompany();

    res.getContents().add(company);
    transaction1.commit();

    final CDOID id = CDOUtil.getCDOObject(company).cdoID();

    for (int i = 0; i < 20; i++)
    {
      final TestAdapter testAdapter = new TestAdapter();
      CDOSession session2 = openSession();
      final CDOTransaction transaction2 = session2.openTransaction();
      company.setName(String.valueOf(i));

      Company company2 = transaction2.sync().call(() -> {
        transaction1.commit();

        transaction2.options().setInvalidationNotificationEnabled(true);
        Company company21 = (Company)CDOUtil.getEObject(transaction2.getObject(id, true));
        company21.eAdapters().add(testAdapter);

        return company21;
      });

      assertEquals(String.valueOf(i), company2.getName());
      // Need a way to test if an error occured in the invalidation process.
    }
  }
}
