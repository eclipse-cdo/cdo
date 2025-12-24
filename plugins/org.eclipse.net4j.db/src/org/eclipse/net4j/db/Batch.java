/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Eike Stepper
 * @since 4.11
 */
public final class Batch implements AutoCloseable
{
  private final Statement statement;

  public Batch(Connection connection, String... sqls)
  {
    try
    {
      statement = connection.createStatement();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }

    for (String sql : sqls)
    {
      add(sql);
    }
  }

  public void add(String sql)
  {
    try
    {
      statement.addBatch(sql);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public void clear()
  {
    try
    {
      statement.clearBatch();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  public int[] execute()
  {
    try
    {
      return statement.executeBatch();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  @Override
  public void close() throws DBException
  {
    try
    {
      statement.close();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }
}
