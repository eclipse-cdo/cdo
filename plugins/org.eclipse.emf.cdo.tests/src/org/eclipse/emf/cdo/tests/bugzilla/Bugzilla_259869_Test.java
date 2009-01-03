/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOXATransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.util.SessionUtil;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * CommitTransactionRequest sent redundantly
 * 
 * @see https://bugs.eclipse.org/259869
 * @author Simon McDuff
 */
public class Bugzilla_259869_Test extends AbstractCDOTest
{
  final static public String REPOSITORY2_NAME = "repo2";

  public void testBugzilla_259869() throws InterruptedException
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(getModel1Package());

    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource("test1");
    Company company = getModel1Factory().createCompany();
    resource1.getContents().add(company);

    transaction.commit();
    company.setName("Simon");
    long start = System.currentTimeMillis();
    transaction.commit();
    long timeCommitOnce = System.currentTimeMillis() - start;

    start = System.currentTimeMillis();
    for (int i = 0; i < 500; i++)
    {
      transaction.commit();
    }
    long timeToCommit500Times = System.currentTimeMillis() - start;

    assertTrue(timeCommitOnce * 500 > timeToCommit500Times * 1.5);
  }

  public void testBugzilla_259869_XA() throws InterruptedException
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

    CDOResource resA = transactionA1.createResource("/resA");
    CDOResource resB = transactionB1.createResource("/resB");

    Company companyA = getModel1Factory().createCompany();
    resA.getContents().add(companyA);

    transactionA1.commit();

    Company companyB = getModel1Factory().createCompany();
    resB.getContents().add(companyB);

    transactionB1.commit();

    companyA.setName("Simon");
    companyB.setName("Simon");
    long start = System.currentTimeMillis();
    xaTransaction.commit();
    long timeCommitOnce = System.currentTimeMillis() - start;

    start = System.currentTimeMillis();
    for (int i = 0; i < 500; i++)
    {
      xaTransaction.commit();
    }
    long timeToCommit500Times = System.currentTimeMillis() - start;

    assertTrue(timeCommitOnce * 500 > timeToCommit500Times * 1.5);
  }

}
