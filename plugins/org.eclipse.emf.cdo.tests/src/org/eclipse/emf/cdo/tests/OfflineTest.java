/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.server.mem.MEMStore;
import org.eclipse.emf.cdo.server.InternalStore;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.MEMOffline;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * @author Eike Stepper
 */
public class OfflineTest extends AbstractCDOTest
{
  @Override
  protected void doSetUp() throws Exception
  {
    skipUnlessConfig(MEM_OFFLINE);
    super.doSetUp();
  }

  @Override
  public MEMOffline getRepositoryConfig()
  {
    return (MEMOffline)super.getRepositoryConfig();
  }

  public void testSynchronizer() throws Exception
  {
    CDOSession session = openSession(getRepository().getName() + "_master");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/my/resource");

    Company company = getModel1Factory().createCompany();
    company.setName("Test");
    resource.getContents().add(company);
    transaction.commit();
    dumpClone();

    for (int i = 0; i < 10; i++)
    {
      company.setName("Test" + i);
      transaction.commit();
      dumpClone();
    }

    for (int i = 0; i < 10; i++)
    {
      company.getCategories().add(getModel1Factory().createCategory());
      transaction.commit();
      dumpClone();
    }

    for (int i = 0; i < 10; i++)
    {
      company.getCategories().remove(0);
      transaction.commit();
      dumpClone();
    }

    session.close();
  }

  private void dumpClone()
  {
    InternalStore store = getRepository().getStore();
    if (store instanceof MEMStore)
    {
      sleep(10);
      System.out.println("\n\n\n\n\n\n\n\n\n\n" + //
          CDORevisionUtil.dumpAllRevisions(((MEMStore)store).getAllRevisions()));
    }
  }
}
