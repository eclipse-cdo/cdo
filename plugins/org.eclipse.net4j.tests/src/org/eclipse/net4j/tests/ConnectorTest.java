/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.tests;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBufferPool;
import org.eclipse.net4j.internal.tcp.TCPAcceptor;
import org.eclipse.net4j.internal.tcp.TCPClientConnector;
import org.eclipse.net4j.internal.tcp.TCPSelector;
import org.eclipse.net4j.tcp.ITCPSelector;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.ChallengeNegotiator;
import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.net4j.util.security.Randomizer;
import org.eclipse.net4j.util.security.ResponseNegotiator;
import org.eclipse.net4j.util.security.UserManager;
import org.eclipse.net4j.util.tests.AbstractOMTest;

import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Eike Stepper
 */
public class ConnectorTest extends AbstractOMTest
{
  private static final int TIMEOUT = 10000;

  private static final String USER_ID = "stepper";

  private static final char[] PASSWORD1 = "eike2008".toCharArray();

  private static final char[] PASSWORD2 = "invalid".toCharArray();

  private static final PasswordCredentials CREDENTIALS = new PasswordCredentials(USER_ID, PASSWORD1);

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
    LifecycleUtil.deactivate(connector);
    LifecycleUtil.deactivate(responseNegotiator);
    LifecycleUtil.deactivate(credentialsProvider);
    connector = null;
    responseNegotiator = null;
    credentialsProvider = null;

    LifecycleUtil.deactivate(acceptor);
    LifecycleUtil.deactivate(challengeNegotiator);
    LifecycleUtil.deactivate(userManager);
    LifecycleUtil.deactivate(randomizer);
    acceptor = null;
    challengeNegotiator = null;
    userManager = null;
    randomizer = null;

    LifecycleUtil.deactivate(selector);
    LifecycleUtil.deactivate(bufferPool);
    LifecycleUtil.deactivate(threadPool);
    selector = null;
    bufferPool = null;
    threadPool = null;
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

  public void testDeferredActivation100() throws Exception
  {
    for (int i = 0; i < 100; i++)
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
    userManager.addUser(USER_ID, PASSWORD1);

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
    // acceptor.setNegotiator(challengeNegotiator);
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
    // connector.setNegotiator(responseNegotiator);
    connector.setSelector(selector);
    connector.setHost("localhost");
    connector.setPort(2036);
    connector.activate();

    boolean connected = connector.waitForConnection(TIMEOUT);
    assertEquals(true, connected);
  }

  public void testNegotiationSuccess100() throws Exception
  {
    for (int i = 0; i < 100; i++)
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

  public void testNegotiationFailure() throws Exception
  {
    ExecutorService threadPool = Executors.newCachedThreadPool();
    LifecycleUtil.activate(threadPool);

    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    LifecycleUtil.activate(bufferPool);

    Randomizer randomizer = new Randomizer();
    randomizer.activate();

    UserManager userManager = new UserManager();
    userManager.activate();
    userManager.addUser(USER_ID, PASSWORD2);

    ChallengeNegotiator challengeNegotiator = new ChallengeNegotiator();
    challengeNegotiator.setRandomizer(randomizer);
    challengeNegotiator.setUserManager(userManager);
    challengeNegotiator.activate();

    TCPSelector selector = new TCPSelector();
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

    PasswordCredentialsProvider credentialsProvider = new PasswordCredentialsProvider(CREDENTIALS);
    LifecycleUtil.activate(credentialsProvider);

    ResponseNegotiator responseNegotiator = new ResponseNegotiator();
    responseNegotiator.setCredentialsProvider(credentialsProvider);
    responseNegotiator.activate();

    TCPClientConnector connector = new TCPClientConnector();
    connector.getConfig().setBufferProvider(bufferPool);
    connector.getConfig().setReceiveExecutor(threadPool);
    connector.getConfig().setNegotiator(responseNegotiator);
    connector.setSelector(selector);
    connector.setHost("localhost");
    connector.setPort(2036);
    connector.activate();

    boolean connected = connector.waitForConnection(TIMEOUT);
    assertEquals(false, connected);
  }
}
