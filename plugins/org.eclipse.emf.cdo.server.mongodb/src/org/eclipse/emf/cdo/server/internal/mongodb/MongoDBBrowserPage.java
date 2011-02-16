package org.eclipse.emf.cdo.server.internal.mongodb;

import org.eclipse.emf.cdo.server.CDOServerBrowser;
import org.eclipse.emf.cdo.server.CDOServerBrowser.AbstractPage;
import org.eclipse.emf.cdo.server.mongodbdb.IMongoDBStore;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.factory.ProductCreationException;

import com.mongodb.DB;

import java.io.PrintStream;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class MongoDBBrowserPage extends AbstractPage
{
  public MongoDBBrowserPage()
  {
    super("tables", "Database Tables");
  }

  public boolean canDisplay(InternalRepository repository)
  {
    return repository.getStore() instanceof IMongoDBStore;
  }

  public void display(CDOServerBrowser browser, InternalRepository repository, PrintStream out)
  {
    IMongoDBStore store = (IMongoDBStore)repository.getStore();
    DB db = store.getDB();

    out.print("<table border=\"0\">\r\n");
    out.print("<tr>\r\n");

    out.print("<td valign=\"top\">\r\n");
    String table = showTables(browser, out, db, repository.getName());
    out.print("</td>\r\n");

    if (table != null)
    {
      out.print("<td valign=\"top\">\r\n");
      showTable(browser, out, db, table);
      out.print("</td>\r\n");
    }

    out.print("</tr>\r\n");
    out.print("</table>\r\n");
  }

  /**
   * @since 4.0
   */
  protected String showTables(CDOServerBrowser browser, PrintStream pout, DB db, String repo)
  {
    String table = browser.getParam("table");

    Set<String> allTableNames = db.getCollectionNames();
    for (String tableName : allTableNames)
    {
      if (table == null)
      {
        table = tableName;
      }

      String label = browser.escape(tableName)/* .toLowerCase() */;
      if (tableName.equals(table))
      {
        pout.print("<b>" + label + "</b><br>\r\n");
      }
      else
      {
        pout.print(browser.href(label, getName(), "table", tableName, "order", null, "direction", null) + "<br>\r\n");
      }
    }

    return table;
  }

  /**
   * @since 4.0
   */
  protected void showTable(CDOServerBrowser browser, PrintStream pout, DB db, String table)
  {
    try
    {
      String order = browser.getParam("order");
      executeQuery(browser, pout, db, "SELECT * FROM " + table
          + (order == null ? "" : " ORDER BY " + order + " " + browser.getParam("direction")));
    }
    catch (Exception ex)
    {
      browser.removeParam("order");
      browser.removeParam("direction");
      executeQuery(browser, pout, db, "SELECT * FROM " + table);
    }
  }

  protected void executeQuery(CDOServerBrowser browser, PrintStream pout, DB db, String sql)
  {
    // String order = browser.getParam("order");
    // String direction = browser.getParam("direction");
    //
    // Statement stmt = null;
    // ResultSet resultSet = null;
    //
    // try
    // {
    // stmt = db.createStatement();
    // resultSet = stmt.executeQuery(sql);
    //
    // ResultSetMetaData metaData = resultSet.getMetaData();
    // int columns = metaData.getColumnCount();
    //
    // pout.print("<table border=\"1\" cellpadding=\"2\">\r\n");
    // pout.print("<tr>\r\n");
    // pout.print("<td>&nbsp;</td>\r\n");
    // for (int i = 0; i < columns; i++)
    // {
    // String column = metaData.getColumnLabel(1 + i);
    // String type = metaData.getColumnTypeName(1 + i).toLowerCase();
    //
    // String dir = column.equals(order) && "ASC".equals(direction) ? "DESC" : "ASC";
    // pout.print("<td align=\"center\"><b>" + browser.href(column, getName(), "order", column, "direction", dir));
    // pout.print("</b><br>" + type + "</td>\r\n");
    // }
    //
    // pout.print("</tr>\r\n");
    //
    // int row = 0;
    // while (resultSet.next())
    // {
    // ++row;
    // pout.print("<tr>\r\n");
    // pout.print("<td><b>" + row + "</b></td>\r\n");
    // for (int i = 0; i < columns; i++)
    // {
    // pout.print("<td>" + browser.escape(resultSet.getString(1 + i)) + "</td>\r\n");
    // }
    //
    // pout.print("</tr>\r\n");
    // }
    //
    // pout.print("</table>\r\n");
    // }
    // catch (SQLException ex)
    // {
    // ex.printStackTrace();
    // }
    // finally
    // {
    // DBUtil.close(resultSet);
    // DBUtil.close(stmt);
    // }
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String TYPE = "default";

    public Factory()
    {
      super(PRODUCT_GROUP, TYPE);
    }

    public MongoDBBrowserPage create(String description) throws ProductCreationException
    {
      return new MongoDBBrowserPage();
    }
  }
}
