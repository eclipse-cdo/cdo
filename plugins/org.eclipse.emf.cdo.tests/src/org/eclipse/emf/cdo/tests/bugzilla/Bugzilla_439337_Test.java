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
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.util.RequestCallCounter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentAdapter;

import java.util.Map;

/**
 * Bug 439337 about {@link CDOLockState lock state} prefetch following a {@link CDORevision revision} prefetch.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_439337_Test extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "test1.model1";

  private static final int NB_CATEGORIES = 10;

  @Override
  public void setUp() throws Exception
  {
    super.setUp();
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource = transaction.getOrCreateResource(getResourcePath(RESOURCE_NAME));
    Company company = getModel1Factory().createCompany();
    for (int i = 0; i < NB_CATEGORIES; i++)
    {
      Category category = getModel1Factory().createCategory();
      category.setName("Category n°" + i);
      company.getCategories().add(category);
    }
    resource.getContents().add(company);
    transaction.commit();
  }

  /**
   * Test {@link CDOLockState} API without prefetch.
   */
  public void testCDOLockStateWithoutPrefetch() throws Exception
  {
    CDOSession session = openSession();
    CDOView view = session.openView();
    testCDOLockState(view, false);
  }

  /**
   * Test {@link CDOLockState} API with prefetch.
   */
  public void testCDOLockStateWithPrefetch() throws Exception
  {
    CDOSession session = openSession();
    CDOView view = session.openView();
    view.options().setLockStatePrefetchEnabled(true);
    testCDOLockState(view, true);
  }

  private void testCDOLockState(CDOView view, boolean cdoLockStatePrefetchEnabled)
  {
    view.getResourceSet().eAdapters().add(new EContentAdapterQueringCDOLockState());
    RequestCallCounter nbRequestsCallsCounter = new RequestCallCounter(view.getSession());
    view.getResource(getResourcePath(RESOURCE_NAME + "?" + CDOResource.PREFETCH_PARAMETER + "=" + Boolean.TRUE));

    Map<Short, Integer> nbRequestsCalls = nbRequestsCallsCounter.getNBRequestsCalls();
    String assertMessage = "4 differents kinds of requests should have been sent, QueryRequest, QueryCancel, LoadRevisionsRequest and LockStateRequest";
    // QueryRequest, QueryCancel are used to get the resourcePath
    assertEquals(assertMessage, 4, nbRequestsCalls.size());
    assertEquals(true, nbRequestsCalls.containsKey(CDOProtocolConstants.SIGNAL_QUERY));
    assertEquals(true, nbRequestsCalls.containsKey(CDOProtocolConstants.SIGNAL_QUERY_CANCEL));
    assertEquals(true, nbRequestsCalls.containsKey(CDOProtocolConstants.SIGNAL_LOAD_REVISIONS));
    assertEquals(true, nbRequestsCalls.containsKey(CDOProtocolConstants.SIGNAL_LOCK_STATE));
    assertEquals("1 single query request should have been sent to get the resourcePath", Integer.valueOf(1),
        nbRequestsCalls.get(CDOProtocolConstants.SIGNAL_QUERY));
    assertEquals("1 single query request should have been sent to cancel the single QueryRequest", Integer.valueOf(1),
        nbRequestsCalls.get(CDOProtocolConstants.SIGNAL_QUERY_CANCEL));
    assertEquals(
        "3 load revisions request should have been sent, 2 first for CDORevisions of CDOResourceFolders to get resource path and another in prefetch to load all CDORevisions of CDOResource",
        Integer.valueOf(3), nbRequestsCalls.get(CDOProtocolConstants.SIGNAL_LOAD_REVISIONS));
    Integer expectedNbLockStateRequestCalls = Integer.valueOf((cdoLockStatePrefetchEnabled ? 0 : NB_CATEGORIES) + 2);
    assertEquals("As CDOLockState prefetch is " + (cdoLockStatePrefetchEnabled ? "" : "not ") + "enabled "
        + expectedNbLockStateRequestCalls + " LockStateRequests should have been sent to the server",
        expectedNbLockStateRequestCalls, nbRequestsCalls.get(CDOProtocolConstants.SIGNAL_LOCK_STATE));
  }

  /**
   * A {@link EContentAdapter} to request {@link CDOLockState} on each object of a {@link ResourceSet}.
   */
  private static class EContentAdapterQueringCDOLockState extends EContentAdapter
  {

    @Override
    protected void addAdapter(Notifier notifier)
    {
      if (notifier instanceof EObject)
      {
        EObject eObject = (EObject)notifier;
        CDOObject cdoObject = CDOUtil.getCDOObject(eObject);
        if (cdoObject != null)
        {
          cdoObject.cdoLockState();
        }
      }

      super.addAdapter(notifier);

    }
  }

}
