/*
 * Copyright (c) 2010-2012 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.ecore.EObject;

import java.util.Iterator;

/**
 * @author Eike Stepper
 */
public class Bugzilla_316145_Test extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "resource";

  private CDOID id;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    // create model history
    CDONet4jSession session = (CDONet4jSession)openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath(RESOURCE_NAME));

    // creating initial commit
    Company company = getModel1Factory().createCompany();
    company.setName("Company");
    company.setCity("City");
    company.setStreet("Street");
    resource.getContents().add(company);

    transaction.commit();

    id = CDOUtil.getCDOObject(company).cdoID();

    session.close();

    IRepository repository = getScenario().getRepositoryConfig().getRepository(IRepositoryConfig.REPOSITORY_NAME);
    clearCache(repository.getRevisionManager());
  }

  public void testRevisionInListNull()
  {
    CDONet4jSession session = (CDONet4jSession)openSession();
    CDOTransaction transaction = session.openTransaction();
    CDORevision revision = session.getRevisionManager().getRevision(id, transaction, 0, 0, true);
    msg(revision);

    CDOObject object = transaction.getObject(id);
    for (Iterator<EObject> it = object.eAllContents(); it.hasNext();)
    {
      it.next();
    }

    session.close();
  }
}
