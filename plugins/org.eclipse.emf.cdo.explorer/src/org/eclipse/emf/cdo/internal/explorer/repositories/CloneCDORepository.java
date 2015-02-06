/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.repositories;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.net4j.CDONet4jSessionConfiguration;
import org.eclipse.emf.cdo.net4j.CDONet4jUtil;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositorySynchronizer;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.ISynchronizableRepository;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.h2.jdbcx.JdbcDataSource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class CloneCDORepository extends CDORepositoryImpl
{
  private String connectorType;

  private String connectorDescription;

  private int reconnectSeconds;

  private int recommitSeconds;

  private int recommitAttempts;

  private ISynchronizableRepository repository;

  public CloneCDORepository()
  {
  }

  public final String getConnectorType()
  {
    return "jvm";
  }

  public final String getConnectorDescription()
  {
    return "local";
  }

  public final int getReconnectSeconds()
  {
    return reconnectSeconds;
  }

  public final int getRecommitSeconds()
  {
    return recommitSeconds;
  }

  public final int getRecommitAttempts()
  {
    return recommitAttempts;
  }

  @Override
  protected void init(File folder, String type, Properties properties)
  {
    super.init(folder, type, properties);
    connectorType = properties.getProperty("connectorType");
    connectorDescription = properties.getProperty("connectorDescription");
    reconnectSeconds = Integer.parseInt(properties.getProperty("reconnectSeconds"));
    recommitSeconds = Integer.parseInt(properties.getProperty("recommitSeconds"));
    recommitAttempts = Integer.parseInt(properties.getProperty("recommitAttempts"));

  }

  @Override
  protected void collectProperties(Properties properties)
  {
    super.collectProperties(properties);
    properties.put("connectorType", connectorType);
    properties.put("connectorDescription", connectorDescription);
    properties.put("reconnectSeconds", Integer.toString(reconnectSeconds));
    properties.put("recommitSeconds", Integer.toString(recommitSeconds));
    properties.put("recommitAttempts", Integer.toString(recommitAttempts));
  }

  @Override
  protected CDOSession openSession()
  {
    final String repositoryName = getRepositoryName();
    File folder = new File(getFolder(), "db");

    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:" + folder);

    IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(true, true, true);
    IDBAdapter dbAdapter = DBUtil.getDBAdapter("h2");
    IDBConnectionProvider connectionProvider = DBUtil.createConnectionProvider(dataSource);
    IStore store = CDODBUtil.createStore(mappingStrategy, dbAdapter, connectionProvider);

    Map<String, String> props = new HashMap<String, String>();
    props.put(IRepository.Props.OVERRIDE_UUID, "");
    props.put(IRepository.Props.SUPPORTING_AUDITS, Boolean.toString(true));
    props.put(IRepository.Props.SUPPORTING_BRANCHES, Boolean.toString(true));
    props.put(IRepository.Props.ID_GENERATION_LOCATION, IDGenerationLocation.CLIENT.toString());

    final IManagedContainer container = getContainer();
    CDOSessionConfigurationFactory remoteSessionConfigurationFactory = new CDOSessionConfigurationFactory()
    {
      public CDOSessionConfiguration createSessionConfiguration()
      {
        IConnector connector = Net4jUtil.getConnector(container, connectorType, connectorDescription);

        CDONet4jSessionConfiguration config = CDONet4jUtil.createNet4jSessionConfiguration();
        config.setConnector(connector);
        config.setRepositoryName(repositoryName);
        config.setRevisionManager(CDORevisionUtil.createRevisionManager(CDORevisionCache.NOOP));

        return config;
      }
    };

    IRepositorySynchronizer synchronizer = CDOServerUtil
        .createRepositorySynchronizer(remoteSessionConfigurationFactory);

    repository = CDOServerUtil.createOfflineClone(repositoryName, store, props, synchronizer);

    CDOServerUtil.addRepository(container, repository);
    Net4jUtil.getAcceptor(container, getConnectorType(), getConnectorDescription());

    return super.openSession();
  }

  @Override
  protected void closeSession()
  {
    super.closeSession();

    if (repository != null)
    {
      repository.deactivate();
      repository = null;
    }
  }
}
