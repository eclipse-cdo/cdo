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

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.derby.EmbeddedDerbyAdapter;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.TMPUtil;

import org.apache.derby.jdbc.EmbeddedDataSource;

import javax.sql.DataSource;

import java.io.File;

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
    addScenario(parent, COMBINED, AllTestsDBDerby.Derby.INSTANCE, TCP, NATIVE);
  }

  /**
   * @author Eike Stepper
   */
  public static class Derby extends DBStoreRepositoryConfig
  {
    private static final long serialVersionUID = 1L;
  
    public static final AllTestsDBDerby.Derby INSTANCE = new Derby("DBStore: Derby");
  
    private transient File dbFolder;
  
    private transient EmbeddedDataSource dataSource;
  
    public Derby(String name)
    {
      super(name);
    }
  
    @Override
    protected IMappingStrategy createMappingStrategy()
    {
      return CDODBUtil.createHorizontalMappingStrategy();
    }
  
    @Override
    protected IDBAdapter createDBAdapter()
    {
      return new EmbeddedDerbyAdapter();
    }
  
    @Override
    protected DataSource createDataSource()
    {
      dbFolder = TMPUtil.createTempFolder("derby_", null, new File("/temp"));
      deleteDBFolder();
  
      dataSource = new EmbeddedDataSource();
      dataSource.setDatabaseName(dbFolder.getAbsolutePath());
      dataSource.setCreateDatabase("create");
      return dataSource;
    }
  
    @Override
    public void tearDown() throws Exception
    {
      deleteDBFolder();
      super.tearDown();
    }
  
    private void deleteDBFolder()
    {
      IOUtil.delete(dbFolder);
    }
  }
}
