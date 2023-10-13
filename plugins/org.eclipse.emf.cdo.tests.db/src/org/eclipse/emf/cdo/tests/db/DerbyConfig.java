/*
 * Copyright (c) 2011, 2012, 2015-2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.server.db.IDBStore;

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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class DerbyConfig extends DBConfig
{
  public static final String DB_ADAPTER_NAME = "Derby";

  private static final long serialVersionUID = 1L;

  private static HashMap<String, File> dbFolders = new HashMap<>();

  private static HashMap<File, EmbeddedDataSource> dataSources = new HashMap<>();

  public DerbyConfig()
  {
    super(DB_ADAPTER_NAME);
  }

  @Override
  protected String getDBAdapterName()
  {
    return DB_ADAPTER_NAME;
  }

  public Collection<File> getDBFolders()
  {
    return dbFolders.values();
  }

  @Override
  protected IDBAdapter createDBAdapter()
  {
    return new EmbeddedDerbyAdapter();
  }

  @Override
  protected DataSource createDataSource(String repoName)
  {
    File reusableFolder = dbFolders.get(repoName);

    boolean needsNewFolder = reusableFolder == null;
    if (needsNewFolder)
    {
      reusableFolder = createDBFolder(repoName);
      IOUtil.delete(reusableFolder);
      dbFolders.put(repoName, reusableFolder);
    }

    EmbeddedDataSource dataSource = new EmbeddedDataSource();
    dataSource.setDatabaseName(reusableFolder.getAbsolutePath());
    dataSource.setCreateDatabase("create");
    dataSources.put(reusableFolder, dataSource);

    if (!needsNewFolder)
    {
      tearDownClean(reusableFolder);
    }

    return dataSource;
  }

  @Override
  protected Map<String, String> createStoreProperties(String repoName)
  {
    Map<String, String> props = super.createStoreProperties(repoName);
    props.put(IDBStore.Props.SCHEMA_NAME, "APP");
    return props;
  }

  @Override
  protected void deactivateRepositories()
  {
    for (File folder : getDBFolders())
    {
      tearDownClean(folder);
    }

    super.deactivateRepositories();
  }

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

  protected File createDBFolder(String repo)
  {
    return TMPUtil.createTempFolder("derby_" + repo + "_", "_test");
  }
}
