/*
 * Copyright (c) 2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.db.revisioncache;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.derby.EmbeddedDerbyAdapter;
import org.eclipse.net4j.util.io.TMPUtil;

import javax.sql.DataSource;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Andre Dietisheim
 */
public class DerbyDBRevisionCacheTest extends AbstractDBRevisionCacheTest
{
  private static final String DB_NAME = TMPUtil.getTempFolder().getAbsolutePath() //
      + File.separatorChar //
      + "derbyDBRevisionCacheTest";

  @Override
  public DataSource createDataSource()
  {
    Map<Object, Object> properties = new HashMap<>();
    properties.put("class", "org.apache.derby.jdbc.EmbeddedDataSource");
    properties.put("databaseName", DB_NAME);
    properties.put("createDatabase", "create");
    return DBUtil.createDataSource(properties);
  }

  /**
   * Drop all table on a given derby database.
   */
  @Override
  public void dropAllTables(Connection connection)
  {
    DBUtil.dropAllTables(connection, DB_NAME);
  }

  @Override
  public IDBAdapter getAdapter()
  {
    return new EmbeddedDerbyAdapter();
  }
}
