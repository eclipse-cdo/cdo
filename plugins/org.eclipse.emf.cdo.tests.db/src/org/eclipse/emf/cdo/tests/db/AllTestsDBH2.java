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

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.h2.H2Adapter;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.TMPUtil;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsDBH2 extends DBConfigs
{
  public static Test suite()
  {
    return new AllTestsDBH2().getTestSuite("CDO Tests (DBStoreRepositoryConfig H2 Horizontal)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, AllTestsDBH2.H2.INSTANCE, JVM, NATIVE);
  }

  /**
   * @author Eike Stepper
   */
  public static class H2 extends DBStoreRepositoryConfig
  {
    private static final long serialVersionUID = 1L;

    public static final AllTestsDBH2.H2 INSTANCE = new H2("DBStore: H2");

    protected transient File dbFolder;

    protected transient JdbcDataSource dataSource;

    public H2(String name)
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
      return new H2Adapter();
    }

    @Override
    protected DataSource createDataSource()
    {
      dbFolder = createDBFolder();
      tearDownClean();

      dataSource = new JdbcDataSource();
      dataSource.setURL("jdbc:h2:" + dbFolder.getAbsolutePath());
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
      return TMPUtil.createTempFolder("h2_", "_test", new File("/temp"));
    }
  }
}
