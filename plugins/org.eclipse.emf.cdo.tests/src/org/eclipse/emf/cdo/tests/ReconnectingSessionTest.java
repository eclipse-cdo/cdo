/*
 * Copyright (c) 2014, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSessionRecoveryEvent;
import org.eclipse.emf.cdo.net4j.ReconnectingCDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author Eike Stepper
 */
@Requires(ISessionConfig.CAPABILITY_NET4J_TCP)
public class ReconnectingSessionTest extends AbstractCDOTest
{
  private static final String ADDRESS2 = SessionConfig.Net4j.TCP.CONNECTOR_HOST + ":2040";

  @Override
  protected void doSetUp() throws Exception
  {
    disableConsole();
    super.doSetUp();
  }

  public void testReconnect() throws Exception
  {
    disableConsole();

    CDOSession session1 = openSession();
    CDOTransaction transaction = session1.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("res1"));
    resource1.getContents().add(getModel1Factory().createCategory());
    transaction.commit();

    ITCPAcceptor acceptor = null;
    CDONet4jSession session2 = null;

    try
    {
      IManagedContainer serverContainer = getServerContainer();
      dumpEvents(serverContainer);

      acceptor = TCPUtil.getAcceptor(serverContainer, ADDRESS2);
      dumpEvents(acceptor);

      String repositoryName = session1.getRepositoryInfo().getName();
      IManagedContainer clientContainer = getClientContainer();
      dumpEvents(clientContainer);

      ReconnectingCDOSessionConfiguration configuration = CDONet4jUtil.createReconnectingSessionConfiguration(ADDRESS2, repositoryName, clientContainer);
      configuration.setHeartBeatEnabled(true);

      session2 = (CDONet4jSession)openSession(configuration);
      dumpEvents(session2);

      final CountDownLatch recoveryStarted = new CountDownLatch(1);
      final CountDownLatch recoveryFinished = new CountDownLatch(1);
      session2.addListener(new IListener()
      {
        public void notifyEvent(final IEvent event)
        {
          if (event instanceof CDOSessionRecoveryEvent)
          {
            CDOSessionRecoveryEvent recoveryEvent = (CDOSessionRecoveryEvent)event;
            switch (recoveryEvent.getType())
            {
            case STARTED:
              recoveryStarted.countDown();
              break;
            case FINISHED:
              recoveryFinished.countDown();
              break;
            }
          }
        }
      });

      IConnector connector2 = (IConnector)session2.options().getNet4jProtocol().getChannel().getMultiplexer();
      dumpEvents(connector2);

      CDOView view = session2.openView();
      CDOResource resource2 = view.getResource(getResourcePath("res1"));
      assertEquals(1, resource2.getContents().size());

      resource1.getContents().add(getModel1Factory().createCategory());
      commitAndSync(transaction, view);
      assertEquals(2, resource2.getContents().size());

      IOUtil.OUT().println();
      IOUtil.OUT().println("Deactivating acceptor...");
      LifecycleUtil.deactivate(acceptor);
      recoveryStarted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);

      IOUtil.OUT().println();
      IOUtil.OUT().println("Reactivating acceptor...");
      acceptor = TCPUtil.getAcceptor(serverContainer, ADDRESS2);
      dumpEvents(acceptor);
      recoveryFinished.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);

      IOUtil.OUT().println();
      IOUtil.OUT().println("Committing...");
      resource1.getContents().add(getModel1Factory().createCategory());
      commitAndSync(transaction, view);
      assertEquals(3, resource2.getContents().size());
    }
    finally
    {
      LifecycleUtil.deactivate(session2);
      LifecycleUtil.deactivate(acceptor);
    }
  }

  public void testReconnectWithCommitsWhileUnconnected() throws Exception
  {
    disableConsole();

    CDOSession session1 = openSession();
    CDOTransaction transaction = session1.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("res1"));
    resource1.getContents().add(getModel1Factory().createCategory());
    transaction.commit();

    ITCPAcceptor acceptor = null;
    CDONet4jSession session2 = null;

    try
    {
      IManagedContainer serverContainer = getServerContainer();
      dumpEvents(serverContainer);

      acceptor = TCPUtil.getAcceptor(serverContainer, ADDRESS2);
      dumpEvents(acceptor);

      String repositoryName = session1.getRepositoryInfo().getName();
      IManagedContainer clientContainer = getClientContainer();
      dumpEvents(clientContainer);

      ReconnectingCDOSessionConfiguration configuration = CDONet4jUtil.createReconnectingSessionConfiguration(ADDRESS2, repositoryName, clientContainer);
      configuration.setHeartBeatEnabled(true);

      session2 = (CDONet4jSession)openSession(configuration);
      dumpEvents(session2);

      final CountDownLatch recoveryStarted = new CountDownLatch(1);
      final CountDownLatch recoveryFinished = new CountDownLatch(1);
      session2.addListener(new IListener()
      {
        public void notifyEvent(final IEvent event)
        {
          if (event instanceof CDOSessionRecoveryEvent)
          {
            CDOSessionRecoveryEvent recoveryEvent = (CDOSessionRecoveryEvent)event;
            switch (recoveryEvent.getType())
            {
            case STARTED:
              recoveryStarted.countDown();
              break;
            case FINISHED:
              recoveryFinished.countDown();
              break;
            }
          }
        }
      });

      IConnector connector2 = (IConnector)session2.options().getNet4jProtocol().getChannel().getMultiplexer();
      dumpEvents(connector2);

      CDOView view = session2.openView();
      CDOResource resource2 = view.getResource(getResourcePath("res1"));
      assertEquals(1, resource2.getContents().size());

      resource1.getContents().add(getModel1Factory().createCategory());
      commitAndSync(transaction, view);
      assertEquals(2, resource2.getContents().size());

      IOUtil.OUT().println();
      IOUtil.OUT().println("Deactivating acceptor...");
      LifecycleUtil.deactivate(acceptor);
      recoveryStarted.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);

      IOUtil.OUT().println();
      IOUtil.OUT().println("Committing...");
      resource1.getContents().add(getModel1Factory().createCategory());
      transaction.commit();

      IOUtil.OUT().println();
      IOUtil.OUT().println("Reactivating acceptor...");
      acceptor = TCPUtil.getAcceptor(serverContainer, ADDRESS2);
      dumpEvents(acceptor);
      recoveryFinished.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);

      assertEquals(3, resource2.getContents().size());
    }
    finally
    {
      LifecycleUtil.deactivate(session2);
      LifecycleUtil.deactivate(acceptor);
    }
  }

  @Requires(IRepositoryConfig.CAPABILITY_BRANCHING)
  public void testNonTransportFailure() throws Exception
  {
    String repositoryName;

    {
      CDOSession session = openSession();
      repositoryName = session.getRepositoryInfo().getName();
      session.close();
    }

    ITCPAcceptor acceptor = null;
    CDONet4jSession session = null;

    try
    {
      acceptor = TCPUtil.getAcceptor(getServerContainer(), ADDRESS2);

      ReconnectingCDOSessionConfiguration configuration = CDONet4jUtil.createReconnectingSessionConfiguration(ADDRESS2, repositoryName, getClientContainer());
      configuration.setHeartBeatEnabled(true);

      session = (CDONet4jSession)openSession(configuration);
      final CDOBranch branch = session.getBranchManager().getBranch(9999999);

      try
      {
        new ThreadTimeOuter()
        {
          public void run()
          {
            branch.getName(); // This would hang without the fix in RecoveringExceptionHandler.handleException()
          }
        }.assertNoTimeOut();

        fail("RemoteException expected");
      }
      catch (RemoteException expected)
      {
        assertInstanceOf(NullPointerException.class, expected.getCause());
      }
    }
    finally
    {
      LifecycleUtil.deactivate(session);
      LifecycleUtil.deactivate(acceptor);
    }
  }
}
