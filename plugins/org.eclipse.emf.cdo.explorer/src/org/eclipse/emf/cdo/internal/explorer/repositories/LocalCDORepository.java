/*
 * Copyright (c) 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer.repositories;

import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.h2.jdbcx.JdbcDataSource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class LocalCDORepository extends CDORepositoryImpl
{
  public static final String PROP_TCP_DISABLED = "tcpDisabled";

  public static final String PROP_TCP_PORT = "tcpPort";

  private boolean tcpDisabled;

  private int tcpPort;

  private IRepository repository;

  private IAcceptor tcpAcceptor;

  public LocalCDORepository()
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
    return false;
  }

  @Override
  public boolean isLocal()
  {
    return true;
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
    if (tcpDisabled)
    {
      return getConnectorType() + "://" + getConnectorDescription() + "/" + getName();
    }

    return "tcp://localhost:" + tcpPort + "/" + getName();
  }

  public final boolean isTCPDisabled()
  {
    return tcpDisabled;
  }

  public final int getTCPPort()
  {
    return tcpPort;
  }

  @Override
  protected void init(File folder, String type, Properties properties)
  {
    super.init(folder, type, properties);
    tcpDisabled = Boolean.parseBoolean(properties.getProperty(PROP_TCP_DISABLED));
    if (!tcpDisabled)
    {
      tcpPort = Integer.parseInt(properties.getProperty(PROP_TCP_PORT));
    }
  }

  @Override
  protected void collectProperties(Properties properties)
  {
    super.collectProperties(properties);
    properties.setProperty(PROP_TCP_DISABLED, Boolean.toString(tcpDisabled));
    properties.setProperty(PROP_TCP_PORT, Integer.toString(tcpPort));
  }

  @Override
  public CDOSession openSession()
  {
    String repositoryName = getName();
    File folder = new File(getFolder(), "db");

    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:" + folder);

    IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(getVersioningMode().isSupportingAudits(),
        getVersioningMode().isSupportingBranches(), false);
    mappingStrategy.setProperties(getMappingStrategyProperties());

    IDBAdapter dbAdapter = DBUtil.getDBAdapter("h2");
    IDBConnectionProvider connectionProvider = DBUtil.createConnectionProvider(dataSource);
    IDBStore store = CDODBUtil.createStore(mappingStrategy, dbAdapter, connectionProvider);

    repository = CDOServerUtil.createRepository(repositoryName, store, getRepositoryProperties());

    IManagedContainer container = getContainer();
    CDOServerUtil.addRepository(container, repository);
    Net4jUtil.getAcceptor(container, getConnectorType(), getConnectorDescription());

    if (!tcpDisabled)
    {
      tcpAcceptor = Net4jUtil.getAcceptor(container, "tcp", "0.0.0.0:" + tcpPort);
    }

    return super.openSession();
  }

  protected Map<String, String> getRepositoryProperties()
  {
    Map<String, String> props = new HashMap<>();
    props.put(IRepository.Props.OVERRIDE_UUID, "");
    props.put(IRepository.Props.SUPPORTING_AUDITS, Boolean.toString(getVersioningMode().isSupportingAudits()));
    props.put(IRepository.Props.SUPPORTING_BRANCHES, Boolean.toString(getVersioningMode().isSupportingBranches()));
    props.put(IRepository.Props.ID_GENERATION_LOCATION, getIDGeneration().getLocation().toString());
    return props;
  }

  protected Map<String, String> getMappingStrategyProperties()
  {
    Map<String, String> props = new HashMap<>();
    props.put(IMappingStrategy.Props.QUALIFIED_NAMES, "true");
    props.put(CDODBUtil.PROP_COPY_ON_BRANCH, "true");
    return props;
  }

  @Override
  protected void closeSession()
  {
    super.closeSession();

    LifecycleUtil.deactivate(tcpAcceptor);
    tcpAcceptor = null;

    LifecycleUtil.deactivate(repository);
    repository = null;
  }
}
