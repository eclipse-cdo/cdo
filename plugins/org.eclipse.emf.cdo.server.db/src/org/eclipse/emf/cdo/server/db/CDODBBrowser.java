/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.db;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.internal.db.bundle.OM;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.concurrent.Worker;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.ProductCreationException;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public class CDODBBrowser extends Worker
{
  private static final String REQUEST_PREFIX = "GET ";

  private static final String REQUEST_SUFFIX = " HTTP/1.1";

  private ThreadLocal<Map<String, String>> params = new InheritableThreadLocal<Map<String, String>>()
  {
    @Override
    protected Map<String, String> initialValue()
    {
      return new HashMap<String, String>();
    }
  };

  private int port = 7777;

  private ServerSocket serverSocket;

  private Map<String, InternalRepository> repositories;

  public CDODBBrowser(Map<String, InternalRepository> repositories)
  {
    this.repositories = repositories;
    setDaemon(true);
  }

  public Map<String, InternalRepository> getRepositories()
  {
    return repositories;
  }

  public int getPort()
  {
    return port;
  }

  public void setPort(int port)
  {
    this.port = port;
  }

  @Override
  protected void work(WorkContext context) throws Exception
  {
    Socket socket = null;

    try
    {
      socket = serverSocket.accept();
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      OutputStream out = new BufferedOutputStream(socket.getOutputStream());
      PrintStream pout = new PrintStream(out);
      printHeader(pout);

      String line;
      while ((line = in.readLine()) != null)
      {
        if (line.startsWith(REQUEST_PREFIX) && line.endsWith(REQUEST_SUFFIX))
        {
          String request = line.substring(REQUEST_PREFIX.length(), line.length() - REQUEST_SUFFIX.length()).trim();
          String resource = request;
          String params = "";
          int pos = request.indexOf('?');
          if (pos != -1)
          {
            resource = request.substring(0, pos);
            params = request.substring(pos + 1);
          }

          initParams(params);
          if ("/".equals(resource))
          {
            showMenu(pout);
          }
          else if ("/data".equals(resource))
          {
            showData(pout);
          }
        }

        out.flush();
        return;
      }
    }
    catch (Exception ex)
    {
      if (isActive())
      {
        ex.printStackTrace();
      }
    }
    finally
    {
      params.remove();
      if (socket != null)
      {
        socket.close();
      }
    }
  }

  protected void initParams(String params)
  {
    Map<String, String> map = this.params.get();
    for (String param : params.split("&"))
    {
      if (param.length() != 0)
      {
        String[] keyValue = param.split("=");
        map.put(keyValue[0], keyValue[1]);
      }
    }
  }

  protected void clearParams()
  {
    Map<String, String> map = params.get();
    map.clear();
  }

  protected void removeParam(String key)
  {
    Map<String, String> map = params.get();
    map.remove(key);
  }

  protected String getParam(String key)
  {
    Map<String, String> map = params.get();
    return map.get(key);
  }

  protected String href(String label, String resource, String... params)
  {
    Map<String, String> map = new HashMap<String, String>(this.params.get());
    for (int i = 0; i < params.length;)
    {
      map.put(params[i++], params[i++]);
    }

    List<String> list = new ArrayList<String>(map.keySet());
    Collections.sort(list);

    StringBuilder builder = new StringBuilder();
    for (String key : list)
    {
      String value = map.get(key);
      if (value != null)
      {
        if (builder.length() != 0)
        {
          builder.append("&");
        }

        builder.append(key);
        builder.append("=");
        builder.append(value);
      }
    }

    return "<a href=\"/" + escape(resource) + "?" + escape(builder.toString()) + "\">" + escape(label) + "</a>";
  }

  protected String escape(String raw)
  {
    if (raw == null)
    {
      return "null";
    }

    return raw.replace("<", "&lt;");
  }

  protected void printHeader(PrintStream pout)
  {
    pout.print("HTTP/1.1 200 OK\r\n");
    pout.print("Content-Type: text/html\r\n");
    pout.print("Date: " + new Date() + "\r\n");
    pout.print("Server: DBBrowser 3.0\r\n");
    pout.print("\r\n");
  }

  protected void showMenu(PrintStream pout)
  {
    clearParams();
    pout.print("<h1>DBBrowser 3.0</h1>\r\n");

    Set<String> names = repositories.keySet();
    if (names.size() == 1)
    {
      String repo = names.iterator().next();
      pout.print("<h3>" + href("Show Data", "data", "repo", repo) + "</h3>");
    }
    else
    {
      pout.print("<h3><a href=\"/data\">Show Data</a></h3>");
    }
  }

  protected void showData(PrintStream pout)
  {
    String repo = getParam("repo");
    String table = getParam("table");

    List<String> repoNames = new ArrayList<String>(repositories.keySet());
    Collections.sort(repoNames);

    pout.print("<h1>");
    for (String repoName : repoNames)
    {
      if (repoName.equals(repo))
      {
        pout.print("<b>" + escape(repoName) + "</b>&nbsp;");
      }
      else
      {
        pout.print(href(repoName, "data", "repo", repoName) + "&nbsp;");
      }
    }

    pout.print("</h1>");

    InternalRepository repository = repositories.get(repo);
    if (repository != null)
    {
      IStore store = repository.getStore();
      if (store instanceof IDBConnectionProvider)
      {
        IDBConnectionProvider connectionProvider = (IDBConnectionProvider)store;
        Connection connection = null;

        try
        {
          connection = connectionProvider.getConnection();

          pout.print("<p>\r\n");
          pout.print("<table border=\"0\">\r\n");
          pout.print("<tr>\r\n");

          pout.print("<td valign=\"top\">\r\n");
          showTables(pout, connection, repo);
          pout.print("</td>\r\n");

          if (table != null)
          {
            pout.print("<td valign=\"top\">\r\n");
            showTable(pout, connection);
            pout.print("</td>\r\n");
          }

          pout.print("</tr>\r\n");
          pout.print("</table>\r\n");
        }
        catch (DBException ex)
        {
          ex.printStackTrace();
        }
        finally
        {
          DBUtil.close(connection);
        }
      }
    }
  }

  protected void showTables(PrintStream pout, Connection connection, String repo)
  {
    String table = getParam("table");

    List<String> allTableNames = DBUtil.getAllTableNames(connection, repo);
    for (String tableName : allTableNames)
    {
      String label = escape(tableName)/* .toLowerCase() */;
      if (tableName.equals(table))
      {
        pout.print("<b>" + label + "</b><br>\r\n");
      }
      else
      {
        pout.print(href(label, "data", "table", tableName, "order", null, "direction", null) + "<br>\r\n");
      }
    }
  }

  protected void showTable(PrintStream pout, Connection connection)
  {
    String table = getParam("table");
    try
    {
      String order = getParam("order");
      executeQuery(pout, connection, "SELECT * FROM \"" + table + "\""
          + (order == null ? "" : " ORDER BY " + order + " " + getParam("direction")));
    }
    catch (Exception ex)
    {
      removeParam("order");
      removeParam("direction");
      executeQuery(pout, connection, "SELECT * FROM " + table);
    }
  }

  protected void executeQuery(PrintStream pout, Connection connection, String sql)
  {
    String order = getParam("order");
    String direction = getParam("direction");

    Statement stmt = null;
    ResultSet resultSet = null;

    try
    {
      stmt = connection.createStatement();
      resultSet = stmt.executeQuery(sql);

      ResultSetMetaData metaData = resultSet.getMetaData();
      int columns = metaData.getColumnCount();

      pout.print("<table border=\"1\" cellpadding=\"2\">\r\n");
      pout.print("<tr>\r\n");
      pout.print("<td>&nbsp;</td>\r\n");
      for (int i = 0; i < columns; i++)
      {
        String column = metaData.getColumnLabel(1 + i)/* .toLowerCase() */;
        String dir = column.equals(order) && "ASC".equals(direction) ? "DESC" : "ASC";
        pout.print("<td><b>" + href(column, "data", "order", column, "direction", dir) + "</b></td>\r\n");
      }

      pout.print("</tr>\r\n");

      int row = 0;
      while (resultSet.next())
      {
        ++row;
        pout.print("<tr>\r\n");
        pout.print("<td><b>" + row + "</b></td>\r\n");
        for (int i = 0; i < columns; i++)
        {
          pout.print("<td>" + escape(resultSet.getString(1 + i)) + "</td>\r\n");
        }

        pout.print("</tr>\r\n");
      }

      pout.print("</table>\r\n");
    }
    catch (SQLException ex)
    {
      ex.printStackTrace();
    }
    finally
    {
      DBUtil.close(resultSet);
      DBUtil.close(stmt);
    }
  }

  @Override
  protected String getThreadName()
  {
    return "DBBrowser";
  }

  @Override
  protected void doActivate() throws Exception
  {
    try
    {
      serverSocket = new ServerSocket(port);
    }
    catch (Exception ex)
    {
      throw new IllegalStateException("Could not open socket on port " + port, ex);
    }

    super.doActivate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    serverSocket.close();
    super.doDeactivate();
  }

  /**
   * @author Eike Stepper
   */
  public static class ContainerBased extends CDODBBrowser
  {
    private IContainer<?> container;

    private IListener containerListener = new ContainerEventAdapter<Object>()
    {
      @Override
      protected void onAdded(IContainer<Object> container, Object element)
      {
        addElement(element);
      }

      @Override
      protected void onRemoved(IContainer<Object> container, Object element)
      {
        removeElement(element);
      }
    };

    public ContainerBased(IContainer<?> container)
    {
      super(new HashMap<String, InternalRepository>());
      this.container = container;
    }

    public ContainerBased()
    {
      this(IPluginContainer.INSTANCE);
    }

    public IContainer<?> getContainer()
    {
      return container;
    }

    @Override
    protected void doActivate() throws Exception
    {
      super.doActivate();
      for (Object element : container.getElements())
      {
        addElement(element);
      }

      container.addListener(containerListener);
    }

    @Override
    protected void doDeactivate() throws Exception
    {
      container.removeListener(containerListener);
      super.doDeactivate();
    }

    private void addElement(Object element)
    {
      if (element instanceof InternalRepository)
      {
        InternalRepository repository = (InternalRepository)element;
        getRepositories().put(repository.getName(), repository);
      }
    }

    private void removeElement(Object element)
    {
      if (element instanceof InternalRepository)
      {
        InternalRepository repository = (InternalRepository)element;
        getRepositories().remove(repository.getName());
      }
    }

    /**
     * @author Eike Stepper
     */
    public static class Factory extends org.eclipse.net4j.util.factory.Factory
    {
      public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.db.browsers";

      public static final String TYPE = "default";

      public Factory()
      {
        super(PRODUCT_GROUP, TYPE);
      }

      public CDODBBrowser.ContainerBased create(String description) throws ProductCreationException
      {
        CDODBBrowser.ContainerBased browser = new CDODBBrowser.ContainerBased();

        try
        {
          if (!StringUtil.isEmpty(description))
          {
            browser.setPort(Integer.valueOf(description));
          }
        }
        catch (Exception ex)
        {
          OM.LOG.warn(ex);
        }

        return browser;
      }
    }
  }
}
