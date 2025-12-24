/*
 * Copyright (c) 2008-2012, 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOXATransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

/**
 * While committing, error in CommitIndicationTransaction.indicating doesn't stop the commit process
 * <p>
 * See bug 251752
 *
 * @author Simon McDuff
 */
public class Bugzilla_251752_Test extends AbstractCDOTest
{
  public void testBug_251752() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction1 = session.openTransaction();
    CDOResource res = transaction1.createResource(getResourcePath("/test1"));
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();

    CDOTransaction transaction2 = session.openTransaction();
    CDOResource res2 = transaction2.getResource(getResourcePath("/test1"));
    res.getContents().add(getModel1Factory().createCompany());
    res2.getContents().add(getModel1Factory().createCompany());
    transaction2.commit();

    try
    {
      transaction1.commit();
      fail("Exception expected");
    }
    catch (Exception expected)
    {
      // Success
    }

    assertEquals(1, res.cdoRevision().getVersion());
  }

  public void testBug_251752_XA() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction1 = session.openTransaction();
    CDOXATransaction xaTransaction = CDOUtil.createXATransaction();
    xaTransaction.add(CDOUtil.getViewSet(transaction1.getResourceSet()));

    CDOResource res = transaction1.createResource(getResourcePath("/test1"));
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();

    CDOTransaction transaction2 = session.openTransaction();
    CDOResource res2 = transaction2.getResource(getResourcePath("/test1"));
    res.getContents().add(getModel1Factory().createCompany());
    res2.getContents().add(getModel1Factory().createCompany());
    transaction2.commit();

    try
    {
      transaction1.commit();
      fail("Exception expected");
    }
    catch (Exception expected)
    {
      // Success
    }

    assertEquals(1, res.cdoRevision().getVersion());
  }
}
