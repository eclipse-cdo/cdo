/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 *    Christian W. Damus (CEA) - add test case for view closure
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.impl.AdapterImpl;

/**
 * @author Esteban Dugueperoux
 */
@CleanRepositoriesBefore
@Requires(IModelConfig.CAPABILITY_LEGACY)
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

    try
    {
      company.eAdapters().remove(adapter);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      fail("Exception in removal of adapter.");
    }
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

    try
    {
      company.eAdapters().remove(adapter);
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      fail("Exception in removal of adapter.");
    }
  }

}
