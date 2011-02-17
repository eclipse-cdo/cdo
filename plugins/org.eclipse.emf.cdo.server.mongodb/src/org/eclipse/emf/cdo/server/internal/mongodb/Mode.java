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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryResourcesContext;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * @author Eike Stepper
 */
public abstract class Mode
{
  private MongoDBStore store;

  protected Mode()
  {
  }

  public MongoDBStore getStore()
  {
    return store;
  }

  public void setStore(MongoDBStore store)
  {
    this.store = store;
  }

  public abstract DBObject createResourcesQuery(CDOID folderID, String name, boolean exactMatch,
      QueryResourcesContext context);

  /**
   * @author Eike Stepper
   */
  public static class Normal extends Mode
  {
    @Override
    public DBObject createResourcesQuery(CDOID folderID, String name, boolean exactMatch, QueryResourcesContext context)
    {
      DBObject query = new BasicDBObject();
      query.put("_version", new BasicDBObject("$gt", 0));
      getStore().getIDHandler().write(query, "_container", folderID);
      if (name == null)
      {
        query.put("name", new BasicDBObject("$exists", false));
      }
      else
      {
        query.put("name", exactMatch ? name : new BasicDBObject("$regex", "/^" + name + "/"));
      }

      return query;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Audditing extends Mode
  {
    @Override
    public DBObject createResourcesQuery(CDOID folderID, String name, boolean exactMatch, QueryResourcesContext context)
    {
      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Branching extends Mode
  {
    @Override
    public DBObject createResourcesQuery(CDOID folderID, String name, boolean exactMatch, QueryResourcesContext context)
    {
      return null;
    }
  }
}
