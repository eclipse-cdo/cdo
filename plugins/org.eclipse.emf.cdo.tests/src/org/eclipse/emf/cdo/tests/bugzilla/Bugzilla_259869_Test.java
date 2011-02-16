/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOXATransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.session.SessionUtil;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * CommitTransactionRequest sent redundantly
 * <p>
 * See bug 259869
 * 
 * @author Simon McDuff
 */
public class Bugzilla_259869_Test extends AbstractCDOTest
{
  final static public String REPOSITORY2_NAME = "repo2";

  public void testBugzilla_259869() throws Exception
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource(getResourcePath("test1"));
    Company company = getModel1Factory().createCompany();
    resource1.getContents().add(company);

    transaction.commit();

    long start = System.currentTimeMillis();
    for (int i = 0; i < 500; i++)
    {
      company.setName(String.valueOf(i));
      transaction.commit();
    }

    long timeCommitWithChanges = System.currentTimeMillis() - start;
    start = System.currentTimeMillis();
    for (int i = 0; i < 500; i++)
    {
      transaction.commit();
    }

    long timeToCommitNoChange = System.currentTimeMillis() - start;
    assertEquals(true, timeCommitWithChanges > timeToCommitNoChange * 1.2);
  }

  public void testBugzilla_259869_XA() throws Exception
  {
    getRepository(REPOSITORY2_NAME);

    CDOSession sessionA = openSession();
    CDOSession sessionB = openSession(REPOSITORY2_NAME);

    ResourceSet resourceSet = new ResourceSetImpl();
    CDOXATransaction xaTransaction = CDOUtil.createXATransaction();

    SessionUtil.prepareResourceSet(resourceSet);
    xaTransaction.add(CDOUtil.getViewSet(resourceSet));

    sessionA.getPackageRegistry().putEPackage(getModel1Package());
    sessionB.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransaction transactionA1 = sessionA.openTransaction(resourceSet);
    CDOTransaction transactionB1 = sessionB.openTransaction(resourceSet);

    CDOResource resA = transactionA1.createResource(getResourcePath("/resA"));
    CDOResource resB = transactionB1.createResource(getResourcePath("/resB"));

    Company companyA = getModel1Factory().createCompany();
    resA.getContents().add(companyA);

    transactionA1.commit();

    Company companyB = getModel1Factory().createCompany();
    resB.getContents().add(companyB);

    transactionB1.commit();

    long start = System.currentTimeMillis();
    for (int i = 0; i < 500; i++)
    {
      companyA.setName(String.valueOf(i));
      companyB.setName(String.valueOf(i));
      xaTransaction.commit();
    }

    long timeCommitWithChanges = System.currentTimeMillis() - start;
    start = System.currentTimeMillis();
    for (int i = 0; i < 500; i++)
    {
      xaTransaction.commit();
    }

    long timeToCommitNoChange = System.currentTimeMillis() - start;
    assertEquals(true, timeCommitWithChanges > timeToCommitNoChange * 1.2);
  }
}
