/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.tests.AllTestsAllConfigs;
import org.eclipse.emf.cdo.tests.AuditTest;
import org.eclipse.emf.cdo.tests.ExternalReferenceTest;
import org.eclipse.emf.cdo.tests.XATransactionTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_252214_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_259869_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.db.bundle.OM;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.postgresql.PostgreSQLAdapter;

import org.postgresql.jdbc3.Jdbc3SimpleDataSource;

import javax.sql.DataSource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/*
 * Database preparation before test execution.
 * 1. create user sa (pass: sa)
 * 2. create database cdodb1
 * 
 * Database creation/removal is avoided since takes too long and makes test-suite impractical.
 */

/**
 * @author Victor Roldan Betancort
 */
public class AllTestsDBPsql extends AllTestsAllConfigs
{
  public static Test suite()
  {
    return new AllTestsDBPsql().getTestSuite("CDO Tests (DBStoreRepositoryConfig PSQL Horizontal)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, AllTestsDBPsql.Psql.INSTANCE, TCP, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    super.initTestClasses(testClasses);

    // TODO never finishes - need to fix?
    testClasses.remove(Bugzilla_259869_Test.class);
    testClasses.remove(ExternalReferenceTest.class);
    testClasses.remove(XATransactionTest.class);

    // non-audit mode - remove audit tests
    testClasses.remove(AuditTest.class);
    testClasses.remove(AuditTest.LocalAuditTest.class);
    testClasses.remove(Bugzilla_252214_Test.class);
  }

  /**
   * @author Victor Roldan Betancort
   */
  public static class Psql extends DBStoreRepositoryConfig
  {
    private static final long serialVersionUID = 1L;

    public static final AllTestsDBPsql.Psql INSTANCE = new Psql("PostgreSQL");

    private transient Jdbc3SimpleDataSource dataSource;

    private transient Jdbc3SimpleDataSource setupDataSource;

    public Psql(String name)
    {
      super(name);
    }

    @Override
    protected IMappingStrategy createMappingStrategy()
    {
      return CDODBUtil.createHorizontalMappingStrategy(false);
    }

    @Override
    protected IDBAdapter createDBAdapter()
    {
      return new PostgreSQLAdapter();
    }

    @Override
    protected DataSource createDataSource()
    {
      dataSource = new Jdbc3SimpleDataSource();
      dataSource.setServerName("localhost");
      dataSource.setDatabaseName("cdodb1");
      dataSource.setUser("sa");
      dataSource.setPassword("sa");

      try
      {
        dataSource.setLogWriter(new PrintWriter(System.err));
      }
      catch (SQLException ex)
      {
        OM.LOG.warn(ex);
      }

      return dataSource;
    }

    @Override
    public void tearDown() throws Exception
    {
      super.tearDown();
      dropDatabase();
    }

    private void dropDatabase() throws Exception
    {
      Connection connection = null;

      try
      {
        connection = getSetupDataSource().getConnection();
        DBUtil.dropAllTables(connection, "cdodb1");
      }
      catch (SQLException ignore)
      {
        System.err.println(ignore);
      }
      finally
      {
        DBUtil.close(connection);
      }
    }

    private DataSource getSetupDataSource()
    {
      if (setupDataSource == null)
      {
        setupDataSource = new Jdbc3SimpleDataSource();
        setupDataSource.setServerName("localhost");
        setupDataSource.setDatabaseName("cdodb1");
        setupDataSource.setUser("sa");
        setupDataSource.setPassword("sa");
      }

      return setupDataSource;
    }
  }
}
