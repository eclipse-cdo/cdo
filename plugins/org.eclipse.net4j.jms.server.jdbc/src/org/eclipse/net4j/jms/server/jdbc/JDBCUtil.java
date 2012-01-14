/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.jms.server.jdbc;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.jms.server.IStore;
import org.eclipse.net4j.jms.server.internal.jdbc.JDBCStore;
import org.eclipse.net4j.jms.server.internal.jdbc.bundle.OM;

import javax.sql.DataSource;

import java.util.Properties;

/**
 * @author Eike Stepper
 */
public final class JDBCUtil
{
  private JDBCUtil()
  {
  }

  public static IStore getStore(IDBAdapter dbAdapter, DataSource dataSource, String instanceID)
  {
    JDBCStore store = new JDBCStore(dbAdapter, dataSource);
    store.initDatabase(instanceID);
    return store;
  }

  public static IStore getStore()
  {
    Properties properties = OM.BUNDLE.getConfigProperties();
    String adapterName = properties.getProperty("store.adapterName", "derby-embedded"); //$NON-NLS-1$ //$NON-NLS-2$
    IDBAdapter dbAdapter = DBUtil.getDBAdapter(adapterName);
    DataSource dataSource = DBUtil.createDataSource(properties, "datasource"); //$NON-NLS-1$
    String instanceID = properties.getProperty("store.instanceID", "00000000"); //$NON-NLS-1$ //$NON-NLS-2$
    return getStore(dbAdapter, dataSource, instanceID);
  }
}
