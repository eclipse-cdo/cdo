/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.IDBType;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Eike Stepper
 */
@Deprecated
public abstract class DBType<JAVA> implements IDBType<JAVA>
{
  public static final DBType<Boolean> BOOLEAN = new DBType<Boolean>()
  {
    public String getName()
    {
      return "BOOLEAN";
    }

    public int getCode()
    {
      return 0;
    }

    public Boolean get(ResultSet resultSet, int index) throws SQLException
    {
      return resultSet.getBoolean(index);
    }

    public void set(PreparedStatement statement, int index, Boolean value) throws SQLException
    {
    }
  };

  protected DBType()
  {
  }

  public String format(JAVA value)
  {
    return value.toString();
  }

  @Override
  public String toString()
  {
    return getName();
  }
}
