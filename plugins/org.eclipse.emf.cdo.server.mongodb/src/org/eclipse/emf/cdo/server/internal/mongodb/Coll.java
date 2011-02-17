/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.mongodb;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
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

    collection.ensureIndex(index);
  }

  public void ensureIndex(String element, String... fields)
  {
    DBObject index = new BasicDBObject();
    for (String field : fields)
    {
      index.put(element + "." + field, 1);

    }

    collection.ensureIndex(index);
  }
}
