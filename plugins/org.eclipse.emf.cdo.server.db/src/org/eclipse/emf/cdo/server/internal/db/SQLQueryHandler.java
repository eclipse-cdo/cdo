/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kai Schlamp - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.common.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.spi.common.id.CDOIDLongImpl;

import org.eclipse.net4j.db.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements server side SQL query execution.
 * 
 * @author Kai Schlamp
 */
public class SQLQueryHandler implements IQueryHandler
{
  public static final String QUERY_LANGUAGE = "sql";

  public static final String FIRST_RESULT = "firstResult";

  public static final String CDO_OBJECT_QUERY = "cdoObjectQuery";

  public static final String QUERY_STATEMENT = "queryStatement";

  private DBStoreAccessor storeAccessor;

  public SQLQueryHandler(DBStoreAccessor storeAccessor)
  {
    this.storeAccessor = storeAccessor;
  }

  public DBStoreAccessor getStoreAccessor()
  {
    return storeAccessor;
  }

  /**
   * Executes SQL queries. Gets the connection from {@link DBStoreAccessor}, creates a SQL query and sets the parameters
   * taken from the {@link CDOQueryInfo#getParameters()}.
   * <p>
   * Takes into account the {@link CDOQueryInfo#getMaxResults()} and the {@link SQLQueryHandler#FIRST_RESULT} (numbered
   * from 0) values for paging.
   * <p>
   * By default (parameter {@link SQLQueryHandler#CDO_OBJECT_QUERY} == true) a query for CDO Objects is exectued. The
   * SQL query must return the CDO ID in the first column for this to work. If you set
   * {@link SQLQueryHandler#CDO_OBJECT_QUERY} parameter to false, the value of the first column of a row itself is
   * returned.
   * <p>
   * By default (parameter {@link SQLQueryHandler#QUERY_STATEMENT} == true) query statements are executed. Set this
   * parameter to false for update/DDL statements.
   * <p>
   * It is possible to use variables inside the SQL string with ":" as prefix. E.g.
   * "SELECT cdo_id FROM Company WHERE name LIKE :name". The value must then be set by using a parameter. E.g.
   * query.setParameter(":name", "Foo%");
   * 
   * @param info
   *          the object containing the query and parameters
   * @param context
   *          the query results are placed in the context
   * @see IQueryHandler#executeQuery(CDOQueryInfo, IQueryContext)
   */
  public void executeQuery(CDOQueryInfo info, IQueryContext context)
  {
    if (!QUERY_LANGUAGE.equals(info.getQueryLanguage()))
    {
      throw new IllegalArgumentException("Query language " + info.getQueryLanguage() + " not supported by this store");
    }

    Connection connection = storeAccessor.getConnection();
    PreparedStatement statement = null;
    ResultSet resultSet = null;
    String query = info.getQueryString();

    try
    {
      int firstResult = -1;
      boolean queryStatement = true;
      boolean cdoObjectQuery = true;

      HashMap<String, List<Integer>> paramMap = new HashMap<String, List<Integer>>();
      query = parse(query, paramMap);
      statement = connection.prepareStatement(query);

      for (String key : info.getParameters().keySet())
      {
        if (FIRST_RESULT.equalsIgnoreCase(key))
        {
          final Object o = info.getParameters().get(key);
          if (o != null)
          {
            try
            {
              firstResult = (Integer)o;
            }
            catch (ClassCastException ex)
            {
              throw new IllegalArgumentException("Parameter firstResult must be an integer but it is a " + o
                  + " class " + o.getClass().getName(), ex);
            }
          }
        }
        else if (QUERY_STATEMENT.equalsIgnoreCase(key))
        {
          final Object o = info.getParameters().get(key);
          if (o != null)
          {
            try
            {
              queryStatement = (Boolean)o;
            }
            catch (ClassCastException ex)
            {
              throw new IllegalArgumentException("Parameter queryStatement must be an boolean but it is a " + o
                  + " class " + o.getClass().getName(), ex);
            }
          }
        }
        else if (CDO_OBJECT_QUERY.equalsIgnoreCase(key))
        {
          final Object o = info.getParameters().get(key);
          if (o != null)
          {
            try
            {
              cdoObjectQuery = (Boolean)o;
            }
            catch (ClassCastException ex)
            {
              throw new IllegalArgumentException("Parameter cdoObjectQuery must be a boolean but it is a " + o
                  + " class " + o.getClass().getName(), ex);
            }
          }
        }
        else
        {
          if (!paramMap.containsKey(key) || paramMap.get(key) == null)
          {
            throw new IllegalArgumentException("No parameter value found for named parameter " + key);
          }

          Integer[] indexes = paramMap.get(key).toArray(new Integer[0]);
          for (int i = 0; i < indexes.length; i++)
          {
            statement.setObject(indexes[i], info.getParameters().get(key));
          }
        }
      }

      if (queryStatement)
      {
        resultSet = statement.executeQuery();
        if (firstResult > -1)
        {
          resultSet.absolute(firstResult);
        }

        int counter = 0;
        while (resultSet.next())
        {
          if (info.getMaxResults() != -1 && counter++ >= info.getMaxResults())
          {
            break;
          }

          if (cdoObjectQuery)
          {
            context.addResult(new CDOIDLongImpl(resultSet.getLong(1)));
          }
          else
          {
            context.addResult(resultSet.getLong(1));
          }
        }
      }
      else
      {
        int result = statement.executeUpdate();
        context.addResult(result);
      }
    }
    catch (SQLException ex)
    {
      throw new IllegalArgumentException("Illegal SQL query: " + query, ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(statement);
    }
  }

  @SuppressWarnings("unchecked")
  private String parse(String query, Map<String, List<Integer>> paramMap)
  {
    int length = query.length();
    StringBuffer parsedQuery = new StringBuffer(length);
    boolean inSingleQuote = false;
    boolean inDoubleQuote = false;
    int index = 1;

    for (int i = 0; i < length; i++)
    {
      char c = query.charAt(i);
      if (inSingleQuote)
      {
        if (c == '\'')
        {
          inSingleQuote = false;
        }
      }
      else if (inDoubleQuote)
      {
        if (c == '"')
        {
          inDoubleQuote = false;
        }
      }
      else
      {
        if (c == '\'')
        {
          inSingleQuote = true;
        }
        else if (c == '"')
        {
          inDoubleQuote = true;
        }
        else if (c == ':' && i + 1 < length && Character.isJavaIdentifierStart(query.charAt(i + 1)))
        {
          int j = i + 2;
          while (j < length && Character.isJavaIdentifierPart(query.charAt(j)))
          {
            j++;
          }

          String name = query.substring(i + 1, j);
          c = '?';
          i += name.length();

          List<Integer> indexList = paramMap.get(name);
          if (indexList == null)
          {
            indexList = new ArrayList();
            paramMap.put(name, indexList);
          }

          indexList.add(new Integer(index));
          index++;
        }
      }

      parsedQuery.append(c);
    }

    return parsedQuery.toString();
  }
}
