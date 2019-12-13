/*
 * Copyright (c) 2014-2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionImpl;
import org.eclipse.emf.cdo.internal.net4j.protocol.CommitTransactionRequest;
import org.eclipse.emf.cdo.internal.net4j.protocol.LoadRevisionsRequest;
import org.eclipse.emf.cdo.internal.net4j.protocol.QueryCancelRequest;
import org.eclipse.emf.cdo.internal.net4j.protocol.QueryRequest;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.signal.AcknowledgeCompressedStringsIndication;
import org.eclipse.net4j.signal.AcknowledgeCompressedStringsRequest;
import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.signal.MonitorProgressIndication;
import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.signal.SignalCounter;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.core.runtime.NullProgressMonitor;

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
    ((CDONet4jSession)session).options().setCommitTimeout(1000 * CommitTransactionRequest.DEFAULT_MONITOR_TIMEOUT_SECONDS);
    ((CDONet4jSessionImpl)session).setSignalTimeout(10000 * SignalProtocol.DEFAULT_TIMEOUT);
    CDOTransaction transaction = session.openTransaction();
    ISignalProtocol<?> protocol = ((org.eclipse.emf.cdo.net4j.CDONet4jSession)session).options().getNet4jProtocol();
    SignalCounter signalCounter = new SignalCounter(protocol);
    CDOResource resource = transaction.getOrCreateResource(getResourcePath(RESOURCE_NAME));
    Company company = getModel1Factory().createCompany();
    resource.getContents().add(company);
    transaction.commit(useMonitor ? new NullProgressMonitor() : null);

    signalCounter.removeCountFor(AcknowledgeCompressedStringsRequest.class);
    signalCounter.removeCountFor(AcknowledgeCompressedStringsIndication.class);

    int nbExpectedCalls;
    String assertMessage = " differents kinds of requests should have been sent, QueryRequest, QueryCancel, LoadRevisionsRequest and CommitTransactionRequest";

    if (useMonitor)
    {
      nbExpectedCalls = 5;
      assertMessage += " and MonitorProgressIndications should have been received";

      // QueryRequest, QueryCancel are used to get the resourcePath
      assertEquals(nbExpectedCalls + assertMessage, nbExpectedCalls, signalCounter.getCountForSignalTypes());
      assertNotSame(0, signalCounter.getCountFor(QueryRequest.class));
      assertNotSame(0, signalCounter.getCountFor(QueryCancelRequest.class));
      assertNotSame(0, signalCounter.getCountFor(LoadRevisionsRequest.class));
      assertNotSame(0, signalCounter.getCountFor(CommitTransactionRequest.class));
      assertNotSame(0, signalCounter.getCountFor(MonitorProgressIndication.class));
    }
    else
    {
      // QueryRequest, QueryCancel are used to get the resourcePath
      nbExpectedCalls = 4;
      assertEquals(nbExpectedCalls + assertMessage, nbExpectedCalls, signalCounter.getCountForSignalTypes());
      assertNotSame(0, signalCounter.getCountFor(QueryRequest.class));
      assertNotSame(0, signalCounter.getCountFor(QueryCancelRequest.class));
      assertNotSame(0, signalCounter.getCountFor(LoadRevisionsRequest.class));
      assertNotSame(0, signalCounter.getCountFor(CommitTransactionRequest.class));
    }

    protocol.removeListener(signalCounter);
  }

  /**
  * @author Esteban Dugueperoux
  */
  private static final class CommitTransactionIndicationWaiting implements CDOCommitInfoHandler
  {
    @Override
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
