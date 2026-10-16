/*
 * Copyright (c) 2014-2016, 2020, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
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
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOLockStatePrefetcher;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.session.DelegatingSessionProtocol;

import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.signal.SignalCounter;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.LockStateQueryResult;
import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Bug 439337 about {@link CDOLockState lock state} prefetch following a {@link CDORevision revision} prefetch.
 *
 * @author Esteban Dugueperoux
 */
@Requires(ISessionConfig.CAPABILITY_NET4J)
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
  @Requires(ISessionConfig.CAPABILITY_NET4J)
  public void testCDOLockStateWithoutPrefetch() throws Exception
  {
    run(false);
  }

  /**
   * Test {@link CDOLockState} API with prefetch.
   */
  @Requires(ISessionConfig.CAPABILITY_NET4J)
  public void testCDOLockStateWithPrefetch() throws Exception
  {
    run(true);
  }

  private void run(boolean lockStatePrefetchEnabled) throws Exception
  {
    CDONet4jSession session = (CDONet4jSession)openSession();
    CDOView view = session.openView();
    InternalCDOSession internalSession = (InternalCDOSession)session;
    CDOSessionProtocol originalProtocol = internalSession.getSessionProtocol();
    CapturingLockStateQueryProtocol capturingProtocol = new CapturingLockStateQueryProtocol(originalProtocol);
    internalSession.setSessionProtocol(capturingProtocol);

    ISignalProtocol<?> protocol = session.options().getNet4jProtocol();
    SignalCounter signalCounter = new SignalCounter(protocol);

    try
    {
      if (lockStatePrefetchEnabled)
      {
        new CDOLockStatePrefetcher(view, false);
      }

      CDOResource resource = view.getResource(getResourcePath(RESOURCE_NAME + "?" + CDOResource.PREFETCH_PARAMETER + "=" + Boolean.TRUE));
      List<CDOID> objectIDs = getObjectIDs(resource);
      assertEquals(NB_CATEGORIES + 1, objectIDs.size());

      if (lockStatePrefetchEnabled)
      {
        LockStateQueryCapture prefetchQuery = findPrefetchQuery(capturingProtocol.getQueries(), objectIDs);
        assertNotNull("The prefetch query must include all loaded objects", prefetchQuery); //$NON-NLS-1$
        awaitPrefetchInstallation(internalSession, prefetchQuery.result.getLockModCount());
      }

      int lockStateRequestsBeforeAccess = signalCounter.getCountFor(LockStateRequest.class);
      view.getResourceSet().eAdapters().add(new EContentAdapterQueryingCDOLockState(new HashSet<>(objectIDs)));
      int additionalLockStateRequests = signalCounter.getCountFor(LockStateRequest.class) - lockStateRequestsBeforeAccess;

      int expectedAdditionalRequests = lockStatePrefetchEnabled ? 0 : NB_CATEGORIES + 1;
      assertEquals("Accessing already loaded objects must issue only the lock-state requests not covered by prefetch", //$NON-NLS-1$
          expectedAdditionalRequests, additionalLockStateRequests);

      // QueryRequest, QueryCancel are used to get the resourcePath
      assertEquals("4 differents kinds of requests should have been sent, QueryRequest, QueryCancel, LoadRevisionsRequest and LockStateRequest", 4,
          signalCounter.getCountForSignalTypes());

      assertNotSame(0, signalCounter.getCountFor(QueryRequest.class));
      assertNotSame(0, signalCounter.getCountFor(QueryCancelRequest.class));
      assertNotSame(0, signalCounter.getCountFor(LoadRevisionsRequest.class));
      assertNotSame(0, signalCounter.getCountFor(LockStateRequest.class));

      assertEquals("1 single query request should have been sent to get the resourcePath", 1, signalCounter.getCountFor(QueryRequest.class));
      assertEquals("1 single query cancel request should have been sent", 1, signalCounter.getCountFor(QueryCancelRequest.class));
      assertEquals(
          "3 load revision requests should have been sent, 2 for revisions of CDOResourceFolders to get resource path and another in prefetch to load all revisions of CDOResource",
          3, signalCounter.getCountFor(LoadRevisionsRequest.class));
    }
    finally
    {
      protocol.removeListener(signalCounter);
      internalSession.setSessionProtocol(originalProtocol);
      view.close();
      session.close();
    }
  }

  private static List<CDOID> getObjectIDs(CDOResource resource)
  {
    List<CDOID> ids = new ArrayList<>();

    for (EObject object : resource.getContents())
    {
      CDOObject cdoObject = CDOUtil.getCDOObject(object);
      if (cdoObject != null)
      {
        ids.add(cdoObject.cdoID());
      }

      for (Iterator<EObject> it = object.eAllContents(); it.hasNext();)
      {
        CDOObject child = CDOUtil.getCDOObject(it.next());
        if (child != null)
        {
          ids.add(child.cdoID());
        }
      }
    }

    return ids;
  }

  private static LockStateQueryCapture findPrefetchQuery(List<LockStateQueryCapture> queries, List<CDOID> objectIDs)
  {
    for (int i = queries.size() - 1; i >= 0; i--)
    {
      LockStateQueryCapture query = queries.get(i);

      if (query.ids.containsAll(objectIDs))
      {
        return query;
      }
    }

    return null;
  }

  private void awaitPrefetchInstallation(InternalCDOSession session, long lockModCount) throws Exception
  {
    CompletableFuture<Boolean> completion = new CompletableFuture<>();
    assertTrue("The prefetch installation barrier was rejected", //$NON-NLS-1$
        session.enqueueLockStateQuery(lockModCount, () -> {
        }, completion));

    // view.getResource() has returned, so this wait is outside view Access. The marker follows the prefetch action at
    // this baseline.
    // A false result means an authoritative snapshot superseded the baseline and was installed instead.
    completion.get(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
  }

  /**
   * A {@link EContentAdapter} to request {@link CDOLockState} on each object of a {@link ResourceSet}.
   *
   * @author Esteban Dugueperoux
   */
  private static class EContentAdapterQueryingCDOLockState extends EContentAdapter
  {
    private final Set<CDOID> objectIDs;

    public EContentAdapterQueryingCDOLockState(Set<CDOID> objectIDs)
    {
      this.objectIDs = objectIDs;
    }

    @Override
    protected void addAdapter(Notifier notifier)
    {
      if (notifier instanceof EObject)
      {
        EObject eObject = (EObject)notifier;
        CDOObject cdoObject = CDOUtil.getCDOObject(eObject);
        if (cdoObject != null && objectIDs.contains(cdoObject.cdoID()))
        {
          cdoObject.cdoLockState();
        }
      }

      super.addAdapter(notifier);
    }
  }

  /**
   * Captures lock-state query IDs and baselines without changing the protocol result or its sequencing.
   *
   * @author Eike Stepper
   */
  private static final class CapturingLockStateQueryProtocol extends DelegatingSessionProtocol
  {
    private final List<LockStateQueryCapture> queries = Collections.synchronizedList(new ArrayList<>());

    public CapturingLockStateQueryProtocol(CDOSessionProtocol delegate)
    {
      super(delegate, null);
    }

    @Override
    public LockStateQueryResult getLockStates3(int branchID, Collection<CDOID> ids, int depth)
    {
      LockStateQueryResult result = super.getLockStates3(branchID, ids, depth);
      queries.add(new LockStateQueryCapture(ids == null ? Collections.emptyList() : new ArrayList<>(ids), result));
      return result;
    }

    public List<LockStateQueryCapture> getQueries()
    {
      synchronized (queries)
      {
        return new ArrayList<>(queries);
      }
    }
  }

  /**
   * Records the IDs and baseline returned by one lock-state query.
   *
   * @author Eike Stepper
   */
  private static final class LockStateQueryCapture
  {
    private final List<CDOID> ids;

    private final LockStateQueryResult result;

    public LockStateQueryCapture(List<CDOID> ids, LockStateQueryResult result)
    {
      this.ids = ids;
      this.result = result;
    }
  }
}
