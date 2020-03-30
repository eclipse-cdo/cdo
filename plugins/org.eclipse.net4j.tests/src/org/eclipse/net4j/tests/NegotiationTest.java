/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.tests;

import org.eclipse.net4j.ITransportConfig;
import org.eclipse.net4j.TransportInjector;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.ConnectorException;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.tests.bundle.OM;
import org.eclipse.net4j.tests.config.AbstractConfigTest;
import org.eclipse.net4j.tests.config.Net4jTestSuite.ExcludedConfig;
import org.eclipse.net4j.tests.config.TestConfig.JVM;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.ChallengeNegotiator;
import org.eclipse.net4j.util.security.IPasswordCredentials;
import org.eclipse.net4j.util.security.NegotiationException;
import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.net4j.util.security.Randomizer;
import org.eclipse.net4j.util.security.ResponseNegotiator;
import org.eclipse.net4j.util.security.UserManager;

import org.eclipse.spi.net4j.Connector;
import org.eclipse.spi.net4j.InternalAcceptor;
import org.eclipse.spi.net4j.InternalConnector;

/**
 * @author Eike Stepper
 */
@ExcludedConfig(JVM.class)
public class NegotiationTest extends AbstractConfigTest
{
  private static final String USER_ID = "stepper"; //$NON-NLS-1$

  private static final String INVALID_USER_ID = "invalid"; //$NON-NLS-1$

  private static final char[] PASSWORD = "eike2008".toCharArray(); //$NON-NLS-1$

  private static final char[] INVALID_PASSWORD = "invalid".toCharArray(); //$NON-NLS-1$

  private static final PasswordCredentials CREDENTIALS = new PasswordCredentials(USER_ID, PASSWORD);

  public void testNegotiationSuccess() throws Exception
  {
    acceptorContainer.addPostProcessor(new AcceptorNegotiatorInjector(USER_ID, PASSWORD));
    connectorContainer.addPostProcessor(new ConnectorNegotiatorInjector(CREDENTIALS));

    for (int i = 0; i < 5; i++)
    {
      IOUtil.OUT().println(" RUN = " + i); //$NON-NLS-1$

      IAcceptor acceptor = getAcceptor();

      IConnector connector = getConnector();
      connector.waitForConnection(DEFAULT_TIMEOUT);

      IChannel clientChannel = connector.openChannel();
      assertEquals(USER_ID, clientChannel.getUserID());

      IConnector serverConnector = acceptor.getElements()[0];
      IChannel serverChannel = serverConnector.getElements()[0];
      assertEquals(USER_ID, serverChannel.getUserID());

      stopTransport();
      sleep(1000);
    }
  }

  public void testInvalidUser() throws Exception
  {
    acceptorContainer.addPostProcessor(new AcceptorNegotiatorInjector(INVALID_USER_ID, PASSWORD));
    connectorContainer.addPostProcessor(new ConnectorNegotiatorInjector(CREDENTIALS));

    getAcceptor();

    IConnector connector = getConnector(false);

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
    acceptorContainer.addPostProcessor(new AcceptorNegotiatorInjector(USER_ID, INVALID_PASSWORD));
    connectorContainer.addPostProcessor(new ConnectorNegotiatorInjector(CREDENTIALS));

    getAcceptor();

    IConnector connector = getConnector(false);

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
    getAcceptor();

    Connector connector = (Connector)getConnector(false);
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
    getAcceptor();
    Connector connector = (Connector)getConnector();

    try
    {
      connector.getConfig().setNegotiator(new ResponseNegotiator());
      fail("IllegalStateException expected"); //$NON-NLS-1$
    }
    catch (IllegalStateException ex)
    {
      OM.LOG.info("Expected IllegalStateException:", ex); //$NON-NLS-1$
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class AcceptorNegotiatorInjector extends TransportInjector
  {
    private Randomizer randomizer;

    private UserManager userManager;

    private ChallengeNegotiator challengeNegotiator;

    public AcceptorNegotiatorInjector(String userID, char[] password)
    {
      randomizer = new Randomizer();
      randomizer.activate();

      userManager = new UserManager();
      userManager.activate();
      userManager.addUser(userID, password);

      challengeNegotiator = new ChallengeNegotiator();
      challengeNegotiator.setRandomizer(randomizer);
      challengeNegotiator.setUserManager(userManager);
      challengeNegotiator.activate();
    }

    @Override
    protected void processAcceptor(IManagedContainer container, String factoryType, String description, InternalAcceptor acceptor)
    {
      ITransportConfig config = acceptor.getConfig();
      if (config.getNegotiator() == null)
      {
        config.setNegotiator(challengeNegotiator);
      }
    }

    @Override
    protected void processConnector(IManagedContainer container, String factoryType, String description, InternalConnector connector)
    {
      // Do nothing.
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class ConnectorNegotiatorInjector extends TransportInjector
  {
    private PasswordCredentialsProvider credentialsProvider;

    private ResponseNegotiator responseNegotiator;

    public ConnectorNegotiatorInjector(IPasswordCredentials credentials)
    {
      credentialsProvider = new PasswordCredentialsProvider(credentials);
      LifecycleUtil.activate(credentialsProvider);

      responseNegotiator = new ResponseNegotiator();
      responseNegotiator.setCredentialsProvider(credentialsProvider);
      responseNegotiator.activate();
    }

    @Override
    protected void processAcceptor(IManagedContainer container, String factoryType, String description, InternalAcceptor acceptor)
    {
      // Do nothing.
    }

    @Override
    protected void processConnector(IManagedContainer container, String factoryType, String description, InternalConnector connector)
    {
      ITransportConfig config = connector.getConfig();
      if (config.getNegotiator() == null)
      {
        config.setNegotiator(responseNegotiator);
      }
    }
  }
}
