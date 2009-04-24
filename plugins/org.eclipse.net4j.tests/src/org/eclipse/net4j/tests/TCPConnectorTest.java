/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBufferPool;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.internal.tcp.TCPAcceptor;
import org.eclipse.net4j.internal.tcp.TCPClientConnector;
import org.eclipse.net4j.internal.tcp.TCPSelector;
import org.eclipse.net4j.tcp.ITCPSelector;
import org.eclipse.net4j.tests.bundle.OM;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.ChallengeNegotiator;
import org.eclipse.net4j.util.security.NegotiationException;
import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.net4j.util.security.Randomizer;
import org.eclipse.net4j.util.security.ResponseNegotiator;
import org.eclipse.net4j.util.security.UserManager;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import org.eclipse.spi.net4j.InternalChannel;

import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Eike Stepper
 */
public class TCPConnectorTest extends AbstractOMTest
{
  private static final int TIMEOUT = 10000;

  private static final String USER_ID = "stepper";

  private static final String INVALID_USER_ID = "crap";

  private static final char[] PASSWORD = "eike2008".toCharArray();

  private static final char[] INVALID_PASSWORD = "invalid".toCharArray();

  private static final PasswordCredentials CREDENTIALS = new PasswordCredentials(USER_ID, PASSWORD);

  private ExecutorService threadPool;

  private IBufferPool bufferPool;

  private TCPSelector selector;

  private TCPAcceptor acceptor;

  private TCPClientConnector connector;

  private Randomizer randomizer;

  private UserManager userManager;

  private ChallengeNegotiator challengeNegotiator;

  private PasswordCredentialsProvider credentialsProvider;

  private ResponseNegotiator responseNegotiator;

  @Override
  protected void doTearDown() throws Exception
  {
    cleanup();
    super.doTearDown();
  }

  private void cleanup() throws Exception
  {
    sleep(100);

    if (connector != null)
    {
      connector.close();
      connector = null;
    }

    if (responseNegotiator != null)
    {
      LifecycleUtil.deactivate(responseNegotiator);
      responseNegotiator = null;
    }

    if (credentialsProvider != null)
    {
      LifecycleUtil.deactivate(credentialsProvider);
      credentialsProvider = null;
    }

    if (acceptor != null)
    {
      acceptor.close();
      acceptor = null;
    }

    if (challengeNegotiator != null)
    {
      LifecycleUtil.deactivate(challengeNegotiator);
      challengeNegotiator = null;
    }

    if (userManager != null)
    {
      LifecycleUtil.deactivate(userManager);
      userManager = null;
    }

    if (randomizer != null)
    {
      LifecycleUtil.deactivate(randomizer);
      randomizer = null;
    }

    if (selector != null)
    {
      LifecycleUtil.deactivate(selector);
      selector = null;
    }

    if (bufferPool != null)
    {
      LifecycleUtil.deactivate(bufferPool);
      bufferPool = null;
    }

    if (threadPool != null)
    {
      LifecycleUtil.deactivate(threadPool);
      threadPool = null;
    }
  }

  public void testDeferredActivation() throws Exception
  {
    final long DELAY = 500L;
    threadPool = Executors.newCachedThreadPool();
    LifecycleUtil.activate(threadPool);

    bufferPool = Net4jUtil.createBufferPool();
    LifecycleUtil.activate(bufferPool);

    selector = new TCPSelector();
    selector.activate();

    acceptor = new TCPAcceptor()
    {
      @Override
      public void handleAccept(ITCPSelector selector, ServerSocketChannel serverSocketChannel)
      {
        ConcurrencyUtil.sleep(DELAY);
        super.handleAccept(selector, serverSocketChannel);
      }
    };

    acceptor.setStartSynchronously(true);
    acceptor.setSynchronousStartTimeout(TIMEOUT);
    acceptor.getConfig().setBufferProvider(bufferPool);
    acceptor.getConfig().setReceiveExecutor(threadPool);
    acceptor.setSelector(selector);
    acceptor.setAddress("0.0.0.0");
    acceptor.setPort(2036);
    acceptor.activate();

    connector = new TCPClientConnector();
    connector.getConfig().setBufferProvider(bufferPool);
    connector.getConfig().setReceiveExecutor(threadPool);
    connector.setSelector(selector);
    connector.setHost("localhost");
    connector.setPort(2036);
    connector.activate();
    assertEquals(false, connector.isActive());

    boolean connected = connector.waitForConnection(DELAY + TIMEOUT);
    assertEquals(true, connected);
    assertEquals(true, connector.isActive());
  }

  public void testDeferredActivation10() throws Exception
  {
    for (int i = 0; i < 10; i++)
    {
      IOUtil.OUT().println();
      IOUtil.OUT().println();
      IOUtil.OUT().println();
      IOUtil.OUT().println("#####################################################");
      IOUtil.OUT().println(" RUN = " + i);
      IOUtil.OUT().println("#####################################################");
      IOUtil.OUT().println();
      IOUtil.OUT().println();
      IOUtil.OUT().println();
      testDeferredActivation();
      cleanup();
    }
  }

  public void testNegotiationSuccess() throws Exception
  {
    threadPool = Executors.newCachedThreadPool();
    LifecycleUtil.activate(threadPool);

    bufferPool = Net4jUtil.createBufferPool();
    LifecycleUtil.activate(bufferPool);

    randomizer = new Randomizer();
    randomizer.activate();

    userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD);

    challengeNegotiator = new ChallengeNegotiator();
    challengeNegotiator.setRandomizer(randomizer);
    challengeNegotiator.setUserManager(userManager);
    challengeNegotiator.activate();

    selector = new TCPSelector();
    selector.activate();

    acceptor = new TCPAcceptor();
    acceptor.setStartSynchronously(true);
    acceptor.setSynchronousStartTimeout(TIMEOUT);
    acceptor.getConfig().setBufferProvider(bufferPool);
    acceptor.getConfig().setReceiveExecutor(threadPool);
    acceptor.getConfig().setNegotiator(challengeNegotiator);
    acceptor.setSelector(selector);
    acceptor.setAddress("0.0.0.0");
    acceptor.setPort(2036);
    acceptor.activate();

    credentialsProvider = new PasswordCredentialsProvider(CREDENTIALS);
    LifecycleUtil.activate(credentialsProvider);

    responseNegotiator = new ResponseNegotiator();
    responseNegotiator.setCredentialsProvider(credentialsProvider);
    responseNegotiator.activate();

    connector = new TCPClientConnector();
    connector.getConfig().setBufferProvider(bufferPool);
    connector.getConfig().setReceiveExecutor(threadPool);
    connector.getConfig().setNegotiator(responseNegotiator);
    connector.setSelector(selector);
    connector.setHost("localhost");
    connector.setPort(2036);
    connector.activate();

    boolean connected = connector.waitForConnection(TIMEOUT);
    assertEquals(true, connected);

    InternalChannel clientChannel = connector.openChannel();
    assertEquals(USER_ID, clientChannel.getUserID());

    IConnector serverConnector = acceptor.getElements()[0];
    IChannel serverChannel = serverConnector.getElements()[0];
    assertEquals(USER_ID, serverChannel.getUserID());

    System.out.println(serverChannel);
  }

  public void testNegotiationSuccess10() throws Exception
  {
    for (int i = 0; i < 10; i++)
    {
      IOUtil.OUT().println();
      IOUtil.OUT().println();
      IOUtil.OUT().println();
      IOUtil.OUT().println("#####################################################");
      IOUtil.OUT().println("                          RUN = " + i);
      IOUtil.OUT().println("#####################################################");
      IOUtil.OUT().println();
      IOUtil.OUT().println();
      IOUtil.OUT().println();
      testNegotiationSuccess();
      cleanup();
    }
  }

  public void testInvalidUser() throws Exception
  {
    threadPool = Executors.newCachedThreadPool();
    LifecycleUtil.activate(threadPool);

    bufferPool = Net4jUtil.createBufferPool();
    LifecycleUtil.activate(bufferPool);

    randomizer = new Randomizer();
    randomizer.activate();

    userManager = new UserManager();
    userManager.activate();
    userManager.addUser(INVALID_USER_ID, PASSWORD);

    challengeNegotiator = new ChallengeNegotiator();
    challengeNegotiator.setRandomizer(randomizer);
    challengeNegotiator.setUserManager(userManager);
    challengeNegotiator.activate();

    selector = new TCPSelector();
    selector.activate();

    acceptor = new TCPAcceptor();
    acceptor.setStartSynchronously(true);
    acceptor.setSynchronousStartTimeout(TIMEOUT);
    acceptor.getConfig().setBufferProvider(bufferPool);
    acceptor.getConfig().setReceiveExecutor(threadPool);
    acceptor.getConfig().setNegotiator(challengeNegotiator);
    acceptor.setSelector(selector);
    acceptor.setAddress("0.0.0.0");
    acceptor.setPort(2036);
    acceptor.activate();

    credentialsProvider = new PasswordCredentialsProvider(CREDENTIALS);
    LifecycleUtil.activate(credentialsProvider);

    responseNegotiator = new ResponseNegotiator();
    responseNegotiator.setCredentialsProvider(credentialsProvider);
    responseNegotiator.activate();

    connector = new TCPClientConnector();
    connector.getConfig().setBufferProvider(bufferPool);
    connector.getConfig().setReceiveExecutor(threadPool);
    connector.getConfig().setNegotiator(responseNegotiator);
    connector.setSelector(selector);
    connector.setHost("localhost");
    connector.setPort(2036);

    try
    {
      connector.connectAsync();
      connector.waitForConnection(TIMEOUT);
      fail("ConnectorException expected");
    }
    catch (ConnectorException ex)
    {
      OM.LOG.info("Expected ConnectorException:", ex);
      assertEquals(true, ex.getCause() instanceof NegotiationException);
    }
  }

  public void testInvalidPassword() throws Exception
  {
    threadPool = Executors.newCachedThreadPool();
    LifecycleUtil.activate(threadPool);

    bufferPool = Net4jUtil.createBufferPool();
    LifecycleUtil.activate(bufferPool);

    randomizer = new Randomizer();
    randomizer.activate();

    userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, INVALID_PASSWORD);

    challengeNegotiator = new ChallengeNegotiator();
    challengeNegotiator.setRandomizer(randomizer);
    challengeNegotiator.setUserManager(userManager);
    challengeNegotiator.activate();

    selector = new TCPSelector();
    selector.activate();

    acceptor = new TCPAcceptor();
    acceptor.setStartSynchronously(true);
    acceptor.setSynchronousStartTimeout(TIMEOUT);
    acceptor.getConfig().setBufferProvider(bufferPool);
    acceptor.getConfig().setReceiveExecutor(threadPool);
    acceptor.getConfig().setNegotiator(challengeNegotiator);
    acceptor.setSelector(selector);
    acceptor.setAddress("0.0.0.0");
    acceptor.setPort(2036);
    acceptor.activate();

    credentialsProvider = new PasswordCredentialsProvider(CREDENTIALS);
    LifecycleUtil.activate(credentialsProvider);

    responseNegotiator = new ResponseNegotiator();
    responseNegotiator.setCredentialsProvider(credentialsProvider);
    responseNegotiator.activate();

    connector = new TCPClientConnector();
    connector.getConfig().setBufferProvider(bufferPool);
    connector.getConfig().setReceiveExecutor(threadPool);
    connector.getConfig().setNegotiator(responseNegotiator);
    connector.setSelector(selector);
    connector.setHost("localhost");
    connector.setPort(2036);

    try
    {
      connector.connectAsync();
      connector.waitForConnection(TIMEOUT);
      fail("ConnectorException expected");
    }
    catch (ConnectorException ex)
    {
      OM.LOG.info("Expected ConnectorException:", ex);
      assertEquals(true, ex.getCause() instanceof NegotiationException);
    }
  }

  public void testNoNegotiator() throws Exception
  {
    threadPool = Executors.newCachedThreadPool();
    LifecycleUtil.activate(threadPool);

    bufferPool = Net4jUtil.createBufferPool();
    LifecycleUtil.activate(bufferPool);

    selector = new TCPSelector();
    selector.activate();

    acceptor = new TCPAcceptor();
    acceptor.setStartSynchronously(true);
    acceptor.setSynchronousStartTimeout(TIMEOUT);
    acceptor.getConfig().setBufferProvider(bufferPool);
    acceptor.getConfig().setReceiveExecutor(threadPool);
    acceptor.setSelector(selector);
    acceptor.setAddress("0.0.0.0");
    acceptor.setPort(2036);
    acceptor.activate();

    connector = new TCPClientConnector();
    connector.getConfig().setBufferProvider(bufferPool);
    connector.getConfig().setReceiveExecutor(threadPool);
    connector.setSelector(selector);
    connector.setHost("localhost");
    connector.setPort(2036);
    connector.setUserID("SHOULD_FAIL_LATER");

    try
    {
      connector.connect();
      fail("ConnectorException expected");
    }
    catch (ConnectorException ex)
    {
      OM.LOG.info("Expected ConnectorException:", ex);
      assertEquals(true, ex.getCause() instanceof IllegalStateException);
    }
  }

  public void testNegotiatorTooLate() throws Exception
  {
    threadPool = Executors.newCachedThreadPool();
    LifecycleUtil.activate(threadPool);

    bufferPool = Net4jUtil.createBufferPool();
    LifecycleUtil.activate(bufferPool);

    selector = new TCPSelector();
    selector.activate();

    acceptor = new TCPAcceptor();
    acceptor.setStartSynchronously(true);
    acceptor.setSynchronousStartTimeout(TIMEOUT);
    acceptor.getConfig().setBufferProvider(bufferPool);
    acceptor.getConfig().setReceiveExecutor(threadPool);
    acceptor.setSelector(selector);
    acceptor.setAddress("0.0.0.0");
    acceptor.setPort(2036);
    acceptor.activate();

    connector = new TCPClientConnector();
    connector.getConfig().setBufferProvider(bufferPool);
    connector.getConfig().setReceiveExecutor(threadPool);
    connector.setSelector(selector);
    connector.setHost("localhost");
    connector.setPort(2036);
    connector.connect();

    credentialsProvider = new PasswordCredentialsProvider(CREDENTIALS);
    LifecycleUtil.activate(credentialsProvider);

    responseNegotiator = new ResponseNegotiator();
    responseNegotiator.setCredentialsProvider(credentialsProvider);
    responseNegotiator.activate();

    try
    {
      connector.getConfig().setNegotiator(responseNegotiator);
      fail("IllegalStateException expected");
    }
    catch (IllegalStateException ex)
    {
      OM.LOG.info("Expected IllegalStateException:", ex);
    }
  }
}
