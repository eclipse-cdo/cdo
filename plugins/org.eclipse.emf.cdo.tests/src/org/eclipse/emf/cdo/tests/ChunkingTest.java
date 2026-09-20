/*
 * Copyright (c) 2007-2013, 2015, 2016, 2018, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Bernd Fuhrmann - testEnsureChunk for bug 502932
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOCollectionLoadingConfig;
import org.eclipse.emf.cdo.common.revision.CDOCollectionLoadingConfig.ChunkConfig;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager.Request.Config.LookupMode;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOFetchRule;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.net4j.protocol.LoadChunkRequest;
import org.eclipse.emf.cdo.internal.net4j.protocol.LoadRevisionsRequest;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Customer;
import org.eclipse.emf.cdo.tests.model1.Product1;
import org.eclipse.emf.cdo.tests.model1.SalesOrder;
import org.eclipse.emf.cdo.tests.model5.GenListOfInt;
import org.eclipse.emf.cdo.tests.model5.Model5Factory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOUserSavepoint;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.session.CDOCollectionLoadingPolicyImpl;
import org.eclipse.emf.internal.cdo.session.CDOCollectionLoadingResolver;
import org.eclipse.emf.internal.cdo.session.CDOSessionImpl;

import org.eclipse.net4j.signal.SignalCounter;
import org.eclipse.net4j.util.io.IOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eike Stepper
 */
@Requires(IRepositoryConfig.CAPABILITY_CHUNKING)
public class ChunkingTest extends AbstractCDOTest
{
  private static final String RESOURCE_PATH = "/test";

  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testSessionChangeSetMaterializesPartialRevision() throws Exception
  {
    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 1));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));
    Company company = getModel1Factory().createCompany();
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    company.getCategories().add(getModel1Factory().createCategory());
    resource.getContents().add(company);
    transaction.commit();

    CDOID companyID = CDOUtil.getCDOObject(company).cdoID();
    CDOBranchPoint branchPoint = transaction.getBranch().getHead();
    InternalCDORevisionManager revisionManager = (InternalCDORevisionManager)session.getRevisionManager();
    InternalCDORevision completeRevision = revisionManager.getRevision(companyID, branchPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);

    clearCache(revisionManager);
    InternalCDORevision partialRevision = revisionManager.getRevision(companyID, branchPoint, 1, CDORevision.DEPTH_NONE, true);
    EStructuralFeature categoriesFeature = getModel1Package().getCompany_Categories();
    assertFalse(partialRevision.getListOrNull(categoriesFeature).isFullyLoaded());
    assertFalse(partialRevision.getListOrNull(categoriesFeature).isLoadedAt(1));

    SignalCounter signalCounter = new SignalCounter(((CDONet4jSession)session).options().getNet4jProtocol());

    InternalCDORevision changedRevision = completeRevision.copy();
    EStructuralFeature nameFeature = getModel1Package().getAddress_Name();
    changedRevision.setValue(nameFeature, "changed");
    changedRevision.setVersion(completeRevision.getVersion() + 1);

    Method method = CDOSessionImpl.class.getDeclaredMethod("createChangeSetData", Set.class, CDORevisionProvider.class, CDORevisionProvider.class);
    method.setAccessible(true);
    CDOChangeSetData changeSetData = (CDOChangeSetData)method.invoke(session, Collections.singleton(companyID), (CDORevisionProvider)id -> partialRevision,
        (CDORevisionProvider)id -> changedRevision);

    assertEquals(1, signalCounter.getCountFor(LoadChunkRequest.class));
    assertTrue(partialRevision.getListOrNull(categoriesFeature).isFullyLoaded());
    assertEquals(1, changeSetData.getChangedObjects().size());
    CDORevisionDelta delta = (CDORevisionDelta)changeSetData.getChangedObject(companyID);
    assertEquals(CDOFeatureDelta.Type.SET, delta.getFeatureDelta(nameFeature).getType());
    assertEquals(null, delta.getFeatureDelta(categoriesFeature));

    transaction.close();
    signalCounter.dispose();
    session.close();
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testPCL026MaterializationRequestEconomics() throws Exception
  {
    CDOSession writerSession = openSession();
    CDOTransaction writer = writerSession.openTransaction();
    CDOResource resource = writer.createResource(getResourcePath("/pcl026"));
    GenListOfInt value = Model5Factory.eINSTANCE.createGenListOfInt();
    for (int i = 0; i < 40; i++)
    {
      value.getElements().add(i);
    }

    resource.getContents().add(value);
    writer.commit();
    CDOID id = CDOUtil.getCDOObject(value).cdoID();
    CDOBranchPoint branchPoint = writer.getBranch().getHead();
    writer.close();
    writerSession.close();
    clearCache(getRepository().getRevisionManager());

    EStructuralFeature feature = getModel5Package().getGenListOfInt_Elements();

    CDOSession oneRunSession = openSession();
    oneRunSession.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 1));
    SignalCounter oneRunCounter = new SignalCounter(((CDONet4jSession)oneRunSession).options().getNet4jProtocol());
    CDOView oneRunView = oneRunSession.openView();
    GenListOfInt oneRunValue = (GenListOfInt)oneRunView.getResource(getResourcePath("/pcl026")).getContents().get(0);
    InternalCDORevision oneRunRevision = (InternalCDORevision)CDOUtil.getCDOObject(oneRunValue).cdoRevision();
    CDOList oneRunList = oneRunRevision.getListOrNull(feature);
    assertEquals(1, countUnloadedRuns(oneRunList));
    oneRunCounter.clearCounts();
    new CDOCollectionLoadingResolver(oneRunSession).resolveAllProxies(oneRunRevision, feature);
    assertEquals(1, oneRunCounter.getCountFor(LoadChunkRequest.class));
    assertTrue(oneRunList.isFullyLoaded());
    oneRunCounter.dispose();
    oneRunView.close();
    oneRunSession.close();
    clearCache(getRepository().getRevisionManager());

    CDOSession multiRunSession = openSession();
    multiRunSession.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 1));
    SignalCounter multiRunCounter = new SignalCounter(((CDONet4jSession)multiRunSession).options().getNet4jProtocol());
    CDOView multiRunView = multiRunSession.openView();
    GenListOfInt multiRunValue = (GenListOfInt)multiRunView.getResource(getResourcePath("/pcl026")).getContents().get(0);
    InternalCDORevision multiRunRevision = (InternalCDORevision)CDOUtil.getCDOObject(multiRunValue).cdoRevision();
    CDOList multiRunList = multiRunRevision.getListOrNull(feature);
    multiRunValue.getElements().get(5);
    multiRunValue.getElements().get(15);
    multiRunValue.getElements().get(25);
    int missingRuns = countUnloadedRuns(multiRunList);
    assertEquals(4, missingRuns);
    multiRunCounter.clearCounts();
    new CDOCollectionLoadingResolver(multiRunSession).resolveAllProxies(multiRunRevision, feature);
    assertEquals(1, multiRunCounter.getCountFor(LoadChunkRequest.class));
    assertTrue(multiRunList.isFullyLoaded());
    for (int i = 0; i < 40; i++)
    {
      assertEquals(i, multiRunValue.getElements().get(i).intValue());
    }

    multiRunCounter.clearCounts();
    new CDOCollectionLoadingResolver(multiRunSession).resolveAllProxies(multiRunRevision, feature);
    assertEquals(0, multiRunCounter.getCountFor(LoadChunkRequest.class));
    multiRunValue.getElements().get(15);
    assertEquals(0, multiRunCounter.getCountFor(LoadChunkRequest.class));

    InternalCDORevision unchunkedRevision = ((InternalCDORevisionManager)multiRunSession.getRevisionManager()).getRevision(id, branchPoint,
        CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
    multiRunCounter.clearCounts();
    new CDOCollectionLoadingResolver(multiRunSession).resolveAllProxies(unchunkedRevision, feature);
    assertEquals(0, multiRunCounter.getCountFor(LoadChunkRequest.class));

    multiRunCounter.dispose();
    multiRunView.close();
    multiRunSession.close();
  }

  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testPCL027LegacyExecutionOverrideIsRejected() throws Exception
  {
    createInitialList();

    AtomicInteger resolveProxyCalls = new AtomicInteger();
    CDOSession session = openSession();
    CDOCollectionLoadingPolicyImpl policy = new CDOCollectionLoadingPolicyImpl(1, 1)
    {
      @Override
      @Deprecated
      public Object resolveProxy(CDORevision revision, EStructuralFeature feature, int accessIndex, int serverIndex)
      {
        resolveProxyCalls.incrementAndGet();
        return super.resolveProxy(revision, feature, accessIndex, serverIndex);
      }
    };
    try
    {
      session.options().setCollectionLoadingPolicy(policy);
      fail("A legacy execution override must be rejected during installation");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("collection loading configuration"));
    }

    assertEquals(0, resolveProxyCalls.get());
    session.close();
  }

  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testPCL027LegacyResolveAllExecutionOverrideIsRejected()
  {
    CDOSession session = openSession();
    CDOCollectionLoadingPolicyImpl policy = new CDOCollectionLoadingPolicyImpl(1, 1)
    {
      @Override
      @Deprecated
      public void resolveAllProxies(CDORevision revision, EStructuralFeature feature)
      {
      }
    };
    try
    {
      session.options().setCollectionLoadingPolicy(policy);
      fail("A legacy resolveAllProxies override must be rejected during installation");
    }
    catch (IllegalArgumentException expected)
    {
      assertTrue(expected.getMessage().contains("collection loading configuration"));
    }
    session.close();
  }

  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testPCL027DeclarativeLegacySubclassIsAccepted()
  {
    CDOSession session = openSession();
    CDOCollectionLoadingPolicyImpl policy = new CDOCollectionLoadingPolicyImpl(2, 3)
    {
      @Override
      public int getResolveChunkSize(CDORevision revision, EStructuralFeature feature)
      {
        return 1;
      }
    };
    session.options().setCollectionLoadingPolicy(policy);
    assertSame(policy, session.options().getCollectionLoadingPolicy());
    session.close();
  }

  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testPCL027LegacyPolicyControlsRemoteResolution() throws Exception
  {
    createInitialList();

    AtomicInteger initialCalls = new AtomicInteger();
    AtomicInteger resolveCalls = new AtomicInteger();
    CDOCollectionLoadingPolicy policy = new CDOCollectionLoadingPolicyImpl(1, 1)
    {
      @Override
      public int getInitialChunkSize(CDORevision revision, EStructuralFeature feature)
      {
        initialCalls.incrementAndGet();
        return 1;
      }

      @Override
      public int getResolveChunkSize(CDORevision revision, EStructuralFeature feature)
      {
        resolveCalls.incrementAndGet();
        return 1;
      }
    };

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(policy);
    GenListOfInt list = (GenListOfInt)session.openView().getResource(getResourcePath(RESOURCE_PATH)).getContents().get(0);
    InternalCDORevision revision = (InternalCDORevision)CDOUtil.getCDOObject(list).cdoRevision();
    CDOList values = revision.getListOrNull(getModel5Package().getGenListOfInt_Elements());
    assertEquals(getModelConfig().isLegacy(), values.isLoadedAt(1));
    assertTrue(initialCalls.get() > 0);

    list.getElements().get(1);
    assertTrue(resolveCalls.get() > 0);
    session.close();
  }

  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testPCL030HistoricalChunkSizeEntryPoint() throws Exception
  {
    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 1));
    CDOTransaction writer = session.openTransaction();
    CDOResource resource = writer.createResource(getResourcePath("/pcl030-int"));
    Company value = getModel1Factory().createCompany();
    value.getCategories().add(getModel1Factory().createCategory());
    value.getCategories().add(getModel1Factory().createCategory());
    value.getCategories().add(getModel1Factory().createCategory());
    resource.getContents().add(value);
    writer.commit();
    CDOID id = CDOUtil.getCDOObject(value).cdoID();
    CDOBranchPoint branchPoint = writer.getBranch().getHead();

    EStructuralFeature feature = getModel1Package().getCompany_Categories();
    InternalCDORevisionManager revisionManager = (InternalCDORevisionManager)session.getRevisionManager();
    revisionManager.getRevision(id, branchPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
    clearCache(revisionManager);
    InternalCDORevision partial = (InternalCDORevision)session.getRevisionManager().getRevision(id, branchPoint, 1, CDORevision.DEPTH_NONE, true);
    assertFalse(partial.getListOrNull(feature).isLoadedAt(1));

    clearCache(revisionManager);
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(CDORevision.UNCHUNKED, CDORevision.UNCHUNKED));
    InternalCDORevision unchunked = (InternalCDORevision)session.getRevisionManager().getRevision(id, branchPoint, CDORevision.UNCHUNKED,
        CDORevision.DEPTH_NONE, true);
    assertTrue(unchunked.getListOrNull(feature).isLoadedAt(2));
    session.close();
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testModernInitialCollectionLoadingUsesFeatureSizes() throws Exception
  {
    CDOSession writerSession = openSession();
    CDOTransaction writer = writerSession.openTransaction();
    CDOResource resource = writer.createResource(getResourcePath("/modern-initial"));
    Company company = getModel1Factory().createCompany();
    for (int i = 0; i < 120; i++)
    {
      company.getCategories().add(getModel1Factory().createCategory());
      company.getCustomers().add(getModel1Factory().createCustomer());
    }

    resource.getContents().add(company);
    writer.commit();
    writer.close();
    writerSession.close();
    clearCache(getRepository().getRevisionManager());

    EStructuralFeature categories = getModel1Package().getCompany_Categories();
    EStructuralFeature customers = getModel1Package().getCompany_Customers();
    Map<EModelElement, ChunkConfig> overrides = new LinkedHashMap<>();
    overrides.put(categories, new ChunkConfig(10, ChunkConfig.INHERIT));
    overrides.put(customers, new ChunkConfig(100, ChunkConfig.INHERIT));

    CDOSession session = openSession();
    session.options().setCollectionLoadingConfig(new CDOCollectionLoadingConfig(new ChunkConfig(ChunkConfig.INHERIT, ChunkConfig.INHERIT), overrides));

    CDOView view = session.openView();
    Company loadedCompany = (Company)view.getResource(getResourcePath("/modern-initial")).getContents().get(0);
    InternalCDORevision revision = (InternalCDORevision)CDOUtil.getCDOObject(loadedCompany).cdoRevision();

    CDOList categoryList = revision.getListOrNull(categories);
    assertEquals(120, categoryList.size());
    assertTrue(categoryList.isLoadedAt(9));
    assertFalse(categoryList.isLoadedAt(10));

    CDOList customerList = revision.getListOrNull(customers);
    assertEquals(120, customerList.size());
    assertTrue(customerList.isLoadedAt(99));
    assertFalse(customerList.isLoadedAt(100));

    view.close();
    session.close();
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testModernInitialCollectionLoadingByVersion() throws Exception
  {
    CDOSession writerSession = openSession();
    CDOTransaction writer = writerSession.openTransaction();
    CDOResource resource = writer.createResource(getResourcePath("/modern-by-version"));
    GenListOfInt value = Model5Factory.eINSTANCE.createGenListOfInt();
    value.getElements().addAll(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
    resource.getContents().add(value);
    writer.commit();
    CDOID id = CDOUtil.getCDOObject(value).cdoID();
    CDOBranchPoint branchPoint = writer.getBranch().getHead();
    writer.close();
    writerSession.close();
    clearCache(getRepository().getRevisionManager());

    EStructuralFeature feature = getModel5Package().getGenListOfInt_Elements();
    CDOSession session = openSession();
    session.options().setCollectionLoadingConfig(new CDOCollectionLoadingConfig(new ChunkConfig(ChunkConfig.INHERIT, ChunkConfig.INHERIT),
        Collections.<EModelElement, ChunkConfig> singletonMap(feature, new ChunkConfig(5, ChunkConfig.INHERIT))));

    InternalCDORevision revision = ((InternalCDORevisionManager)session.getRevisionManager()).getRevisionByVersion(id, branchPoint.getBranch().getVersion(1),
        new Request.Config(LookupMode.CACHE_THEN_LOADER, CDORevision.DEPTH_NONE, false, 1));
    CDOList list = revision.getListOrNull(feature);
    assertTrue(list.isLoadedAt(4));
    assertFalse(list.isLoadedAt(5));
    session.close();
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testModernInitialCollectionLoadingAllAndNone() throws Exception
  {
    createInitialList();
    EStructuralFeature feature = getModel5Package().getGenListOfInt_Elements();

    CDOSession allSession = openSession();
    allSession.options().setCollectionLoadingConfig(new CDOCollectionLoadingConfig(new ChunkConfig(ChunkConfig.INHERIT, ChunkConfig.INHERIT),
        Collections.<EModelElement, ChunkConfig> singletonMap(feature, new ChunkConfig(ChunkConfig.ALL, ChunkConfig.INHERIT))));
    CDOView allView = allSession.openView();
    GenListOfInt allValue = (GenListOfInt)allView.getResource(getResourcePath(RESOURCE_PATH)).getContents().get(0);
    InternalCDORevision allRevision = (InternalCDORevision)CDOUtil.getCDOObject(allValue).cdoRevision();
    assertTrue(allRevision.getListOrNull(feature).isFullyLoaded());
    allView.close();
    allSession.close();

    CDOSession noneSession = openSession();
    noneSession.options().setCollectionLoadingConfig(new CDOCollectionLoadingConfig(new ChunkConfig(ChunkConfig.INHERIT, ChunkConfig.INHERIT),
        Collections.<EModelElement, ChunkConfig> singletonMap(feature, new ChunkConfig(ChunkConfig.NONE, ChunkConfig.INHERIT))));
    CDOView noneView = noneSession.openView();
    GenListOfInt noneValue = (GenListOfInt)noneView.getResource(getResourcePath(RESOURCE_PATH)).getContents().get(0);
    InternalCDORevision noneRevision = (InternalCDORevision)CDOUtil.getCDOObject(noneValue).cdoRevision();
    CDOList noneList = noneRevision.getListOrNull(feature);
    assertEquals(9, noneList.size());
    assertFalse(noneList.isLoadedAt(0));
    noneView.close();
    noneSession.close();
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testModernSinglePositionResolutionUsesEffectiveResolveSize() throws Exception
  {
    createInitialList();
    EStructuralFeature feature = getModel5Package().getGenListOfInt_Elements();

    CDOSession noneSession = openSession();
    noneSession.options().setCollectionLoadingConfig(new CDOCollectionLoadingConfig(new ChunkConfig(ChunkConfig.NONE, ChunkConfig.NONE),
        Collections.<EModelElement, ChunkConfig> singletonMap(feature, new ChunkConfig(ChunkConfig.NONE, ChunkConfig.NONE))));
    GenListOfInt noneValue = (GenListOfInt)noneSession.openView().getResource(getResourcePath(RESOURCE_PATH)).getContents().get(0);
    CDOList noneList = ((InternalCDORevision)CDOUtil.getCDOObject(noneValue).cdoRevision()).getListOrNull(feature);
    noneValue.getElements().get(4);
    assertTrue(noneList.isLoadedAt(4));
    assertFalse(noneList.isLoadedAt(3));
    assertFalse(noneList.isLoadedAt(5));
    noneSession.close();

    CDOSession oneSession = openSession();
    oneSession.options().setCollectionLoadingConfig(new CDOCollectionLoadingConfig(new ChunkConfig(ChunkConfig.NONE, ChunkConfig.NONE),
        Collections.<EModelElement, ChunkConfig> singletonMap(feature, new ChunkConfig(ChunkConfig.NONE, 1))));
    GenListOfInt oneValue = (GenListOfInt)oneSession.openView().getResource(getResourcePath(RESOURCE_PATH)).getContents().get(0);
    CDOList oneList = ((InternalCDORevision)CDOUtil.getCDOObject(oneValue).cdoRevision()).getListOrNull(feature);
    oneValue.getElements().get(4);
    assertTrue(oneList.isLoadedAt(4));
    assertFalse(oneList.isLoadedAt(3));
    assertFalse(oneList.isLoadedAt(5));
    oneSession.close();

    CDOSession twoSession = openSession();
    twoSession.options().setCollectionLoadingConfig(new CDOCollectionLoadingConfig(new ChunkConfig(ChunkConfig.NONE, ChunkConfig.NONE),
        Collections.<EModelElement, ChunkConfig> singletonMap(feature, new ChunkConfig(ChunkConfig.NONE, 2))));
    GenListOfInt twoValue = (GenListOfInt)twoSession.openView().getResource(getResourcePath(RESOURCE_PATH)).getContents().get(0);
    CDOList twoList = ((InternalCDORevision)CDOUtil.getCDOObject(twoValue).cdoRevision()).getListOrNull(feature);
    twoValue.getElements().get(4);
    assertTrue(twoList.isLoadedAt(3));
    assertTrue(twoList.isLoadedAt(4));
    assertFalse(twoList.isLoadedAt(2));
    assertFalse(twoList.isLoadedAt(5));
    twoSession.close();

    CDOSession threeSession = openSession();
    threeSession.options().setCollectionLoadingConfig(new CDOCollectionLoadingConfig(new ChunkConfig(ChunkConfig.NONE, ChunkConfig.NONE),
        Collections.<EModelElement, ChunkConfig> singletonMap(feature, new ChunkConfig(ChunkConfig.NONE, 3))));
    GenListOfInt threeValue = (GenListOfInt)threeSession.openView().getResource(getResourcePath(RESOURCE_PATH)).getContents().get(0);
    CDOList threeList = ((InternalCDORevision)CDOUtil.getCDOObject(threeValue).cdoRevision()).getListOrNull(feature);
    threeValue.getElements().get(4);
    assertTrue(threeList.isLoadedAt(3));
    assertTrue(threeList.isLoadedAt(4));
    assertTrue(threeList.isLoadedAt(5));
    assertFalse(threeList.isLoadedAt(2));
    assertFalse(threeList.isLoadedAt(6));
    threeSession.close();

    CDOSession allSession = openSession();
    allSession.options().setCollectionLoadingConfig(new CDOCollectionLoadingConfig(new ChunkConfig(ChunkConfig.NONE, ChunkConfig.NONE),
        Collections.<EModelElement, ChunkConfig> singletonMap(feature, new ChunkConfig(ChunkConfig.NONE, ChunkConfig.ALL))));
    GenListOfInt allValue = (GenListOfInt)allSession.openView().getResource(getResourcePath(RESOURCE_PATH)).getContents().get(0);
    CDOList allList = ((InternalCDORevision)CDOUtil.getCDOObject(allValue).cdoRevision()).getListOrNull(feature);
    allValue.getElements().get(4);
    assertTrue(allList.isFullyLoaded());
    allSession.close();
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testModernInitialCollectionLoadingDisabledPreservesScalarBehavior() throws Exception
  {
    createInitialList();

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 1));
    CDOView view = session.openView();
    GenListOfInt value = (GenListOfInt)view.getResource(getResourcePath(RESOURCE_PATH)).getContents().get(0);
    InternalCDORevision revision = (InternalCDORevision)CDOUtil.getCDOObject(value).cdoRevision();
    CDOList list = revision.getListOrNull(getModel5Package().getGenListOfInt_Elements());
    assertTrue(list.isLoadedAt(0));
    assertFalse(list.isLoadedAt(1));
    view.close();
    session.close();
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testPCL030FetchRulePrefetchedRevisionUsesModernConfig() throws Exception
  {
    CDOSession writerSession = openSession();
    CDOTransaction writer = writerSession.openTransaction();
    CDOResource resource = writer.createResource(getResourcePath("/pcl030-fetch"));
    Company company = getModel1Factory().createCompany();
    Category category = getModel1Factory().createCategory();
    for (int i = 0; i < 4; i++)
    {
      category.getProducts().add(getModel1Factory().createProduct1());
    }

    company.getCategories().add(category);
    resource.getContents().add(company);
    writer.commit();

    CDOID companyID = CDOUtil.getCDOObject(company).cdoID();
    CDOID categoryID = CDOUtil.getCDOObject(category).cdoID();
    writer.close();
    writerSession.close();

    clearCache(getRepository().getRevisionManager());

    EStructuralFeature products = getModel1Package().getCategory_Products();
    CDOSession session = openSession();
    session.options().setCollectionLoadingConfig(new CDOCollectionLoadingConfig(new ChunkConfig(ChunkConfig.INHERIT, ChunkConfig.INHERIT),
        Collections.<EModelElement, ChunkConfig> singletonMap(products, new ChunkConfig(1, 1))));
    SignalCounter counter = new SignalCounter(((CDONet4jSession)session).options().getNet4jProtocol());
    ((CDOSessionImpl)session).setFetchRuleManager(new CDOFetchRuleManager()
    {
      @Override
      public CDOID getContext()
      {
        return companyID;
      }

      @Override
      public List<CDOFetchRule> getFetchRules(Collection<CDOID> ids)
      {
        if (ids.contains(companyID))
        {
          CDOFetchRule rule = new CDOFetchRule(getModel1Package().getCompany());
          rule.addFeature(getModel1Package().getCompany_Categories());
          return Collections.singletonList(rule);
        }

        return null;
      }
    });

    CDOView view = session.openView();
    Company fetchedCompany = (Company)view.getObject(companyID);
    int loadRevisionsCount = counter.getCountFor(LoadRevisionsRequest.class);
    assertTrue(loadRevisionsCount > 0);
    Category prefetchedCategory = fetchedCompany.getCategories().get(0);
    assertEquals(loadRevisionsCount, counter.getCountFor(LoadRevisionsRequest.class));
    InternalCDORevision prefetched = (InternalCDORevision)CDOUtil.getCDOObject(prefetchedCategory).cdoRevision();
    assertFalse(prefetched.getListOrNull(products).isLoadedAt(1));
    assertEquals(categoryID, CDOUtil.getCDOObject(prefetchedCategory).cdoID());
    view.close();
    counter.dispose();
    session.close();
  }

  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testModernInitialCollectionLoadingFetchRuleAdditionalRevision() throws Exception
  {
    CDOSession writerSession = openSession();
    CDOTransaction writer = writerSession.openTransaction();
    CDOResource resource = writer.createResource(getResourcePath("/modern-fetch"));
    Company company = getModel1Factory().createCompany();
    Category category = getModel1Factory().createCategory();
    for (int i = 0; i < 4; i++)
    {
      category.getProducts().add(getModel1Factory().createProduct1());
    }

    company.getCategories().add(category);
    resource.getContents().add(company);
    writer.commit();
    CDOID companyID = CDOUtil.getCDOObject(company).cdoID();
    writer.close();
    writerSession.close();
    clearCache(getRepository().getRevisionManager());

    EStructuralFeature products = getModel1Package().getCategory_Products();
    CDOSession session = openSession();
    session.options().setCollectionLoadingConfig(new CDOCollectionLoadingConfig(new ChunkConfig(ChunkConfig.INHERIT, ChunkConfig.INHERIT),
        Collections.<EModelElement, ChunkConfig> singletonMap(products, new ChunkConfig(3, ChunkConfig.INHERIT))));
    ((CDOSessionImpl)session).setFetchRuleManager(new CDOFetchRuleManager()
    {
      @Override
      public CDOID getContext()
      {
        return companyID;
      }

      @Override
      public List<CDOFetchRule> getFetchRules(Collection<CDOID> ids)
      {
        if (ids.contains(companyID))
        {
          CDOFetchRule rule = new CDOFetchRule(getModel1Package().getCompany());
          rule.addFeature(getModel1Package().getCompany_Categories());
          return Collections.singletonList(rule);
        }

        return null;
      }
    });

    CDOView view = session.openView();
    Company loadedCompany = (Company)view.getResource(getResourcePath("/modern-fetch")).getContents().get(0);
    Category loadedCategory = loadedCompany.getCategories().get(0);
    InternalCDORevision revision = (InternalCDORevision)CDOUtil.getCDOObject(loadedCategory).cdoRevision();
    CDOList productList = revision.getListOrNull(products);
    assertTrue(productList.isLoadedAt(2));
    assertFalse(productList.isLoadedAt(3));
    view.close();
    session.close();
  }

  private static int countUnloadedRuns(CDOList list)
  {
    int result = 0;
    boolean inRun = false;
    for (int i = 0; i < list.size(); i++)
    {
      boolean unloaded = !list.isLoadedAt(i);
      if (unloaded && !inRun)
      {
        ++result;
      }

      inRun = unloaded;
    }

    return result;
  }

  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testReadNative() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");
      resource.getContents().add(customer);

      for (int i = 0; i < 100; i++)
      {
        SalesOrder salesOrder = getModel1Factory().createSalesOrder();
        salesOrder.setId(i);
        salesOrder.setCustomer(customer);
        resource.getContents().add(salesOrder);
      }

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(10, 10));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));

    Customer customer = (Customer)resource.getContents().get(0);
    EList<SalesOrder> salesOrders = customer.getSalesOrders();
    int i = 0;
    for (Iterator<SalesOrder> it = salesOrders.iterator(); it.hasNext();)
    {
      IOUtil.OUT().println(i++);
      SalesOrder salesOrder = it.next();
      IOUtil.OUT().println(salesOrder);
    }
  }

  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testWriteNative() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");
      resource.getContents().add(customer);

      for (int i = 0; i < 100; i++)
      {
        SalesOrder salesOrder = getModel1Factory().createSalesOrder();
        salesOrder.setId(i);
        salesOrder.setCustomer(customer);
        resource.getContents().add(salesOrder);
      }

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(10, 10));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));

    Customer customer = (Customer)resource.getContents().get(0);
    EList<SalesOrder> salesOrders = customer.getSalesOrders();
    for (int i = 50; i < 70; i++)
    {
      SalesOrder salesOrder = getModel1Factory().createSalesOrder();
      salesOrder.setId(i + 1000);
      resource.getContents().add(salesOrder);
      salesOrders.set(i, salesOrder);
    }

    transaction.commit();
  }

  public void testChunkWithTemporaryObject() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      Customer customer = getModel1Factory().createCustomer();
      customer.setName("customer");
      resource.getContents().add(customer);

      transaction.commit();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    transaction.options().setRevisionPrefetchingPolicy(CDOUtil.createRevisionPrefetchingPolicy(10));
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));

    Customer customer = getModel1Factory().createCustomer();
    customer.setName("customer");
    resource.getContents().add(customer);
    for (EObject element : resource.getContents())
    {
      msg(element);
    }

    transaction.commit();
  }

  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testReadAfterUpdateBeforeCommit() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      for (int i = 0; i < 100; i++)
      {
        msg("Creating salesOrder" + i);
        SalesOrder salesOrder = getModel1Factory().createSalesOrder();
        salesOrder.setId(i);
        resource.getContents().add(salesOrder);
      }

      transaction.commit();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(10, 10));

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));

    for (int i = 50; i < 70; i++)
    {
      SalesOrder salesOrder = getModel1Factory().createSalesOrder();
      salesOrder.setId(i + 1000);
      resource.getContents().add(i, salesOrder);
    }

    for (int i = 70; i < 120; i++)
    {
      SalesOrder saleOrders = (SalesOrder)resource.getContents().get(i);
      assertEquals(i - 20, saleOrders.getId());
    }

    transaction.commit();
  }

  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testReadAfterUpdateAfterCommit() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));

      for (int i = 0; i < 100; i++)
      {
        msg("Creating salesOrder" + i);
        SalesOrder salesOrder = getModel1Factory().createSalesOrder();
        salesOrder.setId(i);
        resource.getContents().add(salesOrder);
      }

      transaction.commit();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(10, 10));

    msg("Creating resource");
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath("/test1"));

    for (int i = 50; i < 70; i++)
    {
      SalesOrder salesOrder = getModel1Factory().createSalesOrder();
      salesOrder.setId(i + 1000);
      resource.getContents().add(i, salesOrder);
    }

    transaction.commit();

    for (int i = 70; i < 120; i++)
    {
      SalesOrder saleOrders = (SalesOrder)resource.getContents().get(i);
      assertEquals(i - 20, saleOrders.getId());
    }
  }

  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testPartiallyLoadedAdd() throws CommitException
  {
    createInitialList();

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(3, 1));
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.getResource(getResourcePath(RESOURCE_PATH));

    GenListOfInt list = (GenListOfInt)resource.getContents().get(0);
    list.getElements().add(9);

    tx.commit();
    session.close();
    clearCache(getRepository().getRevisionManager());

    testListResult(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
  }

  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testPartiallyLoadedAddAtIndex() throws CommitException
  {
    createInitialList();

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(3, 1));
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.getResource(getResourcePath(RESOURCE_PATH));

    GenListOfInt list = (GenListOfInt)resource.getContents().get(0);
    list.getElements().add(5, 9);

    tx.commit();
    tx.close();
    session.close();
    clearCache(getRepository().getRevisionManager());

    testListResult(0, 1, 2, 3, 4, 9, 5, 6, 7, 8);
  }

  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testPartiallyLoadedSet() throws CommitException
  {
    createInitialList();

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(3, 1));
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.getResource(getResourcePath(RESOURCE_PATH));

    GenListOfInt list = (GenListOfInt)resource.getContents().get(0);
    list.getElements().set(5, 9);

    tx.commit();
    tx.close();
    session.close();
    clearCache(getRepository().getRevisionManager());

    testListResult(0, 1, 2, 3, 4, 9, 6, 7, 8);
  }

  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testPartiallyLoadedRemoveIndex() throws CommitException
  {
    createInitialList();

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(3, 1));
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.getResource(getResourcePath(RESOURCE_PATH));

    GenListOfInt list = (GenListOfInt)resource.getContents().get(0);
    list.getElements().remove(5);

    tx.commit();
    tx.close();
    session.close();
    clearCache(getRepository().getRevisionManager());

    testListResult(0, 1, 2, 3, 4, 6, 7, 8);
  }

  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testPCL013PartiallyLoadedLocalStructuralTransitions() throws Exception
  {
    createInitialList();

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(3, 1));
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE_PATH));
    GenListOfInt list = (GenListOfInt)resource.getContents().get(0);

    list.getElements().add(0, 9);
    assertEquals(Arrays.asList(9, 0, 1, 2, 3, 4, 5, 6, 7, 8), list.getElements());

    list.getElements().remove(0);
    assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8), list.getElements());

    list.getElements().move(0, list.getElements().get(8));
    assertEquals(Arrays.asList(8, 0, 1, 2, 3, 4, 5, 6, 7), list.getElements());

    transaction.close();
    session.close();
  }

  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testPCL013PartiallyLoadedRemoteStructuralTransition() throws Exception
  {
    createInitialList();

    CDOSession readerSession = openSession();
    readerSession.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(3, 1));
    CDOTransaction reader = readerSession.openTransaction();
    GenListOfInt readerList = (GenListOfInt)reader.getResource(getResourcePath(RESOURCE_PATH)).getContents().get(0);

    CDOSession writerSession = openSession();
    CDOTransaction writer = writerSession.openTransaction();
    GenListOfInt writerList = (GenListOfInt)writer.getResource(getResourcePath(RESOURCE_PATH)).getContents().get(0);
    writerList.getElements().add(0, 9);
    CDOCommitInfo commitInfo = writer.commit();

    assertTrue(readerSession.waitForUpdate(commitInfo.getTimeStamp(), DEFAULT_TIMEOUT));
    assertNoTimeout(() -> readerList.getElements().size() == 10);
    assertEquals(Arrays.asList(9, 0, 1, 2, 3, 4, 5, 6, 7, 8), readerList.getElements());

    reader.close();
    readerSession.close();
    writer.close();
    writerSession.close();
  }

  /**
   * Verifies that a direct NEW containment object composes with partial loading, local index
   * changes, savepoint rollback, and successful commit without being treated as an unloaded value.
   *
   * @throws Exception if the remote protocol lifecycle fails.
   */
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testPCL033DirtyPartialCollectionLifecycle() throws Exception
  {
    createInitialCompanyWithCategories();

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 1));
    CDOTransaction transaction = session.openTransaction();
    Company company = (Company)transaction.getResource(getResourcePath("/pcl033")).getContents().get(0);
    EList<Category> categories = company.getCategories();
    InternalCDORevision revision = (InternalCDORevision)CDOUtil.getCDOObject(company).cdoRevision();
    CDOList revisionList = revision.getListOrNull(getModel1Package().getCompany_Categories());

    assertFalse(revisionList.isFullyLoaded());
    assertTrue(revisionList.isLoadedAt(0));
    assertFalse(revisionList.isLoadedAt(1));

    Category inserted = getModel1Factory().createCategory();
    inserted.setName("new");
    categories.add(0, inserted);
    assertSame(inserted, categories.get(0));
    assertEquals(CDOState.NEW, CDOUtil.getCDOObject(inserted).cdoState());

    // The access index (4) now maps to server index 3 because of the local insertion.
    assertEquals("category-3", categories.get(4).getName());
    assertTrue(revisionList.isLoadedAt(4));

    CDOUserSavepoint savepoint = transaction.setSavepoint();
    assertEquals("category-5", categories.get(6).getName());

    Category rolledBack = getModel1Factory().createCategory();
    rolledBack.setName("rolled-back");
    categories.add(1, rolledBack);
    savepoint.rollback();

    assertEquals(9, categories.size());
    assertSame(inserted, categories.get(0));
    assertEquals("category-3", categories.get(4).getName());
    assertEquals("category-5", categories.get(6).getName());

    transaction.commit();

    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(inserted).cdoState());
    assertFalse(CDOUtil.getCDOObject(inserted).cdoID().isTemporary());
    assertEquals("new", categories.get(0).getName());
    assertEquals("category-3", categories.get(4).getName());
    assertEquals("category-5", categories.get(6).getName());

    transaction.close();
    session.close();
  }

  private void createInitialCompanyWithCategories() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/pcl033"));
    Company company = getModel1Factory().createCompany();

    for (int i = 0; i < 8; i++)
    {
      Category category = getModel1Factory().createCategory();
      category.setName("category-" + i);
      company.getCategories().add(category);
    }

    resource.getContents().add(company);
    transaction.commit();
    transaction.close();
    session.close();
    clearCache(getRepository().getRevisionManager());
  }

  private void createInitialList() throws CommitException
  {
    GenListOfInt list = getModel5Factory().createGenListOfInt();
    list.getElements().addAll(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));

    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(list);

    tx.commit();
    session.close();
    clearCache(getRepository().getRevisionManager());
  }

  private void testListResult(Integer... expected)
  {
    List<Integer> expectedList = Arrays.asList(expected);

    CDOSession session = openSession();
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath(RESOURCE_PATH));

    EList<Integer> actualList = ((GenListOfInt)resource.getContents().get(0)).getElements();

    assertEquals("List sizes differ", expectedList.size(), actualList.size());

    for (int index = 0; index < expectedList.size(); index++)
    {
      assertEquals("Map.Entry at index " + index + " differs", expectedList.get(index), actualList.get(index));
    }

    view.close();
    session.close();
  }

  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testRemove() throws CommitException
  {
    // Init model
    Company company = getModel1Factory().createCompany();
    addCategoryAndProducts(company, "Software");

    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.createResource(getResourcePath(RESOURCE_PATH));
    resource.getContents().add(company);

    tx.commit();
    tx.close();
    session.close();
    clearCache(getRepository().getRevisionManager());

    // Test session
    session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(3, 3));

    tx = session.openTransaction();
    resource = tx.getResource(getResourcePath(RESOURCE_PATH));
    company = (Company)resource.getContents().get(0);

    Category software = company.getCategories().get(0);
    software.getProducts().remove(15);

    tx.commit();
  }

  private void addCategoryAndProducts(Company company, String name)
  {
    Category category = addCategory(company, name);

    for (int i = 0; i < 20; i++)
    {
      Product1 product = getModel1Factory().createProduct1();
      product.setName(name + "-" + i);
      category.getProducts().add(product);
    }
  }

  private Category addCategory(Company company, String name)
  {
    Category category = getModel1Factory().createCategory();
    category.setName(name);
    company.getCategories().add(category);
    return category;
  }

  /**
   * Bug 502932.
   */
  @Requires("DB")
  @SuppressWarnings("deprecation") // Testing legacy CollectionLoadingPolicy.
  public void testEnsureChunk() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/test1"));
      Company company1 = getModel1Factory().createCompany();
      company1.setName("company1");
      resource.getContents().add(company1);

      Company company2 = getModel1Factory().createCompany();
      company2.setName("company2");
      resource.getContents().add(company2);
      for (int i = 0; i < 3000; i++)
      {
        Customer customer = getModel1Factory().createCustomer();
        customer.setName("customer" + i);
        company2.getCustomers().add(customer);
      }

      transaction.commit();
      session.close();
    }

    clearCache(getRepository().getRevisionManager());

    CDOSession session = openSession();
    session.options().setCollectionLoadingPolicy(CDOUtil.createCollectionLoadingPolicy(1, 10));
    CDOView view = session.openView();
    CDOResource resource = view.getResource(getResourcePath("/test1"));

    Company company1 = (Company)resource.getContents().get(0);
    Company company2 = (Company)resource.getContents().get(1);
    company1.getCustomers();
    company2.getCustomers().get(1);
  }
}
