/*
 * Copyright (c) 2011, 2012, 2015, 2017, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.mongodb;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreFactory;
import org.eclipse.emf.cdo.server.mongodb.CDOMongoDBUtil;
import org.eclipse.emf.cdo.spi.server.RepositoryConfigurator;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;

import org.w3c.dom.Element;

import java.util.Map;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("deprecation")
public class MongoDBStoreFactory implements IStoreFactory
{
  public MongoDBStoreFactory()
  {
  }

  @Override
  public String getStoreType()
  {
    return MongoDBStore.TYPE;
  }

  @Override
  public IStore createStore(String repositoryName, Map<String, String> repositoryProperties, Element storeConfig)
  {
    Map<String, String> properties = RepositoryConfigurator.getProperties(storeConfig, 1);
    String uri = properties.get("uri");
    if (StringUtil.isEmpty(uri))
    {
      throw new IllegalArgumentException("Property 'uri' missing");
    }

    MongoURI mongoURI = new MongoURI(uri);
    String dbName = properties.get("db");
    if (StringUtil.isEmpty(dbName))
    {
      dbName = repositoryName;
    }

    String drop = properties.get("drop");
    if (Boolean.toString(true).equals(drop))
    {
      dropDatabase(mongoURI, dbName);
    }

    return CDOMongoDBUtil.createStore(uri, dbName);
  }

  protected void dropDatabase(MongoURI mongoURI, String dbName)
  {
    Mongo mongo = null;

    try
    {
      mongo = new Mongo(mongoURI);
      DB db = mongo.getDB(dbName);
      db.dropDatabase();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      if (mongo != null)
      {
        mongo.close();
      }
    }
  }
}
