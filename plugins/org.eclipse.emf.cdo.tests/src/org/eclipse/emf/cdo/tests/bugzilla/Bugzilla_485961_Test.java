/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.ManagedRevisionProvider;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

/**
 * @author Eike Stepper
 */
public class Bugzilla_485961_Test extends AbstractCDOTest
{
  public void testProperContents() throws Exception
  {
    Company company = getModel1Factory().createCompany();
    Category cat1 = getModel1Factory().createCategory();
    Category cat2 = getModel1Factory().createCategory();
    Category cat3 = getModel1Factory().createCategory();

    company.getCategories().add(cat1);
    cat1.getCategories().add(cat2);
    cat1.getCategories().add(cat3);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource res1 = transaction.createResource(getResourcePath("/res1"));
    res1.getContents().add(company);

    CDOResource res2 = transaction.createResource(getResourcePath("/res2"));
    res2.getContents().add(cat3);

    transaction.commit();

    InternalRepository repository = getRepository();
    CDORevisionProvider revisionProvider = new ManagedRevisionProvider(repository.getRevisionManager(),
        repository.getBranchManager().getMainBranch().getHead());

    StoreThreadLocal.setSession(repository.getSessionManager().getSession(session.getSessionID()));

    try
    {
      assertEquals(4, traverse(revisionProvider, res1.cdoID()));
    }
    finally
    {
      StoreThreadLocal.release();
    }
  }

  private int traverse(CDORevisionProvider revisionProvider, CDOID id)
  {
    CDORevision revision = revisionProvider.getRevision(id);

    int count = 1;
    for (CDORevision child : CDORevisionUtil.getChildRevisions(revision, revisionProvider, true))
    {
      count += traverse(revisionProvider, child.getID());
    }

    return count;
  }
}
