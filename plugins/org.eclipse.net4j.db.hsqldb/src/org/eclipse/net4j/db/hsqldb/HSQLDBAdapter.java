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
package org.eclipse.net4j.db.hsqldb;

import org.eclipse.net4j.db.DBType;
import org.eclipse.net4j.db.ddl.IDBField;
import org.eclipse.net4j.spi.db.DBAdapter;

import org.hsqldb.jdbcDriver;

import javax.sql.DataSource;

import java.sql.Driver;
import java.util.StringTokenizer;

/**
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

  public Driver getJDBCDriver()
  {
    return new jdbcDriver();
  }

  public DataSource createJDBCDataSource()
  {
    return new HSQLDBDataSource();
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

  public String[] getReservedWords()
  {
    return getSQL92ReservedWords();
  }

  @Override
  public void appendValue(StringBuilder builder, IDBField field, Object value)
  {
    Object newValue = value;

    if (value instanceof String)
    {
      // HSQLDB just adds one additional single quote for a single quote
      String str = (String)value;
      StringTokenizer tokenizer = new StringTokenizer(str, "\'", true); // split on single quote //$NON-NLS-1$
      StringBuilder newValueBuilder = new StringBuilder();

      while (tokenizer.hasMoreTokens())
      {
        String current = tokenizer.nextToken();
        if (current.length() == 0)
        {
          continue;
        }

        if (current.length() > 1) // >1 -> can not be token -> normal string
        {
          newValueBuilder.append(current);
        }
        else
        { // length == 1
          newValueBuilder.append(processEscape(current.charAt(0)));
        }
      }

      newValue = newValueBuilder.toString();
    }
    else if (value instanceof Character)
    {
      newValue = processEscape((Character)value);
    }

    super.appendValue(builder, field, newValue);
  }

  private Object processEscape(char c)
  {
    if (c == '\'') // one single quote -->
    {
      return "\'\'"; // results two single quotes //$NON-NLS-1$
    }

    return c; // no escape character --> return as is
  }
}
