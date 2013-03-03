/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.tests;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.util.io.IOUtil;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class H2Test extends AbstractDBTest
{
  private File dbFolder;

  @Override
  protected IDBAdapter createDBAdapter()
  {
    return new org.eclipse.net4j.db.h2.H2Adapter();
  }

  @Override
  protected DataSource createDataSource()
  {
    dbFolder = createTempFolder("h2_");
    deleteDBFolder();
    msg("Using DB folder: " + dbFolder.getAbsolutePath());

    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL("jdbc:h2:" + dbFolder.getAbsolutePath() + "/h2test");
    return dataSource;
  }

  @Override
  protected void doTearDown() throws Exception
  {
    deleteDBFolder();
    super.doTearDown();
  }

  private void deleteDBFolder()
  {
    IOUtil.delete(dbFolder);
  }
}
