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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * TODO:
 * <ul>
 * <li>Are indexes always unique? Do unique indexes exist?
 * <li>Are <code>_id</code> fields in embedded objects automatically indexed?
 * </ul>
 * 
 * @author Eike Stepper
 */
public class MongoDBStore extends Store implements IMongoDBStore
{
  public static final String PROPERTIES = "props";

  public static final String TYPE = "mongodb"; //$NON-NLS-1$

  private MongoURI mongoURI;

  private String dbName;

  private IDHandler idHandler = new IDHandler.LongValue(this);

  private Mapper mapper = new Mapper(this);

  private DB db;

  private Props props;

  private Commits commits;

  private boolean firstStart;

  private long creationTime;

  private boolean branching;

  private int lastClassifierID;

  private Map<EClassifier, Integer> classifierToIDs = new HashMap<EClassifier, Integer>();

  private Map<Integer, EClassifier> idToClassifiers = new HashMap<Integer, EClassifier>();

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

  public DB getDB()
  {
    return db;
  }

  public Props getProps()
  {
    return props;
  }

  public Commits getCommits()
  {
    return commits;
  }

  public Map<String, String> getPropertyValues(Set<String> names)
  {
    return props.get(names);
  }

  public void setPropertyValues(Map<String, String> properties)
  {
    props.set(properties);
  }

  public void removePropertyValues(Set<String> names)
  {
    props.remove(names);
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
    map.put(Props.REPOSITORY_CREATED, Long.toString(creationTime));
    props.set(map);
  }

  public boolean isBranching()
  {
    return branching;
  }

  public int getLastClassifierID()
  {
    return lastClassifierID;
  }

  public void setLastClassifierID(int lastClassifierID)
  {
    this.lastClassifierID = lastClassifierID;
  }

  public synchronized int mapNewClassifier(EClassifier classifier)
  {
    int id = ++lastClassifierID;
    classifierToIDs.put(classifier, id);
    idToClassifiers.put(id, classifier);
    return id;
  }

  public synchronized int getClassifierID(EClassifier classifier)
  {
    return classifierToIDs.get(classifier);
  }

  public synchronized EClassifier getClassifier(int id)
  {
    return idToClassifiers.get(id);
  }

  public synchronized EClass getClass(int id)
  {
    return (EClass)idToClassifiers.get(id);
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
    InternalRepository repository = getRepository();
    branching = repository.isSupportingBranches();

    REPOS.put(repository.getName(), repository);

    super.doActivate();

    Mongo mongo = new Mongo(mongoURI);
    db = mongo.getDB(dbName);

    Set<String> collectionNames = db.getCollectionNames();
    firstStart = !collectionNames.contains(PROPERTIES);

    props = new Props(this);
    commits = new Commits(this);

    LifecycleUtil.activate(idHandler);
    setObjectIDTypes(idHandler.getObjectIDTypes());

    LifecycleUtil.activate(mapper);

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
    map.put(Props.GRACEFULLY_SHUT_DOWN, Boolean.TRUE.toString());
    map.put(Props.REPOSITORY_STOPPED, Long.toString(getRepository().getTimeStamp()));
    map.put(Props.NEXT_LOCAL_CDOID, Store.idToString(idHandler.getNextLocalObjectID()));
    map.put(Props.LAST_CDOID, Store.idToString(idHandler.getLastObjectID()));
    map.put(Props.LAST_CLASSIFIERID, Integer.toString(getLastClassifierID()));
    map.put(Props.LAST_BRANCHID, Integer.toString(getLastBranchID()));
    map.put(Props.LAST_LOCAL_BRANCHID, Integer.toString(getLastLocalBranchID()));
    map.put(Props.LAST_COMMITTIME, Long.toString(getLastCommitTime()));
    map.put(Props.LAST_NONLOCAL_COMMITTIME, Long.toString(getLastNonLocalCommitTime()));
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

  protected void firstStart()
  {
    setCreationTime(getRepository().getTimeStamp());
    OM.LOG.info("First start: " + CDOCommonUtil.formatTimeStamp(creationTime));
  }

  protected void reStart()
  {
    Set<String> names = new HashSet<String>();
    names.add(Props.REPOSITORY_CREATED);
    names.add(Props.GRACEFULLY_SHUT_DOWN);

    Map<String, String> map = getPropertyValues(names);
    creationTime = Long.valueOf(map.get(Props.REPOSITORY_CREATED));

    if (map.containsKey(Props.GRACEFULLY_SHUT_DOWN))
    {
      names.clear();
      names.add(Props.NEXT_LOCAL_CDOID);
      names.add(Props.LAST_CDOID);
      names.add(Props.LAST_CLASSIFIERID);
      names.add(Props.LAST_BRANCHID);
      names.add(Props.LAST_LOCAL_BRANCHID);
      names.add(Props.LAST_COMMITTIME);
      names.add(Props.LAST_NONLOCAL_COMMITTIME);
      map = getPropertyValues(names);

      idHandler.setNextLocalObjectID(stringToID(map.get(Props.NEXT_LOCAL_CDOID)));
      idHandler.setLastObjectID(stringToID(map.get(Props.LAST_CDOID)));
      setLastClassifierID(Integer.valueOf(map.get(Props.LAST_CLASSIFIERID)));
      setLastBranchID(Integer.valueOf(map.get(Props.LAST_BRANCHID)));
      setLastLocalBranchID(Integer.valueOf(map.get(Props.LAST_LOCAL_BRANCHID)));
      setLastCommitTime(Long.valueOf(map.get(Props.LAST_COMMITTIME)));
      setLastNonLocalCommitTime(Long.valueOf(map.get(Props.LAST_NONLOCAL_COMMITTIME)));
    }
    else
    {
      repairAfterCrash();
    }

    removePropertyValues(Collections.singleton(Props.GRACEFULLY_SHUT_DOWN));
  }

  protected void repairAfterCrash()
  {
    throw new UnsupportedOperationException("Not yet implemented"); // TODO Implement me
  }
}
