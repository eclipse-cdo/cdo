/*
 * Copyright (c) 2011, 2012, 2016, 2017, 2019, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.mongodb;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.mongodb.CDOMongoDBUtil;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

import org.eclipse.net4j.util.WrappedException;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * @author Eike Stepper
 */
public class MongoDBConfig extends RepositoryConfig
{
  public static final String STORE_NAME = "MongoDB";

  private static final long serialVersionUID = 1L;

  public MongoDBConfig()
  {
    super(STORE_NAME);
  }

  @Override
  protected String getStoreName()
  {
    return STORE_NAME;
  }

  @Override
  public void setUp() throws Exception
  {
    super.setUp();
  }

  @Override
  public IStore createStore(String repoName)
  {
    ConnectionString connectionString = new ConnectionString("mongodb://localhost");
    if (!isRestarting())
    {
      dropDatabase(connectionString, repoName);
    }

    return CDOMongoDBUtil.createStore(connectionString.getConnectionString(), repoName);
  }

  protected void dropDatabase(ConnectionString connectionString, String repoName)
  {
    MongoClient mongoClient = null;

    try
    {
      mongoClient = MongoClients.create(connectionString);
      if (mongoClient.getDatabase(repoName).listCollectionNames().first() != null)
      {
        mongoClient.getDatabase(repoName).drop();
      }
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      if (mongoClient != null)
      {
        mongoClient.close();
      }
    }
  }

  @Override
  public boolean supportingExtRefs()
  {
    return false;
  }
}
