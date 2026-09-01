/*
 * Copyright (c) 2016, 2019, 2025, 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.BatchedStatement;
import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.jdbc.DelegatingPreparedStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/**
 * @author Eike Stepper
 * @since 4.5
 */
public final class BatchedStatementImpl extends DelegatingPreparedStatement implements BatchedStatement
{
  private final Context context;

  private final int batchSize;

  private int batchCount;

  private int pendingCount;

  private int executionCount;

  private int unknownResultCount;

  private int totalResult;

  public BatchedStatementImpl(PreparedStatement delegate, int batchSize) throws DBException
  {
    this(delegate, batchSize, null);
  }

  public BatchedStatementImpl(PreparedStatement delegate, int batchSize, Context context) throws DBException
  {
    this(delegate, batchSize, context, null);
  }

  public BatchedStatementImpl(PreparedStatement delegate, int batchSize, Context context, String diagnosticName) throws DBException
  {
    super(delegate, getConnection(delegate));
    this.context = Objects.requireNonNullElseGet(context, NOOPContext::new);
    this.context.manageStatement(this, diagnosticName);
    this.batchSize = batchSize;
  }

  public Context getContext()
  {
    return context;
  }

  @Override
  public int getBatchSize()
  {
    return batchSize;
  }

  @Override
  public int getBatchCount()
  {
    return batchCount;
  }

  @Override
  public int getTotalResult()
  {
    return totalResult;
  }

  @Override
  public int getPendingCount()
  {
    return pendingCount;
  }

  @Override
  public int getExecutionCount()
  {
    return executionCount;
  }

  @Override
  public int getUnknownResultCount()
  {
    return unknownResultCount;
  }

  @Override
  public int executeUpdate() throws SQLException
  {
    PreparedStatement delegate = getDelegate();
    delegate.addBatch();

    ++batchCount;
    ++pendingCount;

    int result = 0;

    if (batchSize > 0 && pendingCount >= batchSize)
    {
      result = doExecuteBatch();
    }

    context.afterExecuteUpdate(this);
    return result;
  }

  @Override
  public void close() throws SQLException
  {
    if (pendingCount != 0)
    {
      doExecuteBatch();
    }

    super.close();
  }

  @Override
  public ResultSet getResultSet() throws SQLException
  {
    throw new UnsupportedOperationException("Only updates are supported");
  }

  @Override
  public ResultSet executeQuery() throws SQLException
  {
    throw new UnsupportedOperationException("Only updates are supported");
  }

  @Deprecated
  @Override
  public ResultSet executeQuery(String sql) throws SQLException
  {
    throw new UnsupportedOperationException("Only updates are supported");
  }

  private int doExecuteBatch() throws SQLException
  {
    int sum = 0;
    int[] results;

    try
    {
      results = getDelegate().executeBatch();
      ++executionCount;
      pendingCount = 0;
    }
    catch (SQLException ex)
    {
      clearAfterFailedExecution(ex);
      throw ex;
    }

    for (int i = 0; i < results.length; i++)
    {
      int result = results[i];

      if (result == Statement.SUCCESS_NO_INFO)
      {
        ++unknownResultCount;
      }
      else
      {
        if (result < 0)
        {
          clearAfterFailedExecution(null);
          throw new DBException("Result " + i + " is not successful: " + result);
        }

        sum += result;
      }
    }

    totalResult += sum;
    return sum;
  }

  @Override
  public int flush() throws SQLException
  {
    if (pendingCount == 0)
    {
      return 0;
    }

    return doExecuteBatch();
  }

  @Override
  public int[] executeBatch() throws SQLException
  {
    int pending = pendingCount;
    if (pending == 0)
    {
      return new int[0];
    }

    int[] results;

    try
    {
      results = getDelegate().executeBatch();
      ++executionCount;
      pendingCount = 0;
    }
    catch (SQLException ex)
    {
      clearAfterFailedExecution(ex);
      throw ex;
    }

    for (int i = 0; i < results.length; i++)
    {
      int result = results[i];
      if (result == Statement.SUCCESS_NO_INFO)
      {
        ++unknownResultCount;
      }
      else
      {
        if (result < 0)
        {
          clearAfterFailedExecution(null);
          throw new DBException("Result " + i + " is not successful: " + result); //$NON-NLS-1$ //$NON-NLS-2$
        }

        totalResult += result;
      }
    }

    return results;
  }

  private void clearAfterFailedExecution(Exception failure)
  {
    pendingCount = 0;

    try
    {
      getDelegate().clearBatch();
    }
    catch (SQLException ex)
    {
      if (failure != null)
      {
        failure.addSuppressed(ex);
      }
    }
  }

  @Override
  public void clearBatch() throws SQLException
  {
    getDelegate().clearBatch();
    pendingCount = 0;
  }

  private static Connection getConnection(PreparedStatement delegate) throws DBException
  {
    try
    {
      return delegate.getConnection();
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class NOOPContext implements Context
  {
    public NOOPContext()
    {
    }

    @Override
    public void manageStatement(BatchedStatement statement)
    {
      // Do nothing.
    }

    @Override
    public void manageStatement(BatchedStatement statement, String diagnosticName)
    {
      // Do nothing.
    }

    @Override
    public void afterExecuteUpdate(BatchedStatement statement)
    {
      // Do nothing.
    }
  }
}
