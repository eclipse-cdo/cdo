/*
 * Copyright (c) 2008-2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.hsqldb;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.IDBAdapter;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.spi.db.DBAdapter;

import java.sql.SQLException;

/**
 * A {@link IDBAdapter DB adapter} for <a href="http://hsqldb.org/">HyperSQL</a> databases.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public class HSQLDBAdapter extends DBAdapter
{
  private static final String NAME = "hsqldb"; //$NON-NLS-1$

  public static final String VERSION = "1.8.0.8"; //$NON-NLS-1$

  public HSQLDBAdapter()
  {
    super(NAME, VERSION);
  }

  @Override
  protected String getTypeName(IDBField field)
  {
    DBType type = field.getType();
    switch (type)
    {
    case BOOLEAN:
    case BIT:
    case TINYINT:
    case SMALLINT:
    case INTEGER:
    case BIGINT:
    case FLOAT:
    case REAL:
    case DOUBLE:
    case DATE:
    case TIME:
    case TIMESTAMP:
      return type.toString();

    case LONGVARCHAR:
      return "LONGVARCHAR"; //$NON-NLS-1$

    case LONGVARBINARY:
      return "LONGVARBINARY"; //$NON-NLS-1$

    case BLOB:
      return "LONGVARBINARY"; //$NON-NLS-1$

    case CLOB:
      return "LONGVARCHAR"; //$NON-NLS-1$

    case CHAR:
    case VARCHAR:
    case BINARY:
    case VARBINARY:
      return type.toString() + field.formatPrecision();

    case NUMERIC:
    case DECIMAL:
      return type.toString() + field.formatPrecisionAndScale();
    }

    return super.getTypeName(field);
  }

  @Override
  public String[] getReservedWords()
  {
    return getSQL92ReservedWords();
  }

  @Override
  public boolean isTableNotFoundException(SQLException ex)
  {
    String sqlState = ex.getSQLState();
    return "42501".equals(sqlState);
  }

  @Override
  public boolean isColumnNotFoundException(SQLException ex)
  {
    String sqlState = ex.getSQLState();
    return "42501".equals(sqlState);
  }
}
