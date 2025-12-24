/*
 * Copyright (c) 2011, 2012, 2015, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.mongodb;

import org.eclipse.emf.cdo.server.CDOServerBrowser;
import org.eclipse.emf.cdo.server.CDOServerBrowser.AbstractPage;
import org.eclipse.emf.cdo.server.mongodb.IMongoDBStore;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.factory.ProductCreationException;

import com.mongodb.BasicDBObject;
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
  private static final boolean SHOW_INDEXES = true;

  private static final boolean SHOW_DOCUMENTS = true;

  private static final boolean SHOW_INITIAL_COMMIT = true;

  public MongoDBBrowserPage()
  {
    super("collections", "MongoDB Collections");
  }

  @Override
  public boolean canDisplay(InternalRepository repository)
  {
    return repository.getStore() instanceof IMongoDBStore;
  }

  @Override
  public void display(CDOServerBrowser browser, InternalRepository repository, PrintStream out)
  {
    IMongoDBStore store = (IMongoDBStore)repository.getStore();
    DB db = (DB)store.getDB();

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
    pout.print("<table class=\"data\" cellpadding=\"4\">\r\n");
    pout.print("<tr><td colspan=\"2\" align=\"center\"><h2>" + collection + "</h2></td></tr>\r\n");

    if (SHOW_INDEXES)
    {
      showIndexes(browser, pout, coll);
    }

    if (SHOW_DOCUMENTS)
    {
      showDocuments(browser, pout, coll);
    }
  }

  protected void showIndexes(CDOServerBrowser browser, PrintStream pout, DBCollection coll)
  {
    pout.print("<tr><td colspan=\"2\" align=\"center\" bgcolor=\"EEEEEE\"><h4>Indexes</h4></td></tr>\r\n");

    int i = 0;
    for (DBObject index : coll.getIndexInfo())
    {
      ++i;
      String bg = (i & 1) == 1 ? "bgcolor=\"DDDDDD\"" : "bgcolor=\"EEEEEE\"";
      pout.print("<tr><td valign=\"top\" " + bg + "><b>" + i + "&nbsp;</b></td><td valign=\"top\">");
      showObject(browser, pout, index, "");
      pout.print("</td></tr>\r\n");
    }
  }

  protected void showDocuments(CDOServerBrowser browser, PrintStream pout, DBCollection coll)
  {
    DBCursor cursor = null;

    try
    {
      pout.print("<tr><td colspan=\"2\" align=\"center\" bgcolor=\"EEEEEE\"><h4>Documents</h4></td></tr>\r\n");

      int i = 0;
      cursor = coll.find();

      try
      {
        cursor = cursor.sort(new BasicDBObject("_id", 1));
      }
      catch (Exception ex)
      {
        // Ignore
      }

      while (cursor.hasNext())
      {
        DBObject doc = cursor.next();

        ++i;
        if (i == 1 && showFirstCommit(coll))
        {
          continue;
        }

        String bg = (i & 1) == 1 ? "bgcolor=\"DDDDDD\"" : "bgcolor=\"EEEEEE\"";
        pout.print("<tr><td valign=\"top\" " + bg + "><b>" + i + "&nbsp;</b></td><td valign=\"top\">");
        showObject(browser, pout, doc, "");
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

  protected void showObject(CDOServerBrowser browser, PrintStream pout, DBObject doc, String level)
  {
    Set<String> keySet = doc.keySet();

    boolean highlight = false;
    try
    {
      String paramKey = browser.getParam("key");
      if (paramKey != null)
      {
        String paramValue = browser.getParam("value");
        Object value = doc.get(paramKey);
        if (String.valueOf(value).equals(paramValue))
        {
          highlight = true;
        }
      }
    }
    catch (Exception ex)
    {
      // Ignore
    }

    if (highlight)
    {
      pout.print("<table border=\"0\" bgcolor=\"#FFFFA8\"><tr><td>");
    }

    for (String key : keySet)
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
        showObject(browser, pout, child, level + "&nbsp;&nbsp;");
      }
      else
      {
        pout.print("<font color=\"#0000FF\">");
        if (value instanceof String)
        {
          pout.print("\"");
          pout.print(browser.escape((String)value));
          pout.print("\"");
        }
        else
        {
          String string = String.valueOf(value);
          pout.print(browser.href(string, "collections", "key", key, "value", string));
        }

        pout.print("</font><br>");
      }
    }

    if (highlight)
    {
      pout.print("</td></tr></table>");
    }
  }

  protected boolean showFirstCommit(DBCollection coll)
  {
    return coll.getName().equals(Commits.COMMITS) && !SHOW_INITIAL_COMMIT;
  }

  /**
   * @author Eike Stepper
   */
  public static class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String TYPE = "mongodb";

    public Factory()
    {
      super(PRODUCT_GROUP, TYPE);
    }

    @Override
    public MongoDBBrowserPage create(String description) throws ProductCreationException
    {
      return new MongoDBBrowserPage();
    }
  }
}
