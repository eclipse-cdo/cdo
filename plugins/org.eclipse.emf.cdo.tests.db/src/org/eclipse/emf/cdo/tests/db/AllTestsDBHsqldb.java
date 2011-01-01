/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_266982_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.db.bundle.OM;
import org.eclipse.emf.cdo.tests.db.verifier.DBStoreVerifier;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.hsqldb.HSQLDBAdapter;
import org.eclipse.net4j.db.hsqldb.HSQLDBDataSource;

import javax.sql.DataSource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsDBHsqldb extends DBConfigs
{
  public static Test suite()
  {
    return new AllTestsDBHsqldb().getTestSuite("CDO Tests (DBStore Hsql Horizontal)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, AllTestsDBHsqldb.Hsqldb.INSTANCE, JVM, NATIVE);
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

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    super.initTestClasses(testClasses);

    // this takes ages ...
    testClasses.remove(Bugzilla_266982_Test.class);
  }

  /**
   * @author Eike Stepper
   */
  public static class Hsqldb extends DBStoreRepositoryConfig
  {
    private static final long serialVersionUID = 1L;

    public static final AllTestsDBHsqldb.Hsqldb INSTANCE = new Hsqldb("HSQLDB");

    public static boolean USE_VERIFIER = false;

    private transient ArrayList<HSQLDBDataSource> dataSources = new ArrayList<HSQLDBDataSource>();

    public Hsqldb(String name)
    {
      super(name);
    }

    @Override
    protected IMappingStrategy createMappingStrategy()
    {
      return CDODBUtil.createHorizontalMappingStrategy(true);
    }

    @Override
    protected IDBAdapter createDBAdapter()
    {
      return new HSQLDBAdapter();
    }

    @Override
    protected DataSource createDataSource(String repoName)
    {
      HSQLDBDataSource dataSource = new HSQLDBDataSource();
      dataSource.setDatabase("jdbc:hsqldb:mem:" + repoName);
      dataSource.setUser("sa");

      try
      {
        dataSource.setLogWriter(new PrintWriter(System.err));
      }
      catch (SQLException ex)
      {
        OM.LOG.warn(ex.getMessage());
      }

      dataSources.add(dataSource);
      return dataSource;
    }

    @Override
    public void tearDown() throws Exception
    {
      try
      {
        if (USE_VERIFIER)
        {
          IRepository testRepository = getRepository(REPOSITORY_NAME);
          if (testRepository != null)
          {
            getVerifier(testRepository).verify();
          }
        }
      }
      finally
      {
        try
        {
          super.tearDown();
        }
        finally
        {
          shutDownHsqldb();
        }
      }
    }

    protected DBStoreVerifier getVerifier(IRepository repository)
    {
      return new DBStoreVerifier.Audit(repository);
    }

    private void shutDownHsqldb() throws SQLException
    {
      for (HSQLDBDataSource ds : dataSources)
      {
        Connection connection = null;
        Statement statement = null;

        try
        {
          connection = ds.getConnection();
          statement = connection.createStatement();
          statement.execute("SHUTDOWN");
        }
        finally
        {
          DBUtil.close(statement);
          DBUtil.close(connection);
        }
      }

      dataSources.clear();
    }

    @Override
    protected void initRepositoryProperties(Map<String, String> props)
    {
      super.initRepositoryProperties(props);
      props.put(Props.SUPPORTING_AUDITS, "true");
    }
  }
}
