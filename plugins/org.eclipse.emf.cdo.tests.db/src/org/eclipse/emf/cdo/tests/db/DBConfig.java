/*
 * Copyright (c) 2011-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.mapping.TypeMappingRegistry;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.container.IPluginContainer;

import javax.sql.DataSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class DBConfig extends RepositoryConfig
{
  public static final String CAPABILITY = "DB";

  public static final String CAPABILITY_RANGES = "DB.ranges";

  public static final String CAPABILITY_COPY_ON_BRANCH = "DB.copy.on.branch";

  public static final String PROP_TEST_MAPPING_STRATEGY = "test.repository.MappingStrategy";

  private static final long serialVersionUID = 1L;

  private boolean withRanges;

  private boolean copyOnBranch;

  private transient IDBAdapter dbAdapter;

  public DBConfig(String name, boolean supportingAudits, boolean supportingBranches, boolean withRanges,
      boolean copyOnBranch, IDGenerationLocation idGenerationLocation)
  {
    super(name, supportingAudits, supportingBranches, idGenerationLocation);
    this.withRanges = withRanges;
    this.copyOnBranch = copyOnBranch;
  }

  @Override
  public void initCapabilities(Set<String> capabilities)
  {
    super.initCapabilities(capabilities);
    capabilities.add(CAPABILITY);
    capabilities.add(getDBAdapterName());

    if (isWithRanges())
    {
      capabilities.add(CAPABILITY_RANGES);
    }

    if (isCopyOnBranch())
    {
      capabilities.add(CAPABILITY_COPY_ON_BRANCH);
    }
  }

  protected IDBAdapter getDBAdapter()
  {
    if (dbAdapter == null)
    {
      dbAdapter = createDBAdapter();
    }

    return dbAdapter;
  }

  protected abstract String getDBAdapterName();

  @Override
  protected String getStoreName()
  {
    return "DB";
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
  public void setUp() throws Exception
  {
    CDODBUtil.prepareContainer(IPluginContainer.INSTANCE);
    super.setUp();
    ((TypeMappingRegistry)ITypeMapping.Registry.INSTANCE).init();
  }

  @Override
  protected boolean isOptimizing()
  {
    return true;
  }

  public IStore createStore(String repoName)
  {
    IMappingStrategy mappingStrategy = createMappingStrategy();
    mappingStrategy.setProperties(createMappingStrategyProperties());

    IDBAdapter dbAdapter = getDBAdapter();

    DataSource dataSource = createDataSource(repoName);
    IDBConnectionProvider connectionProvider = dbAdapter.createConnectionProvider(dataSource);

    Map<String, String> props = new HashMap<String, String>();
    // props.put(IDBStore.Props.ID_COLUMN_LENGTH, "66");

    return CDODBUtil.createStore(mappingStrategy, dbAdapter, connectionProvider, props);
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
    IMappingStrategy mappingStrategy = getTestMappingStrategy();
    if (mappingStrategy != null)
    {
      return mappingStrategy;
    }

    return CDODBUtil.createHorizontalMappingStrategy(isSupportingAudits(), isSupportingBranches(), withRanges);
  }

  protected IMappingStrategy getTestMappingStrategy()
  {
    return (IMappingStrategy)getTestProperty(PROP_TEST_MAPPING_STRATEGY);
  }

  @Override
  protected String getMappingStrategySpecialization()
  {
    return (withRanges ? "-ranges" : "") + (copyOnBranch ? "-copy" : "");
  }

  protected abstract IDBAdapter createDBAdapter();

  protected abstract DataSource createDataSource(String repoName);
}
