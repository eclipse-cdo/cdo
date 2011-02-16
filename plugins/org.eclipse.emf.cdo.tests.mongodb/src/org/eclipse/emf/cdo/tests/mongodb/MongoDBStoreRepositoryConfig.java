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
package org.eclipse.emf.cdo.tests.mongodb;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.internal.mongodb.MongoDBStore;
import org.eclipse.emf.cdo.server.mongodbdb.CDOMongoDBUtil;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IPluginContainer;

import com.mongodb.DB;
import com.mongodb.Mongo;

/**
 * @author Eike Stepper
 */
public class MongoDBStoreRepositoryConfig extends RepositoryConfig
{
  public static final MongoDBStoreRepositoryConfig INSTANCE = new MongoDBStoreRepositoryConfig("MongoDBStore");

  private static final long serialVersionUID = 1L;

  public MongoDBStoreRepositoryConfig(String name)
  {
    super(name);
  }

  @Override
  public void setUp() throws Exception
  {
    CDOMongoDBUtil.prepareContainer(IPluginContainer.INSTANCE);
    super.setUp();
  }

  @Override
  public IStore createStore(String repoName)
  {
    try
    {
      Mongo mongo = new Mongo("localhost", 27017);
      DB db = mongo.getDB("cdotests");

      MongoDBStore store = new MongoDBStore();
      store.setDB(db);

      return store;
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }
}
