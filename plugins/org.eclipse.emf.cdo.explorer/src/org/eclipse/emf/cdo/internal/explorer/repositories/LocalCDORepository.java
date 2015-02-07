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
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
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
    versioningMode = VersioningMode.valueOf(properties.getProperty("versioningMode"));
    idGeneration = IDGeneration.valueOf(properties.getProperty("idGeneration"));
    tcpDisabled = Boolean.parseBoolean(properties.getProperty("tcpDisabled"));
    tcpPort = Integer.parseInt(properties.getProperty("tcpPort"));
  }

  @Override
  protected void collectProperties(Properties properties)
  {
    super.collectProperties(properties);
    properties.put("versioningMode", versioningMode.toString());
    properties.put("idGeneration", idGeneration.toString());
    properties.put("tcpDisabled", Boolean.toString(tcpDisabled));
    properties.put("tcpPort", Integer.toString(tcpPort));
  }

  @Override
  protected CDOSession openSession()
  {
    String repositoryName = getName();
    File folder = new File(getFolder(), "db");

    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:" + folder);

    boolean auditing = versioningMode.isSupportingAudits();
    boolean branching = versioningMode.isSupportingBranches();
    boolean withRanges = false;

    IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(auditing, branching, withRanges);
    IDBAdapter dbAdapter = DBUtil.getDBAdapter("h2");
    IDBConnectionProvider connectionProvider = DBUtil.createConnectionProvider(dataSource);
    IStore store = CDODBUtil.createStore(mappingStrategy, dbAdapter, connectionProvider);

    Map<String, String> props = new HashMap<String, String>();
    props.put(IRepository.Props.OVERRIDE_UUID, "");
    props.put(IRepository.Props.SUPPORTING_AUDITS, Boolean.toString(auditing));
    props.put(IRepository.Props.SUPPORTING_BRANCHES, Boolean.toString(branching));
    props.put(IRepository.Props.ID_GENERATION_LOCATION, idGeneration.getLocation().toString());

    repository = CDOServerUtil.createRepository(repositoryName, store, props);

    IManagedContainer container = getContainer();
    CDOServerUtil.addRepository(container, repository);
    Net4jUtil.getAcceptor(container, getConnectorType(), getConnectorDescription());

    if (!tcpDisabled)
    {
      tcpAcceptor = Net4jUtil.getAcceptor(container, "tcp", "0.0.0.0:" + tcpPort);
    }

    return super.openSession();
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
