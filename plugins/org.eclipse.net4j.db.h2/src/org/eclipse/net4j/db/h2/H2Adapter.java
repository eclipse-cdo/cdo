/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.h2;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.spi.db.DBAdapter;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

import java.sql.Driver;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class H2Adapter extends DBAdapter
{
  private static final String NAME = "h2"; //$NON-NLS-1$

  public static final String VERSION = "1.1.114"; //$NON-NLS-1$

  public H2Adapter()
  {
    super(NAME, VERSION);
  }

  public Driver getJDBCDriver()
  {
    return new org.h2.Driver();
  }

  public DataSource createJDBCDataSource()
  {
    return new JdbcDataSource();
  }

  @Override
  protected String getTypeName(IDBField field)
  {
    DBType type = field.getType();
    switch (type)
    {
    case BIT:
      return "SMALLINT"; //$NON-NLS-1$

    case FLOAT:
      return "REAL"; //$NON-NLS-1$

    case LONGVARCHAR:
      return "VARCHAR"; //$NON-NLS-1$

    case NUMERIC:
      return "DECIMAL"; //$NON-NLS-1$

    case LONGVARBINARY:
    case VARBINARY:
      return "BLOB"; //$NON-NLS-1$
    }

    return super.getTypeName(field);
  }

  public String[] getReservedWords()
  {
    return getSQL92ReservedWords();
  }
}
