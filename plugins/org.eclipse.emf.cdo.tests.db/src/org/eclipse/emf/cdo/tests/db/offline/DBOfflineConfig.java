/*
 * Copyright (c) 2012, 2013, 2015-2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db.offline;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig.OfflineConfig;
import org.eclipse.emf.cdo.tests.db.DBConfig;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.container.IPluginContainer;

import javax.sql.DataSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Stefan Winkler
 * @author Eike Stepper
 */
public abstract class DBOfflineConfig extends OfflineConfig
{
  private static final long serialVersionUID = 1L;

  private boolean withRanges;

  private boolean copyOnBranch;

  public DBOfflineConfig(String name)
  {
    super(name);
  }

  @Override
  public DBOfflineConfig supportingAudits(boolean supportingAudits)
  {
    return (DBOfflineConfig)super.supportingAudits(supportingAudits);
  }

  @Override
  public DBOfflineConfig supportingBranches(boolean supportingBranches)
  {
    return (DBOfflineConfig)super.supportingBranches(supportingBranches);
  }

  @Override
  public DBOfflineConfig supportingChunks(boolean supportingChunks)
  {
    return (DBOfflineConfig)super.supportingChunks(supportingChunks);
  }

  @Override
  public DBOfflineConfig supportingExtRefs(boolean supportingExtRefs)
  {
    return (DBOfflineConfig)super.supportingExtRefs(supportingExtRefs);
  }

  @Override
  public DBOfflineConfig idGenerationLocation(IDGenerationLocation idGenerationLocation)
  {
    return (DBOfflineConfig)super.idGenerationLocation(idGenerationLocation);
  }

  public boolean withRanges()
  {
    return withRanges;
  }

  public DBOfflineConfig withRanges(boolean withRanges)
  {
    this.withRanges = withRanges;
    return this;
  }

  public boolean copyOnBranch()
  {
    return copyOnBranch;
  }

  public DBOfflineConfig copyOnBranch(boolean copyOnBranch)
  {
    this.copyOnBranch = copyOnBranch;
    return this;
  }

  @Override
  protected String getStoreName()
  {
    return MEMConfig.STORE_NAME;
  }

  @Override
  public void initCapabilities(Set<String> capabilities)
  {
    super.initCapabilities(capabilities);

    if (withRanges())
    {
      capabilities.add(DBConfig.CAPABILITY_RANGES);
    }

    if (copyOnBranch())
    {
      capabilities.add(DBConfig.CAPABILITY_COPY_ON_BRANCH);
    }
  }

  @Override
  public IStore createStore(String repoName)
  {
    IMappingStrategy mappingStrategy = createMappingStrategy();
    mappingStrategy.setProperties(createMappingStrategyProperties());

    IDBAdapter dbAdapter = createDBAdapter();

    DataSource dataSource = createDataSource(repoName);
    IDBConnectionProvider connectionProvider = dbAdapter.createConnectionProvider(dataSource);

    return CDODBUtil.createStore(mappingStrategy, dbAdapter, connectionProvider);
  }

  @Override
  public void setUp() throws Exception
  {
    CDODBUtil.prepareContainer(IPluginContainer.INSTANCE);
    super.setUp();
  }

  protected Map<String, String> createMappingStrategyProperties()
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(IMappingStrategy.Props.QUALIFIED_NAMES, "true");
    props.put(CDODBUtil.PROP_COPY_ON_BRANCH, Boolean.toString(copyOnBranch));
    return props;
  }

  protected IMappingStrategy createMappingStrategy()
  {
    return CDODBUtil.createHorizontalMappingStrategy(supportingAudits(), supportingBranches(), withRanges);
  }

  @Override
  protected String getMappingStrategySpecialization()
  {
    return (withRanges ? "-ranges" : "") + (copyOnBranch ? "-copy" : "");
  }

  protected abstract DataSource createDataSource(String repoName);

  protected abstract IDBAdapter createDBAdapter();
}
