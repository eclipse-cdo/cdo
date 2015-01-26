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

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionImpl;
import org.eclipse.emf.cdo.internal.net4j.protocol.CommitTransactionRequest;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.util.RequestCallCounter;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.core.runtime.NullProgressMonitor;

import java.util.Map;

/**
 * Test {@link RequestWithMonitoring @link IndicationWithMonitoring} with and without {@link OMMonitor}.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_441136_Test extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "test1.model1";

  /**
   * Test that a {@link RequestWithMonitoring}/{@link IndicationWithMonitoring} without {@link OMMonitor} does not trigger MonitorProgressRequest/MonitorProgressIndication.
   */
  public void testRequestWithMonitoringWithoutProgressMonitor() throws Exception
  {
    testRequestWithMonitoring(false);
  }

  /**
   * Test that a {@link RequestWithMonitoring}/{@link IndicationWithMonitoring} with {@link OMMonitor} does trigger MonitorProgressRequest/MonitorProgressIndication.
   */
  public void testRequestWithMonitoringWithProgressMonitor() throws Exception
  {
    testRequestWithMonitoring(true);
  }

  private void testRequestWithMonitoring(boolean useMonitor) throws Exception
  {
    CDOSession session = openSession();
    getRepository().getCommitInfoManager().addCommitInfoHandler(new CommitTransactionIndicationWaiting());
    ((CDONet4jSession)session).options().setCommitTimeout(
        1000 * CommitTransactionRequest.DEFAULT_MONITOR_TIMEOUT_SECONDS);
    ((CDONet4jSessionImpl)session).setSignalTimeout(10000 * SignalProtocol.DEFAULT_TIMEOUT);
    CDOTransaction transaction = session.openTransaction();
    RequestCallCounter nbRequestsCallsCounter = new RequestCallCounter(session);
    CDOResource resource = transaction.getOrCreateResource(getResourcePath(RESOURCE_NAME));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit(useMonitor ? new NullProgressMonitor() : null);
    String assertMessage = " differents kinds of requests should be sent, QueryRequest, QueryCancel, LoadRevisionsRequest and CommitTransactionRequest";
    int nbExpectedCalls;
    Map<Short, Integer> nbRequestsCalls = nbRequestsCallsCounter.getNBRequestsCalls();
    if (!useMonitor)
    {

      // QueryRequest, QueryCancel are used to get the resourcePath
      nbExpectedCalls = 4;
      assertEquals(nbExpectedCalls + assertMessage, nbExpectedCalls, nbRequestsCalls.size());
      assertEquals(true, nbRequestsCalls.containsKey(CDOProtocolConstants.SIGNAL_QUERY));
      assertEquals(true, nbRequestsCalls.containsKey(CDOProtocolConstants.SIGNAL_QUERY_CANCEL));
      assertEquals(true, nbRequestsCalls.containsKey(CDOProtocolConstants.SIGNAL_LOAD_REVISIONS));
      assertEquals(true, nbRequestsCalls.containsKey(CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION));
    }
    else
    {
      nbExpectedCalls = 5;
      assertMessage += " and MonitorProgressIndications should be received";
      // QueryRequest, QueryCancel are used to get the resourcePath
      assertEquals(nbExpectedCalls + assertMessage, nbExpectedCalls, nbRequestsCalls.size());
      assertEquals(true, nbRequestsCalls.containsKey(CDOProtocolConstants.SIGNAL_QUERY));
      assertEquals(true, nbRequestsCalls.containsKey(CDOProtocolConstants.SIGNAL_QUERY_CANCEL));
      assertEquals(true, nbRequestsCalls.containsKey(CDOProtocolConstants.SIGNAL_LOAD_REVISIONS));
      assertEquals(true, nbRequestsCalls.containsKey(CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION));
      assertEquals(true, nbRequestsCalls.containsKey(SignalProtocol.SIGNAL_MONITOR_PROGRESS));
    }
  }

  class CommitTransactionIndicationWaiting implements CDOCommitInfoHandler
  {

    public void handleCommitInfo(CDOCommitInfo commitInfo)
    {
      try
      {
        Thread.sleep(1000 * CommitTransactionRequest.DEFAULT_MONITOR_TIMEOUT_SECONDS / 2);
      }
      catch (InterruptedException ex)
      {
        ex.printStackTrace();
      }
    }

  }
}
