/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.server.internal.db.jdbc.PreparedStatementJDBCDelegateProvider;
import org.eclipse.emf.cdo.server.internal.db.jdbc.StatementJDBCDelegateProvider;
import org.eclipse.emf.cdo.server.internal.db.mapping.HorizontalMappingStrategy;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.WrappedException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

/**
 * @author Eike Stepper
 */
public final class CDODBUtil
{
  /**
   * @since 2.0
   */
  public static final String EXT_POINT_MAPPING_STRATEGIES = "mappingStrategies";

  /**
   * @since 2.0
   */
  public static final String EXT_POINT_JDBC_DELEGATE_PROVIDERS = "jdbcDelegateProviders";

  private CDODBUtil()
  {
  }

  /**
   * @since 2.0
   */
  public static IDBStore createStore(IMappingStrategy mappingStrategy, IDBAdapter dbAdapter,
      IDBConnectionProvider dbConnectionProvider, IJDBCDelegateProvider delegateProvider)
  {
    DBStore store = new DBStore();
    store.setMappingStrategy(mappingStrategy);
    store.setDbAdapter(dbAdapter);
    store.setDbConnectionProvider(dbConnectionProvider);
    store.setJDBCDelegateProvider(delegateProvider);
    mappingStrategy.setStore(store);
    return store;
  }

  public static IMappingStrategy createHorizontalMappingStrategy()
  {
    return new HorizontalMappingStrategy();
  }

  /**
   * @since 2.0
   */
  public static IJDBCDelegateProvider createStatementJDBCDelegateProvider()
  {
    return new StatementJDBCDelegateProvider();
  }

  /**
   * @since 2.0
   */
  public static IJDBCDelegateProvider createPreparedStatementJDBCDelegateProvider()
  {
    return new PreparedStatementJDBCDelegateProvider();
  }

  /**
   * Can only be used when Eclipse is running. In standalone scenarios create the mapping strategy instance by directly
   * calling the constructor of the mapping strategy class.
   * 
   * @see #createHorizontalMappingStrategy()
   */
  public static IMappingStrategy createMappingStrategy(String type)
  {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IConfigurationElement[] elements = registry.getConfigurationElementsFor(OM.BUNDLE_ID, EXT_POINT_MAPPING_STRATEGIES);
    for (final IConfigurationElement element : elements)
    {
      if ("mappingStrategy".equals(element.getName()))
      {
        String typeAttr = element.getAttribute("type");
        if (ObjectUtil.equals(typeAttr, type))
        {
          try
          {
            return (IMappingStrategy)element.createExecutableExtension("class");
          }
          catch (CoreException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }
      }
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public static IJDBCDelegateProvider createDelegateProvider(String type)
  {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IConfigurationElement[] elements = registry.getConfigurationElementsFor(OM.BUNDLE_ID,
        EXT_POINT_JDBC_DELEGATE_PROVIDERS);
    for (final IConfigurationElement element : elements)
    {
      if ("jdbcDelegateProvider".equals(element.getName()))
      {
        String typeAttr = element.getAttribute("type");
        if (ObjectUtil.equals(typeAttr, type))
        {
          try
          {
            return (IJDBCDelegateProvider)element.createExecutableExtension("class");
          }
          catch (CoreException ex)
          {
            throw WrappedException.wrap(ex);
          }
        }
      }
    }

    return null;
  }

  // public static CDODBStoreManager getStoreManager(IDBAdapter dbAdapter,
  // DataSource dataSource)
  // {
  // CDODBStoreManager storeManager = new CDODBStoreManager(dbAdapter,
  // dataSource);
  // storeManager.initDatabase();
  // return storeManager;
  // }
  //
  // public static CDODBStoreManager getStoreManager()
  // {
  // Properties properties = OM.BUNDLE.getConfigProperties();
  // String adapterName = properties.getProperty("store.adapterName", "derby-embedded");
  // IDBAdapter dbAdapter = DBUtil.getDBAdapter(adapterName);
  // DataSource dataSource = DBUtil.createDataSource(properties, "datasource");
  // return getStoreManager(dbAdapter, dataSource);
  // }
}
