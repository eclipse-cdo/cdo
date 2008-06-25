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

import org.eclipse.net4j.db.IDBAdapter;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * @author Eike Stepper
 */
public class MysqlTest extends AbstractDBTest<MysqlDataSource>
{
  @Override
  protected IDBAdapter createDBAdapter()
  {
    return new org.eclipse.net4j.db.internal.mysql.MYSQLAdapter();
  }

  @Override
  protected void configureDataSourcer(MysqlDataSource dataSource)
  {
    dataSource.setDatabaseName("dbtest");
  }
}
