/*
 * Copyright (c) 2008-2012, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Simon McDuff - maintenance
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * CDOView.hasResource() is not aware of deleted resources
 * <p>
 * See bug 248117
 *
 * @author Victor Roldan Betancort
 */
public class Bugzilla_248124_Test extends AbstractCDOTest
{
  public void testBugzilla_248124_hasResourceWithCommit() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction1 = session.openTransaction();

    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(getResourcePath(resourcePath));
    res.getContents().add(getModel1Factory().createCompany());
    res.getContents().add(getModel1Factory().createCompany());
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();
    res.delete(null);
    transaction1.commit();
    CDOView view = session.openView();
    assertEquals(false, view.hasResource(getResourcePath(resourcePath)));
  }

  public void testBugzilla_248124_getResourceWithCommit() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction1 = session.openTransaction();

    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(getResourcePath(resourcePath));
    res.getContents().add(getModel1Factory().createCompany());
    res.getContents().add(getModel1Factory().createCompany());
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();
    res.delete(null);
    transaction1.commit();
    CDOView view = session.openView();

    try
    {
      view.getResourceSet().getResource(CDOURIUtil.createResourceURI(view, getResourcePath(resourcePath)), true);
      fail("Exception expected");
    }
    catch (Exception expected)
    {
    }

    CDOView transaction2 = session.openTransaction();

    try
    {
      transaction2.getResourceSet().getResource(CDOURIUtil.createResourceURI(view, getResourcePath(resourcePath)), true);
      fail("RuntimeException expected");
    }
    catch (RuntimeException expected)
    {
    }
  }

  public void testBugzilla_248124_hasResourceWithoutCommit() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction1 = session.openTransaction();

    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(getResourcePath(resourcePath));
    res.getContents().add(getModel1Factory().createCompany());
    res.getContents().add(getModel1Factory().createCompany());
    res.getContents().add(getModel1Factory().createCompany());

    res.delete(null);
    assertEquals(false, transaction1.hasResource(getResourcePath(resourcePath)));
  }

  public void testBugzilla_248124_getResourceWithoutCommit() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction1 = session.openTransaction();

    String resourcePath = "/test1";
    CDOResource res = transaction1.createResource(getResourcePath(resourcePath));
    res.getContents().add(getModel1Factory().createCompany());
    res.getContents().add(getModel1Factory().createCompany());
    res.getContents().add(getModel1Factory().createCompany());
    transaction1.commit();
    res.delete(null);

    try
    {
      transaction1.getResourceSet().getResource(CDOURIUtil.createResourceURI(transaction1, getResourcePath(resourcePath)), true);
      fail("RuntimeException expected");
    }
    catch (RuntimeException expected)
    {
    }
  }
}
