/*
 * Copyright (c) 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * @author Szabolcs Bardy
 */
public class Bugzilla_347964_Test extends AbstractCDOTest
{
  private final static String RESOURCE_NAME = "res347964";

  @Requires(IRepositoryConfig.CAPABILITY_RESTARTABLE)
  public void testIndexDeletion() throws Exception
  {
    CDOSession session = openSession();
    // session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(0, 300));

    CDOTransaction mainTransaction = session.openTransaction();
    CDOResource resource = mainTransaction.createResource(getResourcePath(RESOURCE_NAME));

    Company company = getModel1Factory().createCompany();
    company.setName("TestCompany");

    resource.getContents().add(company);

    mainTransaction.commit();
    mainTransaction.close();

    // restart repository
    restartRepository();

    session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(0, 300));

    CDOTransaction newTransaction = session.openTransaction();
    CDOResource resource2 = newTransaction.getResource(getResourcePath(RESOURCE_NAME));
    EList<EObject> contents = resource2.getContents();
    contents.remove(0);
    newTransaction.commit();

    int contentsSize = contents.size();
    assertEquals(0, contentsSize);
  }
}
