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
package org.eclipse.emf.cdo.examples.server.offline;

import org.eclipse.emf.cdo.common.CDOCommonRepository.Type;
import org.eclipse.emf.cdo.common.util.RepositoryStateChangedEvent;
import org.eclipse.emf.cdo.common.util.RepositoryTypeChangedEvent;
import org.eclipse.emf.cdo.examples.company.CompanyPackage;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.h2.H2Adapter;
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
 * @author Martin Fluegge
 * @since 4.0
 */
public abstract class AbstractOfflineExampleServer
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

  public AbstractOfflineExampleServer(String name, int port, int dbBrowserPort)
  {
    this.name = name;
    this.port = port;
    container = OfflineExampleUtil.createContainer();
    container.getElement("org.eclipse.emf.cdo.server.browsers", "default", dbBrowserPort + ""); //$NON-NLS-1$ //$NON-NLS-2$
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

  protected IAcceptor createAcceptor()
  {
    return (IAcceptor)container.getElement("org.eclipse.net4j.acceptors", AbstractOfflineExampleServer.TRANSPORT_TYPE,
        "0.0.0.0:" + port);
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

  protected abstract IRepository createRepository(IStore store, Map<String, String> props);
}
