/*
 * Copyright (c) 2010-2013, 2016, 2021, 2022, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.internal.common.commit.CDOChangeSetDataImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOMerger.ConflictException;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOSession.MergeData;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testPCL012SemanticListFeatureScopedMaterialization() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOTransaction transaction = session.openTransaction(mainBranch);
    CDOResource resource = transaction.createResource(getResourcePath("/pcl012-merge"));

    Product1 product = getModel1Factory().createProduct1();
    for (int i = 0; i < 8; i++)
    {
      product.getOrderDetails().add(getModel1Factory().createOrderDetail());
    }

    product.getOtherVATs().add(VAT.VAT0);
    product.getOtherVATs().add(VAT.VAT7);
    product.getOtherVATs().add(VAT.VAT15);
    resource.getContents().add(product);
    resource.getContents().addAll(product.getOrderDetails());
    long baseTime = transaction.commit().getTimeStamp();
    CDOBranch source = mainBranch.createBranch(getBranchName("pcl012-source"), baseTime);

    transaction.close();
    session.close();
    clearCache(getRepository().getRevisionManager());

    session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 1));
    CDOTransaction sourceTransaction = session.openTransaction(source);
    Product1 sourceProduct = (Product1)sourceTransaction.getResource(getResourcePath("/pcl012-merge")).getContents().get(0);
    sourceProduct.getOrderDetails().move(0, 3);
    sourceProduct.getOrderDetails().remove(4);
    sourceTransaction.commit();

    MergeData mergeData = ((InternalCDOSession)session).getMergeData(mainBranch.getHead(), source.getHead(), null, null, true);
    CDOID productID = CDOUtil.getCDOObject(sourceProduct).cdoID();
    EStructuralFeature unrelatedFeature = getModel1Package().getProduct1_OtherVATs();
    Map<CDOID, InternalCDORevision> observed = new HashMap<>();
    CDORevisionProvider targetProvider = partialFeatureProvider(mergeData.getTargetBaseInfo(), productID, unrelatedFeature, observed);
    CDORevisionProvider sourceProvider = partialFeatureProvider(mergeData.getSourceBaseInfo(), productID, unrelatedFeature, observed);
    CDORevisionProvider resultProvider = partialFeatureProvider(mergeData.getTargetBaseInfo(), productID, unrelatedFeature, observed);

    CDOChangeSetData result = new DefaultCDOMerger.PerFeature.ManyValued().merge(mergeData.getTargetChanges(), mergeData.getSourceChanges(), targetProvider,
        sourceProvider, resultProvider);
    assertFalse(result.isEmpty());
    assertFalse(observed.isEmpty());
    assertTrue(observed.get(productID).getListOrNull(getModel1Package().getProduct1_OrderDetails()).isFullyLoaded());
    assertFalse(observed.get(productID).getListOrNull(unrelatedFeature).isFullyLoaded());

    sourceTransaction.close();
    session.close();
  }

  /**
   * Verifies that applying a scalar-only change set does not materialize an unrelated partial collection.
   */
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testPCL017ApplyScalarPreservesUnrelatedPartialCollection() throws Exception
  {
    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 1));

    CDOTransaction writer = session.openTransaction();
    CDOResource resource = writer.createResource(getResourcePath("/pcl017-apply-scalar"));
    Company company = getModel1Factory().createCompany();
    company.setName("before");
    for (int i = 0; i < 8; i++)
    {
      company.getCategories().add(getModel1Factory().createCategory());
    }

    resource.getContents().add(company);
    writer.commit();
    writer.close();
    clearCache(session.getRevisionManager());

    CDOTransaction transaction = session.openTransaction();
    Company loaded = (Company)transaction.getResource(getResourcePath("/pcl017-apply-scalar")).getContents().get(0);
    InternalCDORevision targetRevision = (InternalCDORevision)CDOUtil.getCDOObject(loaded).cdoRevision();
    assertFalse(targetRevision.getListOrNull(getModel1Package().getCompany_Categories()).isFullyLoaded());

    InternalCDORevisionDelta scalarDelta = new CDORevisionDeltaImpl(targetRevision);
    scalarDelta.addFeatureDelta(new CDOSetFeatureDeltaImpl(getModel1Package().getAddress_Name(), 0, "after", "before"), null);
    CDOChangeSetData changeSet = new CDOChangeSetDataImpl(null, Collections.singletonList(scalarDelta), null);
    InternalCDOTransaction internalTransaction = (InternalCDOTransaction)transaction;
    internalTransaction.applyChangeSet(changeSet, id -> targetRevision, id -> targetRevision, null, false);

    assertEquals("after", loaded.getName());
    assertFalse(((InternalCDORevision)CDOUtil.getCDOObject(loaded).cdoRevision()).getListOrNull(getModel1Package().getCompany_Categories()).isFullyLoaded());

    transaction.close();
    session.close();
  }

  private CDORevisionProvider partialFeatureProvider(CDORevisionProvider delegate, CDOID productID, EStructuralFeature feature,
      Map<CDOID, InternalCDORevision> observed)
  {
    return id -> {
      CDORevision revision = delegate.getRevision(id);
      if (id.equals(productID) && revision instanceof InternalCDORevision)
      {
        InternalCDORevision partial = copyWithPartialFeature((InternalCDORevision)revision, feature);
        observed.put(id, partial);
        return revision;
      }

      return revision;
    };
  }

  private InternalCDORevision copyWithPartialFeature(InternalCDORevision revision, EStructuralFeature feature)
  {
    InternalCDORevision copy = new CDORevisionImpl(revision.getEClass());
    copy.setID(revision.getID());
    copy.setVersion(revision.getVersion());
    copy.setBranchPoint(revision.getBranch().getPoint(revision.getTimeStamp()));
    copy.setRevised(revision.getRevised());

    for (EStructuralFeature currentFeature : revision.getEClass().getEAllStructuralFeatures())
    {
      if (!EMFUtil.isPersistent(currentFeature))
      {
        continue;
      }

      if (currentFeature.isMany())
      {
        CDOList full = revision.getListOrNull(currentFeature);
        if (full == null)
        {
          continue;
        }

        int size = full.size();
        int chunkSize = currentFeature == feature ? 1 : CDORevision.UNCHUNKED;
        InternalCDOList list = (InternalCDOList)CDOListFactory.DEFAULT.createList(currentFeature, size, size, chunkSize);
        for (int i = 0; i < size; i++)
        {
          if (currentFeature != feature || i == 0)
          {
            list.set(i, full.get(i));
          }
        }

        list.finishConstruction(true);
        copy.setList(currentFeature, list);
      }
      else
      {
        copy.setValue(currentFeature, revision.getValue(currentFeature));
      }
    }

    return copy;
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
    try
    {
      CDOBranch mainBranch = session.getBranchManager().getMainBranch();
      CDOBranch source1 = mainBranch.getBranch(getBranchName("source1"));

      CDOTransaction transaction = session.openTransaction(mainBranch);
      CDOChangeSetData check = transaction.merge(source1.getHead(), source1.getPoint(commitInfo.getTimeStamp()), new DefaultCDOMerger.PerFeature.ManyValued());
      assertEquals(true, check.isEmpty());
      assertEquals(false, transaction.isDirty());
    }
    finally
    {
      session.close();
    }
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
