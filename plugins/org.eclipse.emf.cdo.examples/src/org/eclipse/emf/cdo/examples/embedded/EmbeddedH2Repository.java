/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.examples.embedded;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IDBStore;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.embedded.CDOEmbeddedRepositoryConfig;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.h2.jdbcx.JdbcDataSource;

import java.io.File;
import java.sql.Connection;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class EmbeddedH2Repository extends CDOEmbeddedRepositoryConfig
{
  private final boolean auditing;

  private final boolean branching;

  private final File dbFolder;

  public EmbeddedH2Repository(String repositoryName, boolean auditing, boolean branching, File dbFolder)
  {
    super(repositoryName);
    this.auditing = auditing;
    this.branching = branching;
    this.dbFolder = dbFolder;
  }

  public boolean isAuditing()
  {
    return auditing;
  }

  public boolean isBranching()
  {
    return branching;
  }

  public File getDbFolder()
  {
    return dbFolder;
  }

  public Connection getJDBCConnection()
  {
    IDBStore store = (IDBStore)getRepository().getStore();
    return store.getConnection();
  }

  @Override
  public IStore createStore(IManagedContainer container)
  {
    dbFolder.mkdirs();

    IMappingStrategy mappingStrategy = createMappingStrategy();
    IDBAdapter dbAdapter = createDBAdapter();

    JdbcDataSource dataSource = createDataSource();
    IDBConnectionProvider dbConnectionProvider = DBUtil.createConnectionProvider(dataSource);

    return CDODBUtil.createStore(mappingStrategy, dbAdapter, dbConnectionProvider);
  }

  protected IMappingStrategy createMappingStrategy()
  {
    IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(auditing, branching);
    mappingStrategy.getProperties().put(IMappingStrategy.Props.FORCE_NAMES_WITH_ID, "true");
    return mappingStrategy;
  }

  protected H2Adapter createDBAdapter()
  {
    return new H2Adapter();
  }

  protected JdbcDataSource createDataSource()
  {
    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:" + dbFolder + "/" + getRepositoryName() + ";MVCC=FALSE");
    return dataSource;
  }

  @Override
  public void initProperties(IManagedContainer container, Map<String, String> properties)
  {
    properties.put(IRepository.Props.SUPPORTING_AUDITS, Boolean.toString(auditing));
    properties.put(IRepository.Props.SUPPORTING_BRANCHES, Boolean.toString(branching));
  }
}
