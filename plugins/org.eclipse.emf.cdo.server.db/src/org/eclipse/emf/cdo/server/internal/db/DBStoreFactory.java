/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings https://bugs.eclipse.org/bugs/show_bug.cgi?id=271444  
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.server.RepositoryConfigurator;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreFactory;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import javax.sql.DataSource;

import java.util.Map;
import java.util.Properties;

/**
 * @author Eike Stepper
 */
public class DBStoreFactory implements IStoreFactory
{
  public DBStoreFactory()
  {
  }

  public String getStoreType()
  {
    return DBStore.TYPE;
  }

  public IStore createStore(Element storeConfig)
  {
    IMappingStrategy mappingStrategy = getMappingStrategy(storeConfig);
    IDBAdapter dbAdapter = getDBAdapter(storeConfig);
    DataSource dataSource = getDataSource(storeConfig);
    IDBConnectionProvider connectionProvider = DBUtil.createConnectionProvider(dataSource);
    return CDODBUtil.createStore(mappingStrategy, dbAdapter, connectionProvider);
  }

  private IMappingStrategy getMappingStrategy(Element storeConfig)
  {
    NodeList mappingStrategyConfigs = storeConfig.getElementsByTagName("mappingStrategy");
    if (mappingStrategyConfigs.getLength() != 1)
    {
      throw new IllegalStateException("Exactly one mapping strategy must be configured for DB store");
    }

    Element mappingStrategyConfig = (Element)mappingStrategyConfigs.item(0);
    String mappingStrategyType = mappingStrategyConfig.getAttribute("type");
    IMappingStrategy mappingStrategy = CDODBUtil.createMappingStrategy(mappingStrategyType);
    if (mappingStrategy == null)
    {
      throw new IllegalArgumentException("Unknown mapping strategy: " + mappingStrategyType);
    }

    Map<String, String> properties = RepositoryConfigurator.getProperties(mappingStrategyConfig, 1);
    mappingStrategy.setProperties(properties);
    return mappingStrategy;
  }

  private IDBAdapter getDBAdapter(Element storeConfig)
  {
    NodeList dbAdapterConfigs = storeConfig.getElementsByTagName("dbAdapter");
    if (dbAdapterConfigs.getLength() != 1)
    {
      throw new IllegalStateException("Exactly one dbAdapter must be configured for DB store");
    }

    Element dbAdapterConfig = (Element)dbAdapterConfigs.item(0);
    String dbAdapterName = dbAdapterConfig.getAttribute("name");
    IDBAdapter dbAdapter = DBUtil.getDBAdapter(dbAdapterName);
    if (dbAdapter == null)
    {
      throw new IllegalArgumentException("Unknown DB adapter: " + dbAdapterName);
    }

    return dbAdapter;
  }

  private DataSource getDataSource(Element storeConfig)
  {
    NodeList dataSourceConfigs = storeConfig.getElementsByTagName("dataSource");
    if (dataSourceConfigs.getLength() != 1)
    {
      throw new IllegalStateException("Exactly one dataSource must be configured for DB store");
    }

    Properties properties = new Properties();
    Element dataSourceConfig = (Element)dataSourceConfigs.item(0);
    NamedNodeMap attributes = dataSourceConfig.getAttributes();
    for (int i = 0; i < attributes.getLength(); i++)
    {
      Attr attribute = (Attr)attributes.item(i);
      properties.put(attribute.getName(), attribute.getValue());
    }

    return DBUtil.createDataSource(properties);
  }
}
