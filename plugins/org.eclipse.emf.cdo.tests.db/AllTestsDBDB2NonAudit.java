/*
 * Copyright (c) 2010-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.tests.AuditTest;
import org.eclipse.emf.cdo.tests.AuditSameSessionTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_252214_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.db2.DB2Adapter;
import org.eclipse.net4j.db.db2.DB2SchemaDatasource;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsDBDB2NonAudit extends DBConfigs
{
  public static Test suite()
  {
    return new AllTestsDBDB2NonAudit().getTestSuite("CDO Tests (DBStore H2 Horizontal - non-audit mode)"); //$NON-NLS-1$
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, AllTestsDBDB2NonAudit.DB2NonAudit.INSTANCE, JVM, NATIVE);
  }

  @Override
  protected boolean hasAuditSupport()
  {
    return false;
  }

  @Override
  protected boolean hasBranchingSupport()
  {
    return false;
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    super.initTestClasses(testClasses);

    // non-audit mode - remove audit tests
    testClasses.remove(AuditTest.class);
    testClasses.remove(AuditTestSameSession.class);
    testClasses.remove(Bugzilla_252214_Test.class);
  }

  /**
   * @author Eike Stepper
   */
  public static class DB2NonAudit extends DBStoreRepositoryConfig
  {
    private static final String DB_NAME = "SAMPLE"; //$NON-NLS-1$

    private static final String DB_USER = "DB2ADMIN"; //$NON-NLS-1$

    private static final String DB_PASSWORD = "12345"; //$NON-NLS-1$

    static final String DB_SCHEMA = "CDOTEST"; //$NON-NLS-1$

    private static final long serialVersionUID = 1L;

    public static final AllTestsDBDB2NonAudit.DB2NonAudit INSTANCE = new DB2NonAudit("DBStore: DB2 (non-audit)"); //$NON-NLS-1$

    private transient String currentRepoName;

    private static int tableCounter;

    private static int dropCounter;

    public DB2NonAudit(String name)
    {
      super(name);
    }

    @Override
    protected void initRepositoryProperties(Map<String, String> props)
    {
      super.initRepositoryProperties(props);
      props.put(IRepository.Props.SUPPORTING_AUDITS, "false"); //$NON-NLS-1$
    }

    @Override
    protected IMappingStrategy createMappingStrategy()
    {
      Map<String, String> properties = new HashMap<String, String>();
      properties.put("qualifiedNames", "false");
      properties.put("forceNamesWithID", "false");

      IMappingStrategy mappingStrategy = CDODBUtil.createHorizontalMappingStrategy(false);
      mappingStrategy.setProperties(properties);

      return mappingStrategy;
    }

    @Override
    protected IDBAdapter createDBAdapter()
    {
      return new DB2Adapter();
    }

    @Override
    protected DataSource createDataSource(final String repoName)
    {
      if (currentRepoName != null && !repoName.equals(currentRepoName))
      {
        throw new IllegalStateException("This test config only supports a single repository"); //$NON-NLS-1$
      }

      currentRepoName = repoName;

      DB2SchemaDatasource dataSource = new DB2SchemaDatasource(DB_SCHEMA);
      dataSource.setDatabaseName(DB_NAME);
      dataSource.setUser(DB_USER);
      dataSource.setPassword(DB_PASSWORD);

      if (!isRestarting())
      {
        initAllTables(dataSource);
      }

      return dataSource;
    }

    @Override
    public void tearDown() throws Exception
    {
      currentRepoName = null;
      super.tearDown();
    }

    private static void initAllTables(DataSource dataSource)
    {
      Connection conn = null;
      Statement statement = null;
      String sql = null;

      try
      {
        conn = dataSource.getConnection();
        List<String> tableNames = DBUtil.getAllTableNames(conn, DB_SCHEMA);
        if (tableCounter != 0)
        {
          if (tableCounter < tableNames.size())
          {
            dropCounter = 20;
          }
        }

        tableCounter = tableNames.size();

        boolean drop = false;
        if (dropCounter > 0)
        {
          --dropCounter;
        }
        else if (dropCounter == 0)
        {
          tableCounter = 0;
          dropCounter = -1;
          drop = true;
        }

        statement = conn.createStatement();
        for (String tableName : tableNames)
        {
          sql = (drop ? "DROP TABLE " : "DELETE FROM ") + tableName; //$NON-NLS-1$ //$NON-NLS-2$
          DBUtil.trace(sql);
          statement.execute(sql);
        }
      }
      catch (SQLException ex)
      {
        throw new DBException(ex, sql);
      }
      finally
      {
        DBUtil.close(statement);
        DBUtil.close(conn);
      }
    }
  }
}
