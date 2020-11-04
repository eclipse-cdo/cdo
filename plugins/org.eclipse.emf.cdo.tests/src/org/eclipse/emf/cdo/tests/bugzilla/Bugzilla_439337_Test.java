/*
 * Copyright (c) 2014-2016, 2020 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.net4j.protocol.LoadRevisionsRequest;
import org.eclipse.emf.cdo.internal.net4j.protocol.LockStateRequest;
import org.eclipse.emf.cdo.internal.net4j.protocol.QueryCancelRequest;
import org.eclipse.emf.cdo.internal.net4j.protocol.QueryRequest;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.view.CDOViewImpl.OptionsImpl;

import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.signal.SignalCounter;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentAdapter;

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
    ((OptionsImpl)view.options()).setLockStatePrefetchEnabled(true);
    testCDOLockState(view, true);
  }

  private void testCDOLockState(CDOView view, boolean cdoLockStatePrefetchEnabled)
  {
    view.getResourceSet().eAdapters().add(new EContentAdapterQueringCDOLockState());
    ISignalProtocol<?> protocol = ((CDONet4jSession)view.getSession()).options().getNet4jProtocol();
    SignalCounter signalCounter = new SignalCounter(protocol);
    view.getResource(getResourcePath(RESOURCE_NAME + "?" + CDOResource.PREFETCH_PARAMETER + "=" + Boolean.TRUE));

    int numberDifferentRequestCall = signalCounter.getCountForSignalTypes();
    String assertMessage = "4 differents kinds of requests should have been sent, QueryRequest, QueryCancel, LoadRevisionsRequest and LockStateRequest";
    // QueryRequest, QueryCancel are used to get the resourcePath
    assertEquals(assertMessage, 4, numberDifferentRequestCall);
    assertNotSame(0, signalCounter.getCountFor(QueryRequest.class));
    assertNotSame(0, signalCounter.getCountFor(QueryCancelRequest.class));
    assertNotSame(0, signalCounter.getCountFor(LoadRevisionsRequest.class));
    assertNotSame(0, signalCounter.getCountFor(LockStateRequest.class));
    assertEquals("1 single query request should have been sent to get the resourcePath", 1, signalCounter.getCountFor(QueryRequest.class));
    assertEquals("1 single query request should have been sent to cancel the single QueryRequest", 1, signalCounter.getCountFor(QueryCancelRequest.class));
    assertEquals(
        "3 load revisions request should have been sent, 2 first for CDORevisions of CDOResourceFolders to get resource path and another in prefetch to load all CDORevisions of CDOResource",
        3, signalCounter.getCountFor(LoadRevisionsRequest.class));
    int expectedNbLockStateRequestCalls = (cdoLockStatePrefetchEnabled ? 0 : NB_CATEGORIES) + 2;
    assertEquals("As CDOLockState prefetch is " + (cdoLockStatePrefetchEnabled ? "" : "not ") + "enabled " + expectedNbLockStateRequestCalls
        + " LockStateRequests should have been sent to the server", expectedNbLockStateRequestCalls, signalCounter.getCountFor(LockStateRequest.class));

    protocol.removeListener(signalCounter);
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
