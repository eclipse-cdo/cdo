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
package org.eclipse.emf.cdo.server.db;

/**
 * @author Eike Stepper
 */
public final class CDODBUtil
{
  private CDODBUtil()
  {
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
  // String adapterName = properties.getProperty("store.adapterName", "derby");
  // IDBAdapter dbAdapter = DBUtil.getDBAdapter(adapterName);
  // DataSource dataSource = DBUtil.createDataSource(properties, "datasource");
  // return getStoreManager(dbAdapter, dataSource);
  // }
}
