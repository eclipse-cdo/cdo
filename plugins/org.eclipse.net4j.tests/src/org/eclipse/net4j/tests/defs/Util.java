/*
 * Copyright (c) 2008, 2011, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests.defs;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBufferPool;
import org.eclipse.net4j.internal.tcp.TCPAcceptor;
import org.eclipse.net4j.internal.tcp.TCPClientConnector;
import org.eclipse.net4j.internal.tcp.TCPConnector;
import org.eclipse.net4j.internal.tcp.TCPSelector;
import org.eclipse.net4j.internal.util.concurrent.ThreadPool;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.ChallengeNegotiator;
import org.eclipse.net4j.util.security.INegotiator;
import org.eclipse.net4j.util.security.IRandomizer;
import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.net4j.util.security.Randomizer;
import org.eclipse.net4j.util.security.ResponseNegotiator;
import org.eclipse.net4j.util.security.UserManager;

import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
class Util
{
  public static TCPAcceptor createTCPAcceptor(String host, int port, long timeout)
  {
    TCPAcceptor acceptor = new TCPAcceptor();
    acceptor.setStartSynchronously(true);
    acceptor.setSynchronousStartTimeout(timeout);
    acceptor.getConfig().setBufferProvider(createBufferPool());
    acceptor.getConfig().setReceiveExecutor(createThreadPool());
    acceptor.setSelector(createTCPSelector());
    acceptor.setAddress(host);
    acceptor.setPort(port);

    LifecycleUtil.activate(acceptor);

    return acceptor;
  }

  private static IRandomizer createRandomizer()
  {
    Randomizer randomizer = new Randomizer();
    LifecycleUtil.activate(randomizer);

    return randomizer;
  }

  public static void addNegotiator(String userId, String password, TCPAcceptor tcpAcceptor)
  {
    UserManager userManager = new UserManager();
    LifecycleUtil.activate(userManager);
    userManager.addUser(userId, password.toCharArray());

    ChallengeNegotiator challengeNegotiator = new ChallengeNegotiator();
    challengeNegotiator.setRandomizer(createRandomizer());
    challengeNegotiator.setUserManager(userManager);
    LifecycleUtil.activate(challengeNegotiator);

    tcpAcceptor.getConfig().setNegotiator(challengeNegotiator);
  }

  public static void removeNegotiator(TCPAcceptor tcpAcceptor)
  {
    INegotiator negotiator = tcpAcceptor.getConfig().getNegotiator();
    tcpAcceptor.getConfig().setNegotiator(null);
    LifecycleUtil.deactivate(negotiator);
  }

  public static TCPConnector createTCPClientConnector(String host, int port, TCPSelector tcpSelector)
  {
    TCPClientConnector tcpConnector = new TCPClientConnector();
    tcpConnector.getConfig().setBufferProvider(createBufferPool());
    tcpConnector.getConfig().setReceiveExecutor(createThreadPool());
    tcpConnector.setSelector(tcpSelector);
    tcpConnector.setHost(host);
    tcpConnector.setPort(port);

    return tcpConnector;
  }

  public static void addNegotiator(String userId, String password, TCPConnector tcpConnector)
  {
    PasswordCredentials passwordCredentials = new PasswordCredentials(userId, password.toCharArray());
    PasswordCredentialsProvider credentialsProvider = new PasswordCredentialsProvider(passwordCredentials);
    LifecycleUtil.activate(credentialsProvider);

    tcpConnector.getConfig().setNegotiator(createResponseNegotiator(credentialsProvider));
  }

  public static ResponseNegotiator createResponseNegotiator(PasswordCredentialsProvider credentialsProvider)
  {
    ResponseNegotiator responseNegotiator = new ResponseNegotiator();
    responseNegotiator.setCredentialsProvider(credentialsProvider);
    LifecycleUtil.activate(responseNegotiator);
    return responseNegotiator;
  }

  public static void removeNegotiator(TCPConnector tcpConnector)
  {
    INegotiator negotiator = tcpConnector.getConfig().getNegotiator();
    tcpConnector.setNegotiator(null);
    LifecycleUtil.deactivate(negotiator);
  }

  public static TCPSelector createTCPSelector()
  {
    TCPSelector selector = new TCPSelector();
    LifecycleUtil.activate(selector);
    return selector;
  }

  public static IBufferPool createBufferPool()
  {
    IBufferPool bufferPool = Net4jUtil.createBufferPool();
    LifecycleUtil.activate(bufferPool);
    return bufferPool;
  }

  public static ExecutorService createThreadPool()
  {
    return ThreadPool.create();
  }
}
