package org.eclipse.emf.cdo.server.internal.mongodb;

import org.eclipse.emf.cdo.server.CDOServerBrowser;
import org.eclipse.emf.cdo.server.CDOServerBrowser.AbstractPage;
import org.eclipse.emf.cdo.server.mongodb.IMongoDBStore;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.factory.ProductCreationException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

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
    super("collections", "MongoDB Collections");
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
    String collection = showCollections(browser, out, db, repository.getName());
    out.print("</td>\r\n");
    out.print("<td>&nbsp;&nbsp;&nbsp;</td>\r\n");

    if (collection != null)
    {
      out.print("<td valign=\"top\">\r\n");
      showCollection(browser, out, db, collection);
      out.print("</td>\r\n");
    }

    out.print("</tr>\r\n");
    out.print("</table>\r\n");
  }

  protected String showCollections(CDOServerBrowser browser, PrintStream pout, DB db, String repo)
  {
    String collection = browser.getParam("collection");

    Set<String> allCollectionNames = db.getCollectionNames();
    for (String collectionName : allCollectionNames)
    {
      if (collection == null)
      {
        collection = collectionName;
      }

      String label = browser.escape(collectionName)/* .toLowerCase() */;
      if (collectionName.equals(collection))
      {
        pout.print("<b>" + label + "</b><br>\r\n");
      }
      else
      {
        pout.print(browser.href(label, getName(), "collection", collectionName) + "<br>\r\n");
      }
    }

    return collection;
  }

  protected void showCollection(CDOServerBrowser browser, PrintStream pout, DB db, String collection)
  {
    DBCollection coll = db.getCollection(collection);
    pout.print("<table border=\"1\" cellpadding=\"4\">\r\n");
    pout.print("<tr><td colspan=\"2\" align=\"center\"><h2>" + collection + "</h2></td></tr>\r\n");
    pout.print("<tr><td colspan=\"2\" align=\"center\" bgcolor=\"EEEEEE\"><h4>Indexes</h4></td></tr>\r\n");

    int i = 0;
    for (DBObject index : coll.getIndexInfo())
    {
      ++i;
      pout.print("<tr><td valign=\"top\">" + i + "</td><td valign=\"top\">");
      showDocument(browser, pout, index, "");
      pout.print("</td></tr>\r\n");
    }

    DBCursor cursor = null;

    try
    {
      pout.print("<tr><td colspan=\"2\" align=\"center\" bgcolor=\"EEEEEE\"><h4>Documents</h4></td></tr>\r\n");

      i = 0;
      cursor = coll.find();
      while (cursor.hasNext())
      {
        DBObject doc = cursor.next();

        ++i;
        pout.print("<tr><td valign=\"top\">" + i + "</td><td valign=\"top\">");
        showDocument(browser, pout, doc, "");
        pout.print("</td></tr>\r\n");
      }

      pout.print("</table>\r\n");
    }
    finally
    {
      if (cursor != null)
      {
        cursor.close();
      }
    }
  }

  protected void showDocument(CDOServerBrowser browser, PrintStream pout, DBObject doc, String level)
  {
    for (String key : doc.keySet())
    {
      pout.print(level);
      pout.print("<b>");
      pout.print(key);
      pout.print("</b> = ");

      Object value = doc.get(key);
      if (value instanceof DBObject)
      {
        DBObject child = (DBObject)value;
        pout.print("<br>");
        showDocument(browser, pout, child, level + "&nbsp;&nbsp;");
      }
      else
      {
        pout.print("<font color=\"#0000FF\">");
        if (value instanceof String)
        {
          pout.print("\"");
        }

        pout.print(value);
        if (value instanceof String)
        {
          pout.print("\"");
        }

        pout.print("</font><br>");
      }
    }
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
