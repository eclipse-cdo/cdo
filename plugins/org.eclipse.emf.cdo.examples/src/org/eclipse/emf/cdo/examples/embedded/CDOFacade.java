/*
 * Copyright (c) 2017 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.embedded;

import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.server.CDOServerBrowser;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.embedded.CDOEmbeddedRepositoryConfig;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.h2.jdbcx.JdbcDataSource;

import java.io.File;
import java.sql.Connection;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public final class CDOFacade extends CDOEmbeddedRepositoryConfig
{
  public static final CDOFacade INSTANCE = new CDOFacade();

  private static final String NAME = "repo";

  private static final boolean AUDITING = false;

  private static final boolean BRANCHING = false;

  private static final File DB_FOLDER = new File("./database");

  private static final String DS_URL = "jdbc:h2:" + DB_FOLDER + "/" + NAME + ";MVCC=FALSE";

  private CDOServerBrowser serverBrowser;

  private CDONet4jSession session;

  private CDOTransaction transaction;

  private ResourceSet resourceSet;

  private CDOFacade()
  {
    super(NAME);
  }

  public Connection getJDBCConnection()
  {
    IDBStore store = (IDBStore)getRepository().getStore();
    return store.getConnection();
  }

  public synchronized CDONet4jSession getSession(boolean openOnDemand)
  {
    checkActive();

    if (session == null && openOnDemand)
    {
      session = openClientSession();
      session.addListener(new LifecycleEventAdapter()
      {
        @Override
        protected void onDeactivated(ILifecycle lifecycle)
        {
          if (lifecycle == session)
          {
            session = null;
          }
        }
      });
    }

    return session;
  }

  public synchronized CDOTransaction getTransaction()
  {
    checkActive();

    if (transaction == null)
    {
      resourceSet = new ResourceSetImpl();

      CDONet4jSession session = getSession(true);
      transaction = session.openTransaction(resourceSet);

      transaction.addListener(new LifecycleEventAdapter()
      {
        @Override
        protected void onDeactivated(ILifecycle lifecycle)
        {
          if (lifecycle == transaction)
          {
            transaction = null;
            resourceSet = null;
          }
        }
      });
    }

    return transaction;
  }

  public ResourceSet getResourceSet()
  {
    return resourceSet;
  }

  @Override
  public IStore createStore(IManagedContainer container)
  {
    DB_FOLDER.mkdirs();

    IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(AUDITING, BRANCHING);
    mappingStrategy.getProperties().put(IMappingStrategy.Props.FORCE_NAMES_WITH_ID, "true");

    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL(DS_URL);

    IDBAdapter dbAdapter = new H2Adapter();
    IDBConnectionProvider dbConnectionProvider = DBUtil.createConnectionProvider(dataSource);

    return CDODBUtil.createStore(mappingStrategy, dbAdapter, dbConnectionProvider);
  }

  @Override
  public void initProperties(IManagedContainer container, Map<String, String> properties)
  {
    properties.put(IRepository.Props.SUPPORTING_AUDITS, Boolean.toString(AUDITING));
    properties.put(IRepository.Props.SUPPORTING_BRANCHES, Boolean.toString(BRANCHING));
  }

  @Override
  public boolean isInitialPackage(IRepository repository, String nsURI)
  {
    return nsURI.equals("http://www.eclipse.org/emf/CDO/examples/company/1.0.0");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    serverBrowser = new CDOServerBrowser.ContainerBased(getContainer());
    LifecycleUtil.activate(serverBrowser);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(serverBrowser);
    serverBrowser = null;

    super.doDeactivate();
  }
}
