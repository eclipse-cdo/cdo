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
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.mongodbdb.IMongoDBStore;
import org.eclipse.emf.cdo.server.mongodbdb.IMongoDBStoreAccessor;
import org.eclipse.emf.cdo.spi.server.Store;
import org.eclipse.emf.cdo.spi.server.StoreAccessorPool;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;

import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class MongoDBStore extends Store implements IMongoDBStore
{
  public static final String TYPE = "mongodb"; //$NON-NLS-1$

  private MongoURI mongoURI;

  private String dbName;

  private DB db;

  private IsolationLevel isolationLevel;

  private EmbeddingStrategy embeddingStrategy;

  private IDHandler idHandler;

  public MongoDBStore()
  {
    super(TYPE, null, set(ChangeFormat.DELTA), //
        set(RevisionTemporality.AUDITING, RevisionTemporality.NONE), //
        set(RevisionParallelism.NONE, RevisionParallelism.BRANCHING));
  }

  public MongoURI getMongoURI()
  {
    return mongoURI;
  }

  public void setMongoURI(MongoURI mongoURI)
  {
    checkInactive();
    this.mongoURI = mongoURI;
  }

  public String getDBName()
  {
    return dbName;
  }

  public void setDBName(String dbName)
  {
    checkInactive();
    this.dbName = dbName;
  }

  public DB getDB()
  {
    checkActive();
    return db;
  }

  public IsolationLevel getIsolationLevel()
  {
    return isolationLevel;
  }

  public void setIsolationLevel(IsolationLevel isolationLevel)
  {
    checkInactive();
    this.isolationLevel = isolationLevel;
  }

  public EmbeddingStrategy getEmbeddingStrategy()
  {
    return embeddingStrategy;
  }

  public void setEmbeddingStrategy(EmbeddingStrategy embeddingStrategy)
  {
    checkInactive();
    this.embeddingStrategy = embeddingStrategy;
  }

  public IDHandler getIDHandler()
  {
    return idHandler;
  }

  public void setIDHandler(IDHandler idHandler)
  {
    checkInactive();
    this.idHandler = idHandler;
  }

  public Map<String, String> getPropertyValues(Set<String> names)
  {
    return null;
  }

  public void setPropertyValues(Map<String, String> properties)
  {
  }

  public void removePropertyValues(Set<String> names)
  {
  }

  public boolean isFirstTime()
  {
    return false;
  }

  public long getCreationTime()
  {
    return 0;
  }

  public void setCreationTime(long creationTime)
  {
  }

  public CDOID createObjectID(String val)
  {
    return null;
  }

  public boolean isLocal(CDOID id)
  {
    return false;
  }

  @Override
  public IMongoDBStoreAccessor getReader(ISession session)
  {
    return (IMongoDBStoreAccessor)super.getReader(session);
  }

  @Override
  public IMongoDBStoreAccessor getWriter(ITransaction transaction)
  {
    return (IMongoDBStoreAccessor)super.getWriter(transaction);
  }

  @Override
  protected IStoreAccessor createReader(ISession session)
  {
    return null;
  }

  @Override
  protected IStoreAccessor createWriter(ITransaction transaction)
  {
    return null;
  }

  @Override
  protected StoreAccessorPool getReaderPool(ISession session, boolean forReleasing)
  {
    return null;
  }

  @Override
  protected StoreAccessorPool getWriterPool(IView view, boolean forReleasing)
  {
    return null;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkState(mongoURI, "mongoURI");
    checkState(dbName, "dbName");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    Mongo mongo = new Mongo(mongoURI);
    db = mongo.getDB(dbName);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    if (db != null)
    {
      db.getMongo().close();
      db = null;
    }

    super.doDeactivate();
  }
}
