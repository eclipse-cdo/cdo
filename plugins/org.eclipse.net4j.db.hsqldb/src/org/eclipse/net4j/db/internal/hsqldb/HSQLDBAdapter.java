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
package org.eclipse.net4j.db.internal.hsqldb;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.internal.db.DBAdapter;
import org.eclipse.net4j.internal.db.DBField;

import org.hsqldb.jdbcDriver;

import java.sql.Driver;

/**
 * @author Eike Stepper
 */
public class HSQLDBAdapter extends DBAdapter
{
  public HSQLDBAdapter()
  {
    super("hsqldb", "1.8.0.8");
  }

  public Driver getJDBCDriver()
  {
    return new jdbcDriver();
  }

  @Override
  protected String getTypeName(DBField field)
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
      return "LONGVARCHAR";

    case LONGVARBINARY:
      return "LONGVARBINARY";

    case BLOB:
      return "LONGVARBINARY";

    case CLOB:
      return "LONGVARCHAR";

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
}
