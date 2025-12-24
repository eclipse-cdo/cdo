/*
 * Copyright (c) 2012, 2015, 2016, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.lissome;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDOAllRevisionsProvider;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.internal.lissome.db.Index;
import org.eclipse.emf.cdo.server.internal.lissome.file.Journal;
import org.eclipse.emf.cdo.server.internal.lissome.file.Vob;
import org.eclipse.emf.cdo.server.internal.lissome.optimizer.Optimizer;
import org.eclipse.emf.cdo.server.lissome.ILissomeStore;
import org.eclipse.emf.cdo.server.lissome.ILissomeStoreAccessor;
import org.eclipse.emf.cdo.spi.server.InternalStore.NoChangeSets;
import org.eclipse.emf.cdo.spi.server.InternalStore.NoDurableLocking;
import org.eclipse.emf.cdo.spi.server.InternalStore.NoLargeObjects;
import org.eclipse.emf.cdo.spi.server.InternalStore.NoQueryXRefs;
import org.eclipse.emf.cdo.spi.server.InternalStore.NoRawAccess;
import org.eclipse.emf.cdo.spi.server.LongIDStore;
import org.eclipse.emf.cdo.spi.server.Store;
import org.eclipse.emf.cdo.spi.server.StoreAccessorPool;

import org.eclipse.net4j.db.DBException;
import org.eclipse.net4j.db.IDBConnectionProvider;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class LissomeStore extends Store
    implements ILissomeStore, CDOAllRevisionsProvider, IDBConnectionProvider, NoRawAccess, NoLargeObjects, NoQueryXRefs, NoChangeSets, NoDurableLocking
{
  public static final String TYPE = "lissome"; //$NON-NLS-1$

  public static final String PERSISTENT_PROPERTIES_EXTENSION = "properties";

  private static final String PROP_REPOSITORY_CREATED = "org.eclipse.emf.cdo.server.lissome.repositoryCreated"; //$NON-NLS-1$

  private static final String PROP_REPOSITORY_STOPPED = "org.eclipse.emf.cdo.server.lissome.repositoryStopped"; //$NON-NLS-1$

  private static final String PROP_LAST_CDOID = "org.eclipse.emf.cdo.server.lissome.lastCDOID"; //$NON-NLS-1$

  private static final String PROP_LAST_BRANCHID = "org.eclipse.emf.cdo.server.lissome.lastBranchID"; //$NON-NLS-1$

  private static final String PROP_LAST_METAID = "org.eclipse.emf.cdo.server.lissome.lastMetaID"; //$NON-NLS-1$

  private static final String PROP_LAST_COMMITTIME = "org.eclipse.emf.cdo.server.lissome.lastCommitTime"; //$NON-NLS-1$

  private static final String PROP_GRACEFULLY_SHUT_DOWN = "org.eclipse.emf.cdo.server.lissome.gracefullyShutDown"; //$NON-NLS-1$

  @ExcludeFromDump
  private transient StoreAccessorPool readerPool = new StoreAccessorPool(this, null);

  @ExcludeFromDump
  private transient StoreAccessorPool writerPool = new StoreAccessorPool(this, null);

  private Optimizer optimizer = createOptimizer();

  private Vob vob;

  private Index index;

  private Map<String, String> properties;

  private Properties persistentProperties = new Properties();

  private File folder;

  private Journal journal;

  private boolean firstStart;

  private long creationTime;

  private long lastCDOID;

  private int lastMetaID;

  private Map<Integer, Object> metaObjects = new HashMap<>();

  private Map<Object, Integer> metaIDs = new HashMap<>();

  public LissomeStore()
  {
    super(TYPE, null, set(ChangeFormat.DELTA), set(RevisionTemporality.AUDITING), set(RevisionParallelism.BRANCHING));
  }

  public void setProperties(Map<String, String> properties)
  {
    this.properties = properties;
  }

  public Map<String, String> getProperties()
  {
    return properties;
  }

  @Override
  public Set<ChangeFormat> getSupportedChangeFormats()
  {
    return set(ChangeFormat.DELTA);
  }

  public File getFolder()
  {
    return folder;
  }

  public void setFolder(File folder)
  {
    this.folder = folder;
  }

  public File getLogFile()
  {
    return journal;
  }

  @Override
  public Map<String, String> getPersistentProperties(Set<String> names)
  {
    Map<String, String> properties = new HashMap<>();

    synchronized (persistentProperties)
    {
      for (String name : names)
      {
        String value = persistentProperties.getProperty(name);
        if (value != null)
        {
          properties.put(name, value);
        }
      }
    }

    return properties;
  }

  @Override
  public void setPersistentProperties(Map<String, String> properties)
  {
    synchronized (persistentProperties)
    {
      persistentProperties.putAll(properties);
      savePersistentProperties();
    }
  }

  @Override
  public void removePersistentProperties(Set<String> names)
  {
    synchronized (persistentProperties)
    {
      for (String name : names)
      {
        persistentProperties.remove(name);
      }

      savePersistentProperties();
    }
  }

  protected void loadPersistentProperties(File file)
  {
    InputStream fis = null;

    try
    {
      fis = new FileInputStream(file);
      persistentProperties.load(fis);
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      IOUtil.close(fis);
    }
  }

  protected void savePersistentProperties()
  {
    OutputStream stream = null;

    try
    {
      stream = new FileOutputStream(getPersistentPropertiesFile());
      persistentProperties.store(stream, "Lissome Persistent Properties");
    }
    catch (IOException ex)
    {
      throw new IORuntimeException(ex);
    }
    finally
    {
      IOUtil.close(stream);
    }
  }

  protected File getPersistentPropertiesFile()
  {
    return new File(folder, getRepository().getName() + "." + PERSISTENT_PROPERTIES_EXTENSION);
  }

  @Override
  public CDOID createObjectID(String val)
  {
    // TODO: implement LissomeStore.createObjectID(val)
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public boolean isLocal(CDOID id)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public LissomeStoreReader getReader(ISession session)
  {
    return (LissomeStoreReader)super.getReader(session);
  }

  @Override
  public LissomeStoreWriter getWriter(ITransaction transaction)
  {
    return (LissomeStoreWriter)super.getWriter(transaction);
  }

  @Override
  protected StoreAccessorPool getReaderPool(ISession session, boolean forReleasing)
  {
    return readerPool;
  }

  @Override
  protected StoreAccessorPool getWriterPool(IView view, boolean forReleasing)
  {
    return writerPool;
  }

  @Override
  protected LissomeStoreReader createReader(ISession session)
  {
    return new LissomeStoreReader(this, session);
  }

  @Override
  protected LissomeStoreWriter createWriter(ITransaction transaction)
  {
    return new LissomeStoreWriter(this, transaction);
  }

  @Override
  public Map<CDOBranch, List<CDORevision>> getAllRevisions()
  {
    final Map<CDOBranch, List<CDORevision>> result = new HashMap<>();
    ILissomeStoreAccessor accessor = getReader(null);
    StoreThreadLocal.setAccessor(accessor);

    try
    {
      accessor.handleRevisions(null, null, CDOBranchPoint.UNSPECIFIED_DATE, true, new CDORevisionHandler.Filtered.Undetached(new CDORevisionHandler()
      {
        @Override
        public boolean handleRevision(CDORevision revision)
        {
          CDOBranch branch = revision.getBranch();
          List<CDORevision> list = result.get(branch);
          if (list == null)
          {
            list = new ArrayList<>();
            result.put(branch, list);
          }

          list.add(revision);
          return true;
        }
      }));
    }
    finally
    {
      StoreThreadLocal.release();
    }

    return result;
  }

  @Override
  public long getCreationTime()
  {
    return creationTime;
  }

  @Override
  public void setCreationTime(long creationTime)
  {
    this.creationTime = creationTime;

    Map<String, String> map = new HashMap<>();
    map.put(PROP_REPOSITORY_CREATED, Long.toString(creationTime));
    setPersistentProperties(map);
  }

  @Override
  public boolean isFirstStart()
  {
    return firstStart;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    checkNull(folder, "folder");
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    IDGenerationLocation idGenerationLocation = getRepository().getIDGenerationLocation();
    if (idGenerationLocation == IDGenerationLocation.CLIENT)
    {
      setObjectIDTypes(Collections.singleton(ObjectType.UUID));
    }
    else
    {
      setObjectIDTypes(LongIDStore.OBJECT_ID_TYPES);
    }

    vob = createVob();
    index = createIndex();
    journal = createJournal();
    optimizer.activate();

    File persistentPropertiesFile = getPersistentPropertiesFile();
    firstStart = !persistentPropertiesFile.exists() || persistentPropertiesFile.length() == 0;
    if (firstStart)
    {
      firstStart();
    }
    else
    {
      loadPersistentProperties(persistentPropertiesFile);
      reStart();
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    Map<String, String> map = new HashMap<>();
    map.put(PROP_GRACEFULLY_SHUT_DOWN, StringUtil.TRUE);
    map.put(PROP_REPOSITORY_STOPPED, Long.toString(getRepository().getTimeStamp()));

    if (getRepository().getIDGenerationLocation() == IDGenerationLocation.STORE)
    {
      map.put(PROP_LAST_CDOID, Long.toString(getLastCDOID()));
    }

    map.put(PROP_LAST_BRANCHID, Integer.toString(getLastBranchID()));
    map.put(PROP_LAST_METAID, Integer.toString(getLastMetaID()));
    map.put(PROP_LAST_COMMITTIME, Long.toString(getLastCommitTime()));
    setPersistentProperties(map);

    if (readerPool != null)
    {
      readerPool.dispose();
    }

    if (writerPool != null)
    {
      writerPool.dispose();
    }

    optimizer.deactivate();
    super.doDeactivate();
  }

  protected void firstStart()
  {
    long timeStamp = getRepository().getTimeStamp();
    setCreationTime(timeStamp);

    journal.firstStart();
    index.createTables();
  }

  protected void reStart() throws IOException
  {
    Set<String> names = new HashSet<>();
    names.add(PROP_REPOSITORY_CREATED);
    names.add(PROP_GRACEFULLY_SHUT_DOWN);

    Map<String, String> map = getPersistentProperties(names);
    creationTime = Long.valueOf(map.get(PROP_REPOSITORY_CREATED));

    if (map.containsKey(PROP_GRACEFULLY_SHUT_DOWN))
    {
      names.clear();

      boolean generatingIDs = getRepository().getIDGenerationLocation() == IDGenerationLocation.STORE;
      if (generatingIDs)
      {
        names.add(PROP_LAST_CDOID);
      }

      names.add(PROP_LAST_BRANCHID);
      names.add(PROP_LAST_METAID);
      names.add(PROP_LAST_COMMITTIME);
      map = getPersistentProperties(names);

      if (generatingIDs)
      {
        setLastCDOID(Long.valueOf(map.get(PROP_LAST_CDOID)));
      }

      setLastBranchID(Integer.valueOf(map.get(PROP_LAST_BRANCHID)));
      setLastMetaID(Integer.valueOf(map.get(PROP_LAST_METAID)));
      setLastCommitTime(Long.valueOf(map.get(PROP_LAST_COMMITTIME)));
    }
    else
    {
      repairAfterCrash();
    }

    removePersistentProperties(Collections.singleton(PROP_GRACEFULLY_SHUT_DOWN));

    journal.reStart();
  }

  protected void repairAfterCrash()
  {
    // TODO: implement LissomeStore.repairAfterCrash()
    throw new UnsupportedOperationException();
  }

  public Journal getJournal()
  {
    return journal;
  }

  protected Journal createJournal() throws FileNotFoundException
  {
    return new Journal(this);
  }

  public Optimizer getOptimizer()
  {
    return optimizer;
  }

  protected Optimizer createOptimizer()
  {
    return new Optimizer(this, true);
  }

  public Vob getVob()
  {
    return vob;
  }

  protected Vob createVob() throws IOException
  {
    return new Vob(this);
  }

  public Index getIndex()
  {
    return index;
  }

  protected Index createIndex()
  {
    return new Index(this);
  }

  public long getLastCDOID()
  {
    return lastCDOID;
  }

  public void setLastCDOID(long lastCDOID)
  {
    this.lastCDOID = lastCDOID;
  }

  public CDOID getNextCDOID()
  {
    return CDOIDUtil.createLong(++lastCDOID);
  }

  public int getLastMetaID()
  {
    return lastMetaID;
  }

  public void setLastMetaID(int lastMetaID)
  {
    this.lastMetaID = lastMetaID;
  }

  public int mapMetaObject(Object metaObject)
  {
    int id = ++lastMetaID;
    mapMetaObject(metaObject, id);
    return id;
  }

  public void mapMetaObject(Object metaObject, int id)
  {
    metaObjects.put(id, metaObject);
    metaIDs.put(metaObject, id);
  }

  public int getMetaID(Object metaObject)
  {
    return metaIDs.get(metaObject);
  }

  public Object getMetaObject(int metaID)
  {
    return metaObjects.get(metaID);
  }

  @Override
  public Connection getConnection() throws DBException
  {
    return index.getConnection();
  }
}
