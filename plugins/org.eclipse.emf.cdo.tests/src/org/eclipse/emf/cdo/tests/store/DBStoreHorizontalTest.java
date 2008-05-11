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
package org.eclipse.emf.cdo.tests.store;

import org.eclipse.emf.cdo.internal.server.Transaction;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.DBStore;
import org.eclipse.emf.cdo.server.internal.db.HorizontalMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.MappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.ToMany;
import org.eclipse.emf.cdo.server.internal.db.ToOne;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.hsqldb.HSQLDBDataSource;
import org.eclipse.net4j.db.internal.hsqldb.HSQLDBAdapter;
import org.eclipse.net4j.internal.db.DataSourceConnectionProvider;
import org.eclipse.net4j.util.WrappedException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class DBStoreHorizontalTest extends TestLogic
{
  private HorizontalMappingStrategy mappingStrategy;

  private HSQLDBAdapter dbAdapter;

  private IDBConnectionProvider dbConnectionProvider;

  private DBStore store;

  public DBStoreHorizontalTest()
  {
  }

  @Override
  protected void doTearDown() throws Exception
  {
    store.getDBSchema().drop(dbAdapter, dbConnectionProvider);
    super.doTearDown();
    CDODBSchema.INSTANCE.drop(dbAdapter, dbConnectionProvider);
  }

  @Override
  protected IStore createStore()
  {
    mappingStrategy = createMappingStrategy();
    dbAdapter = createDBAdapter();
    dbConnectionProvider = createDBConnectionProvider();

    store = new DBStore();
    store.setMappingStrategy(mappingStrategy);
    store.setDbAdapter(dbAdapter);
    store.setDbConnectionProvider(dbConnectionProvider);
    return store;
  }

  protected IDBConnectionProvider createDBConnectionProvider()
  {
    HSQLDBDataSource dataSource = new HSQLDBDataSource();
    dataSource.setDatabase("jdbc:hsqldb:mem:storetest");
    dataSource.setUser("sa");

    return new DataSourceConnectionProvider(dataSource);
  }

  protected HSQLDBAdapter createDBAdapter()
  {
    return new HSQLDBAdapter();
  }

  protected HorizontalMappingStrategy createMappingStrategy()
  {
    Map<String, String> props = new HashMap<String, String>();
    props.put(MappingStrategy.PROP_TO_MANY_REFERENCE_MAPPING, ToMany.PER_CLASS.toString());
    props.put(MappingStrategy.PROP_TO_ONE_REFERENCE_MAPPING, ToOne.LIKE_ATTRIBUTES.toString());

    HorizontalMappingStrategy mappingStrategy = new HorizontalMappingStrategy();
    mappingStrategy.setProperties(props);
    return mappingStrategy;
  }

  private void verifyRowCount(int expectedRows, String tableName)
  {
    int actuaRowsl = query("select count(*) from " + tableName);
    assertEquals("Rows in " + tableName.toUpperCase(), expectedRows, actuaRowsl);
  }

  private int query(String sql)
  {
    Connection connection = null;
    Statement statement = null;
    ResultSet resultSet = null;

    try
    {
      connection = store.getConnection();
      statement = connection.createStatement();
      resultSet = statement.executeQuery(sql);
      if (!resultSet.next())
      {
        return 0;
      }

      return resultSet.getInt(1);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(statement);
      DBUtil.close(connection);
    }
  }

  @Override
  protected void verifyCreateModel1(Transaction transaction)
  {
    verifyRowCount(1, "cdo_repository");
    verifyRowCount(1, "cdo_packages");
    verifyRowCount(11, "cdo_classes");
    verifyRowCount(8, "cdo_supertypes");
    verifyRowCount(26, "cdo_features");
  }

  @Override
  protected void verifyCreateModel2(Transaction transaction)
  {
    verifyRowCount(1, "cdo_repository");
    verifyRowCount(2, "cdo_packages");
    verifyRowCount(12, "cdo_classes");
    verifyRowCount(9, "cdo_supertypes");
    verifyRowCount(28, "cdo_features");
  }

  @Override
  protected void verifyCreateModel3(Transaction transaction)
  {
    verifyRowCount(1, "cdo_repository");
    verifyRowCount(1, "cdo_packages");
    verifyRowCount(1, "cdo_classes");
    verifyRowCount(0, "cdo_supertypes");
    verifyRowCount(1, "cdo_features");
  }

  @Override
  protected void verifyCreateMango(Transaction transaction)
  {
    verifyRowCount(1, "cdo_repository");
    verifyRowCount(1, "cdo_packages");
    verifyRowCount(2, "cdo_classes");
    verifyRowCount(0, "cdo_supertypes");
    verifyRowCount(3, "cdo_features");
  }
}
