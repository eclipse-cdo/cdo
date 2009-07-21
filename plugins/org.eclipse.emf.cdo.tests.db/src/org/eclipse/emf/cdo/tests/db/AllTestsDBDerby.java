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
import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsDBDerby extends DBConfigs
{
  public static Test suite()
  {
    return new AllTestsDBDerby().getTestSuite("CDO Tests (DBStoreRepositoryConfig Derby Horizontal)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, AllTestsDBDerby.Derby.ReusableFolder.INSTANCE, JVM, NATIVE);
    // addScenario(parent, COMBINED, AllTestsDBDerby.Derby.INSTANCE, TCP, NATIVE);
  }

  /**
   * @author Eike Stepper
   */
  public static class Derby extends DBStoreRepositoryConfig
  {
    private static final long serialVersionUID = 1L;

    public static final AllTestsDBDerby.Derby INSTANCE = new Derby("DBStore: Derby");

    private transient ArrayList<File> dbFolders = new ArrayList<File>();

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
    protected DataSource createDataSource(String repoName)
    {
      File dbFolder = createDBFolder(repoName);
      dbFolders.add(dbFolder);

      tearDownClean(dbFolder);

      EmbeddedDataSource dataSource = new EmbeddedDataSource();
      dataSource.setDatabaseName(dbFolder.getAbsolutePath());
      dataSource.setCreateDatabase("create");

      return dataSource;
    }

    @Override
    public void tearDown() throws Exception
    {
      for (File folder : dbFolders)
      {
        tearDownClean(folder);
      }
      
      super.tearDown();
    }

    protected void tearDownClean(File dbFolder)
    {
      IOUtil.delete(dbFolder);
    }

    protected File createDBFolder(String repo)
    {
      return TMPUtil.createTempFolder("derby_" + repo + "_", "_test", new File("/temp"));
    }

    /**
     * @author Eike Stepper
     */
    public static class ReusableFolder extends Derby
    {
      private static final long serialVersionUID = 1L;

      public static final ReusableFolder INSTANCE = new ReusableFolder("DBStore: Derby (Reusable Folder)");

      private static HashMap<String, File> dbFolders = new HashMap<String, File>();

      private static HashMap<File, EmbeddedDataSource> dataSources = new HashMap<File, EmbeddedDataSource>();

      public ReusableFolder(String name)
      {
        super(name);
      }

      @Override
      protected DataSource createDataSource(String repoName)
      {
        EmbeddedDataSource dataSource = new EmbeddedDataSource();
        File reusableFolder = dbFolders.get(repoName);

        if (reusableFolder == null)
        {
          reusableFolder = createDBFolder(repoName);
          IOUtil.delete(reusableFolder);
          dbFolders.put(repoName, reusableFolder);
        }

        dataSource.setDatabaseName(reusableFolder.getAbsolutePath());
        dataSource.setCreateDatabase("create");
        dataSources.put(reusableFolder, dataSource);

        tearDownClean(reusableFolder);

        return dataSource;
      }

      @Override
      protected void tearDownClean(File folder)
      {
        folder.deleteOnExit();
        Connection connection = null;

        EmbeddedDataSource dataSource = dataSources.get(folder);

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
