/*
 * Copyright (c) 2014-2016, 2018, 2019, 2021, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchDoesNotExistException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSessionRecoveryEvent;
import org.eclipse.emf.cdo.net4j.ReconnectingCDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionManager;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.ISessionConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.tests.config.impl.SessionConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewLocksChangedEvent;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.tcp.ITCPAcceptor;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.IAuthenticator;
import org.eclipse.net4j.util.security.NotAuthenticatedException;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.net4j.util.tests.TestListener;

import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author Eike Stepper
 */
@Requires(ISessionConfig.CAPABILITY_NET4J_TCP)
public class ReconnectingSessionTest extends AbstractCDOTest
{
  private static final String ADDRESS2 = SessionConfig.Net4j.TCP.CONNECTOR_HOST + ":2040";

  public void testReconnect() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction = session1.openTransaction();
    CDOResource resource1 = transaction.createResource(getResourcePath("res1"));
    resource1.getContents().add(getModel1Factory().createCategory());
    transaction.commit();

    ITCPAcceptor acceptor = null;
    CDONet4jSession reconnectingSession = null;
    CDOTransaction durableTransaction = null;

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

      reconnectingSession = (CDONet4jSession)openSession(configuration);

      dumpEvents(reconnectingSession);

      CountDownLatch recoveryStarted = new CountDownLatch(1);
      CountDownLatch recoveryFinished = new CountDownLatch(1);
      reconnectingSession.addListener(new IListener()
      {
        @Override
        public void notifyEvent(IEvent event)
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

      RemoteMessageListener remoteMessageListener = new RemoteMessageListener();
      reconnectingSession.getRemoteSessionManager().addListener(remoteMessageListener);

      CDOView viewWithLockNotifications = reconnectingSession.openView();
      viewWithLockNotifications.options().setLockNotificationEnabled(true);

      CDOResource resReconn = viewWithLockNotifications.getObject(resource1);
      checkLockNotifications(resource1, resReconn);

      IConnector connector = (IConnector)reconnectingSession.options().getNet4jProtocol().getChannel().getMultiplexer();
      dumpEvents(connector);

      CDOView view = reconnectingSession.openView();
      CDOResource resource2 = view.getResource(getResourcePath("res1"));
      assertEquals(1, resource2.getContents().size());

      resource1.getContents().add(getModel1Factory().createCategory());
      commitAndSync(transaction, view);
      assertEquals(2, resource2.getContents().size());

      sendMessageToAllSessions("message1", session1);
      remoteMessageListener.assertReceivedMessages(Arrays.asList("message1"));

      durableTransaction = reconnectingSession.openTransaction();
      durableTransaction.enableDurableLocking();
      durableTransaction.options().setAutoReleaseLocksEnabled(false);
      CDOResource lockRes = durableTransaction.createResource(getResourcePath("durableLockTest"));
      durableTransaction.commit();
      lockRes.cdoWriteLock().lock(1000);

      IOUtil.OUT().println();
      IOUtil.OUT().println("Deactivating acceptor...");
      LifecycleUtil.deactivate(acceptor);
      await(recoveryStarted);

      IOUtil.OUT().println();
      IOUtil.OUT().println("Reactivating acceptor...");
      acceptor = TCPUtil.getAcceptor(serverContainer, ADDRESS2);
      dumpEvents(acceptor);
      await(recoveryFinished);

      IOUtil.OUT().println();
      IOUtil.OUT().println("Committing...");
      resource1.getContents().add(getModel1Factory().createCategory());
      commitAndSync(transaction, view);
      assertEquals(3, resource2.getContents().size());

      checkLockNotifications(resource1, resReconn);

      sendMessageToAllSessions("message2", session1);
      remoteMessageListener.assertReceivedMessages(Arrays.asList("message1", "message2"));

      durableTransaction.refreshLockStates(null);
      assertFalse(lockRes.cdoWriteLock().isLockedByOthers());
      assertTrue(lockRes.cdoWriteLock().isLocked());
    }
    finally
    {
      if (durableTransaction != null)
      {
        try
        {
          durableTransaction.disableDurableLocking(true);
        }
        catch (Exception ignore)
        {
          // Do nothing.
        }
      }

      LifecycleUtil.deactivate(reconnectingSession);
      LifecycleUtil.deactivate(acceptor);
    }
  }

  private void sendMessageToAllSessions(String type, CDOSession session) throws InterruptedException
  {
    CDORemoteSession[] remoteSessions = session.getRemoteSessionManager().getRemoteSessions();
    for (CDORemoteSession remoteSession : remoteSessions)
    {
      remoteSession.sendMessage(new CDORemoteSessionMessage(type));
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class RemoteMessageListener extends CDORemoteSessionManager.EventAdapter
  {
    private List<String> received = new ArrayList<>();

    public RemoteMessageListener()
    {
    }

    @Override
    protected void onMessageReceived(CDORemoteSession remoteSession, CDORemoteSessionMessage message)
    {
      received.add(message.getType());
    }

    public void assertReceivedMessages(List<String> expected) throws InterruptedException
    {
      long timeout = System.currentTimeMillis() + 2000;
      while (System.currentTimeMillis() < timeout)
      {
        if (expected.equals(received))
        {
          break;
        }

        Thread.sleep(20);
      }

      assertEquals(expected, received);
    }
  }

  private void checkLockNotifications(CDOResource resource1, CDOResource resReconn) throws Exception
  {
    CDOView viewReconn = resReconn.cdoView();
    assertEquals(true, viewReconn.options().isLockNotificationEnabled());
    assertNull(resReconn.cdoLockState().getWriteLockOwner());

    TestListener listener = new TestListener(viewReconn);

    resource1.cdoWriteLock().lock(1000);

    listener.assertEvent(CDOViewLocksChangedEvent.class, e -> {
      assertNotNull(resReconn.cdoLockState().getWriteLockOwner());
      assertEquals(((InternalCDOView)resource1.cdoView()).getLockOwner(), resReconn.cdoLockState().getWriteLockOwner());
    });

    listener.clearEvents();

    resource1.cdoWriteLock().unlock();

    listener.assertEvent(CDOViewLocksChangedEvent.class, e -> {
      assertNull(resReconn.cdoLockState().getWriteLockOwner());
    });
  }

  public void testReconnectTwice() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction = session1.openTransaction();
    CDOResource[] resource1 = { transaction.createResource(getResourcePath("res0")), transaction.createResource(getResourcePath("res1")) };
    resource1[0].getContents().add(getModel1Factory().createCategory());
    resource1[1].getContents().add(getModel1Factory().createCategory());
    transaction.commit();

    ITCPAcceptor acceptor = null;
    CDONet4jSession reconnectingSession = null;
    InternalRepository repository = null;

    try
    {
      IManagedContainer serverContainer = getServerContainer();
      dumpEvents(serverContainer);

      acceptor = TCPUtil.getAcceptor(serverContainer, ADDRESS2);
      dumpEvents(acceptor);

      String repositoryName = session1.getRepositoryInfo().getName();
      IManagedContainer clientContainer = getClientContainer();
      dumpEvents(clientContainer);

      // See https://www.eclipse.org/forums/index.php/mv/msg/1111831
      repository = getRepository(repositoryName);
      repository.getSessionManager().setAuthenticator(new IAuthenticator()
      {
        @Override
        public void authenticate(String userID, char[] password) throws SecurityException
        {
          if (!"Eike".equals(userID))
          {
            throw new NotAuthenticatedException();
          }
        }
      });

      ReconnectingCDOSessionConfiguration configuration = CDONet4jUtil.createReconnectingSessionConfiguration(ADDRESS2, repositoryName, clientContainer);
      configuration.setHeartBeatEnabled(true);

      // See https://www.eclipse.org/forums/index.php/mv/msg/1111831
      configuration.setCredentialsProvider(new PasswordCredentialsProvider("Eike", "irrelevant"));
      configuration.setActivateOnOpen(true);
      configuration.setPassiveUpdateEnabled(true);
      configuration.setPassiveUpdateMode(PassiveUpdateMode.ADDITIONS);

      reconnectingSession = (CDONet4jSession)openSession(configuration);
      dumpEvents(reconnectingSession);

      for (int i = 0; i < 2; i++)
      {
        IOUtil.OUT().println();
        IOUtil.OUT().println("=================================================================");
        IOUtil.OUT().println("                          Run " + (i + 1));
        IOUtil.OUT().println("=================================================================");
        IOUtil.OUT().println();

        if (i == 1)
        {
          System.out.println();
        }

        CountDownLatch recoveryStarted = new CountDownLatch(1);
        CountDownLatch recoveryFinished = new CountDownLatch(1);
        reconnectingSession.addListener(new IListener()
        {
          @Override
          public void notifyEvent(IEvent event)
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

        IConnector connector2 = (IConnector)reconnectingSession.options().getNet4jProtocol().getChannel().getMultiplexer();
        dumpEvents(connector2);

        CDOView view = reconnectingSession.openView();
        CDOResource resource = view.getResource(getResourcePath("res" + i));
        assertEquals(1, resource.getContents().size());

        resource1[i].getContents().add(getModel1Factory().createCategory());
        commitAndSync(transaction, view);
        assertEquals(2, resource.getContents().size());

        IOUtil.OUT().println();
        IOUtil.OUT().println("Deactivating acceptor...");
        LifecycleUtil.deactivate(acceptor);
        await(recoveryStarted);

        IOUtil.OUT().println();
        IOUtil.OUT().println("Reactivating acceptor...");
        acceptor = TCPUtil.getAcceptor(serverContainer, ADDRESS2);
        dumpEvents(acceptor);
        await(recoveryFinished);

        IOUtil.OUT().println();
        IOUtil.OUT().println("Committing...");
        resource1[i].getContents().add(getModel1Factory().createCategory());
        commitAndSync(transaction, view);
        assertEquals(3, resource.getContents().size());
      }
    }
    catch (Exception ex)
    {
      ex.printStackTrace();
      throw ex;
    }
    finally
    {
      repository.getSessionManager().setAuthenticationServer(null);

      IOUtil.OUT().println();
      IOUtil.OUT().println("Finally...");
      LifecycleUtil.deactivate(reconnectingSession);
      LifecycleUtil.deactivate(acceptor);
    }
  }

  public void testReconnectWithCommitsWhileUnconnected() throws Exception
  {
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

      CountDownLatch recoveryStarted = new CountDownLatch(1);
      CountDownLatch recoveryFinished = new CountDownLatch(1);
      session2.addListener(new IListener()
      {
        @Override
        public void notifyEvent(IEvent event)
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
      await(recoveryStarted);

      IOUtil.OUT().println();
      IOUtil.OUT().println("Committing...");
      resource1.getContents().add(getModel1Factory().createCategory());
      transaction.commit();

      IOUtil.OUT().println();
      IOUtil.OUT().println("Reactivating acceptor...");
      acceptor = TCPUtil.getAcceptor(serverContainer, ADDRESS2);
      dumpEvents(acceptor);
      await(recoveryFinished);

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
      CDOBranch branch = session.getBranchManager().getBranch(9999999); // Will be in state PROXY.

      try
      {
        new ThreadTimeOuter()
        {
          @Override
          public void run()
          {
            // Resolve (load) the proxy.
            // This would hang without the fix in RecoveringExceptionHandler.handleException().
            branch.getName();
          }
        }.assertNoTimeOut();

        fail("RemoteException expected");
      }
      catch (RemoteException expected)
      {
        assertInstanceOf(CDOBranchDoesNotExistException.class, expected.getCause());
      }
    }
    finally
    {
      LifecycleUtil.deactivate(session);
      LifecycleUtil.deactivate(acceptor);
    }
  }
}
