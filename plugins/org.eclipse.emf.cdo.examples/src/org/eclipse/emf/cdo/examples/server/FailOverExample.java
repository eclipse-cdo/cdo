/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.server;

import org.eclipse.emf.cdo.common.CDOCommonRepository.Type;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCache;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionManagerImpl;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSessionConfiguration;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositorySynchronizer;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.net4j.CDONet4jServerUtil;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;
import org.eclipse.net4j.util.om.trace.PrintTraceHandler;

import org.h2.jdbcx.JdbcDataSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class FailOverExample
{
  public static final String TRANSPORT_TYPE = "tcp";

  protected int port;

  protected String name;

  protected boolean master;

  protected String peerHost;

  protected int peerPort;

  protected String peerRepository;

  protected transient IManagedContainer container;

  protected transient IRepository repository;

  protected transient IAcceptor acceptor;

  public FailOverExample(int port, String name, boolean master, String peerHost, int peerPort, String peerRepository)
  {
    this.port = port;
    this.name = name;
    this.master = master;
    this.peerHost = peerHost;
    this.peerPort = peerPort;
    this.peerRepository = peerRepository;

    enableLoggingAndTracing();
    container = createContainer();
  }

  public void init()
  {
    IStore store = createStore();
    Map<String, String> props = createProperties();
    IRepositorySynchronizer synchronizer = createRepositorySynchronizer();

    repository = CDOServerUtil.createFailoverParticipant(name, store, props, synchronizer, master);
    CDOServerUtil.addRepository(container, repository);

    connect();
  }

  public void run() throws Exception
  {
    for (;;)
    {
      System.out.println();
      System.out.println("Enter a command:");
      System.out.println("0 - exit");
      System.out.println("1 - connect repository to network");
      System.out.println("2 - disconnect repository from network");
      System.out.println("3 - dump repository infos");
      System.out.println("4 - set repository type MASTER");
      System.out.println("5 - set repository type BACKUP");
      System.out.print("root@" + name + ":~> ");

      BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
      String command = reader.readLine();

      if ("1".equals(command))
      {
        if (acceptor == null)
        {
          connect();
        }
        else
        {
          System.out.println("Already connected");
        }
      }
      else if ("2".equals(command))
      {
        if (acceptor != null)
        {
          disconnect();
        }
        else
        {
          System.out.println("Already disconnected");
        }
      }
      else if ("3".equals(command))
      {
        System.out.println();
        System.out.println(repository.getName() + ": " + repository.getType()
            + (repository.getType() == Type.BACKUP ? "|" + repository.getState() : ""));
      }
      else if ("4".equals(command))
      {
        if (repository.getType() == Type.BACKUP)
        {
          System.out.println("Setting repository type MASTER...");
          ((InternalRepository)repository).setType(Type.MASTER);
          System.out.println("Type is " + repository.getType());
        }
        else
        {
          System.out.println("Already MASTER");
        }
      }
      else if ("5".equals(command))
      {
        if (repository.getType() == Type.MASTER)
        {
          System.out.println("Setting repository type BACKUP...");
          ((InternalRepository)repository).setType(Type.BACKUP);
          System.out.println("Type is " + repository.getType());
        }
        else
        {
          System.out.println("Already BACKUP");
        }
      }
      else if ("0".equals(command))
      {
        System.out.println("Exiting...");
        break;
      }
      else
      {
        System.out.println("Unknown command");
      }
    }
  }

  public void done()
  {
    LifecycleUtil.deactivate(acceptor);
    LifecycleUtil.deactivate(repository);
    container.deactivate();
  }

  protected void connect()
  {
    System.out.println("Connecting to network...");
    acceptor = createAcceptor();
    System.out.println("Connected");
  }

  protected void disconnect()
  {
    System.out.println("Disconnecting from network...");
    LifecycleUtil.deactivate(acceptor);
    acceptor = null;
    System.out.println("Disconnected");
  }

  protected void enableLoggingAndTracing()
  {
    OMPlatform.INSTANCE.setDebugging(true);
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
    OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
  }

  protected IManagedContainer createContainer()
  {
    IManagedContainer container = ContainerUtil.createContainer();
    Net4jUtil.prepareContainer(container); // Register Net4j factories
    TCPUtil.prepareContainer(container); // Register TCP factories
    CDONet4jUtil.prepareContainer(container); // Register CDO client factories
    CDONet4jServerUtil.prepareContainer(container); // Register CDO server factories
    container.activate();
    return container;
  }

  protected IStore createStore()
  {
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:_database/" + name);

    IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(true, true);
    IDBAdapter dbAdapter = new H2Adapter();
    IDBConnectionProvider dbConnectionProvider = DBUtil.createConnectionProvider(dataSource);
    return CDODBUtil.createStore(mappingStrategy, dbAdapter, dbConnectionProvider);
  }

  protected Map<String, String> createProperties()
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(IRepository.Props.OVERRIDE_UUID, name);
    props.put(IRepository.Props.SUPPORTING_AUDITS, "true");
    props.put(IRepository.Props.SUPPORTING_BRANCHES, "true");
    return props;
  }

  protected IRepositorySynchronizer createRepositorySynchronizer()
  {
    CDOSessionConfigurationFactory factory = new CDOSessionConfigurationFactory()
    {
      public CDOSessionConfiguration createSessionConfiguration()
      {
        IConnector connector = createConnector();

        CDOSessionConfiguration configuration = CDONet4jUtil.createSessionConfiguration();
        configuration.setConnector(connector);
        configuration.setRepositoryName(peerRepository);
        InternalCDORevisionManager revisionManager = new CDORevisionManagerImpl();
        revisionManager.setCache(CDORevisionCache.NOOP);
        configuration.setRevisionManager(revisionManager);
        return configuration;
      }
    };

    IRepositorySynchronizer synchronizer = CDOServerUtil.createRepositorySynchronizer(factory);
    synchronizer.setRetryInterval(2);
    synchronizer.setRawReplication(true);
    synchronizer.setMaxRecommits(10);
    synchronizer.setRecommitInterval(2);
    return synchronizer;
  }

  protected IAcceptor createAcceptor()
  {
    return (IAcceptor)container.getElement("org.eclipse.net4j.acceptors", TRANSPORT_TYPE, "0.0.0.0:" + port);
  }

  protected IConnector createConnector()
  {
    String description = peerHost + ":" + peerPort;
    container.removeElement("org.eclipse.net4j.connectors", TRANSPORT_TYPE, description);
    return (IConnector)container.getElement("org.eclipse.net4j.connectors", TRANSPORT_TYPE, description);
  }

  /**
   * @author Eike Stepper
   */
  public static final class InitialMaster extends FailOverExample
  {
    public InitialMaster()
    {
      super(2036, "repo1", true, "localhost", 2037, "repo2");
    }

    public static void main(String[] args) throws Exception
    {
      FailOverExample example = new InitialMaster();
      example.init();
      example.run();
      example.done();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class InitialBackup extends FailOverExample
  {
    public InitialBackup()
    {
      super(2037, "repo2", false, "localhost", 2036, "repo1");
    }

    public static void main(String[] args) throws Exception
    {
      FailOverExample example = new InitialBackup();
      example.init();
      example.run();
      example.done();
    }
  }
}
