/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.db4o;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.db4o.IDB4OIdentifiableObject;
import org.eclipse.emf.cdo.server.db4o.IDB4OStore;
import org.eclipse.emf.cdo.spi.server.LongIDStore;
import org.eclipse.emf.cdo.spi.server.StoreAccessorPool;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectServer;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.constraints.UniqueFieldValueConstraint;
import com.db4o.query.Query;
import com.db4o.reflect.jdk.JdkReflector;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Victor Roldan Betancort
 */
public class DB4OStore extends LongIDStore implements IDB4OStore
{
  private static final String ID_ATTRIBUTE = "id";

  private transient String storeLocation;

  private transient int port;

  private transient ObjectServer server;

  private transient Configuration serverConfiguration;

  private ServerInfo serverInfo;

  private DB4ODurableLockingManager durableLockingManager = new DB4ODurableLockingManager();

  @ExcludeFromDump
  private transient final StoreAccessorPool readerPool = new StoreAccessorPool(this, null);

  @ExcludeFromDump
  private transient final StoreAccessorPool writerPool = new StoreAccessorPool(this, null);

  public DB4OStore(String storeLocation, int port)
  {
    super(IDB4OStore.TYPE, set(ChangeFormat.REVISION), set(RevisionTemporality.NONE, RevisionTemporality.AUDITING),
        set(RevisionParallelism.NONE, RevisionParallelism.BRANCHING));

    // setRevisionTemporality(RevisionTemporality.AUDITING);
    // setRevisionParallelism(RevisionParallelism.BRANCHING);

    this.storeLocation = storeLocation;
    this.port = port;
  }

  public DB4OStore(String storeLocation, int port, Configuration serverConfiguration)
  {
    this(storeLocation, port);
    this.serverConfiguration = serverConfiguration;
  }

  public String getStoreLocation()
  {
    return storeLocation;
  }

  public int getPort()
  {
    return port;
  }

  public long getCreationTime()
  {
    return getServerInfo().getCreationTime();
  }

  public void setCreationTime(long creationTime)
  {
    getServerInfo().setCreationTime(creationTime);
  }

  public boolean isFirstStart()
  {
    return getServerInfo().isFirstTime();
  }

  public DB4ODurableLockingManager getDurableLockingManager()
  {
    return durableLockingManager;
  }

  public Map<String, String> getPersistentProperties(Set<String> names)
  {
    if (names == null || names.isEmpty())
    {
      return new HashMap<String, String>(getServerInfo().getProperties());
    }

    Map<String, String> result = new HashMap<String, String>();
    for (String key : names)
    {
      String value = getServerInfo().getProperties().get(key);
      if (value != null)
      {
        result.put(key, value);
      }
    }

    return result;
  }

  public void setPersistentProperties(Map<String, String> properties)
  {
    ServerInfo serverInfo = getServerInfo();
    serverInfo.getProperties().putAll(properties);
    commitServerInfo(null);
  }

  public void removePersistentProperties(Set<String> names)
  {
    ServerInfo serverInfo = getServerInfo();
    Map<String, String> properties = serverInfo.getProperties();
    for (String key : names)
    {
      properties.remove(key);
    }

    commitServerInfo(null);
  }

  // @Override
  // public CDOID getNextCDOID(LongIDStoreAccessor accessor, CDORevision revision)
  // {
  // ObjectContainer objectContainer = ((DB4OStoreAccessor)accessor).getObjectContainer();
  // ExtObjectContainer ext = objectContainer.ext();
  // ext.store(revision);
  //
  // long id = ext.getID(revision);
  // return CDOIDUtil.createLong(id);
  // }

  public ObjectContainer openClient()
  {
    return server.openClient();
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    initObjectServer();
    initServerInfo();
    setLastCommitTime(getServerInfo().getLastCommitTime());
    setLastObjectID(fetchLastObjectID());
    LifecycleUtil.activate(durableLockingManager);
  }

  private long fetchLastObjectID()
  {
    ObjectContainer container = openClient();

    try
    {
      Query query = container.query();
      query.constrain(DB4ORevision.class);
      query.descend("id").orderDescending();
      ObjectSet<?> results = query.execute();
      int size = results.size();
      if (size > 0)
      {
        DB4ORevision rev = (DB4ORevision)results.next();
        return rev.getID();
      }
      return 0;
    }
    finally
    {
      closeClient(container);
    }
  }

  @SuppressWarnings("deprecation")
  protected void initObjectServer()
  {
    Configuration configuration = serverConfiguration;
    if (configuration == null)
    {
      configuration = createServerConfiguration();
    }
    {
      File file = new File(getStoreLocation());
      if (!file.exists())
      {
        try
        {
          file.createNewFile();
        }
        catch (IOException ex)
        {
          throw new RuntimeException(ex);
        }
      }
    }

    server = Db4o.openServer(configuration, getStoreLocation(), getPort());
  }

  protected void tearDownObjectServer()
  {
    server.close();
    server = null;
  }

  private ServerInfo getServerInfo()
  {
    if (serverInfo == null)
    {
      initServerInfo();
    }

    return serverInfo;
  }

  private void initServerInfo()
  {
    ObjectContainer container = openClient();

    try
    {
      serverInfo = getServerInfoFromDatabase(container);

      if (serverInfo == null)
      {
        serverInfo = new ServerInfo();
        serverInfo.setFirstTime(true);
        serverInfo.setCreationTime(System.currentTimeMillis());
        commitServerInfo(container);
      }
      else
      {
        if (serverInfo.isFirstTime())
        {
          serverInfo.setFirstTime(false);
          commitServerInfo(container);
        }
      }
    }
    finally
    {
      closeClient(container);
    }
  }

  private ServerInfo getServerInfoFromDatabase(ObjectContainer container)
  {
    ObjectSet<ServerInfo> infos = container.query(ServerInfo.class);
    if (infos.size() > 1)
    {
      throw new IllegalStateException("ServeInfo is stored in container more than once");
    }

    if (infos.size() == 1)
    {
      return infos.get(0);
    }

    return null;
  }

  protected void closeClient(ObjectContainer container)
  {
    container.close();
  }

  private void commitServerInfo(ObjectContainer container)
  {
    ObjectContainer usedContainer = container != null ? container : openClient();

    try
    {
      ServerInfo storedInfo = getServerInfoFromDatabase(usedContainer);
      if (storedInfo != null && storedInfo != serverInfo)
      {
        storedInfo.setCreationTime(serverInfo.getCreationTime());
        storedInfo.setFirstTime(serverInfo.isFirstTime());
        storedInfo.setLastCommitTime(serverInfo.getLastCommitTime());
        storedInfo.setProperties(serverInfo.getProperties());
        serverInfo = storedInfo;
      }

      usedContainer.store(serverInfo);
      usedContainer.commit();
    }
    finally
    {
      if (usedContainer != container)
      {
        closeClient(usedContainer);
      }
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    ObjectContainer container = openClient();

    try
    {
      getServerInfo().setLastCommitTime(getLastCommitTime());
      commitServerInfo(container);
    }
    finally
    {
      closeClient(container);
    }

    LifecycleUtil.deactivate(durableLockingManager);
    tearDownObjectServer();

    super.doDeactivate();
  }

  protected Configuration createServerConfiguration()
  {
    @SuppressWarnings("deprecation")
    Configuration config = Db4o.newConfiguration();
    config.reflectWith(new JdkReflector(getClass().getClassLoader()));

    config.objectClass(DB4ORevision.class).objectField("id").indexed(true);
    config.add(new UniqueFieldValueConstraint(DB4ORevision.class, "id"));

    config.objectClass(DB4OPackageUnit.class).objectField("id").indexed(true);
    config.add(new UniqueFieldValueConstraint(DB4OPackageUnit.class, "id"));

    config.objectClass(DB4OIdentifiableObject.class).objectField("id").indexed(true);
    config.add(new UniqueFieldValueConstraint(DB4OIdentifiableObject.class, "id"));

    config.objectClass(DB4OCommitInfo.class).objectField("timeStamp").indexed(true);

    config.objectClass(DB4OLockArea.class).objectField("id").indexed(true);
    config.add(new UniqueFieldValueConstraint(DB4OLockArea.class, "id"));
    config.objectClass(DB4OLockArea.class).cascadeOnUpdate(true);
    config.objectClass(DB4OLockArea.class).cascadeOnDelete(true);

    return config;
  }

  @Override
  protected IStoreAccessor createReader(ISession session)
  {
    return new DB4OStoreAccessor(this, session);
  }

  @Override
  protected IStoreAccessor createWriter(ITransaction transaction)
  {
    return new DB4OStoreAccessor(this, transaction);
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

  public static DB4ORevision getRevision(ObjectContainer container, CDOID id)
  {
    Query query = container.query();
    query.constrain(DB4ORevision.class);
    query.descend(ID_ATTRIBUTE).constrain(CDOIDUtil.getLong(id));

    ObjectSet<?> revisions = query.execute();
    if (revisions.isEmpty())
    {
      return null;
    }

    return (DB4ORevision)revisions.get(0);
  }

  public static <T> List<T> getElementsOfType(ObjectContainer container, Class<T> clazz)
  {
    ObjectSet<T> elements = container.query(clazz);
    return elements;
  }

  public static IDB4OIdentifiableObject getIdentifiableObject(ObjectContainer container, String id)
  {
    Query query = container.query();
    query.constrain(IDB4OIdentifiableObject.class);
    query.descend(ID_ATTRIBUTE).constrain(id);

    ObjectSet<?> revisions = query.execute();
    if (revisions.isEmpty())
    {
      return null;
    }

    return (IDB4OIdentifiableObject)revisions.get(0);
  }

  public static void removeRevision(ObjectContainer container, CDOID id)
  {
    DB4ORevision revision = getRevision(container, id);
    if (revision != null)
    {
      container.delete(revision);
    }
  }

  /**
   * Carries {@link IStore}-related information.
   *
   * @author Victor Roldan Betancort
   */
  private static final class ServerInfo
  {
    private boolean isFirstTime;

    private long creationTime;

    private long lastCommitTime;

    private Map<String, String> properties = new HashMap<String, String>();

    public boolean isFirstTime()
    {
      return isFirstTime;
    }

    public void setFirstTime(boolean isFirstTime)
    {
      this.isFirstTime = isFirstTime;
    }

    public void setCreationTime(long creationTime)
    {
      this.creationTime = creationTime;
    }

    public long getCreationTime()
    {
      return creationTime;
    }

    public void setProperties(Map<String, String> properties)
    {
      this.properties = properties;
    }

    public Map<String, String> getProperties()
    {
      return properties;
    }

    public long getLastCommitTime()
    {
      return lastCommitTime;
    }

    public void setLastCommitTime(long lastCommitTime)
    {
      this.lastCommitTime = lastCommitTime;
    }
  }
}
