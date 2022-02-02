/*
 * Copyright (c) 2014-2016, 2019, 2021, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.util.CDOFetchRule;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.net4j.protocol.LoadRevisionsRequest;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.signal.SignalCounter;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Test {@link CDOObject#cdoPrefetch(int)} with branch.
 *
 * @author Esteban Dugueperoux
 */
@Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
@CleanRepositoriesBefore(reason = "to not be disturb by branches created by others tests")
public class Bugzilla_436246_Test extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "test1.model1";

  private static final String B1_BRANCH_NAME = "b1";

  private static final String B11_BRANCH_NAME = "b11";

  private static final int NB_CATEGORY = 10;

  @Override
  public void setUp() throws Exception
  {
    super.setUp();
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath(RESOURCE_NAME));
    Company company = getModel1Factory().createCompany();
    for (int i = 0; i < NB_CATEGORY; i++)
    {
      Category category = getModel1Factory().createCategory();
      company.getCategories().add(category);
    }
    resource.getContents().add(company);
    transaction.commit();
    transaction.close();
    session.close();
  }

  /**
   * Test prefetch on different branches with each having changes.
   */
  public void testCDORevisionPrefetchOnOtherBranchWithChanges() throws Exception
  {
    // Setup
    CDOID companyCDOID = setUpChangesOnBranches();

    // Test
    CDOSession session = openSession();
    ISignalProtocol<?> protocol = ((org.eclipse.emf.cdo.net4j.CDONet4jSession)session).options().getNet4jProtocol();
    SignalCounter signalCounter = new SignalCounter(protocol);

    CDOBranch currentBranch = session.getBranchManager().getMainBranch();
    testCDORevisionFetchWithChangesOnAllBranches(session, currentBranch, signalCounter, companyCDOID, NB_CATEGORY, true);

    currentBranch = currentBranch.getBranch(getBranchName(B1_BRANCH_NAME));
    testCDORevisionFetchWithChangesOnAllBranches(session, currentBranch, signalCounter, companyCDOID, 2 * NB_CATEGORY, true);

    currentBranch = currentBranch.getBranch(B11_BRANCH_NAME);
    testCDORevisionFetchWithChangesOnAllBranches(session, currentBranch, signalCounter, companyCDOID, 3 * NB_CATEGORY, true);

    protocol.removeListener(signalCounter);
  }

  /**
   * Test {@link CDOCollectionLoadingPolicy} on different branches with each having changes.
   */
  public void testCDORevisionCDOFetchRuleOnOtherBranchWithChanges() throws Exception
  {
    // Setup
    CDOID companyCDOID = setUpChangesOnBranches();

    // Test
    CDOSession session = openSession();
    InternalCDOSession internalCDOSession = (InternalCDOSession)session;
    CDOFetchRuleManager fetchRuleManager = new CustomCDOFetchRuleManager(companyCDOID);
    internalCDOSession.setFetchRuleManager(fetchRuleManager);
    ISignalProtocol<?> protocol = ((org.eclipse.emf.cdo.net4j.CDONet4jSession)session).options().getNet4jProtocol();
    SignalCounter signalCounter = new SignalCounter(protocol);

    CDOBranch currentBranch = session.getBranchManager().getMainBranch();
    testCDORevisionFetchWithChangesOnAllBranches(session, currentBranch, signalCounter, companyCDOID, NB_CATEGORY, false);

    currentBranch = currentBranch.getBranch(getBranchName(B1_BRANCH_NAME));
    testCDORevisionFetchWithChangesOnAllBranches(session, currentBranch, signalCounter, companyCDOID, 2 * NB_CATEGORY, false);

    currentBranch = currentBranch.getBranch(B11_BRANCH_NAME);
    testCDORevisionFetchWithChangesOnAllBranches(session, currentBranch, signalCounter, companyCDOID, 3 * NB_CATEGORY, false);

    protocol.removeListener(signalCounter);
  }

  private void testCDORevisionFetchWithChangesOnAllBranches(CDOSession session, CDOBranch currentBranch, SignalCounter signalCounter, CDOID companyCDOID,
      int expectedNbCategories, boolean prefetch)
  {
    CDOView view = session.openView(currentBranch);
    assertEquals(0, signalCounter.getCountFor(LoadRevisionsRequest.class));

    String resourcePath = getResourcePath(RESOURCE_NAME);
    List<String> pathSegments = CDOURIUtil.analyzePath(resourcePath);
    CDOResource resource = view.getResource(resourcePath);
    assertEquals(pathSegments.size(), signalCounter.getCountFor(LoadRevisionsRequest.class));

    if (prefetch)
    {
      resource.cdoPrefetch(CDORevision.DEPTH_INFINITE);
    }

    assertEquals(RESOURCE_NAME, resource.getName());

    EObject eObject = resource.getContents().get(0);
    assertTrue(eObject instanceof Company);

    Company company = (Company)eObject;
    assertEquals(pathSegments.size() + 1, signalCounter.getCountFor(LoadRevisionsRequest.class));
    assertEquals(expectedNbCategories, company.getCategories().size());

    view.getRevision(companyCDOID);
    assertEquals(pathSegments.size() + 1, signalCounter.getCountFor(LoadRevisionsRequest.class));

    view.getResourceSet().eAdapters().add(new EContentAdapter());
    assertEquals(pathSegments.size() + 1, signalCounter.getCountFor(LoadRevisionsRequest.class));

    view.close();
    signalCounter.clearCounts();
  }

  private CDOID setUpChangesOnBranches() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch b1Branch = session.getBranchManager().getMainBranch().createBranch(getBranchName(B1_BRANCH_NAME));

    CDOTransaction transaction = session.openTransaction(b1Branch);
    CDOResource resource = transaction.getResource(getResourcePath(RESOURCE_NAME));
    Company company = (Company)resource.getContents().get(0);
    for (int i = 0; i < NB_CATEGORY; i++)
    {
      company.getCategories().add(getModel1Factory().createCategory());
    }
    transaction.commit();
    transaction.close();

    CDOBranch b2Branch = b1Branch.createBranch(B11_BRANCH_NAME);

    transaction = session.openTransaction(b2Branch);
    resource = transaction.getResource(getResourcePath(RESOURCE_NAME));
    company = (Company)resource.getContents().get(0);
    for (int i = 0; i < NB_CATEGORY; i++)
    {
      company.getCategories().add(getModel1Factory().createCategory());
    }
    transaction.commit();
    transaction.close();
    session.close();

    return CDOUtil.getCDOObject(company).cdoID();
  }

  /**
   * Test that after a prefetch of infinite depth of CDORevision on a CDOResource, no more LoadRevisionRequest are sent on the main branch.
   */
  public void testCDORevisionPrefetchOnMainBranch() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    testCDORevisionPrefetchOnBranch(session, mainBranch);
  }

  /**
   * Test that after a prefetch of infinite depth of CDORevision on a CDOResource, no more LoadRevisionRequest are sent on the branch "B1".
   */
  public void testCDORevisionPrefetchOnOtherBranch() throws Exception
  {
    CDOSession session = openSession();
    String newBranchName = getBranchName("B1");
    CDOBranch newBranch = session.getBranchManager().getMainBranch().createBranch(newBranchName);
    testCDORevisionPrefetchOnBranch(session, newBranch);
  }

  private void testCDORevisionPrefetchOnBranch(CDOSession session, CDOBranch cdoBranch) throws Exception
  {
    CDOTransaction view = session.openTransaction(cdoBranch);
    ISignalProtocol<?> protocol = ((org.eclipse.emf.cdo.net4j.CDONet4jSession)session).options().getNet4jProtocol();
    SignalCounter signalCounter = new SignalCounter(protocol);
    assertEquals(0, signalCounter.getCountFor(LoadRevisionsRequest.class));

    String resourcePath = getResourcePath(RESOURCE_NAME);
    List<String> pathSegments = CDOURIUtil.analyzePath(resourcePath);
    CDOResource resource = view.getResource(resourcePath);
    assertEquals(pathSegments.size(), signalCounter.getCountFor(LoadRevisionsRequest.class));

    resource.cdoPrefetch(CDORevision.DEPTH_INFINITE);
    assertEquals(pathSegments.size() + 1, signalCounter.getCountFor(LoadRevisionsRequest.class));

    Company company = (Company)resource.getContents().get(0);
    CDOID companyCDOID = CDOUtil.getCDOObject(company).cdoID();
    assertEquals(pathSegments.size() + 1, signalCounter.getCountFor(LoadRevisionsRequest.class));

    view.getRevision(companyCDOID);
    assertEquals(pathSegments.size() + 1, signalCounter.getCountFor(LoadRevisionsRequest.class));

    view.getResourceSet().eAdapters().add(new EContentAdapter());
    assertEquals(pathSegments.size() + 1, signalCounter.getCountFor(LoadRevisionsRequest.class));

    protocol.removeListener(signalCounter);
  }

  /**
   * @author Eike Stepper
   */
  private class CustomCDOFetchRuleManager implements CDOFetchRuleManager
  {
    private CDOID companyCDOID;

    public CustomCDOFetchRuleManager(CDOID companyCDOID)
    {
      this.companyCDOID = companyCDOID;
    }

    @Override
    public CDOID getContext()
    {
      return companyCDOID;
    }

    @Override
    public List<CDOFetchRule> getFetchRules(Collection<CDOID> ids)
    {
      List<CDOFetchRule> fetchRules = null;
      if (ids.contains(companyCDOID))
      {
        CDOFetchRule fetchRule = new CDOFetchRule(getModel1Package().getCompany());
        fetchRule.addFeature(getModel1Package().getCompany_Categories());
        fetchRules = Collections.singletonList(fetchRule);
      }
      return fetchRules;
    }

    @Override
    public CDOCollectionLoadingPolicy getCollectionLoadingPolicy()
    {
      return CDOUtil.createCollectionLoadingPolicy(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }
  }
}
