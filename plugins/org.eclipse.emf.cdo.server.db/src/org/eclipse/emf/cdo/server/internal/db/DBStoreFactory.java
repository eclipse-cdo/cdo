/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.server.RepositoryConfigurator;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreFactory;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;

import org.eclipse.net4j.db.ConnectionProvider;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;

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
    ConnectionProvider connectionProvider = DBUtil.createConnectionProvider(dataSource);
    DBStore store = new DBStore(mappingStrategy, dbAdapter, connectionProvider);
    mappingStrategy.setStore(store);
    return store;
  }

  private IMappingStrategy getMappingStrategy(Element storeConfig)
  {
    NodeList mappingStrategyConfigs = storeConfig.getElementsByTagName("mappingStrategy");
    if (mappingStrategyConfigs.getLength() != 1)
    {
      throw new IllegalStateException("Exactly one mappingStrategy must be configured for DB store");
    }

    Element mappingStrategyConfig = (Element)mappingStrategyConfigs.item(0);
    String mappingStrategyType = mappingStrategyConfig.getAttribute("type");
    IMappingStrategy mappingStrategy = CDODBUtil.createMappingStrategy(mappingStrategyType);
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
    return DBUtil.getDBAdapter(dbAdapterName);
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
