/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

import org.eclipse.emf.internal.cdo.util.FSMUtil;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class Bugzilla_273233_Test extends AbstractCDOTest
{
  public void test_Bugzilla_273233_Simple() throws IOException
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);

    CDOTransaction trans = session.openTransaction();
    CDOResource res = trans.createResource("/test/1");

    trans.commit();
    Company company = getModel1Factory().createCompany();
    res.getContents().add(company);
    session.refresh();
    trans.commit();
    trans.close();
    session.close();
  }

  public void test_Bugzilla_273233_TwoSessions() throws IOException
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);

    CDOSession session2 = openSession();

    CDOTransaction trans = session.openTransaction();
    CDOTransaction trans2 = session2.openTransaction();
    CDOResource res = trans.createResource("/test/1");

    trans.commit();
    session2.options().setPassiveUpdateEnabled(false);

    CDOResource res2 = trans2.getResource("/test/1");
    Company company = getModel1Factory().createCompany();
    res.getContents().add(company);
    Company company2 = getModel1Factory().createCompany();
    res2.getContents().add(company2);

    trans.commit();

    assertFalse(FSMUtil.isConflict(res2));
    session2.refresh();
    assertTrue(FSMUtil.isConflict(res2));
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

  public void test_Bugzilla_273233_TwoSessionsWithClearCache() throws IOException
  {
    CDOSession session = openSession();
    session.options().setPassiveUpdateEnabled(false);

    CDOSession session2 = openSession();

    CDOTransaction trans = session.openTransaction();
    CDOTransaction trans2 = session2.openTransaction();
    CDOResource res = trans.createResource("/test/1");

    trans.commit();
    session2.options().setPassiveUpdateEnabled(false);

    CDOResource res2 = trans2.getResource("/test/1");
    Company company = getModel1Factory().createCompany();
    res.getContents().add(company);
    Company company2 = getModel1Factory().createCompany();
    res2.getContents().add(company2);

    trans.commit();
    clearCache(session2.getRevisionManager());
    assertFalse(FSMUtil.isConflict(res2));
    session2.refresh();
    assertTrue(FSMUtil.isConflict(res2));
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
