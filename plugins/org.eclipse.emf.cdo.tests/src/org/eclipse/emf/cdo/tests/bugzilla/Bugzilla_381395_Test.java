/*
 * Copyright (c) 2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 *    Christian W. Damus (CEA) - add test case for view closure
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.impl.AdapterImpl;

/**
 * Bug 412686: NPE when removing/adding an adapter after CDO view closed.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_381395_Test extends AbstractCDOTest
{
  public void testAdapterRemoveAfterCDOServerShutdown() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    Adapter adapter = new AdapterImpl();
    company.eAdapters().add(adapter);

    getScenario().tearDown();

    // Used to throw an NPE without the fix.
    company.eAdapters().remove(adapter);
  }

  public void testAdapterRemoveAfterCDOViewClosed() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    Adapter adapter = new AdapterImpl();
    company.eAdapters().add(adapter);

    transaction.close();

    // Used to throw an NPE without the fix.
    company.eAdapters().remove(adapter);
  }

  public void testAdapterAddAfterCDOServerShutdown() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    getScenario().tearDown();

    // Used to throw an NPE without the fix.
    company.eAdapters().add(new AdapterImpl());
  }

  public void testAdapterAddAfterCDOViewClosed() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/my/resource"));

    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit();

    transaction.close();

    // Used to throw an NPE without the fix.
    company.eAdapters().add(new AdapterImpl());
  }
}
