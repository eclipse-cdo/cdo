/*
 * Copyright (c) 2009, 2011, 2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.store.logic;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.db.IMappingStrategy;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.DBStore;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.WrappedException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Eike Stepper
 */
public abstract class DBStoreTestLogic extends TestLogic
{
  public static final String DEFINITION_MODE = "";

  protected IMappingStrategy mappingStrategy;

  protected IDBAdapter dbAdapter;

  protected IDBConnectionProvider dbConnectionProvider;

  protected DBStore store;

  public DBStoreTestLogic()
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

    store = new DBStore()
    {
      @Override
      protected long getStartupTime()
      {
        return 1;
      }
    };

    store.setMappingStrategy(mappingStrategy);
    store.setDbAdapter(dbAdapter);
    store.setDbConnectionProvider(dbConnectionProvider);
    return store;
  }

  protected abstract IDBConnectionProvider createDBConnectionProvider();

  protected abstract IDBAdapter createDBAdapter();

  protected abstract IMappingStrategy createMappingStrategy();

  protected void defineOrCompare(String fileName) throws IOException
  {
    File file = new File(fileName + ".txt");
    if (fileName.equals(DEFINITION_MODE) || "*".equals(DEFINITION_MODE))
    {
      file.getParentFile().mkdirs();
      PrintStream out = new PrintStream(file);
      exportDatabase(out);
      out.close();
    }
    else
    {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      exportDatabase(new PrintStream(baos));

      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      FileInputStream fis = new FileInputStream(file);
      compareLines(fileName, fis, bais);
    }
  }

  private void compareLines(String fileName, InputStream expectedStream, InputStream actualStream) throws IOException
  {
    BufferedReader expectedReader = new BufferedReader(new InputStreamReader(expectedStream));
    BufferedReader actualReader = new BufferedReader(new InputStreamReader(actualStream));

    int line = 1;
    while (true)
    {
      String expectedLine = expectedReader.readLine();
      String actualLine = actualReader.readLine();
      if (!ObjectUtil.equals(expectedLine, actualLine))
      {
        throw new IllegalStateException("Mismatch at (" + fileName + ":" + line + ")\n" + expectedLine + "\n"
            + actualLine);
      }

      if (expectedLine == null)
      {
        break;
      }

      ++line;
    }
  }

  private void exportDatabase(PrintStream out)
  {
    store.getDBSchema().export(dbConnectionProvider, out);
    CDODBSchema.INSTANCE.export(dbConnectionProvider, out);
  }

  protected void assertRowCount(int expectedRows, String tableName)
  {
    int actualRowsl = (Integer)query("select count(*) from " + tableName);
    assertEquals("Rows in " + tableName.toUpperCase(), expectedRows, actualRowsl);
  }

  protected void assertFieldValue(Object expectedValue, String sql)
  {
    Object actualValue = query(sql);
    assertEquals("Field in " + sql, expectedValue, actualValue);
  }

  protected Object query(String sql)
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
        throw new IllegalStateException("No row: " + sql.toUpperCase());
      }

      return resultSet.getObject(1);
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
}
