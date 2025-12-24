/*
 * Copyright (c) 2009-2012 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.spi.cdo.FSMUtil;

/**
 * @author Simon McDuff
 */
public class Bugzilla_273233_Test extends AbstractCDOTest
{
  public void test_Bugzilla_273233_Simple() throws Exception
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);

    CDOTransaction trans = session.openTransaction();
    CDOResource res = trans.createResource(getResourcePath("/test/1"));

    trans.commit();
    Company company = getModel1Factory().createCompany();
    res.getContents().add(company);
    session.refresh();
    trans.commit();
    trans.close();
    session.close();
  }

  public void test_Bugzilla_273233_TwoSessions() throws Exception
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);

    CDOTransaction trans = session.openTransaction();
    CDOResource res = trans.createResource(getResourcePath("/test/1"));

    trans.commit();

    CDOSession session2 = openSession();
    session2.options().setPassiveUpdateEnabled(false);
    CDOTransaction trans2 = session2.openTransaction();
    CDOResource res2 = trans2.getResource(getResourcePath("/test/1"));

    // Add company in sess/tx #1
    Company company = getModel1Factory().createCompany();
    res.getContents().add(company);

    // Add company in sess/tx #2
    Company company2 = getModel1Factory().createCompany();
    res2.getContents().add(company2);

    // Commit tx #1
    trans.commit();

    assertEquals(false, FSMUtil.isConflict(res2));

    session2.refresh();
    assertEquals(true, FSMUtil.isConflict(res2));

    try
    {
      trans2.commit();
      fail("Should have a conflict exception");
    }
    catch (Exception ex)
    {
      //
    }

    trans.close();
    session.close();
  }

  public void test_Bugzilla_273233_TwoSessionsWithClearCache() throws Exception
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);

    CDOTransaction trans = session.openTransaction();
    CDOResource res = trans.createResource(getResourcePath("/test/1"));

    trans.commit();

    CDOSession session2 = openSession();
    session2.options().setPassiveUpdateEnabled(false);
    CDOTransaction trans2 = session2.openTransaction();
    CDOResource res2 = trans2.getResource(getResourcePath("/test/1"));

    // Add company in sess/tx #1
    Company company = getModel1Factory().createCompany();
    res.getContents().add(company);

    // Add company in sess/tx #2
    Company company2 = getModel1Factory().createCompany();
    res2.getContents().add(company2);

    // Commit tx #1
    trans.commit();

    clearCache(session2.getRevisionManager());
    assertEquals(false, FSMUtil.isConflict(res2));

    session2.refresh();
    assertEquals(true, FSMUtil.isConflict(res2));

    try
    {
      trans2.commit();
      fail("Should have a conflict exception");
    }
    catch (Exception ex)
    {
      //
    }

    trans.close();
    session.close();
  }
}
