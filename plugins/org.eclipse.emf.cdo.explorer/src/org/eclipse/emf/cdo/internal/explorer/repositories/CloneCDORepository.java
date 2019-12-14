/*
 * Copyright (c) 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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
  public static final String PROP_CONNECTOR_TYPE = "connectorType";

  public static final String PROP_CONNECTOR_DESCRIPTION = "connectorDescription";

  public static final String PROP_RECONNECT_SECONDS = "reconnectSeconds";

  public static final String PROP_RECOMMIT_SECONDS = "recommitSeconds";

  public static final String PROP_RECOMMIT_ATTEMPTS = "recommitAttempts";

  private String connectorType;

  private String connectorDescription;

  private int reconnectSeconds;

  private int recommitSeconds;

  private int recommitAttempts;

  private ISynchronizableRepository repository;

  public CloneCDORepository()
  {
  }

  @Override
  public boolean isRemote()
  {
    return false;
  }

  @Override
  public boolean isClone()
  {
    return true;
  }

  @Override
  public boolean isLocal()
  {
    return false;
  }

  @Override
  public final String getConnectorType()
  {
    return "jvm";
  }

  @Override
  public final String getConnectorDescription()
  {
    return "local";
  }

  @Override
  public String getURI()
  {
    return connectorType + "://" + connectorDescription + "/" + getName();
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
    connectorType = properties.getProperty(PROP_CONNECTOR_TYPE);
    connectorDescription = properties.getProperty(PROP_CONNECTOR_DESCRIPTION);
    reconnectSeconds = Integer.parseInt(properties.getProperty(PROP_RECONNECT_SECONDS));
    recommitSeconds = Integer.parseInt(properties.getProperty(PROP_RECOMMIT_SECONDS));
    recommitAttempts = Integer.parseInt(properties.getProperty(PROP_RECOMMIT_ATTEMPTS));
  }

  @Override
  protected void collectProperties(Properties properties)
  {
    super.collectProperties(properties);
    properties.setProperty(PROP_CONNECTOR_TYPE, connectorType);
    properties.setProperty(PROP_CONNECTOR_DESCRIPTION, connectorDescription);
    properties.setProperty(PROP_RECONNECT_SECONDS, Integer.toString(reconnectSeconds));
    properties.setProperty(PROP_RECOMMIT_SECONDS, Integer.toString(recommitSeconds));
    properties.setProperty(PROP_RECOMMIT_ATTEMPTS, Integer.toString(recommitAttempts));
  }

  @Override
  protected CDOSession openSession()
  {
    final String repositoryName = getName();
    File folder = new File(getFolder(), "db");

    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:" + folder);

    IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(true, true, false);
    IDBAdapter dbAdapter = DBUtil.getDBAdapter("h2");
    IDBConnectionProvider connectionProvider = DBUtil.createConnectionProvider(dataSource);
    IStore store = CDODBUtil.createStore(mappingStrategy, dbAdapter, connectionProvider);

    Map<String, String> props = new HashMap<>();
    props.put(IRepository.Props.OVERRIDE_UUID, "");
    props.put(IRepository.Props.SUPPORTING_AUDITS, Boolean.toString(true));
    props.put(IRepository.Props.SUPPORTING_BRANCHES, Boolean.toString(true));
    props.put(IRepository.Props.ID_GENERATION_LOCATION, IDGenerationLocation.CLIENT.toString());

    final IManagedContainer container = getContainer();
    CDOSessionConfigurationFactory remoteSessionConfigurationFactory = new CDOSessionConfigurationFactory()
    {
      @Override
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

    IRepositorySynchronizer synchronizer = CDOServerUtil.createRepositorySynchronizer(remoteSessionConfigurationFactory);

    repository = CDOServerUtil.createOfflineClone(repositoryName + "-clone", store, props, synchronizer);

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
