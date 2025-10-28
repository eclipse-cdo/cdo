/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db;

import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.event.Notifier;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A helper for batching SQL statements.
 *
 * @author Eike Stepper
 * @since 4.13
 */
public class StatementBatcher extends Notifier implements AutoCloseable, Consumer<String>
{
  private final Connection connection;

  private final int batchSize;

  private final List<String> batchedSQL = new ArrayList<>();

  private Statement statement;

  private int totalUpdateCount;

  public StatementBatcher(Connection connection)
  {
    this(connection, DBUtil.MAX_BATCH_SIZE);
  }

  public StatementBatcher(Connection connection, int batchSize)
  {
    this.connection = connection;
    this.batchSize = batchSize;
  }

  public final Connection getConnection()
  {
    return connection;
  }

  public final Statement getStatement()
  {
    return statement;
  }

  public final int getBatchSize()
  {
    return batchSize;
  }

  public final int getBatchCount()
  {
    return batchedSQL.size();
  }

  public final int getTotalUpdateCount()
  {
    return totalUpdateCount;
  }

  @Override
  public final void accept(String sql)
  {
    batch(sql);
  }

  public int batch(String sql)
  {
    fireEvent(new BatchEvent(this, sql));

    try
    {
      ensureStatement().addBatch(sql);
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }

    batchedSQL.add(sql);

    if (getBatchCount() >= batchSize)
    {
      return doExecuteBatch();
    }

    return 0;
  }

  public final boolean isClosed()
  {
    try
    {
      return statement != null && statement.isClosed();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  @Override
  public void close()
  {
    if (!isClosed())
    {
      if (statement != null)
      {
        try
        {
          if (getBatchCount() != 0)
          {
            doExecuteBatch();
          }
        }
        finally
        {
          DBUtil.close(statement);
        }
      }

      fireEvent(new CloseEvent(this, totalUpdateCount));
    }
  }

  protected final Statement ensureStatement() throws SQLException
  {
    if (statement == null)
    {
      statement = connection.createStatement();
    }

    return statement;
  }

  protected final int doExecuteBatch()
  {
    int sum = 0;
    int[] updateCounts;

    try
    {
      updateCounts = statement.executeBatch();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }

    IListener[] listeners = getListeners();

    for (int i = 0; i < updateCounts.length; i++)
    {
      int updateCount = updateCounts[i];
      if (updateCount != Statement.SUCCESS_NO_INFO)
      {
        if (updateCount < 0)
        {
          String sql = batchedSQL.get(i);
          throw new DBException("Batch statement " + i + " was not successful: " + updateCount + "  [" + sql + "]");
        }

        sum += updateCount;
        totalUpdateCount += updateCount;
      }

      if (listeners.length != 0)
      {
        String sql = batchedSQL.get(i);
        fireEvent(new ResultEvent(this, sql, updateCount, totalUpdateCount));
      }
    }

    return sum;
  }

  /**
   * @author Eike Stepper
   */
  public static final class BatchEvent extends Event
  {
    private static final long serialVersionUID = 1L;

    private final String sql;

    private BatchEvent(StatementBatcher batcher, String sql)
    {
      super(batcher);
      this.sql = sql;
    }

    public String getSQL()
    {
      return sql;
    }

    @Override
    public String toString()
    {
      return sql;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class ResultEvent extends Event
  {
    private static final long serialVersionUID = 1L;

    private final String sql;

    private final int updateCount;

    private final int totalUpdateCount;

    private ResultEvent(StatementBatcher batcher, String sql, int updateCount, int totalUpdateCount)
    {
      super(batcher);
      this.sql = sql;
      this.updateCount = updateCount;
      this.totalUpdateCount = totalUpdateCount;
    }

    public String getSQL()
    {
      return sql;
    }

    public int getUpdateCount()
    {
      return updateCount;
    }

    public int getTotalUpdateCount()
    {
      return totalUpdateCount;
    }

    @Override
    public String toString()
    {
      return "Result: " + sql + "  -->  " + updateCount;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class CloseEvent extends Event
  {
    private static final long serialVersionUID = 1L;

    private final int totalUpdateCount;

    private CloseEvent(StatementBatcher batcher, int totalUpdateCount)
    {
      super(batcher);
      this.totalUpdateCount = totalUpdateCount;
    }

    public int getTotalUpdateCount()
    {
      return totalUpdateCount;
    }

    @Override
    public String toString()
    {
      return "Total updates: " + totalUpdateCount;
    }
  }
}
