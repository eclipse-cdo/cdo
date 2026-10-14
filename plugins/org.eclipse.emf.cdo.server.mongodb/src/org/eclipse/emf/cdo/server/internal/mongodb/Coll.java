/*
 * Copyright (c) 2011, 2012, 2015, 2017, 2025 Eike Stepper (Loehne, Germany) and others.
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
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

/**
 * @author Eike Stepper
 */
public class Coll
{
  protected MongoDBStore store;

  protected MongoCollection<DBObject> collection;

  public Coll(MongoDBStore store, String name)
  {
    this.store = store;
    collection = store.getDB().getCollection(name, DBObject.class);
  }

  public MongoDBStore getStore()
  {
    return store;
  }

  public MongoCollection<DBObject> getCollection()
  {
    return collection;
  }

  public void ensureIndex(String element, String field, boolean asc)
  {
    BasicDBObject index = new BasicDBObject();
    index.put(element + "." + field, asc ? 1 : -1);

    collection.createIndex(index);
  }

  public void ensureIndex(String element, String... fields)
  {
    BasicDBObject index = new BasicDBObject();
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
    private BasicDBObject ref;

    public Query(BasicDBObject ref)
    {
      this.ref = ref;
    }

    public BasicDBObject getRef()
    {
      return ref;
    }

    public RESULT execute()
    {
      return execute(collection.find(ref).cursor());
    }

    public RESULT execute(BasicDBObject keys)
    {
      return execute(collection.find(ref).projection(keys).cursor());
    }

    protected RESULT execute(MongoCursor<DBObject> cursor)
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
