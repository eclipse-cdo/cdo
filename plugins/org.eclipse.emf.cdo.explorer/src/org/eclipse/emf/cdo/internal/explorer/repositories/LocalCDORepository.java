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
  public static final String PROP_VERSIONING_MODE = "versioningMode";

  public static final String PROP_ID_GENERATION = "idGeneration";

  public static final String PROP_TCP_DISABLED = "tcpDisabled";

  public static final String PROP_TCP_PORT = "tcpPort";

  private VersioningMode versioningMode;

  private IDGeneration idGeneration;

  private boolean tcpDisabled;

  private int tcpPort;

  private IRepository repository;

  private IAcceptor tcpAcceptor;

  public LocalCDORepository()
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

  public String getURI()
  {
    if (tcpDisabled)
    {
      return getConnectorType() + "://" + getConnectorDescription() + "/" + getName();
    }

    return "tcp://localhost:" + tcpPort + "/" + getName();
  }

  public final VersioningMode getVersioningMode()
  {
    return versioningMode;
  }

  public final IDGeneration getIDGeneration()
  {
    return idGeneration;
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
    versioningMode = VersioningMode.valueOf(properties.getProperty(PROP_VERSIONING_MODE));
    idGeneration = IDGeneration.valueOf(properties.getProperty(PROP_ID_GENERATION));
    tcpDisabled = Boolean.parseBoolean(properties.getProperty(PROP_TCP_DISABLED));
    tcpPort = Integer.parseInt(properties.getProperty(PROP_TCP_PORT));
  }

  @Override
  protected void collectProperties(Properties properties)
  {
    super.collectProperties(properties);
    properties.put(PROP_VERSIONING_MODE, versioningMode.toString());
    properties.put(PROP_ID_GENERATION, idGeneration.toString());
    properties.put(PROP_TCP_DISABLED, Boolean.toString(tcpDisabled));
    properties.put(PROP_TCP_PORT, Integer.toString(tcpPort));
  }

  @Override
  protected CDOSession openSession()
  {
    String repositoryName = getName();
    File folder = new File(getFolder(), "db");

    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:" + folder);

    IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(versioningMode.isSupportingAudits(),
        versioningMode.isSupportingBranches(), false);
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
    Map<String, String> props = new HashMap<String, String>();
    props.put(IRepository.Props.OVERRIDE_UUID, "");
    props.put(IRepository.Props.SUPPORTING_AUDITS, Boolean.toString(versioningMode.isSupportingAudits()));
    props.put(IRepository.Props.SUPPORTING_BRANCHES, Boolean.toString(versioningMode.isSupportingBranches()));
    props.put(IRepository.Props.ID_GENERATION_LOCATION, idGeneration.getLocation().toString());
    return props;
  }

  protected Map<String, String> getMappingStrategyProperties()
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(IMappingStrategy.PROP_QUALIFIED_NAMES, "true");
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

  /**
   * @author Eike Stepper
   */
  public enum VersioningMode
  {
    Normal(false, false), Auditing(true, false), Branching(true, true);

    private boolean supportingAudits;

    private boolean supportingBranches;

    private VersioningMode(boolean supportingAudits, boolean supportingBranches)
    {
      this.supportingAudits = supportingAudits;
      this.supportingBranches = supportingBranches;
    }

    public boolean isSupportingAudits()
    {
      return supportingAudits;
    }

    public boolean isSupportingBranches()
    {
      return supportingBranches;
    }
  }

  /**
   * @author Eike Stepper
   */
  public enum IDGeneration
  {
    Counter(IDGenerationLocation.STORE), UUID(IDGenerationLocation.CLIENT);

    private IDGenerationLocation location;

    private IDGeneration(IDGenerationLocation location)
    {
      this.location = location;
    }

    public final IDGenerationLocation getLocation()
    {
      return location;
    }
  }
}
