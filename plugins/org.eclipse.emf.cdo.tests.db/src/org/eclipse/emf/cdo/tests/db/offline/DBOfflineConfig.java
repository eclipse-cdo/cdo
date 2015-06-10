/*
 * Copyright (c) 2012, 2013, 2015 Eike Stepper (Berlin, Germany) and others.
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

  public DBOfflineConfig(String name, boolean withRanges, boolean copyOnBranch,
      IDGenerationLocation idGenerationLocation)
  {
    super(name, idGenerationLocation);
    this.withRanges = withRanges;
    this.copyOnBranch = copyOnBranch;
  }

  public boolean isWithRanges()
  {
    return withRanges;
  }

  public boolean isCopyOnBranch()
  {
    return copyOnBranch;
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

    if (isWithRanges())
    {
      capabilities.add(DBConfig.CAPABILITY_RANGES);
    }

    if (isCopyOnBranch())
    {
      capabilities.add(DBConfig.CAPABILITY_COPY_ON_BRANCH);
    }
  }

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
    props.put(IMappingStrategy.PROP_QUALIFIED_NAMES, "true");
    props.put(CDODBUtil.PROP_COPY_ON_BRANCH, Boolean.toString(copyOnBranch));
    return props;
  }

  protected IMappingStrategy createMappingStrategy()
  {
    return CDODBUtil.createHorizontalMappingStrategy(isSupportingAudits(), isSupportingBranches(), withRanges);
  }

  @Override
  protected String getMappingStrategySpecialization()
  {
    return (withRanges ? "-ranges" : "") + (copyOnBranch ? "-copy" : "");
  }

  protected abstract DataSource createDataSource(String repoName);

  protected abstract IDBAdapter createDBAdapter();
}
