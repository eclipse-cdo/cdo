/*
 * Copyright (c) 2014 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.util.CDOFetchRule;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOCollectionLoadingPolicy;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.util.RequestCallCounter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOFetchRuleManager;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

import org.junit.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    RequestCallCounter loadRevisionsRequestCounter = new RequestCallCounter(session);

    CDOBranch currentBranch = session.getBranchManager().getMainBranch();
    testCDORevisionFetchWithChangesOnAllBranches(session, currentBranch, loadRevisionsRequestCounter, companyCDOID,
        NB_CATEGORY, true);

    currentBranch = currentBranch.getBranch(B1_BRANCH_NAME);
    testCDORevisionFetchWithChangesOnAllBranches(session, currentBranch, loadRevisionsRequestCounter, companyCDOID,
        2 * NB_CATEGORY, true);

    currentBranch = currentBranch.getBranch(B11_BRANCH_NAME);
    testCDORevisionFetchWithChangesOnAllBranches(session, currentBranch, loadRevisionsRequestCounter, companyCDOID,
        3 * NB_CATEGORY, true);

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
    RequestCallCounter loadRevisionsRequestCounter = new RequestCallCounter(session);

    CDOBranch currentBranch = session.getBranchManager().getMainBranch();
    testCDORevisionFetchWithChangesOnAllBranches(session, currentBranch, loadRevisionsRequestCounter, companyCDOID,
        NB_CATEGORY, false);

    currentBranch = currentBranch.getBranch(B1_BRANCH_NAME);
    testCDORevisionFetchWithChangesOnAllBranches(session, currentBranch, loadRevisionsRequestCounter, companyCDOID,
        2 * NB_CATEGORY, false);

    currentBranch = currentBranch.getBranch(B11_BRANCH_NAME);
    testCDORevisionFetchWithChangesOnAllBranches(session, currentBranch, loadRevisionsRequestCounter, companyCDOID,
        3 * NB_CATEGORY, false);

  }

  private void testCDORevisionFetchWithChangesOnAllBranches(CDOSession session, CDOBranch currentBranch,
      RequestCallCounter loadRevisionsRequestCounter, CDOID companyCDOID, int expectedNbCategories, boolean prefetch)
  {
    Map<Short, Integer> nbRequestsCalls = loadRevisionsRequestCounter.getNBRequestsCalls();
    nbRequestsCalls.put(CDOProtocolConstants.SIGNAL_LOAD_REVISIONS, 0);

    CDOView view = session.openView(currentBranch);
    assertEquals(0, (int)nbRequestsCalls.get(CDOProtocolConstants.SIGNAL_LOAD_REVISIONS));

    String resourcePath = getResourcePath(RESOURCE_NAME);
    List<String> pathSegments = CDOURIUtil.analyzePath(resourcePath);
    CDOResource resource = view.getResource(resourcePath);
    assertEquals(pathSegments.size(), (int)nbRequestsCalls.get(CDOProtocolConstants.SIGNAL_LOAD_REVISIONS));

    if (prefetch)
    {
      resource.cdoPrefetch(CDORevision.DEPTH_INFINITE);
    }
    assertEquals(RESOURCE_NAME, resource.getName());
    EObject eObject = resource.getContents().get(0);
    Assert.assertTrue(eObject instanceof Company);
    Company company = (Company)eObject;
    assertEquals(pathSegments.size() + 1, (int)nbRequestsCalls.get(CDOProtocolConstants.SIGNAL_LOAD_REVISIONS));
    Assert.assertEquals(expectedNbCategories, company.getCategories().size());

    view.getRevision(companyCDOID);

    assertEquals(pathSegments.size() + 1, (int)nbRequestsCalls.get(CDOProtocolConstants.SIGNAL_LOAD_REVISIONS));

    view.getResourceSet().eAdapters().add(new EContentAdapter());

    assertEquals(pathSegments.size() + 1, (int)nbRequestsCalls.get(CDOProtocolConstants.SIGNAL_LOAD_REVISIONS));

    view.close();
    nbRequestsCalls.clear();
  }

  private CDOID setUpChangesOnBranches() throws Exception
  {
    CDOSession session = openSession();
    CDOBranch b1Branch = session.getBranchManager().getMainBranch().createBranch(B1_BRANCH_NAME);

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
    String newBranchName = "B1";
    CDOBranch newBranch = session.getBranchManager().getMainBranch().createBranch(newBranchName);
    testCDORevisionPrefetchOnBranch(session, newBranch);
  }

  private void testCDORevisionPrefetchOnBranch(CDOSession session, CDOBranch cdoBranch) throws Exception
  {
    CDOTransaction view = session.openTransaction(cdoBranch);
    RequestCallCounter requestCallCounter = new RequestCallCounter(session);
    Map<Short, Integer> nbRequestsCalls = requestCallCounter.getNBRequestsCalls();
    nbRequestsCalls.put(CDOProtocolConstants.SIGNAL_LOAD_REVISIONS, 0);
    assertEquals(0, (int)nbRequestsCalls.get(CDOProtocolConstants.SIGNAL_LOAD_REVISIONS));

    String resourcePath = getResourcePath(RESOURCE_NAME);
    List<String> pathSegments = CDOURIUtil.analyzePath(resourcePath);
    CDOResource resource = view.getResource(resourcePath);
    assertEquals(pathSegments.size(), (int)nbRequestsCalls.get(CDOProtocolConstants.SIGNAL_LOAD_REVISIONS));
    resource.cdoPrefetch(CDORevision.DEPTH_INFINITE);

    assertEquals(pathSegments.size() + 1, (int)nbRequestsCalls.get(CDOProtocolConstants.SIGNAL_LOAD_REVISIONS));

    Company company = (Company)resource.getContents().get(0);
    CDOID companyCDOID = CDOUtil.getCDOObject(company).cdoID();

    assertEquals(pathSegments.size() + 1, (int)nbRequestsCalls.get(CDOProtocolConstants.SIGNAL_LOAD_REVISIONS));

    view.getRevision(companyCDOID);

    assertEquals(pathSegments.size() + 1, (int)nbRequestsCalls.get(CDOProtocolConstants.SIGNAL_LOAD_REVISIONS));

    view.getResourceSet().eAdapters().add(new EContentAdapter());

    assertEquals(pathSegments.size() + 1, (int)nbRequestsCalls.get(CDOProtocolConstants.SIGNAL_LOAD_REVISIONS));
  }

  private class CustomCDOFetchRuleManager implements CDOFetchRuleManager
  {

    private CDOID companyCDOID;

    public CustomCDOFetchRuleManager(CDOID companyCDOID)
    {
      this.companyCDOID = companyCDOID;
    }

    public CDOID getContext()
    {
      return companyCDOID;
    }

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

    public CDOCollectionLoadingPolicy getCollectionLoadingPolicy()
    {
      return CDOUtil.createCollectionLoadingPolicy(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

  }

}
