/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping;
import org.eclipse.emf.cdo.server.internal.db.mapping.TypeMappingRegistry;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.util.container.IPluginContainer;

import javax.sql.DataSource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class DBStoreRepositoryConfig extends RepositoryConfig
{
  private static final long serialVersionUID = 1L;

  public DBStoreRepositoryConfig(String name)
  {
    super(name);
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
    IDBAdapter dbAdapter = createDBAdapter();
    DataSource dataSource = createDataSource(repoName);
    return CDODBUtil.createStore(mappingStrategy, dbAdapter, DBUtil.createConnectionProvider(dataSource));
  }

  protected Map<String, String> createMappingStrategyProperties()
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(IMappingStrategy.PROP_QUALIFIED_NAMES, "true");
    return props;
  }

  protected abstract IMappingStrategy createMappingStrategy();

  protected abstract IDBAdapter createDBAdapter();

  protected abstract DataSource createDataSource(String repoName);
}
