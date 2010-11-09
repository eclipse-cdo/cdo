/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - introduced variable mapping strategies
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.TMPUtil;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsDBH2 extends DBConfigs
{
  public static Test suite()
  {
    return new AllTestsDBH2().getTestSuite("CDO Tests (DBStore H2 Horizontal)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    //addScenario(parent, COMBINED, AllTestsDBH2.H2.ReusableFolder.AUDIT_INSTANCE, JVM, NATIVE);
    addScenario(parent, COMBINED, AllTestsDBH2.H2.ReusableFolder.RANGE_INSTANCE, JVM, NATIVE);
  }

  @Override
  protected boolean hasAuditSupport()
  {
    return true;
  }

  @Override
  protected boolean hasBranchingSupport()
  {
    return false;
  }

  /**
   * @author Eike Stepper
   */
  public static class H2 extends DBStoreRepositoryConfig
  {
    private static final long serialVersionUID = 1L;

    public static final AllTestsDBH2.H2 INSTANCE = new H2("DBStore: H2 (audit)",
        "org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalAuditMappingStrategy");

    protected transient File dbFolder;

    private String mappingStrategy;

    public H2(String name, String mappingStrategy)
    {
      super(name);
      this.mappingStrategy = mappingStrategy;
    }

    @Override
    protected void initRepositoryProperties(Map<String, String> props)
    {
      super.initRepositoryProperties(props);
      props.put(IRepository.Props.SUPPORTING_AUDITS, "true");
      props.put(IRepository.Props.SUPPORTING_BRANCHES, "false");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected IMappingStrategy createMappingStrategy()
    {
      try
      {
        Class<IMappingStrategy> clazz = (Class<IMappingStrategy>)Class.forName(mappingStrategy);
        return clazz.newInstance();
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }

    @Override
    protected IDBAdapter createDBAdapter()
    {
      return new H2Adapter();
    }

    @Override
    protected DataSource createDataSource(String repoName)
    {
      if (dbFolder == null)
      {
        dbFolder = createDBFolder();
        tearDownClean();
      }

      JdbcDataSource dataSource = new JdbcDataSource();
      dataSource.setURL("jdbc:h2:" + dbFolder.getAbsolutePath() + "/h2test;SCHEMA=" + repoName);
      return dataSource;
    }

    protected void tearDownClean()
    {
      IOUtil.delete(dbFolder);
    }

    protected File createDBFolder()
    {
      return TMPUtil.createTempFolder("h2_", "_test");
    }

    /**
     * @author Eike Stepper
     */
    public static class ReusableFolder extends H2
    {
      private static final long serialVersionUID = 1L;

      public static final ReusableFolder AUDIT_INSTANCE = new ReusableFolder("DBStore: H2 (Reusable Folder, audit), ",
          "org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalAuditMappingStrategy");

      public static final ReusableFolder RANGE_INSTANCE = new ReusableFolder(
          "DBStore: H2 (Reusable Folder, audit, range-based mapping strategy)",
          "org.eclipse.emf.cdo.server.internal.db.mapping.horizontal.HorizontalAuditMappingStrategyWithRanges");

      private static File reusableFolder;

      private static JdbcDataSource defaultDataSource;

      private transient ArrayList<String> repoNames = new ArrayList<String>();

      public ReusableFolder(String name, String mappingStrategy)
      {
        super(name, mappingStrategy);
      }

      @Override
      protected DataSource createDataSource(String repoName)
      {
        if (reusableFolder == null)
        {
          reusableFolder = createDBFolder();
          IOUtil.delete(reusableFolder);
        }

        dbFolder = reusableFolder;
        if (defaultDataSource == null)
        {
          defaultDataSource = new JdbcDataSource();
          defaultDataSource.setURL("jdbc:h2:" + dbFolder.getAbsolutePath() + "/h2test");
        }

        Connection conn = null;
        Statement stmt = null;

        try
        {
          conn = defaultDataSource.getConnection();
          stmt = conn.createStatement();

          if (!isRestarting())
          {
            stmt.execute("DROP SCHEMA IF EXISTS " + repoName);
          }

          stmt.execute("CREATE SCHEMA IF NOT EXISTS " + repoName);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
        finally
        {
          DBUtil.close(conn);
          DBUtil.close(stmt);
        }

        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:" + dbFolder.getAbsolutePath() + "/h2test;SCHEMA=" + repoName);
        return dataSource;
      }

      @Override
      protected void tearDownClean()
      {
        for (String repoName : repoNames)
        {
          tearDownClean(repoName);
        }
      }

      protected void tearDownClean(String repoName)
      {
        reusableFolder.deleteOnExit();
        Connection connection = null;
        Statement stmt = null;

        try
        {
          connection = defaultDataSource.getConnection();
          stmt = connection.createStatement();
          stmt.execute("DROP SCHEMA " + repoName);
        }
        catch (Exception ex)
        {
          throw WrappedException.wrap(ex);
        }
        finally
        {
          DBUtil.close(stmt);
          DBUtil.close(connection);
        }
      }
    }
  }
}
