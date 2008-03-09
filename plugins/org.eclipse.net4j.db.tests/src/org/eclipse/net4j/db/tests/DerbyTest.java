/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.db.tests;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.db.internal.derby.DerbyAdapter;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.TMPUtil;

import org.apache.derby.jdbc.EmbeddedDataSource;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class DerbyTest extends AbstractDBTest
{
  private File dbFolder;

  @Override
  protected void doTearDown() throws Exception
  {
    deleteDBFolder();
    super.doTearDown();
  }

  @Override
  protected IDBAdapter createDBAdapter()
  {
    return new DerbyAdapter();
  }

  @Override
  protected IDBConnectionProvider createDBConnectionProvider()
  {
    dbFolder = TMPUtil.createTempFolder();
    deleteDBFolder();
    msg("Using DB folder: " + dbFolder.getAbsolutePath());

    EmbeddedDataSource dataSource = new EmbeddedDataSource();
    dataSource.setDatabaseName(dbFolder.getAbsolutePath());
    dataSource.setCreateDatabase("true");

    return DBUtil.createConnectionProvider(dataSource);
  }

  private void deleteDBFolder()
  {
    if (dbFolder != null)
    {
      if (dbFolder.exists())
      {
        IOUtil.delete(dbFolder);
      }
    }
  }
}
