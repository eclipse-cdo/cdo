/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.server;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.CDOCommonRepository.Type;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.util.RepositoryStateChangedEvent;
import org.eclipse.emf.cdo.common.util.RepositoryTypeChangedEvent;
import org.eclipse.emf.cdo.examples.company.CompanyFactory;
import org.eclipse.emf.cdo.examples.company.CompanyPackage;
import org.eclipse.emf.cdo.examples.company.Customer;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.net4j.CDOSession;
import org.eclipse.emf.cdo.net4j.CDOSessionConfiguration;
import org.eclipse.emf.cdo.server.CDOServerBrowser;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositorySynchronizer;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.net4j.CDONet4jServerUtil;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

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
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.h2.jdbcx.JdbcDataSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class OfflineExample
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
    // OMPlatform.INSTANCE.addLogHandler(PrintLogHandler.CONSOLE);

    CompanyPackage.eINSTANCE.getClass(); // Register EPackage in standalone
  }

  public OfflineExample()
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

    container.registerFactory(new CDOServerBrowser.ContainerBased.Factory(container));
    CDODBUtil.prepareContainer(container); // Register DBBrowserPage.Factory

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
      // System.out.println();
      // System.out.println("Enter a command:");
      // showMenu();
      // System.out.println();

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
      System.out.println(repository.getName() + ": " + repository.getType()
          + (repository.getType() == Type.BACKUP ? "|" + repository.getState() : ""));
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

  protected IAcceptor createAcceptor()
  {
    return (IAcceptor)container.getElement("org.eclipse.net4j.acceptors", TRANSPORT_TYPE, "0.0.0.0:" + port);
  }

  protected IConnector createConnector(String description)
  {
    return Net4jUtil.getConnector(container, TRANSPORT_TYPE, description);
  }

  protected IRepositorySynchronizer createRepositorySynchronizer(String connectorDescription, String repositoryName)
  {
    CDOSessionConfigurationFactory factory = createSessionConfigurationFactory(connectorDescription, repositoryName);

    IRepositorySynchronizer synchronizer = CDOServerUtil.createRepositorySynchronizer(factory);
    synchronizer.setRetryInterval(2);
    synchronizer.setRawReplication(true);
    synchronizer.setMaxRecommits(10);
    synchronizer.setRecommitInterval(2);
    return synchronizer;
  }

  protected CDOSessionConfigurationFactory createSessionConfigurationFactory(final String connectorDescription,
      final String repositoryName)
  {
    return new CDOSessionConfigurationFactory()
    {
      public CDOSessionConfiguration createSessionConfiguration()
      {
        IConnector connector = createConnector("localhost:" + Master.PORT);
        return OfflineExample.this.createSessionConfiguration(connector, repositoryName);
      }
    };
  }

  protected CDOSessionConfiguration createSessionConfiguration(IConnector connector, String repositoryName)
  {
    CDOSessionConfiguration configuration = CDONet4jUtil.createSessionConfiguration();
    configuration.setConnector(connector);
    configuration.setRepositoryName(repositoryName);
    configuration.setRevisionManager(CDORevisionUtil.createRevisionManager(CDORevisionCache.NOOP));
    return configuration;
  }

  protected abstract IRepository createRepository(IStore store, Map<String, String> props);

  /**
   * @author Eike Stepper
   */
  public static final class Master extends OfflineExample
  {
    public static final String NAME = "master";

    public static final int PORT = 2036;

    public Master()
    {
      container.getElement("org.eclipse.emf.cdo.server.browsers", "default", "7777"); //$NON-NLS-1$ //$NON-NLS-2$

      name = NAME;
      port = PORT;
    }

    @Override
    protected IRepository createRepository(IStore store, Map<String, String> props)
    {
      return CDOServerUtil.createRepository(name, store, props);
    }

    public static void main(String[] args) throws Exception
    {
      System.out.println("Master repository starting...");
      OfflineExample example = new Master();
      example.init();
      example.run();
      example.done();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Clone extends OfflineExample
  {
    public static final String NAME = "clone";

    public static final int PORT = 2037;

    public Clone()
    {
      container.getElement("org.eclipse.emf.cdo.server.browsers", "default", "7778"); //$NON-NLS-1$ //$NON-NLS-2$

      name = NAME;
      port = PORT;
    }

    @Override
    protected IRepository createRepository(IStore store, Map<String, String> props)
    {
      IRepositorySynchronizer synchronizer = createRepositorySynchronizer("localhost:" + Master.PORT, Master.NAME);
      return CDOServerUtil.createOfflineClone(name, store, props, synchronizer);
    }

    public static void main(String[] args) throws Exception
    {
      System.out.println("Clone repository starting...");
      OfflineExample example = new Clone();
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
      System.out.println("Client starting...");
      IManagedContainer container = createContainer();
      IConnector connector = Net4jUtil.getConnector(container, TRANSPORT_TYPE, "localhost:" + Clone.PORT);

      CDOSessionConfiguration configuration = CDONet4jUtil.createSessionConfiguration();
      configuration.setConnector(connector);
      configuration.setRepositoryName(Clone.NAME);

      CDOSession session = configuration.openSession();
      CDORepositoryInfo repositoryInfo = session.getRepositoryInfo();
      System.out.println("Connected to " + repositoryInfo.getName());

      session.addListener(new IListener()
      {
        public void notifyEvent(IEvent event)
        {
          if (event instanceof CDOCommonRepository.StateChangedEvent)
          {
            CDOCommonRepository.StateChangedEvent e = (CDOCommonRepository.StateChangedEvent)event;
            System.out.println("State changed to " + e.getNewState());
          }
        }
      });

      CDOTransaction tx = session.openTransaction();

      for (;;)
      {
        new BufferedReader(new InputStreamReader(System.in)).readLine();
        addObject(tx);
      }
    }

    private static void addObject(CDOTransaction tx)
    {
      try
      {
        Customer customer = CompanyFactory.eINSTANCE.createCustomer();
        tx.getOrCreateResource("/r1").getContents().add(customer);

        System.out.println("Committing an object to " + tx.getBranch().getPathName());
        CDOCommitInfo commitInfo = tx.commit();
        CDOBranch branch = commitInfo.getBranch();
        System.out.println("Committed an object to  " + branch.getPathName());
        tx.setBranch(branch);
      }
      catch (CommitException x)
      {
        throw new RuntimeException(x);
      }
    }
  }
}
