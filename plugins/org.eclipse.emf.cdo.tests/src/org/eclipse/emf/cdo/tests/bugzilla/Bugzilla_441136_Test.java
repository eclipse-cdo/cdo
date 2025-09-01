/*
 * Copyright (c) 2014-2016, 2018, 2019, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.internal.net4j.protocol.CommitTransactionRequest;
import org.eclipse.emf.cdo.internal.net4j.protocol.LoadRevisionsRequest;
import org.eclipse.emf.cdo.internal.net4j.protocol.QueryCancelRequest;
import org.eclipse.emf.cdo.internal.net4j.protocol.QueryRequest;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Skips;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.signal.AcknowledgeCompressedStringsIndication;
import org.eclipse.net4j.signal.AcknowledgeCompressedStringsRequest;
import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.signal.MonitorProgressIndication;
import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.signal.SignalCounter;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * Test {@link RequestWithMonitoring @link IndicationWithMonitoring} with and without {@link OMMonitor}.
 *
 * @author Esteban Dugueperoux
 */
@Skips(IConfig.CAPABILITY_ALL) // This test runs very long on purpose and signal counting is sometimes a bit flaky.
                               // Don't run it in suites.
public class Bugzilla_441136_Test extends AbstractCDOTest
{
  private static final String RESOURCE_NAME = "test1.model1";

  /**
   * Test that a {@link RequestWithMonitoring}/{@link IndicationWithMonitoring} without {@link OMMonitor} does not trigger MonitorProgressRequest/MonitorProgressIndication.
   */
  public void testRequestWithoutProgressMonitor() throws Exception
  {
    testRequest(false);
  }

  /**
   * Test that a {@link RequestWithMonitoring}/{@link IndicationWithMonitoring} with {@link OMMonitor} does trigger MonitorProgressRequest/MonitorProgressIndication.
   */
  public void testRequestWithProgressMonitor() throws Exception
  {
    testRequest(true);
  }

  private void testRequest(boolean useMonitor) throws Exception
  {
    CDOSession session = openSession();
    getRepository().getCommitInfoManager().addCommitInfoHandler(new CommitTransactionIndicationWaiting());
    ((CDONet4jSession)session).options().setCommitTimeout(1000 * CommitTransactionRequest.DEFAULT_MONITOR_TIMEOUT_SECONDS);
    ((CDONet4jSession)session).options().setSignalTimeout(10000 * SignalProtocol.DEFAULT_TIMEOUT);

    CDOTransaction transaction = session.openTransaction();
    SignalCounter signalCounter = new SignalCounter(((org.eclipse.emf.cdo.net4j.CDONet4jSession)session).options().getNet4jProtocol());

    try
    {
      CDOResource resource = transaction.getOrCreateResource(getResourcePath(RESOURCE_NAME));
      Company company = getModel1Factory().createCompany();
      resource.getContents().add(company);
      transaction.commit(useMonitor ? new NullProgressMonitor() : null);

      signalCounter.removeCountFor(AcknowledgeCompressedStringsRequest.class);
      signalCounter.removeCountFor(AcknowledgeCompressedStringsIndication.class);
      signalCounter.dump(IOUtil.OUT(), false);

      // QueryRequest, QueryCancel are used to get the resourcePath
      assertNotSame(0, signalCounter.getCountFor(QueryRequest.class));
      assertNotSame(0, signalCounter.getCountFor(QueryCancelRequest.class));
      assertNotSame(0, signalCounter.getCountFor(LoadRevisionsRequest.class));
      assertNotSame(0, signalCounter.getCountFor(CommitTransactionRequest.class));

      int expectedSignalTypes = 4;
      String assertMessage = " different signal types should have been seen: QueryRequest, QueryCancelRequest, LoadRevisionsRequest, CommitTransactionRequest";

      if (useMonitor)
      {
        assertNotSame(0, signalCounter.getCountFor(MonitorProgressIndication.class));

        expectedSignalTypes += 1;
        assertMessage += ", MonitorProgressIndication";
      }

      assertEquals(expectedSignalTypes + assertMessage, expectedSignalTypes, signalCounter.getCountForSignalTypes());
    }
    finally
    {
      signalCounter.dispose();
    }
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
