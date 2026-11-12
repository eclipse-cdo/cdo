/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.security.NoPermissionException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.expressions.ExpressionsFactory;
import org.eclipse.emf.cdo.expressions.IntValue;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.security.Access;
import org.eclipse.emf.cdo.security.Realm;
import org.eclipse.emf.cdo.security.Role;
import org.eclipse.emf.cdo.security.SecurityFactory;
import org.eclipse.emf.cdo.security.User;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.embedded.CDOEmbeddedRepositoryConfig;
import org.eclipse.emf.cdo.server.mem.MEMStoreUtil;
import org.eclipse.emf.cdo.server.security.ISecurityManager;
import org.eclipse.emf.cdo.server.security.SecurityManagerUtil;
import org.eclipse.emf.cdo.server.spi.security.InternalSecurityManager;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.IJVMAcceptor;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.signal.IndicationWithResponse;
import org.eclipse.net4j.signal.RequestWithConfirmation;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.signal.SignalReactor;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.security.PasswordCredentials;
import org.eclipse.net4j.util.security.PasswordCredentialsProvider;
import org.eclipse.net4j.ws.IWSAcceptor;
import org.eclipse.net4j.ws.WSUtil;
import org.eclipse.net4j.ws.jetty.server.JettyWSUtil;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeHandler;

import org.eclipse.spi.net4j.ServerProtocolFactory;

import org.junit.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Exercises Central-coordinate consumer paths for the December runtime bundles.
 *
 * @author Eike Stepper
 */
public class ConsumerIntegrationTest
{
  private static final String ECHO_ACCEPTOR = "central-consumer-echo";

  private static final int ECHO_VALUE = 174;

  @Test
  public void jvmTransportCarriesARequestAndResponse() throws Exception
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    container.registerFactory(new EchoProtocol.Factory());

    IJVMAcceptor acceptor = JVMUtil.getAcceptor(container, ECHO_ACCEPTOR); 
    LifecycleUtil.activate(acceptor);

    IConnector connector = JVMUtil.getConnector(container, ECHO_ACCEPTOR);
    LifecycleUtil.activate(connector);

    EchoProtocol protocol = new EchoProtocol();
    try
    {
      protocol.open(connector);
      assertEquals(ECHO_VALUE, new EchoProtocol.IntRequest(protocol, ECHO_VALUE).send().intValue());
    }
    finally
    {
      protocol.close();
      LifecycleUtil.deactivate(connector);
      LifecycleUtil.deactivate(acceptor);
      LifecycleUtil.deactivate(container);
    }
  }

  @Test
  public void webSocketJettyTransportCarriesANet4jRequestAndResponse() throws Exception
  {
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    container.registerFactory(new EchoProtocol.Factory());

    IWSAcceptor acceptor = WSUtil.getAcceptor(container, ECHO_ACCEPTOR);
    LifecycleUtil.activate(acceptor);

    Server server = new Server();
    ServerConnector serverConnector = new ServerConnector(server);
    serverConnector.setHost("127.0.0.1");
    serverConnector.setPort(0);
    server.addConnector(serverConnector);

    ContextHandler context = new ContextHandler("/");
    WebSocketUpgradeHandler upgradeHandler = JettyWSUtil.createUpgradeHandler(server, context, "/net4j/*", acceptor);
    context.setHandler(upgradeHandler);
    server.setHandler(context);

    IConnector connector = null;
    EchoProtocol protocol = null;
    try
    {
      server.start();
      URI serviceURI = new URI("ws://127.0.0.1:" + serverConnector.getLocalPort() + "/net4j");
      connector = WSUtil.getConnector(container, serviceURI, ECHO_ACCEPTOR);
      LifecycleUtil.activate(connector);

      protocol = new EchoProtocol();
      protocol.open(connector);
      assertEquals(ECHO_VALUE, new EchoProtocol.IntRequest(protocol, ECHO_VALUE).send().intValue());
    }
    finally
    {
      if (protocol != null)
      {
        protocol.close();
      }

      try
      {
        LifecycleUtil.deactivate(connector);
      }
      finally
      {
        try
        {
          server.stop();
        }
        finally
        {
          LifecycleUtil.deactivate(acceptor);
          LifecycleUtil.deactivate(container);
        }
      }
    }
  }

  @Test
  public void embeddedServerCommitsAndReloadsAResource() throws Exception
  {
    String repositoryName = "central-consumer-embedded";
    String resourcePath = "/central-consumer/embedded";
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    EmbeddedConfig config = new EmbeddedConfig(repositoryName, container);

    try
    {
      LifecycleUtil.activate(config);

      try (CDONet4jSession session = config.openClientSession())
      {
        CDOTransaction transaction = session.openTransaction();
        try
        {
          transaction.createResource(resourcePath);
          CDOCommitInfo commitInfo = transaction.commit();
          assertNotNull(commitInfo);
        }
        finally
        {
          transaction.close();
        }
      }

      try (CDONet4jSession session = config.openClientSession())
      {
        CDOView view = session.openView();
        try
        {
          CDOResource resource = view.getResource(resourcePath);
          assertNotNull(resource);
          assertEquals(resourcePath, resource.getPath());
        }
        finally
        {
          view.close();
        }
      }
    }
    finally
    {
      LifecycleUtil.deactivate(config);
      LifecycleUtil.deactivate(container);
    }
  }

  @Test
  public void expressionsAndSecurityModelsExposeTheirPublicFactories()
  {
    IntValue expression = ExpressionsFactory.eINSTANCE.createIntValue();
    expression.setLiteral(ECHO_VALUE);
    assertEquals(Integer.valueOf(ECHO_VALUE), expression.getLiteral());

    Realm realm = SecurityFactory.eINSTANCE.createRealm("consumer", Access.READ);
    Role reader = realm.addRole("consumer-reader");
    User user = realm.addUser(new PasswordCredentials("consumer-reader", "secret"));
    user.getRoles().add(reader);
    assertNotNull(realm.getRole("consumer-reader"));
    assertEquals("consumer-reader", user.getId());
  }
 
  @Test
  public void serverSecurityAuthenticatesAUserAndRejectsUnauthorizedWrites() throws Exception
  {
    String repositoryName = "central-consumer-secure";
    String acceptorName = "cdo_server_repo_" + repositoryName;
    String resourcePath = "/central-consumer/forbidden";
    PasswordCredentials credentials = new PasswordCredentials("read-only", "secret");
    IManagedContainer container = ContainerUtil.createInitializedContainer();
    IJVMAcceptor acceptor = null;
    IRepository repository = null;
    ISecurityManager securityManager = null;
    IConnector connector = null;
    CDOSession session = null;

    try
    {
      acceptor = JVMUtil.getAcceptor(container, acceptorName);
      LifecycleUtil.activate(acceptor);

      IStore store = MEMStoreUtil.createMEMStore();
      Map<String, String> properties = new HashMap<>();
      properties.put(IRepository.Props.SUPPORTING_BRANCHES, "false");
      properties.put(IRepository.Props.SUPPORTING_AUDITS, "false");
      repository = CDOServerUtil.createRepository(repositoryName, store, properties);
      CDOServerUtil.addRepository(container, repository);

      InternalSecurityManager manager = (InternalSecurityManager)SecurityManagerUtil.createSecurityManager("security", container);
      manager.setRepository((InternalRepository)repository);
      securityManager = manager;
      LifecycleUtil.activate(manager);
      manager.modify(realm -> {
        realm.setDefaultAccess(Access.READ);
        realm.addUser(credentials);
      }, true);

      CDONet4jSessionConfiguration sessionConfiguration = CDONet4jUtil.createNet4jSessionConfiguration();
      connector = JVMUtil.getConnector(container, acceptorName);
      LifecycleUtil.activate(connector);
      sessionConfiguration.setConnector(connector);
      sessionConfiguration.setRepositoryName(repositoryName);
      sessionConfiguration.setCredentialsProvider(new PasswordCredentialsProvider(credentials));
      session = sessionConfiguration.openNet4jSession();
      assertEquals(credentials.getUserID(), session.getUserID());

      CDOTransaction transaction = session.openTransaction();
      try
      {
        assertThrows(NoPermissionException.class, () -> transaction.createResource(resourcePath));
      }
      finally
      {
        transaction.close();
      }
    }
    finally
    {
      try
      {
        LifecycleUtil.deactivate(session);
      }
      finally
      {
        try
        {
          LifecycleUtil.deactivate(connector);
        }
        finally
        {
          try
          {
            LifecycleUtil.deactivate(securityManager);
          }
          finally
          {
            try
            {
              LifecycleUtil.deactivate(repository);
            }
            finally
            {
              try
              {
                LifecycleUtil.deactivate(acceptor);
              }
              finally
              {
                LifecycleUtil.deactivate(container);
              }
            }
          }
        }
      }
    }
  }

  /**
   * Provides an in-memory repository configuration for the embedded consumer test.
   *
   * @author Eike Stepper
   */
  private static final class EmbeddedConfig extends CDOEmbeddedRepositoryConfig
  {
    private final IManagedContainer container;

    private EmbeddedConfig(String repositoryName, IManagedContainer container)
    {
      super(repositoryName);
      this.container = container;
    }

    @Override
    public IManagedContainer getContainer()
    {
      return container;
    }

    @Override
    public IStore createStore(IManagedContainer container)
    {
      return MEMStoreUtil.createMEMStore();
    }

    @Override
    public IConnector createConnector(IManagedContainer container)
    {
      return JVMUtil.getConnector(container, "cdo_embedded_repo_" + getRepositoryName());
    }

    @Override
    public void initProperties(IManagedContainer container, Map<String, String> properties)
    {
      properties.put(IRepository.Props.SUPPORTING_BRANCHES, "false");
      properties.put(IRepository.Props.SUPPORTING_AUDITS, "false");
    }
  }

  /**
   * Carries the consumer's deterministic integer echo exchange over Net4j transports.
   *
   * @author Eike Stepper
   */
  private static final class EchoProtocol extends SignalProtocol<Object>
  {
    private static final String NAME = "central.consumer.echo";

    private static final short SIGNAL_ID = 1;

    private EchoProtocol()
    {
      super(NAME);
    }

    @Override
    protected SignalReactor createSignalReactor(short signalID)
    {
      if (signalID == SIGNAL_ID)
      {
        return new IndicationWithResponse(this, SIGNAL_ID)
        {
          private int value;

          @Override
          protected void indicating(ExtendedDataInputStream in) throws Exception
          {
            value = in.readInt();
          }

          @Override
          protected void responding(ExtendedDataOutputStream out) throws Exception
          {
            out.writeInt(value);
          }
        };
      }

      return super.createSignalReactor(signalID);
    }

    /**
     * Creates the consumer echo protocol on the server side.
     *
     * @author Eike Stepper
     */
    private static final class Factory extends ServerProtocolFactory
    {
      private Factory()
      {
        super(NAME);
      }

      @Override
      public EchoProtocol create(String description) throws ProductCreationException
      {
        return new EchoProtocol();
      }
    }

    /**
     * Sends one integer and reads its echo response.
     *
     * @author Eike Stepper
     */
    private static final class IntRequest extends RequestWithConfirmation<Integer>
    {
      private final int value;

      private IntRequest(EchoProtocol protocol, int value)
      {
        super(protocol, SIGNAL_ID);
        this.value = value;
      }

      @Override
      protected void requesting(ExtendedDataOutputStream out) throws Exception
      {
        out.writeInt(value);
      }

      @Override
      protected Integer confirming(ExtendedDataInputStream in) throws Exception
      {
        return in.readInt();
      }
    }
  }
}
