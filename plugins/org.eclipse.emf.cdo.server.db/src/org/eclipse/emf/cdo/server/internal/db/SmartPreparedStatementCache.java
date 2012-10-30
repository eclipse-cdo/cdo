/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Winkler - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.util.ImplementationError;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Stefan Winkler
 * @since 2.0
 */
public class SmartPreparedStatementCache extends AbstractPreparedStatementCache
{
  private final int capacity;

  private TreeMap<String, CachedPreparedStatement> cache = new TreeMap<String, CachedPreparedStatement>();

  private Map<PreparedStatement, CachedPreparedStatement> checkOuts = new HashMap<PreparedStatement, CachedPreparedStatement>();

  private long lastTouch;

  public SmartPreparedStatementCache(int capacity)
  {
    this.capacity = capacity;
  }

  public final int getCapacity()
  {
    return capacity;
  }

  public int size()
  {
    return cache.size();
  }

  public PreparedStatement getPreparedStatement(String sql, ReuseProbability probability)
  {
    PreparedStatement preparedStatement;

    CachedPreparedStatement cachedStatement = cache.remove(sql);
    if (cachedStatement == null)
    {
      try
      {
        Connection connection = getConnection();
        preparedStatement = connection.prepareStatement(sql);
        cachedStatement = new CachedPreparedStatement(sql, preparedStatement, probability);
      }
      catch (SQLException ex)
      {
        throw new DBException(ex);
      }
    }
    else
    {
      preparedStatement = cachedStatement.getPreparedStatement();
    }

    checkOuts.put(preparedStatement, cachedStatement);
    return preparedStatement;
  }

  public void releasePreparedStatement(PreparedStatement preparedStatement)
  {
    if (preparedStatement == null)
    {
      // Bug 276926: Silently accept preparedStatement == null and do nothing.
      return;
    }

    CachedPreparedStatement cachedStatement = checkOuts.remove(preparedStatement);
    cachedStatement.setTouch(++lastTouch);

    String sql = cachedStatement.getSQL();
    if (cache.put(sql, cachedStatement) != null)
    {
      throw new ImplementationError(sql + " already in cache"); //$NON-NLS-1$
    }

    if (cache.size() > capacity)
    {
      CachedPreparedStatement old = cache.remove(cache.firstKey());
      DBUtil.close(old.getPreparedStatement());
    }
  }

  @Override
  protected void doBeforeDeactivate() throws Exception
  {
    if (!checkOuts.isEmpty())
    {
      OM.LOG.warn("Statement leak detected"); //$NON-NLS-1$
    }
  }

  /**
   * @author Stefan Winkler
   */
  private static final class CachedPreparedStatement implements Comparable<CachedPreparedStatement>
  {
    private String sql;

    private PreparedStatement preparedStatement;

    private ReuseProbability probability;

    private long touch;

    public CachedPreparedStatement(String sql, PreparedStatement preparedStatement, ReuseProbability probability)
    {
      this.sql = sql;
      this.preparedStatement = preparedStatement;
      this.probability = probability;
    }

    public String getSQL()
    {
      return sql;
    }

    public PreparedStatement getPreparedStatement()
    {
      return preparedStatement;
    }

    public void setTouch(long touch)
    {
      this.touch = touch;
    }

    public int compareTo(CachedPreparedStatement o)
    {
      int result = probability.compareTo(o.probability);
      if (result == 0)
      {
        result = (int)(o.touch - touch);
      }

      return result;
    }

    @Override
    public String toString()
    {
      return "CachedPreparedStatement[sql=" + sql + ", probability=" + probability + ", touch=" + touch + "]";
    }
  }
}
