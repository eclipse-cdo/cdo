/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.server.internal.db.HorizontalMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

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
  public static final String EXT_POINT = "mappingStrategies";

  private CDODBUtil()
  {
  }

  public static IDBStore createStore(IMappingStrategy mappingStrategy, IDBAdapter dbAdapter,
      IDBConnectionProvider dbConnectionProvider)
  {
    DBStore store = new DBStore();
    store.setMappingStrategy(mappingStrategy);
    store.setDbAdapter(dbAdapter);
    store.setDbConnectionProvider(dbConnectionProvider);
    mappingStrategy.setStore(store);
    return store;
  }

  public static IMappingStrategy createHorizontalMappingStrategy()
  {
    return new HorizontalMappingStrategy();
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
    IConfigurationElement[] elements = registry.getConfigurationElementsFor(OM.BUNDLE_ID, EXT_POINT);
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
