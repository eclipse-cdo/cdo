/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.IMEMStore;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

/**
 * @author Eike Stepper
 */
public class MergingTest extends AbstractCDOTest
{
  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    skipUnlessBranching();
  }

  public void testFromEmptyBranches() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOResource resource = transaction.createResource("/res");
    EList<EObject> contents = resource.getContents();
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time1 = transaction.commit().getTimeStamp();
    CDOBranch source1 = mainBranch.createBranch("source1", time1);

    sleep(10);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time2 = transaction.commit().getTimeStamp();
    CDOBranch source2 = mainBranch.createBranch("source2", time2);

    sleep(10);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time3 = transaction.commit().getTimeStamp();
    CDOBranch source3 = mainBranch.createBranch("source3", time3);

    CDOChangeSetData result = transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    result = transaction.merge(source2.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    result = transaction.merge(source3.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    session.close();
  }

  public void testFromBranchWithAdditions() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOResource resource = transaction.createResource("/res");
    EList<EObject> contents = resource.getContents();
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time1 = transaction.commit().getTimeStamp();
    CDOBranch source1 = mainBranch.createBranch("source1", time1);

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
    CDOResource res1 = tx1.getResource("/res");
    EList<EObject> contents1 = res1.getContents();
    addCompany(contents1);
    addCompany(contents1);
    commitAndSync(tx1, transaction);
    tx1.close();

    CDOChangeSetData result = transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(false, result.isEmpty());
    assertEquals(2, result.getNewObjects().size());
    assertEquals(1, result.getChangedObjects().size());
    assertEquals(0, result.getDetachedObjects().size());
    assertEquals(true, transaction.isDirty());

    CDOCommitInfo commitInfo1 = transaction.commit();
    assertEquals(2, commitInfo1.getNewObjects().size());
    assertEquals(1, commitInfo1.getChangedObjects().size());
    assertEquals(0, commitInfo1.getDetachedObjects().size());
    assertEquals(false, transaction.isDirty());
    assertEquals(mainBranch, ((CDORevision)commitInfo1.getNewObjects().get(0)).getBranch());
    assertEquals(mainBranch, ((CDORevision)commitInfo1.getNewObjects().get(1)).getBranch());
    assertEquals(1, ((CDORevision)commitInfo1.getNewObjects().get(0)).getVersion());
    assertEquals(1, ((CDORevision)commitInfo1.getNewObjects().get(1)).getVersion());

    session.close();
  }

  @SuppressWarnings("unused")
  public void testRemergeAfterAdditionsInSource() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOResource resource = transaction.createResource("/res");
    EList<EObject> contents = resource.getContents();
    Company company0 = addCompany(contents);
    Company company1 = addCompany(contents);
    Company company2 = addCompany(contents);
    Company company3 = addCompany(contents);
    Company company4 = addCompany(contents);
    long time1 = transaction.commit().getTimeStamp();
    CDOBranch source1 = mainBranch.createBranch("source1", time1);

    sleep(10);
    Company company5 = addCompany(contents);
    Company company6 = addCompany(contents);
    Company company7 = addCompany(contents);
    Company company8 = addCompany(contents);
    transaction.commit();

    sleep(10);
    Company company9 = addCompany(contents);
    Company company10 = addCompany(contents);
    Company company11 = addCompany(contents);
    transaction.commit();

    sleep(10);
    CDOTransaction tx1 = session.openTransaction(source1);
    CDOResource res1 = tx1.getResource("/res");
    EList<EObject> contents1 = res1.getContents();
    addCompany(contents1);
    addCompany(contents1);
    commitAndSync(tx1, transaction);
    tx1.close();

    transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    transaction.commit();

    CDOChangeSetData result = transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    session.close();
  }

  @SuppressWarnings("unused")
  public void testRemergeAfterAdditionsInSource2() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOResource resource = transaction.createResource("/res");
    EList<EObject> contents = resource.getContents();
    Company company0 = addCompany(contents);
    Company company1 = addCompany(contents);
    Company company2 = addCompany(contents);
    Company company3 = addCompany(contents);
    Company company4 = addCompany(contents);
    long time1 = transaction.commit().getTimeStamp();
    CDOBranch source1 = mainBranch.createBranch("source1", time1);

    sleep(10);
    Company company5 = addCompany(contents);
    Company company6 = addCompany(contents);
    Company company7 = addCompany(contents);
    Company company8 = addCompany(contents);
    transaction.commit();

    sleep(10);
    Company company9 = addCompany(contents);
    Company company10 = addCompany(contents);
    Company company11 = addCompany(contents);
    transaction.commit();

    {
      sleep(10);
      CDOTransaction tx1 = session.openTransaction(source1);
      CDOResource res1 = tx1.getResource("/res");
      EList<EObject> contents1 = res1.getContents();
      addCompany(contents1);
      addCompany(contents1);
      commitAndSync(tx1, transaction);
      tx1.close();
    }

    transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    transaction.commit();

    {
      sleep(10);
      CDOTransaction tx1 = session.openTransaction(source1);
      CDOResource res1 = tx1.getResource("/res");
      EList<EObject> contents1 = res1.getContents();
      addCompany(contents1);
      commitAndSync(tx1, transaction);
      tx1.close();
    }

    CDOChangeSetData result = transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(false, result.isEmpty());
    assertEquals(1, result.getNewObjects().size());
    assertEquals(1, result.getChangedObjects().size());
    assertEquals(0, result.getDetachedObjects().size());
    assertEquals(true, transaction.isDirty());

    CDOCommitInfo commitInfo1 = transaction.commit();
    assertEquals(1, commitInfo1.getNewObjects().size());
    assertEquals(1, commitInfo1.getChangedObjects().size());
    assertEquals(0, commitInfo1.getDetachedObjects().size());
    assertEquals(false, transaction.isDirty());
    assertEquals(mainBranch, ((CDORevision)commitInfo1.getNewObjects().get(0)).getBranch());
    assertEquals(1, ((CDORevision)commitInfo1.getNewObjects().get(0)).getVersion());

    result = transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    session.close();
  }

  public void testAdditionsInSourceAndTarget() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOResource resource = transaction.createResource("/res");
    EList<EObject> contents = resource.getContents();
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time1 = transaction.commit().getTimeStamp();
    CDOBranch source1 = mainBranch.createBranch("source1", time1);

    sleep(10);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time2 = transaction.commit().getTimeStamp();
    CDOBranch source2 = mainBranch.createBranch("source2", time2);

    sleep(10);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    transaction.commit();

    sleep(10);
    CDOTransaction tx1 = session.openTransaction(source1);
    CDOResource res1 = tx1.getResource("/res");
    EList<EObject> contents1 = res1.getContents();
    addCompany(contents1);
    addCompany(contents1);
    commitAndSync(tx1, transaction);
    tx1.close();

    transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    transaction.commit();

    sleep(10);
    CDOTransaction tx2 = session.openTransaction(source2);
    CDOResource res2 = tx2.getResource("/res");
    EList<EObject> contents2 = res2.getContents();
    addCompany(contents2);
    commitAndSync(tx2, transaction);
    tx2.close();

    CDOChangeSetData result = transaction.merge(source2.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(false, result.isEmpty());
    assertEquals(1, result.getNewObjects().size());
    assertEquals(1, result.getChangedObjects().size());
    assertEquals(0, result.getDetachedObjects().size());
    assertEquals(true, transaction.isDirty());

    CDOCommitInfo commitInfo2 = transaction.commit();
    assertEquals(1, commitInfo2.getNewObjects().size());
    assertEquals(1, commitInfo2.getChangedObjects().size());
    assertEquals(0, commitInfo2.getDetachedObjects().size());
    assertEquals(false, transaction.isDirty());
    assertEquals(mainBranch, ((CDORevision)commitInfo2.getNewObjects().get(0)).getBranch());
    assertEquals(1, ((CDORevision)commitInfo2.getNewObjects().get(0)).getVersion());

    result = transaction.merge(source2.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    session.close();
  }

  public void testRemergeAfterAdditionsInSourceAndTarget() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOResource resource = transaction.createResource("/res");
    EList<EObject> contents = resource.getContents();
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time1 = transaction.commit().getTimeStamp();
    CDOBranch source1 = mainBranch.createBranch("source1", time1);

    sleep(10);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time2 = transaction.commit().getTimeStamp();
    CDOBranch source2 = mainBranch.createBranch("source2", time2);

    sleep(10);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    transaction.commit();

    sleep(10);
    CDOTransaction tx1 = session.openTransaction(source1);
    CDOResource res1 = tx1.getResource("/res");
    EList<EObject> contents1 = res1.getContents();
    addCompany(contents1);
    addCompany(contents1);
    commitAndSync(tx1, transaction);
    tx1.close();

    transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    transaction.commit();

    sleep(10);
    CDOTransaction tx2 = session.openTransaction(source2);
    CDOResource res2 = tx2.getResource("/res");
    EList<EObject> contents2 = res2.getContents();
    addCompany(contents2);
    commitAndSync(tx2, transaction);
    tx2.close();

    transaction.merge(source2.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    transaction.commit();

    CDOChangeSetData result = transaction.merge(source2.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    session.close();
  }

  public void testFromBranchWithChangesInSource_100() throws Exception
  {
    disableConsole();
    for (int i = 0; i < 100; i++)
    {
      System.out.println("run: " + i);
      mergeFromBranchWithChangesInSource(i);
    }

    enableConsole();
  }

  public void testFromBranchWithChangesInSource() throws Exception
  {
    mergeFromBranchWithChangesInSource(0);
  }

  private void mergeFromBranchWithChangesInSource(int run) throws CommitException
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOResource resource = transaction.createResource("/res" + run);
    EList<EObject> contents = resource.getContents();
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time = transaction.commit().getTimeStamp();
    CDOBranch source = mainBranch.createBranch("source" + run, time);

    sleep(10);
    CDOTransaction tx1 = session.openTransaction(source);
    CDOResource res1 = tx1.getResource("/res" + run);
    EList<EObject> contents1 = res1.getContents();
    ((Company)contents1.get(0)).setName("Company0");
    ((Company)contents1.get(1)).setName("Company1");
    ((Company)contents1.get(2)).setName("Company2");
    commitAndSync(tx1, transaction);
    tx1.close();

    CDOChangeSetData result = transaction.merge(source.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(false, result.isEmpty());
    assertEquals(0, result.getNewObjects().size());
    assertEquals(3, result.getChangedObjects().size());
    assertEquals(0, result.getDetachedObjects().size());
    assertEquals(true, transaction.isDirty());

    CDOCommitInfo commitInfo1 = transaction.commit();
    assertEquals(0, commitInfo1.getNewObjects().size());
    assertEquals(3, commitInfo1.getChangedObjects().size());
    assertEquals(0, commitInfo1.getDetachedObjects().size());
    assertEquals(false, transaction.isDirty());

    session.close();
  }

  @SuppressWarnings("unused")
  public void testRemergeAfterChangesInSource() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOResource resource = transaction.createResource("/res");
    EList<EObject> contents = resource.getContents();
    Company company0 = addCompany(contents);
    Company company1 = addCompany(contents);
    Company company2 = addCompany(contents);
    Company company3 = addCompany(contents);
    Company company4 = addCompany(contents);
    long time = transaction.commit().getTimeStamp();
    CDOBranch source = mainBranch.createBranch("source", time);

    sleep(10);
    CDOTransaction tx1 = session.openTransaction(source);
    CDOResource res1 = tx1.getResource("/res");
    EList<EObject> contents1 = res1.getContents();
    ((Company)contents1.get(0)).setName("Company0");
    ((Company)contents1.get(1)).setName("Company1");
    ((Company)contents1.get(2)).setName("Company2");
    commitAndSync(tx1, transaction);
    tx1.close();

    long updateTime1 = session.getLastUpdateTime();

    transaction.merge(source.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    transaction.commit();
    assertEquals(5, contents.size());

    long updateTime2 = session.getLastUpdateTime();
    assertEquals(false, updateTime1 == updateTime2);
    assertEquals("Company0", ((Company)contents.get(0)).getName());
    assertEquals("Company1", ((Company)contents.get(1)).getName());
    assertEquals("Company2", ((Company)contents.get(2)).getName());

    CDOChangeSetData result = transaction.merge(source.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    session.close();
  }

  public void testFromBranchWithRemovalsInSource() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOResource resource = transaction.createResource("/res");
    EList<EObject> contents = resource.getContents();
    Company company0 = addCompany(contents);
    Company company1 = addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time = transaction.commit().getTimeStamp();
    CDOBranch source = mainBranch.createBranch("source", time);

    sleep(10);
    CDOTransaction tx1 = session.openTransaction(source);
    CDOResource res1 = tx1.getResource("/res");
    EList<EObject> contents1 = res1.getContents();
    ((Company)contents1.get(0)).setName("Company0");
    contents1.remove(1);

    // dumpAllRevisions(getRepository().getStore());
    commitAndSync(tx1, transaction);
    dumpAllRevisions(getRepository().getStore());
    tx1.close();

    CDOChangeSetData result = transaction.merge(source.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(false, result.isEmpty());
    assertEquals(0, result.getNewObjects().size());
    assertEquals(2, result.getChangedObjects().size());
    assertEquals(1, result.getDetachedObjects().size());
    assertEquals(true, transaction.isDirty());
    assertEquals(CDOState.DIRTY, resource.cdoState());
    assertEquals(CDOState.DIRTY, CDOUtil.getCDOObject(company0).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(company1).cdoState());

    CDOCommitInfo commitInfo1 = transaction.commit();
    assertEquals(0, commitInfo1.getNewObjects().size());
    assertEquals(2, commitInfo1.getChangedObjects().size());
    assertEquals(1, commitInfo1.getDetachedObjects().size());
    assertEquals(false, transaction.isDirty());
    assertEquals(CDOState.CLEAN, resource.cdoState());
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(company0).cdoState());
    assertEquals(CDOState.TRANSIENT, CDOUtil.getCDOObject(company1).cdoState());

    session.close();
  }

  public void _testRemergeAfterRemovalsInSource() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOResource resource = transaction.createResource("/res");
    EList<EObject> contents = resource.getContents();
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time = transaction.commit().getTimeStamp();
    CDOBranch source = mainBranch.createBranch("source", time);

    sleep(10);
    CDOTransaction tx1 = session.openTransaction(source);
    CDOResource res1 = tx1.getResource("/res");
    EList<EObject> contents1 = res1.getContents();
    ((Company)contents1.get(0)).setName("Company0");
    contents1.remove(1);
    commitAndSync(tx1, transaction);
    tx1.close();

    transaction.merge(source.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    transaction.commit();

    CDOChangeSetData result = transaction.merge(source.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    session.close();
  }

  /**
   * Bug 309467.
   */
  @SuppressWarnings("unused")
  public void test_Bugzilla_309467() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOResource resource = transaction.createResource("/res");
    EList<EObject> contents = resource.getContents();
    Company company0 = addCompany(contents);
    Company company1 = addCompany(contents);
    Company company2 = addCompany(contents);
    Company company3 = addCompany(contents);
    Company company4 = addCompany(contents);
    long time1 = transaction.commit().getTimeStamp();
    CDOBranch source1 = mainBranch.createBranch("source1", time1);

    {
      sleep(10);
      CDOTransaction tx1 = session.openTransaction(source1);
      CDOResource res1 = tx1.getResource("/res");
      EList<EObject> contents1 = res1.getContents();
      ((Company)contents1.get(0)).setName("C0");
      ((Company)contents1.get(1)).setName("C1");
      ((Company)contents1.get(2)).setName("C2");
      ((Company)contents1.get(3)).setName("C3");
      ((Company)contents1.get(4)).setName("C4");
      commitAndSync(tx1, transaction);
      tx1.close();
    }

    CDOChangeSetData result = transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(false, result.isEmpty());
    assertEquals(true, transaction.isDirty());
    transaction.commit();

    result = transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    session.close();
  }

  /**
   * Bug 309467.
   */
  @SuppressWarnings("unused")
  public void test_Bugzilla_309467_ServerRestart() throws Exception
  {
    skipTest(getRepository().getStore() instanceof IMEMStore);

    {
      CDOSession session = openSession();
      CDOBranch mainBranch = session.getBranchManager().getMainBranch();
      CDOTransaction transaction = session.openTransaction(mainBranch);

      CDOResource resource = transaction.createResource("/res");
      EList<EObject> contents = resource.getContents();
      Company company0 = addCompany(contents);
      Company company1 = addCompany(contents);
      Company company2 = addCompany(contents);
      Company company3 = addCompany(contents);
      Company company4 = addCompany(contents);
      long time1 = transaction.commit().getTimeStamp();
      CDOBranch source1 = mainBranch.createBranch("source1", time1);

      {
        sleep(10);
        CDOTransaction tx1 = session.openTransaction(source1);
        CDOResource res1 = tx1.getResource("/res");
        EList<EObject> contents1 = res1.getContents();
        ((Company)contents1.get(0)).setName("C0");
        ((Company)contents1.get(1)).setName("C1");
        ((Company)contents1.get(2)).setName("C2");
        ((Company)contents1.get(3)).setName("C3");
        ((Company)contents1.get(4)).setName("C4");
        commitAndSync(tx1, transaction);
        tx1.close();
      }

      CDOChangeSetData result = transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
      assertEquals(false, result.isEmpty());
      assertEquals(true, transaction.isDirty());
      transaction.commit();
      session.close();
    }

    restartRepository();

    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOBranch source1 = mainBranch.getBranch("source1");

    CDOTransaction transaction = session.openTransaction(mainBranch);
    CDOChangeSetData result = transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    session.close();
  }

  private Company addCompany(EList<EObject> contents)
  {
    Company company = getModel1Factory().createCompany();
    contents.add(company);
    return company;
  }
}
