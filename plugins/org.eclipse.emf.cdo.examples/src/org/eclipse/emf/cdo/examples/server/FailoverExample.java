/*
 * Copyright (c) 2010-2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.util.RepositoryStateChangedEvent;
import org.eclipse.emf.cdo.common.util.RepositoryTypeChangedEvent;
import org.eclipse.emf.cdo.examples.company.CompanyFactory;
import org.eclipse.emf.cdo.examples.company.CompanyPackage;
import org.eclipse.emf.cdo.examples.company.Customer;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSessionRecoveryEvent;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositorySynchronizer;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.ISynchronizableRepository;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.net4j.CDONet4jServerUtil;
import org.eclipse.emf.cdo.server.net4j.FailoverAgent;
import org.eclipse.emf.cdo.server.net4j.FailoverMonitor;
import org.eclipse.emf.cdo.server.net4j.FailoverMonitor.AgentProtocol;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.spi.server.InternalFailoverParticipant;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.tcp.TCPUtil;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.PrintLogHandler;

import org.h2.jdbcx.JdbcDataSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class FailoverExample
{
  public static final String TRANSPORT_TYPE = "tcp";

  protected int port;

  protected String name;

  protected transient IManagedContainer container;

  protected transient IRepository repository;

  protected transient IAcceptor acceptor;

  static
  {
    // OMPlatform.INSTANCE.setDebugging(true);
    // OMPlatform.INSTANCE.addTraceHandler(PrintTraceHandler.CONSOLE);
    OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);
  }

  public FailoverExample()
  {
    container = createContainer();
  }

  public static IManagedContainer createContainer()
  {
    IManagedContainer container = ContainerUtil.createContainer();
    Net4jUtil.prepareContainer(container); // Register Net4j factories
    TCPUtil.prepareContainer(container); // Register TCP factories
    CDONet4jUtil.prepareContainer(container); // Register CDO client factories
    CDONet4jServerUtil.prepareContainer(container); // Register CDO server factories
    container.activate();
    return container;
  }

  public void init()
  {
    IStore store = createStore();
    Map<String, String> props = createProperties();

    repository = createRepository(store, props);
    CDOServerUtil.addRepository(container, repository);

    repository.addListener(new IListener()
    {
      @Override
      public void notifyEvent(IEvent event)
      {
        if (event instanceof RepositoryTypeChangedEvent)
        {
          RepositoryTypeChangedEvent e = (RepositoryTypeChangedEvent)event;
          System.out.println("Type changed to " + e.getNewType());
        }
        else if (event instanceof RepositoryStateChangedEvent)
        {
          RepositoryStateChangedEvent e = (RepositoryStateChangedEvent)event;
          System.out.println("State changed to " + e.getNewState());
        }
      }
    });

    connect();
  }

  public void run() throws Exception
  {
    for (;;)
    {
      System.out.println();
      System.out.println("Enter a command:");
      showMenu();
      System.out.println();

      String command = new BufferedReader(new InputStreamReader(System.in)).readLine();
      if (handleCommand(command))
      {
        break;
      }
    }
  }

  public void done()
  {
    LifecycleUtil.deactivate(acceptor);
    LifecycleUtil.deactivate(repository);
    container.deactivate();
  }

  protected void showMenu()
  {
    System.out.println("0 - exit");
    System.out.println("1 - connect repository to network");
    System.out.println("2 - disconnect repository from network");
    System.out.println("3 - dump repository infos");
  }

  protected boolean handleCommand(String command)
  {
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
      System.out.println(repository.getName() + ": " + repository.getType() + (repository.getType() == Type.BACKUP ? "|" + repository.getState() : ""));
    }
    else if ("0".equals(command))
    {
      System.out.println("Exiting...");
      return true;
    }
    else
    {
      System.out.println("Unknown command");
    }

    return false;
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

  protected IStore createStore()
  {
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:_database/" + name);

    IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(true, true);
    IDBAdapter dbAdapter = new H2Adapter();
    IDBConnectionProvider dbConnectionProvider = dbAdapter.createConnectionProvider(dataSource);
    return CDODBUtil.createStore(mappingStrategy, dbAdapter, dbConnectionProvider);
  }

  protected Map<String, String> createProperties()
  {
    Map<String, String> props = new HashMap<>();
    props.put(IRepository.Props.OVERRIDE_UUID, name);
    props.put(IRepository.Props.SUPPORTING_AUDITS, "true");
    props.put(IRepository.Props.SUPPORTING_BRANCHES, "true");
    return props;
  }

  protected abstract IRepository createRepository(IStore store, Map<String, String> props);

  protected IAcceptor createAcceptor()
  {
    return (IAcceptor)container.getElement("org.eclipse.net4j.acceptors", TRANSPORT_TYPE, "0.0.0.0:" + port);
  }

  protected IConnector createConnector(String description)
  {
    return Net4jUtil.getConnector(container, TRANSPORT_TYPE, description);
  }

  protected IRepositorySynchronizer createRepositorySynchronizer(String peerHost, int peerPort, String repositoryName)
  {
    CDOSessionConfigurationFactory factory = createSessionConfigurationFactory(peerHost + ":" + peerPort, repositoryName);

    IRepositorySynchronizer synchronizer = CDOServerUtil.createRepositorySynchronizer(factory);
    synchronizer.setRetryInterval(2);
    synchronizer.setMaxRecommits(10);
    synchronizer.setRecommitInterval(2);
    return synchronizer;
  }

  protected CDOSessionConfigurationFactory createSessionConfigurationFactory(final String connectorDescription, final String repositoryName)
  {
    return new CDOSessionConfigurationFactory()
    {
      @Override
      public CDONet4jSessionConfiguration createSessionConfiguration()
      {
        return FailoverExample.this.createSessionConfiguration(connectorDescription, repositoryName);
      }
    };
  }

  protected CDONet4jSessionConfiguration createSessionConfiguration(String connectorDescription, String repositoryName)
  {
    IConnector connector = createConnector(connectorDescription);

    CDONet4jSessionConfiguration configuration = CDONet4jUtil.createNet4jSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName(repositoryName);
    configuration.setRevisionManager(CDORevisionUtil.createRevisionManager(CDORevisionCache.NOOP));
    return configuration;
  }

  /**
   * @author Eike Stepper
   */
  public static class Unmonitored extends FailoverExample
  {
    protected boolean master;

    protected String peerHost;

    protected int peerPort;

    protected String peerRepository;

    public Unmonitored(int port, String name, boolean master, String peerHost, int peerPort, String peerRepository)
    {
      this.port = port;
      this.name = name;
      this.master = master;
      this.peerHost = peerHost;
      this.peerPort = peerPort;
      this.peerRepository = peerRepository;
    }

    @Override
    protected IRepository createRepository(IStore store, Map<String, String> props)
    {
      IRepositorySynchronizer synchronizer = createRepositorySynchronizer(peerHost, peerPort, peerRepository);
      ISynchronizableRepository repository = CDOServerUtil.createFailoverParticipant(name, store, props, synchronizer, master);
      repository.setInitialPackages(CompanyPackage.eINSTANCE);
      return repository;
    }

    @Override
    protected void showMenu()
    {
      super.showMenu();
      System.out.println("4 - set repository type MASTER");
      System.out.println("5 - set repository type BACKUP");
    }

    @Override
    protected boolean handleCommand(String command)
    {
      if ("4".equals(command))
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
      else
      {
        return super.handleCommand(command);
      }

      return false;
    }

    /**
     * @author Eike Stepper
     */
    public static final class InitialMaster extends Unmonitored
    {
      public InitialMaster()
      {
        super(2036, "repo1", true, "localhost", 2037, "repo2");
      }

      public static void main(String[] args) throws Exception
      {
        FailoverExample example = new InitialMaster();
        example.init();
        example.run();
        example.done();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static final class InitialBackup extends Unmonitored
    {
      public InitialBackup()
      {
        super(2037, "repo2", false, "localhost", 2036, "repo1");
      }

      public static void main(String[] args) throws Exception
      {
        FailoverExample example = new InitialBackup();
        example.init();
        example.run();
        example.done();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Monitored extends FailoverExample
  {
    public static final String REPOSITORY_GROUP = "ExampleGroup";

    // public static final String REPOSITORY_MONITOR_HOST = "92.231.107.180";

    public static final String REPOSITORY_MONITOR_HOST = "localhost";

    public static final int REPOSITORY_MONITOR_PORT = 2038;

    protected String host;

    public Monitored(String host, int port, String name)
    {
      this.host = host;
      this.port = port;
      this.name = name;
    }

    @Override
    protected IRepository createRepository(IStore store, Map<String, String> props)
    {
      ISynchronizableRepository repository = CDOServerUtil.createFailoverParticipant(name, store, props);
      repository.setInitialPackages(CompanyPackage.eINSTANCE);
      ((InternalFailoverParticipant)repository).setAllowBackupCommits(true); // Load balancing!

      FailoverAgent agent = new FailoverAgent()
      {
        @Override
        protected org.eclipse.emf.cdo.session.CDOSessionConfiguration createSessionConfiguration(String connectorDescription, String repositoryName)
        {
          return Monitored.this.createSessionConfiguration(connectorDescription, repositoryName);
        }

        @Override
        public IManagedContainer getContainer()
        {
          return container;
        }
      };

      agent.setMonitorConnector(createConnector(REPOSITORY_MONITOR_HOST + ":" + REPOSITORY_MONITOR_PORT));
      agent.setConnectorDescription(host + ":" + port);
      agent.setRepository(repository);
      agent.setGroup(REPOSITORY_GROUP);
      agent.setRate(1000L);
      agent.setTimeout(4000L);
      agent.activate();

      return repository;
    }

    /**
     * @author Eike Stepper
     */
    public static final class Monitor
    {
      public static void main(String[] args) throws Exception
      {
        IManagedContainer container = createContainer();
        FailoverMonitor monitor = (FailoverMonitor)container.getElement(FailoverMonitor.PRODUCT_GROUP, "net4j", REPOSITORY_GROUP);

        monitor.addListener(new ContainerEventAdapter<AgentProtocol>()
        {
          @Override
          protected void onAdded(IContainer<AgentProtocol> monitor, AgentProtocol agent)
          {
            dump((FailoverMonitor)monitor, "Registered", agent);
          }

          @Override
          protected void onRemoved(IContainer<AgentProtocol> monitor, AgentProtocol agent)
          {
            dump((FailoverMonitor)monitor, "Deregistered", agent);
          }

          private void dump(FailoverMonitor monitor, String event, AgentProtocol agent)
          {
            System.out.println(event + " agent " + agent);
            for (AgentProtocol element : monitor.getElements())
            {
              String type = element == monitor.getMasterAgent() ? "MASTER: " : "BACKUP: ";
              System.out.println("   " + type + element);
            }
          }
        });

        container.getElement("org.eclipse.net4j.acceptors", TRANSPORT_TYPE, "0.0.0.0:" + REPOSITORY_MONITOR_PORT);
        System.out.println("Monitoring...");

        for (;;)
        {
          Thread.sleep(100);
        }
      }
    }

    /**
     * @author Eike Stepper
     */
    public static final class Agent1 extends Monitored
    {
      public Agent1()
      {
        super("localhost", 2036, "repo1");
      }

      public static void main(String[] args) throws Exception
      {
        FailoverExample example = new Agent1();
        example.init();
        example.run();
        example.done();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static final class Agent2 extends Monitored
    {
      public Agent2()
      {
        super("localhost", 2037, "repo2");
      }

      public static void main(String[] args) throws Exception
      {
        FailoverExample example = new Agent2();
        example.init();
        example.run();
        example.done();
      }
    }

    /**
     * @author Eike Stepper
     */
    public static final class Client
    {
      public static void main(String[] args) throws Exception
      {
        IManagedContainer container = createContainer();
        CDONet4jSessionConfiguration configuration = CDONet4jUtil.createFailoverSessionConfiguration(REPOSITORY_MONITOR_HOST + ":" + REPOSITORY_MONITOR_PORT,
            REPOSITORY_GROUP, container);

        final CDONet4jSession session = configuration.openNet4jSession();
        System.out.println("Connected");

        final CDOTransaction tx = session.openTransaction();
        addObject(tx);
        System.out.println("Succesfully committed an object to the original tx/session");

        session.addListener(new IListener()
        {
          @Override
          public void notifyEvent(IEvent event)
          {
            if (event instanceof CDOSessionRecoveryEvent)
            {
              CDOSessionRecoveryEvent e = (CDOSessionRecoveryEvent)event;
              System.out.println("Failover " + e.getType() + ": " + e.getSource().getRepositoryInfo());

              if (e.getType() == CDOSessionRecoveryEvent.Type.FINISHED)
              {
                // Let's see if the TX in the failed-over session is usable:
                //
                addObject(tx);
                System.out.println("Succesfully committed an object to the failed-over tx/session");
              }
            }
          }
        });

        while (!session.isClosed())
        {
          Thread.sleep(100);
        }
      }

      private static void addObject(CDOTransaction tx)
      {
        try
        {
          Customer customer = CompanyFactory.eINSTANCE.createCustomer();
          tx.getOrCreateResource("/r1").getContents().add(customer);
          tx.commit();
        }
        catch (CommitException x)
        {
          throw new RuntimeException(x);
        }
      }
    }
  }
}
