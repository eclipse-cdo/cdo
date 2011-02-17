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
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.internal.mongodb.bundle.OM;
import org.eclipse.emf.cdo.server.mongodb.IMongoDBStore;
import org.eclipse.emf.cdo.server.mongodb.IMongoDBStoreAccessor;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.Store;
import org.eclipse.emf.cdo.spi.server.StoreAccessorPool;

import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class MongoDBStore extends Store implements IMongoDBStore
{
  public static final String TYPE = "mongodb"; //$NON-NLS-1$

  private static final String PROP_REPOSITORY_CREATED = "org.eclipse.emf.cdo.server.mongodb.repositoryCreated"; //$NON-NLS-1$

  private static final String PROP_REPOSITORY_STOPPED = "org.eclipse.emf.cdo.server.mongodb.repositoryStopped"; //$NON-NLS-1$

  private static final String PROP_NEXT_LOCAL_CDOID = "org.eclipse.emf.cdo.server.mongodb.nextLocalCDOID"; //$NON-NLS-1$

  private static final String PROP_LAST_CDOID = "org.eclipse.emf.cdo.server.mongodb.lastCDOID"; //$NON-NLS-1$

  private static final String PROP_LAST_BRANCHID = "org.eclipse.emf.cdo.server.mongodb.lastBranchID"; //$NON-NLS-1$

  private static final String PROP_LAST_LOCAL_BRANCHID = "org.eclipse.emf.cdo.server.mongodb.lastLocalBranchID"; //$NON-NLS-1$

  private static final String PROP_LAST_COMMITTIME = "org.eclipse.emf.cdo.server.mongodb.lastCommitTime"; //$NON-NLS-1$

  private static final String PROP_LAST_NONLOCAL_COMMITTIME = "org.eclipse.emf.cdo.server.mongodb.lastNonLocalCommitTime"; //$NON-NLS-1$

  private static final String PROP_GRACEFULLY_SHUT_DOWN = "org.eclipse.emf.cdo.server.mongodb.gracefullyShutDown"; //$NON-NLS-1$

  private MongoURI mongoURI;

  private String dbName;

  private IDHandler idHandler = new IDHandler.LongValue(this);

  private Mapper mapper = new Mapper(this);

  private Mode mode;

  private DB db;

  private DBCollection propertiesCollection;

  private DBCollection packageUnitsCollection;

  private DBCollection commitInfosCollection;

  private boolean firstStart;

  private long creationTime;

  public static Map<String, InternalRepository> REPOS = new HashMap<String, InternalRepository>();

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

  public IDHandler getIDHandler()
  {
    return idHandler;
  }

  public void setIDHandler(IDHandler idHandler)
  {
    checkInactive();
    this.idHandler = idHandler;
  }

  public Mapper getMapper()
  {
    return mapper;
  }

  public Mode getMode()
  {
    return mode;
  }

  public DB getDB()
  {
    return db;
  }

  public DBCollection getPropertiesCollection()
  {
    return propertiesCollection;
  }

  public DBCollection getPackageUnitsCollection()
  {
    return packageUnitsCollection;
  }

  public DBCollection getCommitInfosCollection()
  {
    return commitInfosCollection;
  }

  public Map<String, String> getPropertyValues(Set<String> names)
  {
    Map<String, String> result = new HashMap<String, String>();
    for (String name : names)
    {
      DBObject query = new BasicDBObject("_id", name);
      DBCursor cursor = propertiesCollection.find(query);

      try
      {
        if (cursor.hasNext())
        {
          DBObject doc = cursor.next();
          result.put(name, (String)doc.get("value"));
        }
      }
      finally
      {
        cursor.close();
      }
    }

    return result;
  }

  public void setPropertyValues(Map<String, String> properties)
  {
    for (Entry<String, String> property : properties.entrySet())
    {
      DBObject doc = new BasicDBObject();
      doc.put("_id", property.getKey());
      doc.put("value", property.getValue());

      propertiesCollection.insert(doc);

    }
  }

  public void removePropertyValues(Set<String> names)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public boolean isFirstStart()
  {
    return firstStart;
  }

  public long getCreationTime()
  {
    return creationTime;
  }

  public void setCreationTime(long creationTime)
  {
    this.creationTime = creationTime;

    Map<String, String> map = new HashMap<String, String>();
    map.put(PROP_REPOSITORY_CREATED, Long.toString(creationTime));
    setPropertyValues(map);
  }

  public CDOID createObjectID(String val)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }

  public boolean isLocal(CDOID id)
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
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
    return new MongoDBStoreAccessor(this, session);
  }

  @Override
  protected IStoreAccessor createWriter(ITransaction transaction)
  {
    return new MongoDBStoreAccessor(this, transaction);
  }

  @Override
  protected StoreAccessorPool getReaderPool(ISession session, boolean forReleasing)
  {
    // No pooling needed
    return null;
  }

  @Override
  protected StoreAccessorPool getWriterPool(IView view, boolean forReleasing)
  {
    // No pooling needed
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
    REPOS.put(getRepository().getName(), getRepository());

    super.doActivate();

    Mongo mongo = new Mongo(mongoURI);
    db = mongo.getDB(dbName);

    Set<String> collectionNames = db.getCollectionNames();
    firstStart = !collectionNames.contains("cdo.properties");

    propertiesCollection = db.getCollection("cdo.properties");
    packageUnitsCollection = db.getCollection("cdo.packageUnits");
    commitInfosCollection = db.getCollection("cdo.commitInfos");

    LifecycleUtil.activate(idHandler);
    setObjectIDTypes(idHandler.getObjectIDTypes());

    LifecycleUtil.activate(mapper);
    initMode();

    if (firstStart)
    {
      firstStart();
    }
    else
    {
      reStart();
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    REPOS.remove(getRepository().getName());

    Map<String, String> map = new HashMap<String, String>();
    map.put(PROP_GRACEFULLY_SHUT_DOWN, Boolean.TRUE.toString());
    map.put(PROP_REPOSITORY_STOPPED, Long.toString(getRepository().getTimeStamp()));
    map.put(PROP_NEXT_LOCAL_CDOID, Store.idToString(idHandler.getNextLocalObjectID()));
    map.put(PROP_LAST_CDOID, Store.idToString(idHandler.getLastObjectID()));
    map.put(PROP_LAST_BRANCHID, Integer.toString(getLastBranchID()));
    map.put(PROP_LAST_LOCAL_BRANCHID, Integer.toString(getLastLocalBranchID()));
    map.put(PROP_LAST_COMMITTIME, Long.toString(getLastCommitTime()));
    map.put(PROP_LAST_NONLOCAL_COMMITTIME, Long.toString(getLastNonLocalCommitTime()));
    setPropertyValues(map);

    LifecycleUtil.activate(mapper);
    LifecycleUtil.activate(idHandler);

    if (db != null)
    {
      db.getMongo().close();
      db = null;
    }

    super.doDeactivate();
  }

  protected void initMode()
  {
    if (getRepository().isSupportingBranches())
    {
      mode = new Mode.Branching();
    }
    else if (getRepository().isSupportingBranches())
    {
      mode = new Mode.Audditing();
    }
    else
    {
      mode = new Mode.Normal();
    }

    mode.setStore(this);
  }

  protected void firstStart()
  {
    setCreationTime(getRepository().getTimeStamp());
    OM.LOG.info("First start: " + CDOCommonUtil.formatTimeStamp(creationTime));
  }

  protected void reStart()
  {
    Set<String> names = new HashSet<String>();
    names.add(PROP_REPOSITORY_CREATED);
    names.add(PROP_GRACEFULLY_SHUT_DOWN);

    Map<String, String> map = getPropertyValues(names);
    creationTime = Long.valueOf(map.get(PROP_REPOSITORY_CREATED));

    if (map.containsKey(PROP_GRACEFULLY_SHUT_DOWN))
    {
      names.clear();
      names.add(PROP_NEXT_LOCAL_CDOID);
      names.add(PROP_LAST_CDOID);
      names.add(PROP_LAST_BRANCHID);
      names.add(PROP_LAST_LOCAL_BRANCHID);
      names.add(PROP_LAST_COMMITTIME);
      names.add(PROP_LAST_NONLOCAL_COMMITTIME);
      map = getPropertyValues(names);

      idHandler.setNextLocalObjectID(stringToID(map.get(PROP_NEXT_LOCAL_CDOID)));
      idHandler.setLastObjectID(stringToID(map.get(PROP_LAST_CDOID)));
      setLastBranchID(Integer.valueOf(map.get(PROP_LAST_BRANCHID)));
      setLastLocalBranchID(Integer.valueOf(map.get(PROP_LAST_LOCAL_BRANCHID)));
      setLastCommitTime(Long.valueOf(map.get(PROP_LAST_COMMITTIME)));
      setLastNonLocalCommitTime(Long.valueOf(map.get(PROP_LAST_NONLOCAL_COMMITTIME)));
    }
    else
    {
      repairAfterCrash();
    }

    removePropertyValues(Collections.singleton(PROP_GRACEFULLY_SHUT_DOWN));
  }

  protected void repairAfterCrash()
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }
}
