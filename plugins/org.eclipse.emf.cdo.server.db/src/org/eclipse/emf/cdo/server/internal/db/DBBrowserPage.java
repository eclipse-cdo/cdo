/*
 * Copyright (c) 2010-2013, 2015, 2016, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db;

import org.eclipse.emf.cdo.internal.server.ServerDebugUtil;
import org.eclipse.emf.cdo.server.CDOServerBrowser;
import org.eclipse.emf.cdo.server.CDOServerBrowser.AbstractPage;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.factory.ProductCreationException;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class DBBrowserPage extends AbstractPage
{
  protected DBBrowserPage(String name, String label)
  {
    super(name, label);
  }

  @Override
  public boolean canDisplay(InternalRepository repository)
  {
    return repository.getStore() instanceof IDBConnectionProvider;
  }

  @Override
  public void display(CDOServerBrowser browser, InternalRepository repository, PrintStream out)
  {
    IDBConnectionProvider connectionProvider = (IDBConnectionProvider)repository.getStore();
    Connection connection = null;
    boolean closeConnection = false;

    try
    {
      IStoreAccessor accessor = ServerDebugUtil.getAccessor(repository);
      if (accessor instanceof IDBStoreAccessor)
      {
        connection = ((IDBStoreAccessor)accessor).getConnection();
      }
      else
      {
        connection = connectionProvider.getConnection();
        closeConnection = true;
      }

      display(browser, repository, out, connection);
    }
    catch (DBException ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      if (closeConnection)
      {
        DBUtil.close(connection);
      }
    }
  }

  protected abstract void display(CDOServerBrowser browser, InternalRepository repository, PrintStream out, Connection connection);

  protected void executeQuery(CDOServerBrowser browser, PrintStream pout, Connection connection, String title, boolean ordering, String sql)
  {
    String order = browser.getParam("order");
    String direction = browser.getParam("direction");
    String highlight = browser.getParam("highlight");
    boolean schema = browser.isParam("schema");

    Statement stmt = null;
    ResultSet resultSet = null;

    try
    {
      stmt = connection.createStatement();
      resultSet = stmt.executeQuery(sql);

      ResultSetMetaData metaData = resultSet.getMetaData();
      int columns = metaData.getColumnCount();

      pout.print("<table border=\"1\" cellpadding=\"2\">\r\n");
      pout.print("<tr><td colspan=\"" + (1 + columns) + "\" align=\"center\"><b>" + title + "</b></td></tr>\r\n");
      pout.print("<tr>\r\n");
      pout.print("<td>&nbsp;</td>\r\n");
      for (int i = 0; i < columns; i++)
      {
        String column = metaData.getColumnLabel(1 + i);
        String type = metaData.getColumnTypeName(1 + i).toLowerCase() + "(" + metaData.getPrecision(1 + i) + ")";

        if (ordering)
        {
          String dir = column.equals(order) && "ASC".equals(direction) ? "DESC" : "ASC";
          column = browser.href(column, getName(), "order", column, "direction", dir);
        }

        pout.print("<td align=\"center\"><b>" + column);
        pout.print("</b><br>" + type + "</td>\r\n");
      }

      pout.print("</tr>\r\n");

      if (!schema)
      {
        int row = 0;
        while (resultSet.next())
        {
          ++row;
          pout.print("<tr>\r\n");
          pout.print("<td><b>" + row + "</b></td>\r\n");
          for (int i = 0; i < columns; i++)
          {
            String value = resultSet.getString(1 + i);
            String bgcolor = highlight != null && highlight.equals(value) ? " bgcolor=\"#fffca6\"" : "";
            pout.print("<td" + bgcolor + ">" + browser.href(value, getName(), "highlight", value) + "</td>\r\n");
          }

          pout.print("</tr>\r\n");
        }
      }

      pout.print("</table>\r\n");
    }
    catch (SQLException ex)
    {
      throw new DBException(ex);
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Queries extends DBBrowserPage
  {
    public static final String NAME = "db-queries";

    public Queries()
    {
      super(NAME, "DB Queries");
    }

    @Override
    protected void display(CDOServerBrowser browser, InternalRepository repository, PrintStream out, Connection connection)
    {
      String query = browser.getParam("query");

      out.print("<table border=\"0\">\r\n");
      out.print("<tr>\r\n");

      out.print("<td valign=\"top\">\r\n");
      out.print("<form method=\"get\">\r\n");
      out.print("<textarea name=\"query\" rows=\"10\" cols=\"160\">\r\n");

      if (query != null)
      {
        query = query.trim();
        out.print(query);
      }

      out.print("</textarea>\r\n");
      out.print("<br><br>\r\n");
      out.print("<input type=\"submit\" value=\"Submit\"/>\r\n");
      out.print("</form>\r\n");
      out.print("</td>\r\n");
      out.print("</tr>\r\n");

      if (query != null)
      {
        out.print("<tr>\r\n");
        out.print("<td valign=\"top\">\r\n");

        try
        {
          executeQuery(browser, out, connection, "Results", false, query);
        }
        catch (DBException ex)
        {
          String message = getMessage(ex);
          if (message != null)
          {
            message = message.replace("\r\n", "<br>");
            message = message.replace("\n", "<br>");
          }

          out.print("<font color=\"red\"><b>");
          out.print("<pre>");
          out.print(message);
          out.print("</pre>");
          out.print("</b></font>");
        }

        out.print("</td>\r\n");
        out.print("</tr>\r\n");
      }

      out.print("</table>\r\n");
    }

    private String getMessage(Throwable ex)
    {
      Throwable cause = ex.getCause();
      if (cause instanceof SQLException)
      {
        return cause.getMessage();
      }

      return ex.getMessage();
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends org.eclipse.net4j.util.factory.Factory
    {
      public Factory()
      {
        super(PRODUCT_GROUP, NAME);
      }

      @Override
      public DBBrowserPage create(String description) throws ProductCreationException
      {
        return new Queries();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Tables extends DBBrowserPage
  {
    public static final String NAME = "db-tables";

    public Tables()
    {
      super(NAME, "DB Tables");
    }

    @Override
    protected void display(CDOServerBrowser browser, InternalRepository repository, PrintStream out, Connection connection)
    {
      out.print("<table border=\"0\">\r\n");
      out.print("<tr>\r\n");

      out.print("<td valign=\"top\">\r\n");
      String table = showTables(browser, out, connection, repository.getName());
      out.print("</td>\r\n");

      if (table != null)
      {
        out.print("<td valign=\"top\">\r\n");
        showTable(browser, out, connection, table);
        out.print("</td>\r\n");
      }

      out.print("</tr>\r\n");
      out.print("</table>\r\n");
    }

    /**
     * @since 4.0
     */
    protected String showTables(CDOServerBrowser browser, PrintStream pout, Connection connection, String repo)
    {
      String table = browser.getParam("table");
      boolean used = browser.isParam("used");
      boolean schema = browser.isParam("schema");

      pout.print("<table border=\"0\">\r\n");
      pout.print("<tr><td><b>Empty tables:</b></td><td><b>" + browser.href(used ? "Hidden" : "Shown", getName(), "used", String.valueOf(!used))
          + "</b></td></tr>\r\n");
      pout.print("<tr><td><b>Row data:</b></td><td><b>" + browser.href(schema ? "Hidden" : "Shown", getName(), "schema", String.valueOf(!schema))
          + "</b></td></tr>\r\n");
      pout.print("</table><br>\r\n");

      int totalRows = 0;
      int usedTables = 0;

      List<String> allTableNames = DBUtil.getAllTableNames(connection, repo);
      for (String tableName : allTableNames)
      {
        if (table == null)
        {
          table = tableName;
        }

        String label = browser.escape(tableName)/* .toLowerCase() */;

        int rowCount = DBUtil.getRowCount(connection, tableName);
        if (rowCount > 0)
        {
          // label += " (" + rowCount + ")";
          totalRows += rowCount;
          ++usedTables;
        }
        else if (used)
        {
          continue;
        }

        if (tableName.equals(table))
        {
          pout.print("<b>" + label + "</b>");
        }
        else
        {
          pout.print(browser.href(label, getName(), "table", tableName, "order", null, "direction", null));
        }

        if (rowCount > 0)
        {
          pout.print("&nbsp;(" + rowCount + ")");
        }

        pout.print("<br>\r\n");
      }

      if (totalRows != 0)
      {
        int totalTables = allTableNames.size();
        int emptyTables = totalTables - usedTables;

        pout.print("<br>" + totalTables + " tables total\r\n");
        pout.print("<br>" + usedTables + " tables used (" + totalRows + " rows)\r\n");
        pout.print("<br>" + emptyTables + " tables empty<br>\r\n");
      }

      return table;
    }

    /**
     * @since 4.0
     */
    protected void showTable(CDOServerBrowser browser, PrintStream pout, Connection connection, String table)
    {
      String columns = "*";
      String firstColumn = null;
      ResultSet resultSet = null;

      try
      {
        StringBuilder builder = new StringBuilder();
        DatabaseMetaData metaData = connection.getMetaData();
        resultSet = metaData.getColumns(null, null, table, null);

        while (resultSet.next())
        {
          String name = resultSet.getString("COLUMN_NAME");
          if (firstColumn == null)
          {
            firstColumn = name;
          }

          StringUtil.appendSeparator(builder, ", ");
          builder.append(name);
        }

        columns = builder.toString();
      }
      catch (Exception ex)
      {
        // Do nothing.
      }
      finally
      {
        DBUtil.close(resultSet);
      }

      String sql = "SELECT " + columns + " FROM " + table;
      if (firstColumn != null)
      {
        sql += " ORDER BY " + firstColumn + " ASC";
      }

      String title = browser.href(table, Queries.NAME, "query", sql);

      try
      {
        String order = browser.getParam("order");
        executeQuery(browser, pout, connection, title, true,
            "SELECT * FROM " + table + (order == null ? "" : " ORDER BY " + order + " " + browser.getParam("direction")));
      }
      catch (Exception ex)
      {
        browser.removeParam("order");
        browser.removeParam("direction");
        executeQuery(browser, pout, connection, table, true, "SELECT * FROM " + table);
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends org.eclipse.net4j.util.factory.Factory
    {
      public Factory()
      {
        super(PRODUCT_GROUP, NAME);
      }

      @Override
      public DBBrowserPage create(String description) throws ProductCreationException
      {
        return new Tables();
      }
    }
  }
}
