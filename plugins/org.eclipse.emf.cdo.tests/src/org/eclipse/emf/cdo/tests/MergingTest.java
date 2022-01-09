/*
 * Copyright (c) 2010-2013, 2016, 2021 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOMerger.ConflictException;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

/**
 * @author Eike Stepper
 */
@Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
public class MergingTest extends AbstractCDOTest
{
  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    skipStoreWithoutChangeSets();
  }

  public void testFromEmptyBranches() throws Exception
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
    long time3 = transaction.commit().getTimeStamp();
    CDOBranch source3 = mainBranch.createBranch(getBranchName("source3"), time3);

    CDOChangeSetData result = transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    result = transaction.merge(source2.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, result.isEmpty());
    assertEquals(false, transaction.isDirty());

    CDOChangeSetData check = transaction.merge(source3.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, check.isEmpty());
    assertEquals(false, transaction.isDirty());
  }

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
  }

  public void testRemergeAfterAdditionsInSource() throws Exception
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
    commitAndSync(tx1, transaction);
    tx1.close();

    transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    CDOCommitInfo commitInfo1 = transaction.commit();

    // Remerge from source1.
    CDOChangeSetData check = transaction.merge(source1.getHead(), source1.getPoint(commitInfo1.getTimeStamp()), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, check.isEmpty());
    assertEquals(false, transaction.isDirty());
  }

  public void testRemergeAfterAdditionsInSource2() throws Exception
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

    {
      sleep(10);
      CDOTransaction tx1 = session.openTransaction(source1);
      CDOResource res1 = tx1.getResource(getResourcePath("/res"));
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
      CDOResource res1 = tx1.getResource(getResourcePath("/res"));
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

    // Remerge from source1.
    CDOChangeSetData check = transaction.merge(source1.getHead(), source1.getPoint(commitInfo1.getTimeStamp()), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, check.isEmpty());
    assertEquals(false, transaction.isDirty());
  }

  public void testAdditionsInSourceAndTarget() throws Exception
  {
    String soure1Path = getBranchName("source1");
    String source2Path = getBranchName("source2");

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
    CDOBranch source1 = mainBranch.createBranch(soure1Path, time1);

    sleep(10);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time2 = transaction.commit().getTimeStamp();
    CDOBranch source2 = mainBranch.createBranch(source2Path, time2);

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
    commitAndSync(tx1, transaction);
    tx1.close();

    transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    transaction.commit();

    sleep(10);
    CDOTransaction tx2 = session.openTransaction(source2);
    CDOResource res2 = tx2.getResource(getResourcePath("/res"));
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

    // Remerge from source2.
    CDOChangeSetData check = transaction.merge(source2.getHead(), source2.getPoint(commitInfo2.getTimeStamp()), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, check.isEmpty());
    assertEquals(false, transaction.isDirty());
  }

  public void testRemergeAfterAdditionsInSourceAndTarget() throws Exception
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
    commitAndSync(tx1, transaction);
    tx1.close();

    transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    transaction.commit();

    sleep(10);
    CDOTransaction tx2 = session.openTransaction(source2);
    CDOResource res2 = tx2.getResource(getResourcePath("/res"));
    EList<EObject> contents2 = res2.getContents();
    addCompany(contents2);
    commitAndSync(tx2, transaction);
    tx2.close();

    transaction.merge(source2.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    long now = transaction.commit().getTimeStamp();

    // Remerge from source2.
    CDOChangeSetData check = transaction.merge(source2.getHead(), source2.getPoint(now), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, check.isEmpty());
    assertEquals(false, transaction.isDirty());
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

    CDOResource resource = transaction.createResource(getResourcePath("/res" + run));
    EList<EObject> contents = resource.getContents();
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time = transaction.commit().getTimeStamp();
    CDOBranch source = mainBranch.createBranch(getBranchName("source" + run), time);

    sleep(10);
    CDOTransaction tx1 = session.openTransaction(source);
    CDOResource res1 = tx1.getResource(getResourcePath("/res" + run));
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
  }

  public void testRemergeAfterChangesInSource() throws Exception
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

    CDOChangeSetData check = transaction.merge(source.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, check.isEmpty());
    assertEquals(false, transaction.isDirty());
  }

  public void testRemergeAfterChangesInSourceConflict() throws Exception
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
    ((Company)contents1.get(1)).setName("Company1");
    ((Company)contents1.get(2)).setName("Company2");
    CDOCommitInfo commit1 = commitAndSync(tx1, transaction);

    long updateTime1 = session.getLastUpdateTime();

    transaction.merge(commit1, new DefaultCDOMerger.PerFeature.ManyValued());
    transaction.commit();
    assertEquals(5, contents.size());

    long updateTime2 = session.getLastUpdateTime();
    assertEquals(false, updateTime1 == updateTime2);
    assertEquals("Company0", ((Company)contents.get(0)).getName());
    assertEquals("Company1", ((Company)contents.get(1)).getName());
    assertEquals("Company2", ((Company)contents.get(2)).getName());

    ((Company)contents1.get(0)).setName("CompanyX");
    ((Company)contents1.get(1)).setName("CompanyY");
    ((Company)contents1.get(2)).setName("CompanyZ");
    CDOCommitInfo commit2 = commitAndSync(tx1, transaction);

    try
    {
      transaction.merge(commit2, commit1, new DefaultCDOMerger.PerFeature.ManyValued());
      fail("ConflictException expected");
    }
    catch (ConflictException expected)
    {
      // SUCCEED
    }
  }

  public void testFromBranchWithRemovalsInSource() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    EList<EObject> contents = resource.getContents();
    Company company0 = addCompany(contents);
    Company company1 = addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    addCompany(contents);
    long time1 = transaction.commit().getTimeStamp();
    CDOBranch source = mainBranch.createBranch(getBranchName("source"), time1);

    sleep(10);
    CDOTransaction tx1 = session.openTransaction(source);
    CDOResource res1 = tx1.getResource(getResourcePath("/res"));
    EList<EObject> contents1 = res1.getContents();
    ((Company)contents1.get(0)).setName("Company0");
    contents1.remove(1);

    // dumpAllRevisions(getRepository().getStore());
    long time2 = commitAndSync(tx1, transaction).getTimeStamp();
    assertEquals(true, time1 < time2);
    dumpAllRevisions(getRepository().getStore());
    tx1.close();

    CDOBranchPoint head = source.getHead();
    DefaultCDOMerger.PerFeature.ManyValued merger = new DefaultCDOMerger.PerFeature.ManyValued();
    CDOChangeSetData result = transaction.merge(head, merger);
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
  }

  public void testRemergeAfterRemovalsInSource() throws Exception
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
    commitAndSync(tx1, transaction);
    tx1.close();

    transaction.merge(source.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    transaction.commit();

    CDOChangeSetData check = transaction.merge(source.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, check.isEmpty());
    assertEquals(false, transaction.isDirty());
  }

  public void testFromBranchWithAdditionsTwoTimes() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    EList<EObject> contents = resource.getContents();
    addCompany(contents);
    addCompany(contents);
    long time = transaction.commit().getTimeStamp();

    CDOBranch source = mainBranch.createBranch(getBranchName("source"), time);
    CDOTransaction sourceTx = session.openTransaction(source);
    CDOResource res1 = sourceTx.getResource(getResourcePath("/res"));
    EList<EObject> sourceContents = res1.getContents();
    addCompany(sourceContents);
    long sourceCommit1 = commitAndSync(sourceTx, transaction).getTimeStamp();

    CDOChangeSetData check1 = transaction.merge(source.getHead(), source.getBase(), new DefaultCDOMerger.PerFeature.ManyValued());
    long mainCommit1 = transaction.commit().getTimeStamp();
    assertEquals(1, check1.getNewObjects().size());
    assertEquals(1, check1.getChangedObjects().size());
    assertTrue(check1.getDetachedObjects().isEmpty());
    assertEquals(false, transaction.isDirty());

    addCompany(sourceContents);
    commitAndSync(sourceTx, transaction);
    sourceTx.close();

    CDOChangeSetData check2 = transaction.merge(source.getHead(), source.getPoint(sourceCommit1), mainBranch.getPoint(mainCommit1),
        new DefaultCDOMerger.PerFeature.ManyValued());
    transaction.commit();
    assertEquals(1, check2.getNewObjects().size());
    assertEquals(1, check2.getChangedObjects().size());
    assertTrue(check2.getDetachedObjects().isEmpty());
    assertEquals(false, transaction.isDirty());
  }

  public void testRemergeAfterAdditionsInSourceTwoTimes() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOResource resource = transaction.createResource(getResourcePath("/res"));
    EList<EObject> contents = resource.getContents();
    addCompany(contents);
    addCompany(contents);
    long time = transaction.commit().getTimeStamp();

    CDOBranch source = mainBranch.createBranch(getBranchName("source"), time);
    CDOTransaction sourceTx = session.openTransaction(source);
    CDOResource res1 = sourceTx.getResource(getResourcePath("/res"));
    EList<EObject> sourceContents = res1.getContents();
    addCompany(sourceContents);
    long sourceCommit1 = commitAndSync(sourceTx, transaction).getTimeStamp();

    transaction.merge(source.getHead(), source.getBase(), new DefaultCDOMerger.PerFeature.ManyValued());
    long mainCommit1 = transaction.commit().getTimeStamp();

    addCompany(sourceContents);
    long sourceCommit2 = commitAndSync(sourceTx).getTimeStamp();
    sourceTx.close();

    transaction.merge(source.getHead(), source.getPoint(sourceCommit1), mainBranch.getPoint(mainCommit1), new DefaultCDOMerger.PerFeature.ManyValued());
    long mainCommit2 = transaction.commit().getTimeStamp();

    CDOChangeSetData check = transaction.merge(source.getHead(), source.getPoint(sourceCommit2), mainBranch.getPoint(mainCommit2),
        new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, check.isEmpty());
    assertEquals(false, transaction.isDirty());
  }

  /**
   * Bug 309467.
   */
  public void test_Bugzilla_309467() throws Exception
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

    {
      sleep(10);
      CDOTransaction tx1 = session.openTransaction(source1);
      CDOResource res1 = tx1.getResource(getResourcePath("/res"));
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

    CDOChangeSetData check = transaction.merge(source1.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, check.isEmpty());
    assertEquals(false, transaction.isDirty());
  }

  /**
   * Bug 309467.
   */
  @Requires(IRepositoryConfig.CAPABILITY_RESTARTABLE)
  public void test_Bugzilla_309467_ServerRestart() throws Exception
  {
    CDOCommitInfo commitInfo;

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

      {
        sleep(10);
        CDOTransaction tx1 = session.openTransaction(source1);
        CDOResource res1 = tx1.getResource(getResourcePath("/res"));
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
      commitInfo = transaction.commit();
      session.close();
    }

    restartRepository();

    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOBranch source1 = mainBranch.getBranch(getBranchName("source1"));

    CDOTransaction transaction = session.openTransaction(mainBranch);
    CDOChangeSetData check = transaction.merge(source1.getHead(), source1.getPoint(commitInfo.getTimeStamp()), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(true, check.isEmpty());
    assertEquals(false, transaction.isDirty());
  }

  public void testAutoMerge() throws Exception
  {
    CDOMerger merger = new DefaultCDOMerger.PerFeature.ManyValued();
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
    addCompany(contents);
    long time = transaction.commit().getTimeStamp();
    CDOBranch source = mainBranch.createBranch(getBranchName("source"), time);

    CDOTransaction tx1 = session.openTransaction(source);
    CDOResource res1 = tx1.getResource(getResourcePath("/res"));
    EList<EObject> contents1 = res1.getContents();

    sleep(10);
    ((Company)contents1.get(0)).setName("Company0");
    ((Company)contents1.get(1)).setName("Company1");
    ((Company)contents1.get(2)).setName("Company2");
    commitAndSync(tx1, transaction);

    CDOChangeSetData merge1 = transaction.merge(source, merger);
    assertEquals(false, merge1.isEmpty());
    assertEquals(0, merge1.getNewObjects().size());
    assertEquals(0, merge1.getDetachedObjects().size());
    assertEquals(3, merge1.getChangedObjects().size());
    assertEquals(true, transaction.isDirty());
    assertEquals("Company0", ((Company)contents.get(0)).getName());
    assertEquals("Company1", ((Company)contents.get(1)).getName());
    assertEquals("Company2", ((Company)contents.get(2)).getName());
    transaction.commit();

    sleep(10);
    ((Company)contents1.get(3)).setName("Company3");
    ((Company)contents1.get(4)).setName("Company4");
    ((Company)contents1.get(5)).setName("Company5");
    commitAndSync(tx1, transaction);

    CDOChangeSetData merge2 = transaction.merge(source, merger);
    assertEquals(false, merge2.isEmpty());
    assertEquals(0, merge2.getNewObjects().size());
    assertEquals(0, merge2.getDetachedObjects().size());
    assertEquals(3, merge2.getChangedObjects().size());
    assertEquals(true, transaction.isDirty());
    assertEquals("Company3", ((Company)contents.get(3)).getName());
    assertEquals("Company4", ((Company)contents.get(4)).getName());
    assertEquals("Company5", ((Company)contents.get(5)).getName());
    transaction.commit();
  }

  private Company addCompany(EList<EObject> contents)
  {
    Company company = getModel1Factory().createCompany();
    contents.add(company);
    return company;
  }

  public void testMergeClobChangesInSource() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOTextResource resource = transaction.createTextResource(getResourcePath("text1.txt"));
    resource.setContents(session.newClob("This can be a looooong document"));

    long time = transaction.commit().getTimeStamp();

    CDOBranch source = mainBranch.createBranch(getBranchName("branch"), time);
    CDOTransaction tx1 = session.openTransaction(source);

    CDOTextResource res1 = tx1.getTextResource(getResourcePath("text1.txt"));
    res1.setContents(session.newClob("This is a different document from a different branch"));

    commitAndSync(tx1, transaction);
    tx1.close();

    CDOChangeSetData result = transaction.merge(source.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
    assertEquals(false, result.isEmpty());
    assertEquals(0, result.getNewObjects().size());
    assertEquals(1, result.getChangedObjects().size());
    assertEquals(0, result.getDetachedObjects().size());
    assertEquals(true, transaction.isDirty());

    CDOCommitInfo commitInfo1 = transaction.commit();
    assertEquals(0, commitInfo1.getNewObjects().size());
    assertEquals(1, commitInfo1.getChangedObjects().size());
    assertEquals(0, commitInfo1.getDetachedObjects().size());
    assertEquals(false, transaction.isDirty());

    String contents = resource.getContents().getString();
    assertEquals("This is a different document from a different branch", contents);
  }

  public void testMergeClobChangesInSourceConflict() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);

    CDOTextResource resource = transaction.createTextResource(getResourcePath("text1.txt"));
    resource.setContents(session.newClob("This can be a looooong document"));

    long time = transaction.commit().getTimeStamp();

    CDOBranch source = mainBranch.createBranch(getBranchName("branch"), time);
    CDOTransaction tx1 = session.openTransaction(source);

    CDOTextResource res1 = tx1.getTextResource(getResourcePath("text1.txt"));
    res1.setContents(session.newClob("This is a different document from a different branch"));

    commitAndSync(tx1, transaction);
    tx1.close();

    resource.setContents(session.newClob("This is a different document from the same branch"));
    transaction.commit();

    try
    {
      transaction.merge(source.getHead(), new DefaultCDOMerger.PerFeature.ManyValued());
      fail("ConflictException expected");
    }
    catch (ConflictException expected)
    {
      // SUCCESS
    }
  }
}
