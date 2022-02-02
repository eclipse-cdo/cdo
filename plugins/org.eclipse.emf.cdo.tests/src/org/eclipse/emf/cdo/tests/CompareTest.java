/*
 * Copyright (c) 2010-2012, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * @author Eike Stepper
 */
@Requires(IRepositoryConfig.CAPABILITY_AUDITING)
public class CompareTest extends AbstractCDOTest
{
  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testFromEmptyBranches() throws Exception
  {
    CDOSession session = openSession();
    ((org.eclipse.emf.cdo.net4j.CDONet4jSession)session).options().getNet4jProtocol().setTimeout(1000000000);

    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    EList<EObject> contents = resource.getContents();
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time1 = transaction.commit().getTimeStamp();
    CDOBranch source1 = mainBranch.createBranch(getBranchName("source1"), time1);

    sleep(10);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time2 = transaction.commit().getTimeStamp();
    CDOBranch source2 = mainBranch.createBranch(getBranchName("source2"), time2);

    sleep(10);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time3 = transaction.commit().getTimeStamp();
    CDOBranch source3 = mainBranch.createBranch(getBranchName("source3"), time3);

    assertCompare(session, mainBranch.getHead(), source1.getHead(), 7, 1, 0);
    assertCompare(session, mainBranch.getHead(), source2.getHead(), 3, 1, 0);
    assertCompare(session, mainBranch.getHead(), source3.getHead(), 0, 0, 0);

    assertCompare(session, mainBranch.getHead(), mainBranch.getPoint(time1), 7, 1, 0);
    assertCompare(session, mainBranch.getHead(), mainBranch.getPoint(time2), 3, 1, 0);
    assertCompare(session, mainBranch.getHead(), mainBranch.getPoint(time3), 0, 0, 0);
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testFromBranchWithAdditions() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    EList<EObject> contents = resource.getContents();
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time1 = transaction.commit().getTimeStamp();
    CDOBranch source1 = mainBranch.createBranch(getBranchName("source1"), time1);

    sleep(10);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    transaction.commit();

    sleep(10);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    transaction.commit();

    sleep(10);
    CDOTransaction tx1 = session.openTransaction(source1);
    CDOResource res1 = tx1.getResource(getResourcePath("/res"));
    EList<EObject> contents1 = res1.getContents();
    addCompany(contents1);
    addCompany(contents1);
    tx1.commit();
    tx1.close();

    assertCompare(session, mainBranch.getHead(), source1.getHead(), 7, 1, 2);
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testAdditionsInSourceAndTarget() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    EList<EObject> contents = resource.getContents();
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time1 = transaction.commit().getTimeStamp();
    CDOBranch source1 = mainBranch.createBranch(getBranchName("source1"), time1);

    sleep(10);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time2 = transaction.commit().getTimeStamp();
    CDOBranch source2 = mainBranch.createBranch(getBranchName("source2"), time2);

    sleep(10);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    transaction.commit();

    sleep(10);
    CDOTransaction tx1 = session.openTransaction(source1);
    CDOResource res1 = tx1.getResource(getResourcePath("/res"));
    EList<EObject> contents1 = res1.getContents();
    addCompany(contents1);
    addCompany(contents1);
    tx1.commit();
    tx1.close();

    sleep(10);
    CDOTransaction tx2 = session.openTransaction(source2);
    CDOResource res2 = tx2.getResource(getResourcePath("/res"));
    EList<EObject> contents2 = res2.getContents();
    addCompany(contents2);
    tx2.commit();
    tx2.close();

    assertCompare(session, mainBranch.getHead(), source1.getHead(), 7, 1, 2);
    assertCompare(session, mainBranch.getHead(), source2.getHead(), 3, 1, 1);
    assertCompare(session, source1.getHead(), source2.getHead(), 2, 1, 5);
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testFromBranchWithChangesInSource() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOResource resource = transaction.createResource(getResourcePath("/res" + 0));
    EList<EObject> contents = resource.getContents();
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time = transaction.commit().getTimeStamp();
    CDOBranch source = mainBranch.createBranch(getBranchName("source0"), time);

    sleep(10);
    CDOTransaction tx1 = session.openTransaction(source);
    CDOResource res1 = tx1.getResource(getResourcePath("/res" + 0));
    EList<EObject> contents1 = res1.getContents();
    ((Company)contents1.get(0)).setName("Company0");
    ((Company)contents1.get(1)).setName("Company1");
    ((Company)contents1.get(2)).setName("Company2");
    tx1.commit();
    tx1.close();

    assertCompare(session, mainBranch.getHead(), source.getHead(), 0, 3, 0);
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testFromBranchWithRemovalsInSource() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    EList<EObject> contents = resource.getContents();
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time = transaction.commit().getTimeStamp();
    CDOBranch source = mainBranch.createBranch(getBranchName("source"), time);

    sleep(10);
    CDOTransaction tx1 = session.openTransaction(source);
    CDOResource res1 = tx1.getResource(getResourcePath("/res"));
    EList<EObject> contents1 = res1.getContents();
    ((Company)contents1.get(0)).setName("Company0");
    contents1.remove(1);
    tx1.commit();
    tx1.close();

    assertCompare(session, mainBranch.getHead(), source.getHead(), 1, 2, 0);
  }

  private Company addCompany(EList<EObject> contents)
  {
    Company company = getModel1Factory().createCompany();
    contents.add(company);
    return company;
  }

  private static void assertCompare(CDOSession session, CDOBranchPoint target, CDOBranchPoint source, int n, int c, int d)
  {
    CDOView view = session.openView(target.getBranch(), target.getTimeStamp());
    CDOChangeSetData result = view.compareRevisions(source);
    assertEquals(n == 0 && c == 0 && d == 0, result.isEmpty());
    assertEquals("NewObjects", n, result.getNewObjects().size());
    assertEquals("ChangedObjects", c, result.getChangedObjects().size());
    assertEquals("DetachedObjects", d, result.getDetachedObjects().size());
    view.close();

    view = session.openView(source.getBranch(), source.getTimeStamp());
    result = view.compareRevisions(target);
    assertEquals(n == 0 && c == 0 && d == 0, result.isEmpty());
    assertEquals("NewObjects", d, result.getNewObjects().size());
    assertEquals("ChangedObjects", c, result.getChangedObjects().size());
    assertEquals("DetachedObjects", n, result.getDetachedObjects().size());
    view.close();
  }
}
