/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.tests;

import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.h2.H2Adapter;
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
  protected IDBAdapter createAdapter()
  {
    return new org.eclipse.net4j.db.h2.H2Adapter();
  }

  @Override
  protected DataSource createDataSource()
  {
    dbFolder = createTempFolder("h2_");
    deleteDBFolder();
    msg("Using DB folder: " + dbFolder.getAbsolutePath());

    String url = "jdbc:h2:" + dbFolder.getAbsolutePath() + "/h2test";

    JdbcDataSource dataSource = new JdbcDataSource();
    dataSource.setURL(url);
    H2Adapter.createSchema(dataSource, SCHEMA_NAME, true);

    dataSource = new JdbcDataSource();
    dataSource.setURL(url + ";SCHEMA=" + SCHEMA_NAME);
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
