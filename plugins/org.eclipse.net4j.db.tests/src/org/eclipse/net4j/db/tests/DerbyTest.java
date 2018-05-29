/*
 * Copyright (c) 2008, 2009, 2011-2013 Eike Stepper (Loehne, Germany) and others.
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

import org.apache.derby.jdbc.EmbeddedDataSource;

import javax.sql.DataSource;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class DerbyTest extends AbstractDBTest
{
  private File dbFolder;

  @Override
  protected IDBAdapter createAdapter()
  {
    return new org.eclipse.net4j.db.derby.EmbeddedDerbyAdapter();
  }

  @Override
  protected DataSource createDataSource()
  {
    dbFolder = createTempFolder("derby_");
    deleteDBFolder();
    msg("Using DB folder: " + dbFolder.getAbsolutePath());

    EmbeddedDataSource dataSource = new EmbeddedDataSource();
    dataSource.setDatabaseName(dbFolder.getAbsolutePath());
    dataSource.setCreateDatabase("create");
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
