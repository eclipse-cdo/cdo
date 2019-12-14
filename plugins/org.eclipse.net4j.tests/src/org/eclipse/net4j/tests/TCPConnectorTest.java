/*
 * Copyright (c) 2008-2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Teerawat Chaiyakijpichet (No Magic Asia Ltd.) - SSL
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBufferPool;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.internal.tcp.TCPAcceptor;
import org.eclipse.net4j.internal.tcp.TCPAcceptorFactory;
import org.eclipse.net4j.internal.tcp.TCPClientConnector;
import org.eclipse.net4j.internal.tcp.TCPConnector;
import org.eclipse.net4j.internal.tcp.TCPConnectorFactory;
import org.eclipse.net4j.internal.tcp.TCPSelector;
import org.eclipse.net4j.internal.tcp.ssl.SSLAcceptor;
import org.eclipse.net4j.internal.tcp.ssl.SSLAcceptorFactory;
import org.eclipse.net4j.internal.tcp.ssl.SSLClientConnector;
import org.eclipse.net4j.internal.tcp.ssl.SSLConnectorFactory;
import org.eclipse.net4j.tcp.ITCPSelector;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.tests.bundle.OM;
import org.eclipse.net4j.util.collection.RoundRobinBlockingQueue;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.concurrent.ThreadPool;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.ChallengeNegotiator;
import org.eclipse.net4j.util.security.NegotiationException;
import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.net4j.util.security.Randomizer;
import org.eclipse.net4j.util.security.ResponseNegotiator;
import org.eclipse.net4j.util.security.UserManager;

import org.eclipse.spi.net4j.Channel;
import org.eclipse.spi.net4j.InternalChannel;

import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
 */
public class TCPConnectorTest extends AbstractTransportTest
{
  private static final int TIMEOUT = 10000;

  private static final int PORT = 2040;

  private static final String USER_ID = "stepper"; //$NON-NLS-1$

  private static final String INVALID_USER_ID = "invalid"; //$NON-NLS-1$

  private static final char[] PASSWORD = "eike2008".toCharArray(); //$NON-NLS-1$

  private static final char[] INVALID_PASSWORD = "invalid".toCharArray(); //$NON-NLS-1$

  private static final PasswordCredentials CREDENTIALS = new PasswordCredentials(USER_ID, PASSWORD);

  private ExecutorService threadPool;

  private IBufferPool bufferPool;

  private TCPSelector selector;

  private TCPAcceptor acceptor;

  private TCPConnector connector;

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

  private void provideTransport()
  {
    selector = new TCPSelector();

    if (useSSLTransport())
    {
      acceptor = new SSLAcceptor();
      container.putElement(SSLAcceptorFactory.PRODUCT_GROUP, SSLAcceptorFactory.TYPE, null, acceptor);

      // cannot use same container with the acceptor.
      connector = new SSLClientConnector();
      separateContainer.putElement(SSLConnectorFactory.PRODUCT_GROUP, SSLConnectorFactory.TYPE, null, acceptor);
    }
    else
    {
      acceptor = new TCPAcceptor();
      container.putElement(TCPAcceptorFactory.PRODUCT_GROUP, TCPUtil.FACTORY_TYPE, null, acceptor);

      connector = new TCPClientConnector();
      container.putElement(TCPConnectorFactory.PRODUCT_GROUP, TCPConnectorFactory.TYPE, null, acceptor);
    }
  }

  private void provideTransport(final long increaseDelayAcceptor)
  {
    selector = new TCPSelector();

    if (useSSLTransport())
    {
      acceptor = new SSLAcceptor()
      {
        @Override
        public void handleAccept(ITCPSelector selector, ServerSocketChannel serverSocketChannel)
        {
          ConcurrencyUtil.sleep(increaseDelayAcceptor);
          super.handleAccept(selector, serverSocketChannel);
        }
      };

      connector = new SSLClientConnector();
    }
    else
    {
      acceptor = new TCPAcceptor()
      {
        @Override
        public void handleAccept(ITCPSelector selector, ServerSocketChannel serverSocketChannel)
        {
          ConcurrencyUtil.sleep(increaseDelayAcceptor);
          super.handleAccept(selector, serverSocketChannel);
        }
      };

      connector = new TCPClientConnector();
    }
  }

  public void testDeferredActivation() throws Exception
  {
    final long DELAY = 500L;
    threadPool = ThreadPool.create();
    bufferPool = Net4jUtil.createBufferPool();
    LifecycleUtil.activate(bufferPool);

    provideTransport(DELAY);

    selector.activate();

    acceptor.setStartSynchronously(true);
    acceptor.setSynchronousStartTimeout(TIMEOUT);
    acceptor.getConfig().setBufferProvider(bufferPool);
    acceptor.getConfig().setReceiveExecutor(threadPool);
    acceptor.setSelector(selector);
    acceptor.setAddress("0.0.0.0"); //$NON-NLS-1$
    acceptor.setPort(PORT);
    acceptor.activate();

    connector.getConfig().setBufferProvider(bufferPool);
    connector.getConfig().setReceiveExecutor(threadPool);
    connector.setSelector(selector);
    connector.setHost("localhost"); //$NON-NLS-1$
    connector.setPort(PORT);
    connector.activate();
    // Can fail due to timing variations: assertEquals(false, connector.isActive());

    connector.waitForConnection(DEFAULT_TIMEOUT);
    assertEquals(true, connector.isActive());
  }

  public void testDeferredActivation10() throws Exception
  {
    for (int i = 0; i < 10; i++)
    {
      IOUtil.OUT().println();
      IOUtil.OUT().println();
      IOUtil.OUT().println();
      IOUtil.OUT().println("#####################################################"); //$NON-NLS-1$
      IOUtil.OUT().println(" RUN = " + i); //$NON-NLS-1$
      IOUtil.OUT().println("#####################################################"); //$NON-NLS-1$
      IOUtil.OUT().println();
      IOUtil.OUT().println();
      IOUtil.OUT().println();
      testDeferredActivation();
      cleanup();
    }
  }

  public void testNegotiationSuccess() throws Exception
  {
    threadPool = ThreadPool.create();
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

    provideTransport();

    selector.activate();

    acceptor.setStartSynchronously(true);
    acceptor.setSynchronousStartTimeout(TIMEOUT);
    acceptor.getConfig().setBufferProvider(bufferPool);
    acceptor.getConfig().setReceiveExecutor(threadPool);
    acceptor.getConfig().setNegotiator(challengeNegotiator);
    acceptor.setSelector(selector);
    acceptor.setAddress("0.0.0.0"); //$NON-NLS-1$
    acceptor.setPort(PORT);
    acceptor.activate();

    credentialsProvider = new PasswordCredentialsProvider(CREDENTIALS);
    LifecycleUtil.activate(credentialsProvider);

    responseNegotiator = new ResponseNegotiator();
    responseNegotiator.setCredentialsProvider(credentialsProvider);
    responseNegotiator.activate();

    connector.getConfig().setBufferProvider(bufferPool);
    connector.getConfig().setReceiveExecutor(threadPool);
    connector.getConfig().setNegotiator(responseNegotiator);
    connector.setSelector(selector);
    connector.setHost("localhost"); //$NON-NLS-1$
    connector.setPort(PORT);
    connector.activate();

    connector.waitForConnection(DEFAULT_TIMEOUT);

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
      IOUtil.OUT().println("#####################################################"); //$NON-NLS-1$
      IOUtil.OUT().println("                          RUN = " + i); //$NON-NLS-1$
      IOUtil.OUT().println("#####################################################"); //$NON-NLS-1$
      IOUtil.OUT().println();
      IOUtil.OUT().println();
      IOUtil.OUT().println();
      testNegotiationSuccess();
      cleanup();
    }
  }

  public void testInvalidUser() throws Exception
  {
    threadPool = ThreadPool.create();
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

    provideTransport();

    selector.activate();

    acceptor.setStartSynchronously(true);
    acceptor.setSynchronousStartTimeout(TIMEOUT);
    acceptor.getConfig().setBufferProvider(bufferPool);
    acceptor.getConfig().setReceiveExecutor(threadPool);
    acceptor.getConfig().setNegotiator(challengeNegotiator);
    acceptor.setSelector(selector);
    acceptor.setAddress("0.0.0.0"); //$NON-NLS-1$
    acceptor.setPort(PORT);
    acceptor.activate();

    credentialsProvider = new PasswordCredentialsProvider(CREDENTIALS);
    LifecycleUtil.activate(credentialsProvider);

    responseNegotiator = new ResponseNegotiator();
    responseNegotiator.setCredentialsProvider(credentialsProvider);
    responseNegotiator.activate();

    connector.getConfig().setBufferProvider(bufferPool);
    connector.getConfig().setReceiveExecutor(threadPool);
    connector.getConfig().setNegotiator(responseNegotiator);
    connector.setSelector(selector);
    connector.setHost("localhost"); //$NON-NLS-1$
    connector.setPort(PORT);

    try
    {
      connector.connectAsync();
      connector.waitForConnection(DEFAULT_TIMEOUT_EXPECTED);
      fail("ConnectorException expected"); //$NON-NLS-1$
    }
    catch (ConnectorException ex)
    {
      OM.LOG.info("Expected ConnectorException:", ex); //$NON-NLS-1$
      assertEquals(true, ex.getCause() instanceof NegotiationException);
    }
  }

  public void testInvalidPassword() throws Exception
  {
    threadPool = ThreadPool.create();
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

    provideTransport();

    selector.activate();

    acceptor.setStartSynchronously(true);
    acceptor.setSynchronousStartTimeout(TIMEOUT);
    acceptor.getConfig().setBufferProvider(bufferPool);
    acceptor.getConfig().setReceiveExecutor(threadPool);
    acceptor.getConfig().setNegotiator(challengeNegotiator);
    acceptor.setSelector(selector);
    acceptor.setAddress("0.0.0.0"); //$NON-NLS-1$
    acceptor.setPort(PORT);
    acceptor.activate();

    credentialsProvider = new PasswordCredentialsProvider(CREDENTIALS);
    LifecycleUtil.activate(credentialsProvider);

    responseNegotiator = new ResponseNegotiator();
    responseNegotiator.setCredentialsProvider(credentialsProvider);
    responseNegotiator.activate();

    connector.getConfig().setBufferProvider(bufferPool);
    connector.getConfig().setReceiveExecutor(threadPool);
    connector.getConfig().setNegotiator(responseNegotiator);
    connector.setSelector(selector);
    connector.setHost("localhost"); //$NON-NLS-1$
    connector.setPort(PORT);

    try
    {
      connector.connectAsync();
      connector.waitForConnection(DEFAULT_TIMEOUT_EXPECTED);
      fail("ConnectorException expected"); //$NON-NLS-1$
    }
    catch (ConnectorException ex)
    {
      OM.LOG.info("Expected ConnectorException:", ex); //$NON-NLS-1$
      assertEquals(true, ex.getCause() instanceof NegotiationException);
    }
  }

  public void testNoNegotiator() throws Exception
  {
    threadPool = ThreadPool.create();
    bufferPool = Net4jUtil.createBufferPool();
    LifecycleUtil.activate(bufferPool);

    provideTransport();

    selector.activate();

    acceptor.setStartSynchronously(true);
    acceptor.setSynchronousStartTimeout(TIMEOUT);
    acceptor.getConfig().setBufferProvider(bufferPool);
    acceptor.getConfig().setReceiveExecutor(threadPool);
    acceptor.setSelector(selector);
    acceptor.setAddress("0.0.0.0"); //$NON-NLS-1$
    acceptor.setPort(PORT);
    acceptor.activate();

    connector.getConfig().setBufferProvider(bufferPool);
    connector.getConfig().setReceiveExecutor(threadPool);
    connector.setSelector(selector);
    connector.setHost("localhost"); //$NON-NLS-1$
    connector.setPort(PORT);
    connector.setUserID("SHOULD_FAIL_LATER"); //$NON-NLS-1$

    try
    {
      connector.connect();
      fail("ConnectorException expected"); //$NON-NLS-1$
    }
    catch (ConnectorException ex)
    {
      OM.LOG.info("Expected ConnectorException:", ex); //$NON-NLS-1$
      assertEquals(true, ex.getCause() instanceof IllegalStateException);
    }
  }

  public void testNegotiatorTooLate() throws Exception
  {
    threadPool = ThreadPool.create();
    bufferPool = Net4jUtil.createBufferPool();
    LifecycleUtil.activate(bufferPool);

    provideTransport();

    selector.activate();

    acceptor.setStartSynchronously(true);
    acceptor.setSynchronousStartTimeout(TIMEOUT);
    acceptor.getConfig().setBufferProvider(bufferPool);
    acceptor.getConfig().setReceiveExecutor(threadPool);
    acceptor.setSelector(selector);
    acceptor.setAddress("0.0.0.0"); //$NON-NLS-1$
    acceptor.setPort(PORT);
    acceptor.activate();

    connector.getConfig().setBufferProvider(bufferPool);
    connector.getConfig().setReceiveExecutor(threadPool);
    connector.setSelector(selector);
    connector.setHost("localhost"); //$NON-NLS-1$
    connector.setPort(PORT);
    connector.connect();

    credentialsProvider = new PasswordCredentialsProvider(CREDENTIALS);
    LifecycleUtil.activate(credentialsProvider);

    responseNegotiator = new ResponseNegotiator();
    responseNegotiator.setCredentialsProvider(credentialsProvider);
    responseNegotiator.activate();

    try
    {
      connector.getConfig().setNegotiator(responseNegotiator);
      fail("IllegalStateException expected"); //$NON-NLS-1$
    }
    catch (IllegalStateException ex)
    {
      OM.LOG.info("Expected IllegalStateException:", ex); //$NON-NLS-1$
    }
  }

  public void testRoundRobinBlockingQueue() throws Exception
  {
    BlockingQueue<IChannel> queue = new RoundRobinBlockingQueue<>();

    Channel[] channels = new Channel[3];

    for (int i = 0; i < channels.length; i++)
    {
      Channel c = new Channel();
      c.setID((short)i);
      channels[i] = c;
    }

    assertEquals(true, queue.isEmpty());
    assertNull(queue.peek());
    assertNull(queue.poll());

    // Order will be 0000...1111...2222...
    for (int i = 0; i < channels.length; i++)
    {
      for (int j = 0; j < 10; j++)
      {
        queue.put(channels[i]);
      }
    }

    for (int i = 0; i < 30; i++)
    {
      IChannel peek1 = queue.peek();
      IChannel peek2 = queue.peek();
      assertSame(peek1, peek2);

      IChannel poll = queue.poll();
      // The order should be 012012012012...
      assertEquals(i % 3, poll.getID());
      assertSame(peek1, poll);
    }

    assertEquals(true, queue.isEmpty());
    assertNull(queue.peek());
    assertNull(queue.poll());
  }

  /**
   * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
   */
  public static final class TCP extends TCPConnectorTest
  {
    @Override
    protected boolean useJVMTransport()
    {
      return false;
    }

    @Override
    protected boolean useSSLTransport()
    {
      return false;
    }
  }

  /**
   * @author Teerawat Chaiyakijpichet (No Magic Asia Ltd.)
   */
  public static final class SSL extends TCPConnectorTest
  {
    @Override
    protected boolean useJVMTransport()
    {
      return false;
    }

    @Override
    protected boolean useSSLTransport()
    {
      return true;
    }
  }
}
