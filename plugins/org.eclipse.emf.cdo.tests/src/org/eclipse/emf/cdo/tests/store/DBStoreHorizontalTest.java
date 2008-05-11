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
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class DBStoreHorizontalTest extends TestLogic
{
  private static final String DEFINITION_MODE = "";

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

  private void defineOrCompare(String fileName) throws IOException
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

  private void assertRowCount(int expectedRows, String tableName)
  {
    int actualRowsl = (Integer)query("select count(*) from " + tableName);
    assertEquals("Rows in " + tableName.toUpperCase(), expectedRows, actualRowsl);
  }

  private void assertFieldValue(Object expectedValue, String sql)
  {
    Object actualValue = query(sql);
    assertEquals("Field in " + sql, expectedValue, actualValue);
  }

  private Object query(String sql)
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

  @Override
  protected void verifyCreateModel1(Transaction transaction) throws Exception
  {
    defineOrCompare("defs/horizontal/verifyCreateModel1");
    // assertRowCount(1, "cdo_repository");
    // assertRowCount(1, "cdo_packages");
    // assertRowCount(11, "cdo_classes");
    // assertRowCount(8, "cdo_supertypes");
    // assertRowCount(26, "cdo_features");
  }

  @Override
  protected void verifyCreateModel2(Transaction transaction) throws Exception
  {
    defineOrCompare("defs/horizontal/verifyCreateModel2");
    // assertRowCount(1, "cdo_repository");
    // assertRowCount(2, "cdo_packages");
    // assertRowCount(12, "cdo_classes");
    // assertRowCount(9, "cdo_supertypes");
    // assertRowCount(28, "cdo_features");
  }

  @Override
  protected void verifyCreateModel3(Transaction transaction) throws Exception
  {
    defineOrCompare("defs/horizontal/verifyCreateModel3");
    // assertRowCount(1, "cdo_repository");
    // assertRowCount(1, "cdo_packages");
    // assertRowCount(1, "cdo_classes");
    // assertRowCount(0, "cdo_supertypes");
    // assertRowCount(1, "cdo_features");
  }

  @Override
  protected void verifyCreateMango(Transaction transaction) throws Exception
  {
    defineOrCompare("defs/horizontal/verifyCreateMango");
    // assertRowCount(1, "cdo_repository");
    // assertRowCount(1, "cdo_packages");
    // assertRowCount(2, "cdo_classes");
    // assertRowCount(0, "cdo_supertypes");
    // assertRowCount(3, "cdo_features");
  }

  @Override
  protected void verifyCommitCompany(Transaction transaction) throws Exception
  {
    defineOrCompare("defs/horizontal/verifyCommitCompany");
    // assertRowCount(1, "CDOResource");
    // assertFieldValue("/res1", "select path_0 from CDOResource where cdo_id=1 and cdo_version=1");
    //
    // assertRowCount(1, "Company");
    // assertFieldValue("Sympedia", "select name from Company where cdo_id=1 and cdo_version=1");
    // assertFieldValue("Homestr. 17", "select street from Company where cdo_id=1 and cdo_version=1");
    // assertFieldValue("Berlin", "select city from Company where cdo_id=1 and cdo_version=1");
  }
}
