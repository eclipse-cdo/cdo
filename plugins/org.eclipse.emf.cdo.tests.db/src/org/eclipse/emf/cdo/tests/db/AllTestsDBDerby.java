/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.server.db.CDODBUtil;
import org.eclipse.emf.cdo.server.db.mapping.IMappingStrategy;
import org.eclipse.emf.cdo.tests.AllTestsAllConfigs;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.derby.EmbeddedDerbyAdapter;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.TMPUtil;

import org.apache.derby.jdbc.EmbeddedDataSource;

import javax.sql.DataSource;

import java.io.File;
import java.sql.Connection;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsDBDerby extends AllTestsAllConfigs
{
  public static Test suite()
  {
    return new AllTestsDBDerby().getTestSuite("CDO Tests (DBStoreRepositoryConfig Derby Horizontal)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, AllTestsDBDerby.Derby.ReusableFolder.INSTANCE, TCP, NATIVE);
    // addScenario(parent, COMBINED, AllTestsDBDerby.Derby.INSTANCE, TCP, NATIVE);
  }

  /**
   * @author Eike Stepper
   */
  public static class Derby extends DBStoreRepositoryConfig
  {
    private static final long serialVersionUID = 1L;

    public static final AllTestsDBDerby.Derby INSTANCE = new Derby("DBStore: Derby");

    protected transient File dbFolder;

    protected transient EmbeddedDataSource dataSource;

    public Derby(String name)
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
      return new EmbeddedDerbyAdapter();
    }

    @Override
    protected DataSource createDataSource()
    {
      dbFolder = createDBFolder();
      tearDownClean();

      dataSource = new EmbeddedDataSource();
      dataSource.setDatabaseName(dbFolder.getAbsolutePath());
      dataSource.setCreateDatabase("create");
      return dataSource;
    }

    @Override
    public void tearDown() throws Exception
    {
      tearDownClean();
      super.tearDown();
    }

    protected void tearDownClean()
    {
      IOUtil.delete(dbFolder);
    }

    protected File createDBFolder()
    {
      return TMPUtil.createTempFolder("derby_", "_test", new File("/temp"));
    }

    /**
     * @author Eike Stepper
     */
    public static class ReusableFolder extends Derby
    {
      private static final long serialVersionUID = 1L;

      public static final ReusableFolder INSTANCE = new ReusableFolder("DBStore: Derby (Reusable Folder)");

      private static File reusableFolder;

      public ReusableFolder(String name)
      {
        super(name);
      }

      @Override
      protected DataSource createDataSource()
      {
        dataSource = new EmbeddedDataSource();
        if (reusableFolder == null)
        {
          reusableFolder = createDBFolder();
          IOUtil.delete(reusableFolder);
        }

        dbFolder = reusableFolder;
        dataSource.setDatabaseName(dbFolder.getAbsolutePath());
        dataSource.setCreateDatabase("create");
        return dataSource;
      }

      @Override
      protected void tearDownClean()
      {
        reusableFolder.deleteOnExit();
        Connection connection = null;

        try
        {
          connection = dataSource.getConnection();
          DBUtil.dropAllTables(connection, dataSource.getDatabaseName());
        }
        catch (RuntimeException ex)
        {
          throw ex;
        }
        catch (Exception ex)
        {
          throw WrappedException.wrap(ex);
        }
        finally
        {
          DBUtil.close(connection);
        }
      }
    }
  }
}
