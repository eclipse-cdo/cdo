/*
 * Copyright (c) 2011, 2012, 2015, 2017 Eike Stepper (Loehne, Germany) and others.
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

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author Eike Stepper
 */
public class Coll
{
  protected MongoDBStore store;

  protected DBCollection collection;

  public Coll(MongoDBStore store, String name)
  {
    this.store = store;
    collection = store.getDB().getCollection(name);
  }

  public MongoDBStore getStore()
  {
    return store;
  }

  public DBCollection getCollection()
  {
    return collection;
  }

  public void ensureIndex(String element, String field, boolean asc)
  {
    DBObject index = new BasicDBObject();
    index.put(element + "." + field, asc ? 1 : -1);

    collection.createIndex(index);
  }

  public void ensureIndex(String element, String... fields)
  {
    DBObject index = new BasicDBObject();
    for (String field : fields)
    {
      index.put(element + "." + field, 1);

    }

    collection.createIndex(index);
  }

  /**
   * @author Eike Stepper
   */
  public abstract class Query<RESULT>
  {
    private DBObject ref;

    public Query(DBObject ref)
    {
      this.ref = ref;
    }

    public DBObject getRef()
    {
      return ref;
    }

    public RESULT execute()
    {
      return execute(collection.find(ref));
    }

    public RESULT execute(DBObject keys)
    {
      return execute(collection.find(ref, keys));
    }

    protected RESULT execute(DBCursor cursor)
    {
      try
      {
        while (cursor.hasNext())
        {
          DBObject doc = cursor.next();
          RESULT result = handleDoc(doc);
          if (result != null)
          {
            return result;
          }
        }

        return null;
      }
      finally
      {
        cursor.close();
      }
    }

    protected abstract RESULT handleDoc(DBObject doc);
  }
}
